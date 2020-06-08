/*
 * ============================================================================
 * COPYRIGHT
 *              Pax CORPORATION PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with Pax Corporation and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2017 - ? Pax Corporation. All rights reserved.
 * Module Date: 2017-5-23
 * Module Author: Kim.L
 * Description:
 *
 * ============================================================================
 */
package com.otc.sdk.pax.a920;

public interface IConvert {
    String bcdToStr(byte[] b);

    byte[] strToBcd(String str, EPaddingPosition paddingPosition);

    void longToByteArray(long l, byte[] to, int offset, EEndian endian);

    byte[] longToByteArray(long l, EEndian endian);

    void intToByteArray(int paramInt1, byte[] paramArrayOfByte, int paramInt2, EEndian paramEEndian);

    byte[] intToByteArray(int paramInt, EEndian paramEEndian);

    void shortToByteArray(short paramShort, byte[] paramArrayOfByte, int paramInt, EEndian paramEEndian);

    byte[] shortToByteArray(short paramShort, EEndian paramEEndian);

    long longFromByteArray(byte[] paramArrayOfByte, int paramInt, EEndian paramEEndian);

    int intFromByteArray(byte[] paramArrayOfByte, int paramInt, EEndian paramEEndian);

    short shortFromByteArray(byte[] paramArrayOfByte, int paramInt, EEndian paramEEndian);

    String stringPadding(String paramString, char paramChar, long paramLong, EPaddingPosition paramEPaddingPosition);

    boolean isByteArrayValueSame(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3);

    String toHexString(byte[] data);

    enum EEndian {
        LITTLE_ENDIAN,
        BIG_ENDIAN,
    }

    enum EPaddingPosition {
        PADDING_LEFT,
        PADDING_RIGHT,
    }
}