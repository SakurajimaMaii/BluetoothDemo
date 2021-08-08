package com.gcode.bluetoothdemo;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.gcode.bluetoothdemo.connect.Constant;

/**
 * 作者:created by HP on 2021/8/8 13:31 邮箱:sakurajimamai2020@qq.com
 */
public class MsgHandler extends Handler {

    private Toast mToast;
    private final Context context;

    public MsgHandler(@NonNull Context context, @NonNull Looper looper) {
        super(looper);
        this.context = context;
    }

    public void handleMessage(Message message) {
        super.handleMessage(message);
        switch (message.what) {
            case Constant.MSG_GOT_DATA:
                showToast("data:" + message.obj);
                break;
            case Constant.MSG_ERROR:
                showToast("error:" + message.obj);
                break;
            case Constant.MSG_CONNECTED_TO_SERVER:
                showToast("连接到服务端");
                break;
            case Constant.MSG_GOT_A_CLIENT:
                showToast("找到服务端");
                break;
        }
    }

    private void showToast(String text){
        if(mToast == null){
            mToast = Toast.makeText(context, text,Toast.LENGTH_SHORT);
        }
        else {
            mToast.setText(text);
        }
        mToast.show();
    }
}