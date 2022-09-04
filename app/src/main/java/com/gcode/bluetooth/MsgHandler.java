package com.gcode.bluetooth;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.gcode.bluetooth.connect.Constant;
import com.gcode.vasttools.utils.ToastUtils;

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
                ToastUtils.showShortMsg(context,"data:" + message.obj);
                break;
            case Constant.MSG_ERROR:
                ToastUtils.showShortMsg(context,"error:" + message.obj);
                break;
            case Constant.MSG_CONNECTED_TO_SERVER:
                ToastUtils.showShortMsg(context,"连接到服务端");
                break;
            case Constant.MSG_GOT_A_CLIENT:
                ToastUtils.showShortMsg(context,"找到服务端");
                break;
        }
    }
}