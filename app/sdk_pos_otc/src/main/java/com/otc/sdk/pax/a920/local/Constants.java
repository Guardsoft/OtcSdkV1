package com.otc.sdk.pax.a920.local;

/**
 * Created by chenld on 2017/3/11.
 */

public class Constants {

    /**
     * 交易成功，弹出对话框的显示时间, 单位秒
     */
    public final static int SUCCESS_DIALOG_SHOW_TIME = 2;
    /**
     * 失败 时弹出框的显示时间, 单位秒
     */
    public final static int FAILED_DIALOG_SHOW_TIME = 3;

    /**
     * 自定义软键盘取消键值定义
     */
    public final static int KEY_EVENT_CANCEL = 65535;
    /**
     * 自定义软件盘隐藏键值定义
     */
    public final static int KEY_EVENT_HIDE = 65534;

    /**
     * master密钥索引
     */
    public final static byte INDEX_TMK = 0x01;

    /**
     * mac密钥索引
     */
    public final static byte INDEX_TAK = 0x01;
    /**
     * pin密钥索引
     */
    public static final byte INDEX_TPK = 0x03;
    public static final byte INDEX_TPK2 = 0x02;
    /**
     * des密钥索引
     */
    public static final byte INDEX_TDK = 0x05;

    /**
     * TIK密钥索引
     */
    public static final byte INDEX_TIK = 0x01;

}
