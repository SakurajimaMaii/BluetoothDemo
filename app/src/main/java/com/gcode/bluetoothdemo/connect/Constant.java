package com.gcode.bluetoothdemo.connect;

import java.util.UUID;

/**
 * 给定状态参数常量
 */
public class Constant {
    /**
     * 使用UUID.randomUUID()生成唯一随机的UUID
     */
    public static final String CONNECTION_UUID = UUID.randomUUID().toString();

    /**
     * 开始监听
     */
    public static final int MSG_START_LISTENING = 1;

    /**
     * 结束监听
     */
    public static final int MSG_FINISH_LISTENING = 2;

    /**
     * 有客户端连接
     */
    public static final int MSG_GOT_A_CLIENT = 3;

    /**
     * 连接到服务器
     */
    public static final int MSG_CONNECTED_TO_SERVER = 4;

    /**
     * 获取到数据
     */
    public static final int MSG_GOT_DATA = 5;

    /**
     * 出错
     */
    public static final int MSG_ERROR = -1;
}
