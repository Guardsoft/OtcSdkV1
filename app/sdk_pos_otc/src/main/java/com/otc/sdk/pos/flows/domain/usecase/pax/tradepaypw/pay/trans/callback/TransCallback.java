package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.pay.trans.callback;

import com.pax.jemv.clcommon.CLSS_TORN_LOG_RECORD;
import com.pax.jemv.clcommon.EMV_CAPK;

/**
 * Created by Administrator on 2017/3/22 0022.
 */

public abstract class TransCallback {

    public abstract int removeCardPrompt();

    public abstract int displaySeePhone();

    public abstract int detectRFCardAgain();

    public abstract int dontRemoveCard();

    public abstract int getCapk(byte[] rid, byte index, EMV_CAPK capk);

    public abstract int appLoadTornLog(CLSS_TORN_LOG_RECORD[] tornLogRecords);

    public abstract int appSaveTornLog(CLSS_TORN_LOG_RECORD[] ptTornLog, int nTornNum);
}
