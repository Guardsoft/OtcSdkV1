/*
 * ============================================================================
 * = COPYRIGHT
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2017-? PAX Technology, Inc. All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                           Author	                Action
 * 18:19:08 2017-3-13  	           HuangJs           	    Create
 * ============================================================================
 */
package com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.model;

/**
 *
 */

public class TransResult {
    /**
     * 交易成功
     */
    public static final int SUCC = 0;

    public static final int EMV_ONLINE_APPROVED = 1;       //联机批准；
    public static final int EMV_ONLINE_DENIED = 2;         //联机拒绝
    public static final int EMV_OFFLINE_APPROVED = 3;      //脱机批准；
    public static final int EMV_OFFLINE_DENIED = 4;        //脱机拒绝；
    public static final int EMV_ONLINE_CARD_DENIED = 5;    //主机批准，卡片拒绝；
    public static final int EMV_ABORT_TERMINATED = 6;      //异常
    public static final int EMV_ARQC = 7;             //申请联机
    public static final int EMV_SIMPLEFLOWEND = 8;         //用户终止

    public int result;

}
