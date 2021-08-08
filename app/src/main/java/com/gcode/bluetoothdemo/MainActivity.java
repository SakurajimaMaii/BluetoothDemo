package com.gcode.bluetoothdemo;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gcode.bluetoothdemo.connect.AcceptThread;
import com.gcode.bluetoothdemo.connect.ConnectThread;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 0;
    private final Set<BluetoothDevice> searchDeviceList = new HashSet<>();
    private Set<BluetoothDevice> bondedDeviceList = new HashSet<>();

    private final BlueToothController mController = new BlueToothController(this);
    private MsgHandler mUIHandler;


    private ListView mListView;
    private DeviceAdapter mAdapter;
    private Toast mToast;

    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUIHandler = new MsgHandler(this,Looper.myLooper());

        initUI();

        registerBluetoothReceiver();

        mController.turnOnBlueTooth(this,REQUEST_CODE);
    }

    private void registerBluetoothReceiver(){
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
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                //初始化数据列表
                searchDeviceList.clear();
                mAdapter.notifyDataSetChanged();
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Log.i(TAG,"ACTION_DISCOVERY_FINISHED");
            }
            else if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //找到一个添加一个
                if(device!=null&&!bondedDeviceList.contains(device)){
                    searchDeviceList.add(device);
                    mAdapter.notifyDataSetChanged();
                }
            }
            else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(remoteDevice == null) {
                    showToast("无设备");
                    return;
                }
                int status = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0);
                if(status == BluetoothDevice.BOND_BONDED) {
                    showToast("已绑定" + remoteDevice.getName());
                } else if(status == BluetoothDevice.BOND_BONDING) {
                    showToast("正在绑定" + remoteDevice.getName());
                } else if(status == BluetoothDevice.BOND_NONE) {
                    showToast("未绑定" + remoteDevice.getName());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    /**
     * 导航栏菜单
     * @param item MenuItem
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
            mController.findDevice(this);
            mListView.setOnItemClickListener(bondDeviceClick);
        }
        //查看已绑定设备
        else if (id == R.id.bonded_device) {
            bondedDeviceList = mController.getBondedDeviceList();
            mAdapter.refresh(bondedDeviceList);
            mListView.setOnItemClickListener(bondedDeviceClick);
        }
        //开始监听
        else if( id == R.id.listening) {
            if( mAcceptThread != null) {
                mAcceptThread.cancel();
            }
            if(mController.getAdapter()!=null){
                mAcceptThread = new AcceptThread(mController.getAdapter(), mUIHandler);
                mAcceptThread.start();
            }
        }
        else if( id == R.id.stop_listening) {
            if( mAcceptThread != null) {
                mAcceptThread.cancel();
            }
        }
        else if( id == R.id.disconnect) {
            if( mConnectThread != null) {
                mConnectThread.cancel();
            }
        }
        else if( id == R.id.say_hello) {
            say("Hello\n");
        }
        else if( id == R.id.say_hi) {
            say("Hi\n");
        }

        return super.onOptionsItemSelected(item);
    }

    private void say(String word) {
        if (mAcceptThread != null) {
            mAcceptThread.sendData(word.getBytes(StandardCharsets.UTF_8));
        }

        else if( mConnectThread != null) {
            mConnectThread.sendData(word.getBytes(StandardCharsets.UTF_8));
        }

    }

    private final AdapterView.OnItemClickListener bondDeviceClick = (adapterView, view, i, l) -> {
        BluetoothDevice device = new ArrayList<>(searchDeviceList).get(i);
        device.createBond();
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

    //设置toast的标准格式
    @SuppressLint("ShowToast")
    private void showToast(String text){
        if(mToast == null){
            mToast = Toast.makeText(this, text,Toast.LENGTH_SHORT);
        }
        else {
            mToast.setText(text);
        }
        mToast.show();
    }

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
}
