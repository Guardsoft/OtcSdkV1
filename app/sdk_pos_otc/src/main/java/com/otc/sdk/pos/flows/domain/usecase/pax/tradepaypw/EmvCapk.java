/*
 * ============================================================================
 * COPYRIGHT
 *              Pax CORPORATION PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with Pax Corporation and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2016 - ? Pax Corporation. All rights reserved.
 * Module Date: 2016-11-25
 * Module Author: Steven.W
 * Description:
 *
 * ============================================================================
 */
package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw;


import com.pax.jemv.clcommon.EMV_CAPK;

import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils.str2Bcd;

public class EmvCapk {
    private int id;

    // 应用注册服务商ID
    private String RID;
    // 密钥索引
    private int KeyID;
    // HASH算法标志
    private int HashInd;
    // RSA算法标志
    private int arithInd;
    // 模
    private String module;
    // 指数
    private String Exponent;
    // 有效期(YYMMDD)
    private String expDate;
    // 密钥校验和
    private String checkSum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRID() {

        return RID;
    }

    public void setRID(String rID) {

        RID = rID;
    }

    public int getKeyID() {

        return KeyID;
    }

    public void setKeyID(int keyID) {

        KeyID = keyID;
    }

    public int getHashInd() {

        return HashInd;
    }

    public void setHashInd(int hashInd) {

        HashInd = hashInd;
    }

    public int getArithInd() {

        return arithInd;
    }

    public void setArithInd(int arithInd) {

        this.arithInd = arithInd;
    }

    public String getModule() {

        return module;
    }

    public void setModule(String module) {

        this.module = module;
    }

    public String getExponent() {

        return Exponent;
    }

    public void setExponent(String exponent) {

        Exponent = exponent;
    }

    public String getExpDate() {

        return expDate;
    }

    public void setExpDate(String expDate) {

        this.expDate = expDate;
    }

    public String getCheckSum() {

        return checkSum;
    }

    public void setCheckSum(String checkSum) {

        this.checkSum = checkSum;
    }

    /********************************
     * EmvCapk to Capk
     *******************************/
    public static EMV_CAPK toCapk(EmvCapk readCapk) {
        if (readCapk.getModule() == null || readCapk.getExponent() == null)
            return null;
        EMV_CAPK capk = new EMV_CAPK();
        capk.rID = str2Bcd(readCapk.getRID());
        capk.keyID = (byte) readCapk.getKeyID();
        capk.hashInd = (byte) readCapk.getHashInd();
        capk.arithInd = (byte) readCapk.getArithInd();
        capk.modul = str2Bcd(readCapk.getModule());
        capk.modulLen = (short) capk.modul.length;
        capk.exponent = str2Bcd(readCapk.getExponent());
        capk.exponentLen = (byte) capk.exponent.length;
        capk.expDate = str2Bcd(readCapk.getExpDate());
        capk.checkSum = str2Bcd(readCapk.getCheckSum());

        return capk;
    }
}
