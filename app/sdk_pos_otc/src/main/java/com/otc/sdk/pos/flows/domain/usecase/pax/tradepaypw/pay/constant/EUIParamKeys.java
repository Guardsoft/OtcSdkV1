package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.pay.constant;

public enum EUIParamKeys {
    /**
     * 提示信息1
     */
    PROMPT_1,
    /**
     * 提示信息2
     */
    PROMPT_2,
    /**
     * 输入1数据类型, {@link EInputType}
     */
    INPUT_TYPE_1,
    /**
     * 输入2数据类型, {@link EInputType}
     */
    INPUT_TYPE_2,
    /**
     * 输入1数据最大长度
     */
    INPUT_MAX_LEN_1,
    /**
     * 输入2数据最大长度
     */
    INPUT_MAX_LEN_2,
    /**
     * 显示内容
     */
    CONTENT,
    /**
     * 交易金额
     */
    TRANS_AMOUNT,
    /**
     * 交易日期
     */
    TRANS_DATE,

    /**
     * 寻卡界面类型
     */
    SEARCH_CARD_UI_TYPE,

    /**
     * 是否可直接撤销最后一笔交易
     */
    VOID_LAST_TRANS_UI,
    /**
     * 电子签名特征码
     */
    SIGN_FEATURE_CODE,

    /**
     * 列表1的值
     */
    ARRAY_LIST_1,
    /**
     * 列表2的值
     */
    ARRAY_LIST_2,

    /**
     * 导航栏抬头
     */
    NAV_TITLE,
    /**
     * 导航栏是否显示返回按钮
     */
    NAV_BACK,
    /**
     * 寻卡模式
     */
    CARD_SEARCH_MODE,
    /**
     * 寻卡界面显示授权码
     */
    AUTH_CODE,
    /**
     * 寻卡界面刷卡提醒
     */
    SEARCH_CARD_PROMPT,
    ;
}
