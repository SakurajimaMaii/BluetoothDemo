package com.example.bluetoothdemo;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import static com.example.bluetoothdemo.MainActivity.REQUEST_CODE;

class BlueToothController {
    private BluetoothAdapter mAdapter;

    public BlueToothController(){
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BluetoothAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 打开蓝牙
     */
    public void turnOnBlueTooth(Activity activity, int requestCode) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开蓝牙可见性
     */
    public void enableVisibily(Context context){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
        context.startActivity(intent);
    }

    /**
     * 查找设备
     */
    public void findDevice(Context context) {
        assert (mAdapter != null);
        mAdapter.startDiscovery();
        Boolean str = mAdapter.startDiscovery();
        checkPermission(context, _permissions);
        Log.d("123456789",str.toString());
    }

    /**
     * 获取已绑定设备
     */
    public List<BluetoothDevice> getBondedDeviceList(){
        return new ArrayList<>(mAdapter.getBondedDevices());
    }

    private String[] _permissions = {
Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.BLUETOOTH,Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private void checkPermission(Context ctx, String[] permissions) {
        int REQUEST = 1;
        int permission = 0;

        for (String item :
                permissions) {
            permission += ActivityCompat.checkSelfPermission(ctx, item);
        }

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    (Activity) ctx,
                    permissions,
                    REQUEST
            );
        }
    }

}
