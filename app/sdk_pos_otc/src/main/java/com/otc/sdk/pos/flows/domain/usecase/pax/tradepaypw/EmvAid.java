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

import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils;
import com.pax.jemv.clcommon.EMV_APPLIST;

public class EmvAid {

    private int id;
    /**
     * name
     */
    private String appName;
    /**
     * aid, 应用标志
     */
    private String aid;
    /**
     * 选择标志(PART_MATCH 部分匹配 FULL_MATCH 全匹配)
     */
    private int selFlag;
    /**
     * priority
     */
    private int priority;

    /**
     * 目标百分比数
     */
    private int targetPer;
    /**
     * 最大目标百分比数
     */
    private int maxTargetPer;
    /**
     * 是否检查最低限额
     */
    private int floorLimitCheck;
    /**
     * 是否进行随机交易选择
     */
    private int randTransSel;
    /**
     * 是否进行频度检测
     */
    private int velocityCheck;
    /**
     * 最低限额
     */
    private long floorLimit;
    /**
     * 阀值
     */
    private long threshold;
    /**
     * 终端行为代码(拒绝)
     */
    private String tacDenial;
    /**
     * 终端行为代码(联机)
     */
    private String tacOnline;
    /**
     * 终端行为代码(缺省)
     */
    private String tacDefault;
    /**
     * 收单行标志־
     */
    private String acquirerId;
    /**
     * 终端缺省DDOL
     */
    private String dDOL;
    /**
     * 终端缺省TDOL
     */
    private String tDOL;
    /**
     * 应用版本
     */
    private String version;
    /**
     * 风险管理数据
     */
    private String riskManageData;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public int getSelFlag() {
        return selFlag;
    }

    public void setSelFlag(int selFlag) {
        this.selFlag = selFlag;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getTargetPer() {
        return targetPer;
    }

    public void setTargetPer(int targetPer) {
        this.targetPer = targetPer;
    }

    public int getMaxTargetPer() {
        return maxTargetPer;
    }

    public void setMaxTargetPer(int maxTargetPer) {
        this.maxTargetPer = maxTargetPer;
    }

    public int getFloorLimitCheck() {
        return floorLimitCheck;
    }

    public void setFloorLimitCheck(int floorLimitCheck) {
        this.floorLimitCheck = floorLimitCheck;
    }

    public int getRandTransSel() {
        return randTransSel;
    }

    public void setRandTransSel(int randTransSel) {
        this.randTransSel = randTransSel;
    }

    public int getVelocityCheck() {
        return velocityCheck;
    }

    public void setVelocityCheck(int velocityCheck) {
        this.velocityCheck = velocityCheck;
    }

    public long getFloorLimit() {
        return floorLimit;
    }

    public void setFloorLimit(long floorLimit) {
        this.floorLimit = floorLimit;
    }

    public long getThreshold() {
        return threshold;
    }

    public void setThreshold(long threshold) {
        this.threshold = threshold;
    }

    public String getTacDenial() {
        return tacDenial;
    }

    public void setTacDenial(String tacDenial) {
        this.tacDenial = tacDenial;
    }

    public String getTacOnline() {
        return tacOnline;
    }

    public void setTacOnline(String tacOnline) {
        this.tacOnline = tacOnline;
    }

    public String getTacDefault() {
        return tacDefault;
    }

    public void setTacDefault(String tacDefault) {
        this.tacDefault = tacDefault;
    }

    public String getAcquirerId() {
        return acquirerId;
    }

    public void setAcquirerId(String acquirerId) {
        this.acquirerId = acquirerId;
    }

    public String getDDOL() {
        return dDOL;
    }

    public void setDDOL(String dDOL) {
        this.dDOL = dDOL;
    }

    public String getTDOL() {
        return tDOL;
    }

    public void setTDOL(String tDOL) {
        this.tDOL = tDOL;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRiskManageData() {
        return riskManageData;
    }

    public void setRiskManageData(String riskManageData) {
        this.riskManageData = riskManageData;
    }

    /***************************
     * EmvAidParam to AidParam
     ***********************************/
    public static EMV_APPLIST toAidParams(EmvAid emvAidParam) {
        EMV_APPLIST aidParam = new EMV_APPLIST();
        aidParam.appName = emvAidParam.getAppName().getBytes();
        aidParam.aid = Utils.str2Bcd(emvAidParam.getAid());
        aidParam.aidLen = (byte) aidParam.aid.length;
        aidParam.selFlag = ((byte) emvAidParam.getSelFlag());
        aidParam.priority = ((byte) emvAidParam.getPriority());
        aidParam.floorLimit = (emvAidParam.getFloorLimit());
        aidParam.floorLimitCheck = ((byte) emvAidParam.getFloorLimitCheck());
        aidParam.threshold = (emvAidParam.getThreshold());
        aidParam.targetPer = ((byte) emvAidParam.getTargetPer());
        aidParam.maxTargetPer = ((byte) emvAidParam.getMaxTargetPer());
        aidParam.randTransSel = ((byte) emvAidParam.getRandTransSel());
        aidParam.velocityCheck = ((byte) emvAidParam.getVelocityCheck());
        aidParam.tacDenial = Utils.str2Bcd(emvAidParam.getTacDenial());
        aidParam.tacOnline = Utils.str2Bcd(emvAidParam.getTacOnline());
        aidParam.tacDefault = Utils.str2Bcd(emvAidParam.getTacDefault());
        if (emvAidParam.getAcquirerId() != null) {
            aidParam.acquierId = Utils.str2Bcd(emvAidParam.getAcquirerId());
        }
        if (emvAidParam.getDDOL() != null) {
            aidParam.dDOL = Utils.str2Bcd(emvAidParam.getDDOL());
        }
        if (emvAidParam.getTDOL() != null) {
            aidParam.tDOL = Utils.str2Bcd(emvAidParam.getTDOL());
        }
        aidParam.version = Utils.str2Bcd(emvAidParam.getVersion());
        if (emvAidParam.getRiskManageData() != null) {
            aidParam.riskManData = Utils.str2Bcd(emvAidParam.getRiskManageData());
        }
        return aidParam;
    }

}
