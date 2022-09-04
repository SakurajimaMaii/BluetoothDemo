package com.gcode.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Set;

class BlueToothController {
    private final BluetoothAdapter bluetoothAdapter;

    private final Context context;

    public BlueToothController(@NonNull Context context) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
    }

    /**
     * <p>获取蓝牙适配器<p/>
     * <p>调用静态的 getDefaultAdapter() 方法。此方法会返回一个 BluetoothAdapter 对象，
     * 表示设备自身的蓝牙适配器（蓝牙无线装置）。整个系统只有一个蓝牙适配器，并且您的应用可使用此对象与之进行交互。
     * 如果 getDefaultAdapter() 返回 null，则表示设备不支持蓝牙。<p/>
     * @return BluetoothAdapter
     */
    @Nullable
    public BluetoothAdapter getAdapter() {
        if (null == bluetoothAdapter) {
            Toast.makeText(context, "该设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            return null;
        } else {
            return bluetoothAdapter;
        }
    }

    /**
     * <p>打开蓝牙<p/>
     * <p>调用 isEnabled()，以检查当前是否已启用蓝牙。如果此方法返回 false，则表示蓝牙处于停用状态。
     * 如要请求启用蓝牙，请调用 startActivityForResult()，从而传入一个 ACTION_REQUEST_ENABLE Intent 操作。
     * 此调用会发出通过系统设置启用蓝牙的请求（无需停止应用）。<p/>
     * @param activity    Activity
     * @param requestCode int 局部定义的整型数（必须大于 0）。系统会以 onActivityResult() 实现中的 requestCode 参数形式，向您传回该常量。
     */
    @SuppressLint("MissingPermission")
    public void turnOnBlueTooth(Activity activity, @IntRange(from = 0) int requestCode) {
        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * <p>设置蓝牙可见性时间<p/>
     * <p>默认情况下，设备处于可检测到模式的时间为 120 秒（2 分钟）。
     * 通过添加 EXTRA_DISCOVERABLE_DURATION Extra 属性，
     * 您可以定义不同的持续时间，最高可达 3600 秒（1 小时）。<p/>
     * <p>注意:
     * 如果您将 EXTRA_DISCOVERABLE_DURATION Extra 属性的值设置为 0，
     * 则设备将始终处于可检测到模式。此配置安全性低，因而非常不建议使用。<p/>
     * @param duration int
     */
    @SuppressLint("MissingPermission")
    public void enableVisibility(@IntRange(from = 0) int duration) {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
        context.startActivity(discoverableIntent);
    }

    /**
     * 查找设备
     */
    @SuppressLint("MissingPermission")
    public void findDevice() {
        assert (bluetoothAdapter != null);
        bluetoothAdapter.startDiscovery();
    }

    /**
     * 获取绑定的设备
     * @return Set<BluetoothDevice>
     */
    @SuppressLint("MissingPermission")
    public Set<BluetoothDevice> getBondedDeviceList() {
        return bluetoothAdapter.getBondedDevices();
    }
}