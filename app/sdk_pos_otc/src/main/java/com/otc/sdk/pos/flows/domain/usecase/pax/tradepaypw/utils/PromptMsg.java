package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils;

import com.pax.jemv.clcommon.RetCode;

/**
 * Created by Administrator on 2017/3/21 0021.
 */

public class PromptMsg {
    public static final int ONLY_PAYPASS_PAYWAVE = -300;

    public static String getErrorMsg(int ret) {
        String message = "";
        switch (ret) {
            case RetCode.EMV_OK:
                message = "Transaction Success";
                break;
            case RetCode.EMV_NO_APP:
                message = "There is no AID matched between ICC and terminal";
                break;
            case RetCode.EMV_DATA_ERR:
                message = "Data error is found";
                break;
            case RetCode.EMV_NO_APP_PPSE_ERR:
                message = "No application is supported";
                break;
            case RetCode.ICC_CMD_ERR:
                message = "Read card error";
                break;
            case RetCode.CLSS_PARAM_ERR:
                message = "Parameter is error";
                break;
            case RetCode.EMV_DENIAL:
                message = "Transaction is denied";
                break;
            case ONLY_PAYPASS_PAYWAVE:
                message = "only paypass paywave card";
                break;
            default:
                message = "Undefined Error" + "[" + ret + "]";
                break;
        }
        return message;
    }
}
