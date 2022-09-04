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

package com.gcode.bluetooth.connect;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.gcode.bluetooth.MsgHandler;
import com.gcode.vasttools.helper.ContextHelper;

import java.io.IOException;
import java.util.UUID;


public class ConnectThread extends Thread {
    private static final UUID MY_UUID = UUID.fromString(Constant.CONNECTION_UUID);
    private final BluetoothSocket mmSocket;
    private final BluetoothAdapter mBluetoothAdapter;
    private final MsgHandler mHandler;
    private ConnectedThread mConnectedThread;

    private final String tag = this.getClass().getSimpleName();

    public ConnectThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter, MsgHandler handler) {
        // U将一个临时对象分配给mmSocket，因为mmSocket是最终的
        BluetoothSocket tmp = null;
        mBluetoothAdapter = bluetoothAdapter;
        mHandler = handler;
        // 用BluetoothSocket连接到给定的蓝牙设备
        try {
            // MY_UUID是应用程序的UUID，客户端代码使用相同的UUID
            if (ActivityCompat.checkSelfPermission(ContextHelper.INSTANCE.getAppContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            }
        } catch (IOException e) {
            Log.e(tag, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    @SuppressLint("MissingPermission")
    public void run() {
        // 搜索占用资源大，关掉提高速度
        mBluetoothAdapter.cancelDiscovery();

        try {
            // 通过socket连接设备，阻塞运行直到成功或抛出异常时
            mmSocket.connect();
        } catch (Exception connectException) {
            mHandler.sendMessage(mHandler.obtainMessage(Constant.MSG_ERROR, connectException));
            // 如果无法连接则关闭socket并退出
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(tag, "Could not close the client socket", e);
            }
            return;
        }
        // 在单独的线程中完成管理连接的工作
        manageConnectedSocket(mmSocket);
    }

    private void manageConnectedSocket(BluetoothSocket mmSocket) {
        mHandler.sendEmptyMessage(Constant.MSG_CONNECTED_TO_SERVER);
        mConnectedThread = new ConnectedThread(mmSocket, mHandler);
        mConnectedThread.start();
    }

    /**
     * 取消正在进行的连接并关闭socket
     */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(tag, "Could not close the client socket", e);
        }
    }

    /**
     * 发送数据
     */
    public void sendData(byte[] data) {
        if( mConnectedThread!=null){
            mConnectedThread.write(data);
        }
    }
}