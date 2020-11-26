package com.example.bluetoothdemo.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.util.UUID;

/**
 * 监听连接申请的线程
 */
public class AcceptThread extends Thread {
    private static final String NAME = "BlueToothClass";
    private static final UUID MY_UUID = UUID.fromString(Constant.CONNECTION_UUID);

    private final BluetoothServerSocket mmServerSocket;
    private final Handler mHandler;
    private ConnectedThread mConnectedThread;

    public AcceptThread(BluetoothAdapter adapter, Handler handler) {
        // 使用一个临时对象，该对象稍后被分配给mmServerSocket，因为mmServerSocket是最终的
        mHandler = handler;
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID是应用程序的UUID，客户端代码使用相同的UUID
            tmp = adapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException ignored) { }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket;
        //持续监听，直到出现异常或返回socket
        while (true) {
            try {
                mHandler.sendEmptyMessage(Constant.MSG_START_LISTENING);
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                mHandler.sendMessage(mHandler.obtainMessage(Constant.MSG_ERROR, e));
                break;
            }
            // 如果一个连接被接受
            if (socket != null) {
                // 在单独的线程中完成管理连接的工作
                manageConnectedSocket(socket);
                try {
                    mmServerSocket.close();
                    mHandler.sendEmptyMessage(Constant.MSG_FINISH_LISTENING);
                } catch (IOException e) {
                    e.printStackTrace();
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
        mHandler.sendEmptyMessage(Constant.MSG_GOT_A_CLINET);
        mConnectedThread = new ConnectedThread(socket, mHandler);
        mConnectedThread.start();
    }

    /**
     * 取消监听socket，使此线程关闭
     */
    public void cancel() {
        try {
            mmServerSocket.close();
            mHandler.sendEmptyMessage(Constant.MSG_FINISH_LISTENING);
        } catch (IOException ignored) { }
    }

    public void sendData(byte[] data) {
        if( mConnectedThread!=null){
            mConnectedThread.write(data);
        }
    }
}