package com.gcode.bluetoothdemo.connect;

import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.util.Log;

import com.gcode.bluetoothdemo.MsgHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final MsgHandler mHandler;

    private final String tag = this.getClass().getSimpleName();

    public ConnectedThread(BluetoothSocket socket, MsgHandler handler) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        mHandler = handler;
        // 使用临时对象获取输入和输出流，因为成员流是最终的
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            Log.e(tag, "Error occurred when creating input stream", e);
        }

        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(tag, "Error occurred when creating output stream", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }


    public void run() {
        byte[] buffer = new byte[1024];  // 用于流的缓冲存储
        int bytes; // 从read()返回bytes
        // 持续监听InputStream，直到出现异常
        while (true) {
            try {
                // 从InputStream读取数据
                bytes = mmInStream.read(buffer);
                // 将获得的bytes发送到UI层activity
                if (bytes > 0) {
                    Message message = mHandler.obtainMessage(Constant.MSG_GOT_DATA, new String(buffer, 0, bytes, StandardCharsets.UTF_8));
                    mHandler.sendMessage(message);
                }
                Log.d(tag, "message size" + bytes);
            } catch (IOException e) {
                mHandler.sendMessage(mHandler.obtainMessage(Constant.MSG_ERROR, e));
                Log.d(tag, "Input stream was disconnected", e);
                break;
            }
        }
    }

    /**
     * 在main中调用此函数，将数据发送到远端设备中
     */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.e(tag, "Error occurred when sending data", e);
        }
    }

    /**
     * 在main中调用此函数，断开连接
     */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(tag, "Could not close the connect socket", e);
        }
    }
}
