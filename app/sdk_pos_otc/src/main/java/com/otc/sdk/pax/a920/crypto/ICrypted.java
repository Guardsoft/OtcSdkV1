package com.otc.sdk.pax.a920.crypto;

import com.otc.sdk.pax.a920.IConvert;

public interface ICrypted {

    byte[] getMacSignature(String str, int slotTak);

    byte[] getMacSignature(String str);

}
