package com.otc.sdk.pos.flows.domain.usecase.pax.signature;

import android.util.Log;


import com.otc.sdk.pax.a920.IConvert;
import com.otc.sdk.pax.a920.OtcApplication;
import com.otc.sdk.pax.a920.crypto.device.Device;

import java.io.UnsupportedEncodingException;

public class MacRetailSignature extends Signature {

    private static final String TAG = "MacRetailSignature";

    public MacRetailSignature(RequestToSign request) {
        super(request);
    }

    @Override
    public String prepareStringToSign(String canonicalURL, String xAmzDate) {
        String stringToSign = "";
        stringToSign = "MAC-RETAIL" + "\n";
        stringToSign += (xAmzDate != null ? xAmzDate : SignatureUtil.getTimeStamp()) + "\n";
        stringToSign += (xAmzDate != null && xAmzDate.length() >= 8) ? xAmzDate.substring(0, 8) : SignatureUtil.getCurrentDate() + "/" + request.getService() + "/" + "aws4_request" + "\n";
        stringToSign += SignatureUtil.toHex(SignatureUtil.hash(canonicalURL));
        return stringToSign;
    }

    @Override
    public String buildAuthorizationString(String strSignature) {
        return String.format("MAC-RETAIL Credential=%s/%s/%s/aws4_request, SignedHeaders=%s, Signature=%s",
                accessKey,
                SignatureUtil.getCurrentDate(),
                request.getService(),
                stringSignedHeader,
                strSignature);
    }

    public String macRetail(String stringToSign) {

        int indexKeyTak = 10;

        String hexa = null;
        try {
            hexa = OtcApplication.getConvert().toHexString(stringToSign.getBytes("UTF-8"));
            Log.i(TAG, "HEXA: " + stringToSign);
            Log.i(TAG, "HEXA: " + hexa);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "macRetail: ", e);
        }

        byte[] signature = OtcApplication.getConvert().strToBcd(hexa, IConvert.EPaddingPosition.PADDING_LEFT);
        String result = OtcApplication.getConvert().bcdToStr(Device.getMacRetail(indexKeyTak, signature));

        return result;
    }

    @Override
    public String toString() {
        String xAmzDate = SignatureUtil.getTimeStamp();
        String canonicalURL = "";
        try {
            /* Task 1 - Create a Canonical Request */
            canonicalURL = prepareCanonicalRequest(xAmzDate);
        }catch(UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getLocalizedMessage(), ex);
        }

        /* Task 2 - Create a String to Sign */
        return prepareStringToSign(canonicalURL, xAmzDate);
    }

}
