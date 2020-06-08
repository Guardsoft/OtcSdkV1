package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw;

import com.pax.jemv.clcommon.RetCode;

public enum EEmvExceptions {
    EMV_OK(RetCode.EMV_OK, "success"),
    EMV_ERR_ICC_RESET(RetCode.ICC_RESET_ERR, "icc reset error"),
    EMV_ERR_ICC_CMD(RetCode.ICC_CMD_ERR, "icc cmd error"),
    EMV_ERR_ICC_BLOCK(RetCode.ICC_BLOCK, "icc block"),
    EMV_ERR_RSP(RetCode.EMV_RSP_ERR, "emv response error"),
    EMV_ERR_APP_BLOCK(RetCode.EMV_APP_BLOCK, "emv application block"),
    EMV_ERR_NO_APP(RetCode.EMV_NO_APP, "emv no application"),
    EMV_ERR_USER_CANCEL(RetCode.EMV_USER_CANCEL, "emv user cancel"),
    EMV_ERR_TIME_OUT(RetCode.EMV_TIME_OUT, "emv time out"),
    EMV_ERR_DATA(RetCode.EMV_DATA_ERR, "emv data error"),
    EMV_ERR_NOT_ACCEPT(RetCode.EMV_NOT_ACCEPT, "emv not accept"),
    EMV_ERR_DENIAL(RetCode.EMV_DENIAL, "emv denial"),
    EMV_ERR_KEY_EXP(RetCode.EMV_KEY_EXP, "emv key expiry"),
    EMV_ERR_NO_PINPAD(RetCode.EMV_NO_PINPAD, "emv no pinpad"),
    EMV_ERR_NO_PASSWORD(RetCode.EMV_NO_PASSWORD, "emv no password"),
    EMV_ERR_SUM(RetCode.EMV_SUM_ERR, "emv checksum error"),
    EMV_ERR_NOT_FOUND(RetCode.EMV_NOT_FOUND, "emv not found"),
    EMV_ERR_NO_DATA(RetCode.EMV_NO_DATA, "emv no data"),
    EMV_ERR_OVERFLOW(RetCode.EMV_OVERFLOW, "emv overflow"),
    EMV_ERR_NO_TRANS_LOG(RetCode.NO_TRANS_LOG, "emv no trans log"),
    EMV_ERR_RECORD_NOTEXIST(RetCode.RECORD_NOTEXIST, "emv recode not exist"),
    EMV_ERR_LOGITEM_NOTEXIST(RetCode.LOGITEM_NOTEXIST, "emv log item not exist"),
    EMV_ERR_ICC_RSP_6985(RetCode.ICC_RSP_6985, "icc response 6985"),
    EMV_ERR_CLSS_USE_CONTACT(RetCode.CLSS_USE_CONTACT, "clss use contact"),
    EMV_ERR_FILE(RetCode.EMV_FILE_ERR, "emv file error"),
    EMV_ERR_CLSS_TERMINATE(RetCode.CLSS_TERMINATE, "clss terminate"),
    EMV_ERR_CLSS_FAILED(RetCode.CLSS_FAILED, "clss failed"),
    EMV_ERR_CLSS_DECLINE(RetCode.CLSS_DECLINE, "clss decline"),
    EMV_ERR_PARAM(RetCode.EMV_PARAM_ERR, "emv parameter error"),
    EMV_ERR_CLSS_WAVE2_OVERSEA(RetCode.CLSS_WAVE2_OVERSEA, "clss wave2 oversea"),
    EMV_ERR_CLSS_WAVE2_US_CARD(RetCode.CLSS_WAVE2_US_CARD, "clss wave2 us card"),
    EMV_ERR_CLSS_WAVE3_INS_CARD(RetCode.CLSS_WAVE3_INS_CARD, "clss wave3 ins card"),
    //EMV_ERR_DATA_OVERFLOW(RetCode.EMV_OVERFLOW, "emv data overflow"),
    EMV_ERR_CLSS_CARD_EXPIRED(RetCode.CLSS_CARD_EXPIRED, "clss card expired"),
    EMV_ERR_CLSS_NO_APP_PPSE(RetCode.EMV_NO_APP_PPSE_ERR, "clss no app ppse error"),
    EMV_ERR_CLSS_USE_VSDC(RetCode.CLSS_USE_VSDC, "clss use vsdc"),
    EMV_ERR_CLSS_CVMDECLINE(RetCode.CLSS_CVMDECLINE, "clss cvmdecline"),
    EMV_ERR_CLSS_REFER_CONSUMER_DEVICE(RetCode.CLSS_REFER_CONSUMER_DEVICE, "clss refer consumer device"),
    EMV_ERR_NEXT_CVM(-8053, "emv next CVM"),
    EMV_ERR_QUIT_CVM(-8057, "emv quit CVM"),
    EMV_ERR_SELECT_NEXT(-8059, "emv select next"),

    EMV_ERR_FALL_BACK(-8200, "fall back"),
    EMV_ERR_CHANNEL(-8201, "channel error"),
    EMV_ERR_PARAM_INVA(-8202, "parameter invalid error"),
    EMV_ERR_EC_PARAM(-8203, "electronic cash parameter error"),
    EMV_ERR_CORE_PATH(-8204, "core path error"),
    EMV_ERR_CLSS_NO_BALANCE(-8205, "clss no balance"),
    EMV_ERR_CLSS_OVERFLMT(-8206, "clss overflmt"),
    EMV_ERR_NOCLSSPBOC(-8207, "no clss pboc card error"),
    EMV_ERR_WRITEFILE_FAIL(-8208, "writefile fail error"),
    EMV_ERR_READFILE_FAIL(-8209, "readfile fail error"),
    EMV_ERR_INVAILD_PARA(-8210, "invaild parameter error"),

    EMV_ERR_AMOUNT_FORMAT(-8211, "amount format error"),
    EMV_ERR_PARAM_LENGTH(-8212, "parameter length error"),
    EMV_ERR_LISTENER_IS_NULL(-8213, "listener is null"),
    EMV_ERR_TAG_LENGTH(-8214, "tag length error"),

    EMV_ERR_ONLINE_TRANS_ABORT(-8301, "online transaction abort"),
    EMV_ERR_FUNCTION_NOT_IMPLEMENTED(-8302, "function not implemented"),
    EMV_ERR_PURE_EC_CARD_NOT_ONLINE(-8303, "pure EC card can't online transaction"),
    EMV_ERR_UNKNOWN(-8999, "unknown error");

    private int errCodeFromBasement;
    private String errMsgEn;

    EEmvExceptions(int errCodeFromBasement, String errMsgEn) {
        this.errCodeFromBasement = errCodeFromBasement;
        this.errMsgEn = errMsgEn;
    }

    public int getErrCodeFromBasement() {
        return this.errCodeFromBasement;
    }

    public String getErrMsg() {
        return this.errMsgEn;
    }
}

/* Location:           E:\Linhb\projects\Android\PaxEEmv_V1.00.00_20170401\lib\PaxEEmv_V1.00.00_20170401.jar
 * Qualified Name:     com.pax.eemv.exception.EEmvExceptions
 * JD-Core Version:    0.6.0
 */