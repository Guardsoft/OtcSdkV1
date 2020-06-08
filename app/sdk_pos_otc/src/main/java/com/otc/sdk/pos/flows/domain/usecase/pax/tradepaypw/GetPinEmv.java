package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw;

import android.os.ConditionVariable;

/**
 * Created by yanglj on 2017-05-18.
 */

public class GetPinEmv {
    private static GetPinEmv instance;

    private int pinResult;
    private String pinData;
    private String pinDataEncrypt;
    private ConditionVariable cv;


    public static GetPinEmv getInstance() {
        if (instance == null) {
            instance = new GetPinEmv();
        }
        return instance;
    }

    public int GetPinResult() {
        return pinResult;
    }

    public void setPinResult(int result) {
        pinResult = result;
    }

    public String getPinData() {
        return pinData;
    }

    public void setPinData(String pindata) {
        pinData = pindata;
    }

    public String getPinDataEncrypt() {
        return pinDataEncrypt;
    }

    public void setPinDataEncrypt(String pinDataEncrypt) {
        this.pinDataEncrypt = pinDataEncrypt;
    }
}
