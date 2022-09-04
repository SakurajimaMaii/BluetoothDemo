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