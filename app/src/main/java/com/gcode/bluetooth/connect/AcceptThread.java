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
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.gcode.bluetooth.MsgHandler;
import com.gcode.vasttools.helper.ContextHelper;

import java.io.IOException;
import java.util.UUID;

/**
 * 监听连接申请的线程
 */
public class AcceptThread extends Thread {
    private static final String NAME = "BlueToothClass";
    private static final UUID MY_UUID = UUID.fromString(Constant.CONNECTION_UUID);

    private final BluetoothServerSocket mmServerSocket;
    private final MsgHandler msgHandler;
    private ConnectedThread mConnectedThread;

    private final String tag = this.getClass().getSimpleName();

    public AcceptThread(@NonNull BluetoothAdapter adapter, @NonNull MsgHandler handler) {
        msgHandler = handler;
        // 使用一个临时对象，该对象稍后被分配给mmServerSocket，因为mmServerSocket是最终的
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID是应用程序的UUID，客户端代码使用相同的UUID
            if (ActivityCompat.checkSelfPermission(ContextHelper.INSTANCE.getAppContext(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                tmp = adapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket;
        //持续监听，直到出现异常或返回socket
        while (true) {
            try {
                msgHandler.sendEmptyMessage(Constant.MSG_START_LISTENING);
                socket = mmServerSocket.accept();
            } catch (Exception e) {
                msgHandler.sendMessage(msgHandler.obtainMessage(Constant.MSG_ERROR, e));
                e.printStackTrace();
                break;
            }
            // 如果一个连接被接受
            if (socket != null) {
                // 在单独的线程中完成管理连接的工作
                manageConnectedSocket(socket);
                try {
                    mmServerSocket.close();
                    msgHandler.sendEmptyMessage(Constant.MSG_FINISH_LISTENING);
                } catch (IOException e) {
                    Log.e(tag, "Socket's close() method failed", e);
                }
                break;
            }
        }
    }

    private void manageConnectedSocket(BluetoothSocket socket) {
        //只支持同时处理一个连接
        if( mConnectedThread != null) {
            mConnectedThread.cancel();
        }
        msgHandler.sendEmptyMessage(Constant.MSG_GOT_A_CLIENT);
        mConnectedThread = new ConnectedThread(socket, msgHandler);
        mConnectedThread.start();
    }

    /**
     * 取消监听socket，使此线程关闭
     */
    public void cancel() {
        try {
            mmServerSocket.close();
            msgHandler.sendEmptyMessage(Constant.MSG_FINISH_LISTENING);
        } catch (IOException e) {
            Log.e(tag, "Could not close the connect socket", e);
        }
    }

    public void sendData(byte[] data) {
        if( mConnectedThread!=null){
            mConnectedThread.write(data);
        }
    }
}