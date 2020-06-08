package com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssjspeedy.model;

/**
 * Created by yanglj on 2017-11-23.
 */

public class Clss_JcbAidParam {
    public long threshold;    //FF8133
    public byte targetPer;    //FF8132
    public byte maxTargetPer;     //FF8131
    public byte[] tacDenial = new byte[6];
    public byte[] tacOnline = new byte[6];
    public byte[] tacDefault = new byte[6];
    public byte[] acquierId = new byte[6];
    public byte ucRFU;

    public Clss_JcbAidParam() {
    }

    public Clss_JcbAidParam(long threshold, byte targetPer, byte maxTargetPer, byte[] tacDenial, byte[] tacOnline, byte[] tacDefault, byte[] acquierId, byte ucRFU) {
        this.threshold = threshold;
        this.targetPer = targetPer;
        this.maxTargetPer = maxTargetPer;
        this.tacDenial = tacDenial;
        this.tacOnline = tacOnline;
        this.tacDefault = tacDefault;
        this.acquierId = acquierId;
        this.ucRFU = ucRFU;
    }
}
