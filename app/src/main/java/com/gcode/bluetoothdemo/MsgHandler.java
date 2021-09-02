package com.gcode.bluetoothdemo;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.gcode.bluetoothdemo.connect.Constant;
import com.gcode.tools.utils.MsgWindowUtils;

/**
 * 作者:created by HP on 2021/8/8 13:31 邮箱:sakurajimamai2020@qq.com
 */
public class MsgHandler extends Handler {

    private final Context context;

    public MsgHandler(@NonNull Context context, @NonNull Looper looper) {
        super(looper);
        this.context = context;
    }

    public void handleMessage(Message message) {
        super.handleMessage(message);
        switch (message.what) {
            case Constant.MSG_GOT_DATA:
                MsgWindowUtils.INSTANCE.showShortMsg(context,"data:" + message.obj);
                break;
            case Constant.MSG_ERROR:
                MsgWindowUtils.INSTANCE.showShortMsg(context,"error:" + message.obj);
                break;
            case Constant.MSG_CONNECTED_TO_SERVER:
                MsgWindowUtils.INSTANCE.showShortMsg(context,"连接到服务端");
                break;
            case Constant.MSG_GOT_A_CLIENT:
                MsgWindowUtils.INSTANCE.showShortMsg(context,"找到服务端");
                break;
        }
    }
}