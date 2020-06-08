/*
 * ============================================================================
 * COPYRIGHT
 *              Pax CORPORATION PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with Pax Corporation and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2016 - ? Pax Corporation. All rights reserved.
 * Module Date: 2016-12-1
 * Module Author: Kim.L
 * Description:
 *
 * ============================================================================
 */
package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw;

import com.pax.jemv.clcommon.EMV_APPLIST;

import java.util.ArrayList;
import java.util.List;

public class EmvTestAID extends EmvAid {
    public static final int PART_MATCH = 0;
    public static final int FULL_MATCH = 1;


    public static final EmvTestAID EMV = new EmvTestAID(
            "",
            "A0000000999090",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "00100",
            "D84004F800",
            "D84000A800",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "008c",
            null
    );

    public static final EmvTestAID JCB_TEST = new EmvTestAID(
            "",
            "F1234567890123",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "00100",
            "D84004F800",
            "D84000A800",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "008c",
            null
    );


    public static final EmvTestAID JCB_TEST_1 = new EmvTestAID(
            "",
            "A0000000651010",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "00100",
            "FC60ACF800",
            "FC6024A800",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "0200",
            null
    );

    public static final EmvTestAID VISA_VSDC = new EmvTestAID(
            "VISA CREDIT",
            "A0000000031010",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "00100",
            "D84004F800",
            "D84000A800",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "008c",
            null
    );

    public static final EmvTestAID VISA_ELECTRON = new EmvTestAID(
            "VISA ELECTRON",
            "A0000000032010",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "00100",
            "D84004F800",
            "D84000A800",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "008c",
            null
    );

    public static final EmvTestAID VISA_ELECTRON2 = new EmvTestAID(
            "VISA ELECTRON2",
            "A0000000033010",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "00100",
            "D84004F800",
            "D84000A800",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "008c",
            null
    );


    public static final EmvTestAID MASTER_MCHIP = new EmvTestAID(
            "MCHIP",
            "A0000000041010",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "0400000000",
            "F850ACF800",
            "FC50ACA000",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "0002",
            null
    );

    public static final EmvTestAID MASTER_MAESTRO = new EmvTestAID(
            "MAESTRO",
            "A0000000043060",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "0400800000",
            "F8502CF800",
            "FC50ACA000",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "0002",
            null
    );

    public static final EmvTestAID MASTER_MAESTRO_US = new EmvTestAID(
            "MAESTRO",
            "A0000000042203",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "0400000000",
            "F850ACF800",
            "FC50ACA000",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "0002",
            null
    );

    public static final EmvTestAID MASTER_CIRRUS = new EmvTestAID(
            "CIRRUS",
            "A0000000046000",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "0400000000",
            "F850ACF800",
            "FC50ACA000",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "0002",
            null
    );

    public static final EmvTestAID MCC_4 = new EmvTestAID(
            "",
            "A0000000046010",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "0400000000",
            "F850ACF800",
            "FC50ACA000",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "0002",
            null
    );

    public static final EmvTestAID MCC_5 = new EmvTestAID(
            "",
            "A0000000101030",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "0400000000",
            "F850ACF800",
            "FC50ACA000",
            "000000123456",
            "039F3704",
            "0F9F02065F2A029A039C0195059F3704",
            "0002",
            null
    );

    public static final EmvTestAID AMEX_LIVE = new EmvTestAID(
            "",
            "A00000002501",
            PART_MATCH, 0, 0, 0, 1, 1, 1,
            1000, 0,
            "0000000000",
            "0000000000",
            "0000000000",
            "000000123456",
            "039F3704",
            "9F3704",
            "0001",
            null
    );

    public static final EmvTestAID DPAS_LIVE = new EmvTestAID(
            "",
            "A0000001523010",
            PART_MATCH, 0, 0, 0, 1, 0, 1,    // random=off
            1000, 0,
            "0000000000",
            "0000000000",
            "0000000000",
            "000000123456",
            "039F3704",
            "9F3704",
            "008C",
            null
    );

    public static final EmvTestAID DPAS_LIVE_1 = new EmvTestAID(
            "",
            "A0000003241010",
            PART_MATCH, 0, 0, 0, 1, 0, 1,    // random=off
            0, 0,
            "0000000000",
            "0000000000",
            "0000000000",
            "000000123456",
            "039F3704",
            "9F3704",
            "008c",
            null
    );

