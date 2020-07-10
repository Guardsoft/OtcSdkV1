package com.otc.sdk.pax.a920.crypto;

import com.otc.sdk.pax.a920.IConvert;
import com.otc.sdk.pax.a920.OtcApplication;
import com.otc.sdk.pos.flows.ConfSdk;

public class CryptedImpl implements ICrypted {

    @Override
    public byte[] getMacSignature(String str, int slotTak) {
        byte[] signature = OtcApplication.getConvert().strToBcd(str, IConvert.EPaddingPosition.PADDING_LEFT);
        return DeviceCrypto.getMacRetail(slotTak, signature);
    }

    @Override
    public byte[] getMacSignature(String str) {
        byte[] signature = OtcApplication.getConvert().strToBcd(str, IConvert.EPaddingPosition.PADDING_LEFT);
        return DeviceCrypto.getMacRetail(ConfSdk.keyMac, signature);
    }

}
