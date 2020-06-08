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
 * 16:05:34 2017-3-8  	           HuangJs           	    Create
 * ============================================================================
 */
package com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.model;

/**
 *
 */

public class EntryOutParam {

    public int ucKernType;
    public byte[] sAID = new byte[17];
    public int iAIDLen;
    public byte[] sDataOut = new byte[256];
    public int iDataLen;

    /**
     * Create an EntryOutParam instance.
     */
    public EntryOutParam() {

    }

}
