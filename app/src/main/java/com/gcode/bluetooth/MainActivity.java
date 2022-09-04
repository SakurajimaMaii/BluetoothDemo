/*
 * Copyright 2022 VastGui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gcode.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.gcode.bluetooth.connect.AcceptThread;
import com.gcode.bluetooth.connect.ConnectThread;
import com.gcode.bluetooth.databinding.ActivityMainBinding;
import com.gcode.vasttools.activity.VastVbActivity;
import com.gcode.vasttools.utils.LogUtils;
import com.gcode.vasttools.utils.ToastUtils;
import com.permissionx.guolindev.PermissionX;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends VastVbActivity<ActivityMainBinding> {

    public static final int REQUEST_CODE = 0;
    private final Set<BluetoothDevice> searchDeviceList = new HashSet<>();
    private Set<BluetoothDevice> bondedDeviceList = new HashSet<>();

    private final BlueToothController mController = new BlueToothController(this);
    private MsgHandler mUIHandler;


    private ListView mListView;
    private DeviceAdapter mAdapter;

    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;

    private final String tag = this.getClass().getSimpleName();

    @Override
    public void initView(Bundle savedInstanceState) {
        mUIHandler = new MsgHandler(this, Looper.myLooper());

        initPermission();
        initUI();

        registerBluetoothReceiver();

        mController.turnOnBlueTooth(this, REQUEST_CODE);
    }

    /**
     * 初始化权限
     */
    private void initPermission() {

        List<String> permissions;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            permissions = new ArrayList<>(Arrays.asList(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            ));
        } else {
            permissions = new ArrayList<>(Arrays.asList(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.BLUETOOTH_ADVERTISE
            ));
        }

        PermissionX.init(this).permissions(permissions).request((allGranted, grantedList, deniedList) -> {
            if (allGranted) {
                LogUtils.INSTANCE.i(tag, "所有权限已经授权");
            } else {
                ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), 0x01);
            }
        });
    }

    private void registerBluetoothReceiver() {
        IntentFilter filter = new IntentFilter();
        //开始查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //结束查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //查找设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备扫描模式改变
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        //绑定状态
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(receiver, filter);
    }

    //注册广播监听搜索结果
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //初始化数据列表
                searchDeviceList.clear();
                mAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                LogUtils.INSTANCE.i(tag, "ACTION_DISCOVERY_FINISHED");
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //找到一个添加一个
                if (device != null && !bondedDeviceList.contains(device)) {
                    searchDeviceList.add(device);
                    mAdapter.notifyDataSetChanged();
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (remoteDevice == null) {
                    ToastUtils.showShortMsg(mContext, "无设备");
                    return;
                }
                int status = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0);
                if (status == BluetoothDevice.BOND_BONDED) {
                    // 对于权限进行了适配
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 0x01);
                            return;
                        }
                    }
                    ToastUtils.showShortMsg(mContext, "已绑定" + remoteDevice.getName());
                } else if (status == BluetoothDevice.BOND_BONDING) {
                    ToastUtils.showShortMsg(mContext, "正在绑定" + remoteDevice.getName());
                } else if (status == BluetoothDevice.BOND_NONE) {
                    ToastUtils.showShortMsg(mContext, "未绑定" + remoteDevice.getName());
                }
            }
        }
    };

    //初始化用户界面
    private void initUI() {

        mListView = findViewById(R.id.device_list);
        mAdapter = new DeviceAdapter(searchDeviceList, this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(bondDeviceClick);

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 导航栏菜单
     *
     * @param item MenuItem
     *
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.enable_visibility) {
            mController.enableVisibility(300);
        }
        //查找设备
        else if (id == R.id.find_device) {
            mAdapter.refresh(searchDeviceList);
            mController.findDevice();
            mListView.setOnItemClickListener(bondDeviceClick);
        }
        //查看已绑定设备
        else if (id == R.id.bonded_device) {
            bondedDeviceList = mController.getBondedDeviceList();
            mAdapter.refresh(bondedDeviceList);
            mListView.setOnItemClickListener(bondedDeviceClick);
        }
        //开始监听
        else if (id == R.id.listening) {
            if (mAcceptThread != null) {
                mAcceptThread.cancel();
            }
            if (mController.getAdapter() != null) {
                mAcceptThread = new AcceptThread(mController.getAdapter(), mUIHandler);
                mAcceptThread.start();
            }
        } else if (id == R.id.stop_listening) {
            if (mAcceptThread != null) {
                mAcceptThread.cancel();
            }
        } else if (id == R.id.disconnect) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
            }
        } else if (id == R.id.say_hello) {
            say("Hello\n");
        } else if (id == R.id.say_hi) {
            say("Hi\n");
        }

        return super.onOptionsItemSelected(item);
    }

    private void say(String word) {
        if (mAcceptThread != null) {
            mAcceptThread.sendData(word.getBytes(StandardCharsets.UTF_8));
        } else if (mConnectThread != null) {
            mConnectThread.sendData(word.getBytes(StandardCharsets.UTF_8));
        }
    }

    @SuppressLint("MissingPermission")
    private final AdapterView.OnItemClickListener bondDeviceClick = (adapterView, view, i, l) -> {
        BluetoothDevice device = new ArrayList<>(searchDeviceList).get(i);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT)) {
                ToastUtils.showShortMsg(mContext,"需要蓝牙链接权限");
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 0X02);
            }
        }else{
            device.createBond();
        }
    };

    private final AdapterView.OnItemClickListener bondedDeviceClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothDevice device = new ArrayList<>(bondedDeviceList).get(i);
            if (mConnectThread != null) {
                mConnectThread.cancel();
            }
            mConnectThread = new ConnectThread(device, mController.getAdapter(), mUIHandler);
            mConnectThread.start();
        }
    };

    /**
     * 退出时注销广播、注销连接过程、注销等待连接的监听
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
        }
        if (mConnectThread != null) {
            mConnectThread.cancel();
        }

        unregisterReceiver(receiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0x02){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                ToastUtils.showShortMsg(mContext,"权限已经被授予");
            }
        }
    }
}