    public static final EmvTestAID QPBOC_1 = new EmvTestAID(
            "",
            "A000000333010101",
            PART_MATCH, 0, 0, 0, 1, 0, 1,    // random=off
            0, 0,
            "0010000000",
            "FC78FCF8F0",
            "FC78FCF8F0",
            "000000123456",
            "039F3704",
            "9F3704",
            "0020",
            null
    );

    public static final EmvTestAID QPBOC_2 = new EmvTestAID(
            "",
            "A000000333010102",
            PART_MATCH, 0, 0, 0, 1, 0, 1,    // random=off
            0, 0,
            "0010000000",
            "FC78FCF8F0",
            "FC78FCF8F0",
            "000000123456",
            "039F3704",
            "9F3704",
            "0020",
            null
    );

    public static final EmvTestAID QPBOC_3 = new EmvTestAID(
            "",
            "A000000333010103",
            PART_MATCH, 0, 0, 0, 1, 0, 1,    // random=off
            0, 0,
            "0010000000",
            "FC78FCF8F0",
            "FC78FCF8F0",
            "000000123456",
            "039F3704",
            "9F3704",
            "0020",
            null
    );

    public static final EmvTestAID QPBOC_6 = new EmvTestAID(
            "",
            "A000000333010106",
            PART_MATCH, 0, 0, 0, 1, 0, 1,    // random=off
            0, 0,
            "0010000000",
            "FCF8E4F880",
            "FCF0E40800",
            "000000123456",
            "039F3704",
            "9F3704",
            "0020",
            null
    );

    private EmvTestAID(String appName, String aid, int selFlag, int priority, int targetPer, int maxTargetPer,
                       int floorLimitCheck, int randTransSel, int velocityCheck, long floorLimit, long threshold,
                       String tacDenial, String tacOnline, String tacDefault, String acquierId, String dDOL, String tDOL, String version, String riskManData) {

        setAppName(appName);
        setAid(aid);
        setSelFlag(selFlag);
        setVersion(version);
        setTacDefault(tacDefault);
        setRandTransSel(randTransSel);
        setVelocityCheck(velocityCheck);
        setTacOnline(tacOnline);
        setTacDenial(tacDenial);
        setFloorLimit(floorLimit);
        setFloorLimitCheck(floorLimitCheck);
        setThreshold(threshold);
        setMaxTargetPer(maxTargetPer);
        setTargetPer(targetPer);
        setDDOL(dDOL);
        setTDOL(tDOL);

        setPriority(priority);
        setAcquirerId(acquierId);
        setRiskManageData(riskManData);
    }

    public static List<EMV_APPLIST> genApplists() {
        List<EMV_APPLIST> applists = new ArrayList<EMV_APPLIST>();

        applists.add(EmvAid.toAidParams(EmvTestAID.EMV));
        applists.add(EmvAid.toAidParams(EmvTestAID.JCB_TEST));
        applists.add(EmvAid.toAidParams(EmvTestAID.JCB_TEST_1));
        applists.add(EmvAid.toAidParams(EmvTestAID.VISA_VSDC));
        applists.add(EmvAid.toAidParams(EmvTestAID.VISA_ELECTRON));
        applists.add(EmvAid.toAidParams(EmvTestAID.VISA_ELECTRON2));
        applists.add(EmvAid.toAidParams(EmvTestAID.MASTER_MCHIP));
        applists.add(EmvAid.toAidParams(EmvTestAID.MASTER_MAESTRO));
        applists.add(EmvAid.toAidParams(EmvTestAID.MASTER_MAESTRO_US));
        applists.add(EmvAid.toAidParams(EmvTestAID.MASTER_CIRRUS));
        applists.add(EmvAid.toAidParams(EmvTestAID.MCC_4));
        applists.add(EmvAid.toAidParams(EmvTestAID.MCC_5));
        applists.add(EmvAid.toAidParams(EmvTestAID.AMEX_LIVE));
        applists.add(EmvAid.toAidParams(EmvTestAID.DPAS_LIVE));
        applists.add(EmvAid.toAidParams(EmvTestAID.DPAS_LIVE_1));
        applists.add(EmvAid.toAidParams(EmvTestAID.QPBOC_1));
        applists.add(EmvAid.toAidParams(EmvTestAID.QPBOC_2));
        applists.add(EmvAid.toAidParams(EmvTestAID.QPBOC_3));
        applists.add(EmvAid.toAidParams(EmvTestAID.QPBOC_6));
        return applists;
    }

    ;
}
