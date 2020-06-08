package com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clsspure.trans.model;

/**
 * Created by yanglj on 2017-11-23.
 */

public class Clss_PureAidParam {
    public byte[] ioOption = new byte[1];    //FF8134
    public byte[] appAuthType = new byte[1];    //FF8135
    public byte[] tacDenial = new byte[6];
    public byte[] tacOnline = new byte[6];
    public byte[] tacDefault = new byte[6];
    public byte[] acquierId = new byte[6];
    public byte[] dDOL = new byte[256];
    public int dDolLen;
    public byte[] mtDOL = new byte[256];
    public int mtDolLen;
    public byte[] aTOL = new byte[256];
    public int aTolLen;
    public byte[] atdTOL = new byte[256];
    public int atdTolLen;
    public byte[] Version = new byte[3];
    public byte ucRFU;

    public Clss_PureAidParam() {
    }

    public Clss_PureAidParam(byte appAuthType, byte ioOption, byte maxTargetPer, byte[] tacDenial, byte[] tacOnline, byte[] tacDefault, byte[] acquierId, byte ucRFU) {
        this.appAuthType[0] = appAuthType;
        this.ioOption[0] = ioOption;
        this.tacDenial = tacDenial;
        this.tacOnline = tacOnline;
        this.tacDefault = tacDefault;
        this.acquierId = acquierId;
        this.ucRFU = ucRFU;
    }
}
