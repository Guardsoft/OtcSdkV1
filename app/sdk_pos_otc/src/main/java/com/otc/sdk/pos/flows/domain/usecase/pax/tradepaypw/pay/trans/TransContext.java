package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.pay.trans;

import android.content.Context;

import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.abl.core.AAction;


public class TransContext {
    private static TransContext transContext;

    private String operID;

    private Context currentContext;
    private AAction currentAction;

    private TransContext() {

    }

    public static synchronized TransContext getInstance() {
        if (transContext == null) {
            transContext = new TransContext();
        }
        return transContext;
    }

    public String getOperID() {
        return operID;
    }

    public void setOperID(String operID) {
        this.operID = operID;
    }

    public Context getCurrentContext() {
        return currentContext;
    }

    public void setCurrentContext(Context currentContext) {
        this.currentContext = currentContext;
    }

    public AAction getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(AAction currentAction) {
        this.currentAction = currentAction;
    }

}
