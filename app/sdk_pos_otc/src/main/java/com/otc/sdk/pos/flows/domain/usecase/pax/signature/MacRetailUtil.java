package com.otc.sdk.pos.flows.domain.usecase.pax.signature;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class MacRetailUtil {

    private static final String TAG = "MacRetailUtil";

    public static String signature(String macKey,
                                   String xAmzDate, RequestToSign request) throws Exception {
        MacRetailSignature macRetail = new MacRetailSignature(request);
        return signature(macRetail, macKey, xAmzDate);
    }

    private static String signature(MacRetailSignature macRetail, String macKey, String xAmzDate) throws Exception {

        /* Task 1 - Create a Canonical Request */
        String canonicalURL = macRetail.prepareCanonicalRequest(xAmzDate);

        /* Task 2 - Create a String to Sign */
        String stringToSign = macRetail.prepareStringToSign(canonicalURL, xAmzDate);

        /* Task 3 - Calculate the Signature */
        //TODO: Reemplazar por la firma del dispositivo
        String signature = macRetail.macRetail(stringToSign);

        Log.i(TAG, "signature: " + signature);

        return signature;
    }

    public static Map<String, String> sign(String macKey, RequestToSign request) throws Exception {
        String xAmzDate = SignatureUtil.getTimeStamp();
        return sign(macKey, xAmzDate, request);
    }

    public static Map<String, String> sign(String macKey, String xAmzDate, RequestToSign request) throws Exception {
        Map<String, String> map = new HashMap<>();
        MacRetailSignature macRetail = new MacRetailSignature(request);

        String signature = signature(macRetail,"", xAmzDate);
        /* Task 4 - Add the Signing Information to the Request */
        map.put("Authorization", macRetail.buildAuthorizationString(signature));
        map.put("content-type", "application/json");
        map.put("x-amz-date", xAmzDate);
        map.put("host", request.getHost());

        return map;
    }

    public static boolean verify(
            String macKey,
            String credential,
            String signature,
            String xAmzDate,
            String host,
            String method,
            String path,
            String payload) {
        try {
            RequestToSign request = RequestToSign.builder()
                    .withAccessKey(credential)
                    .withHost(host)
                    .withMethod(method)
                    .withPath(path)
                    .withRegion("global")
                    .withService("authentication")
                    .withPayload(payload)
                    .withQueryParams(null)
                    .build();

            String signResult = signature(macKey, xAmzDate, request);
            return signature.equalsIgnoreCase(signResult);
        } catch (Exception ex) {
            Log.e(TAG, ex.getLocalizedMessage(), ex);
        }
        return false;
    }
    
}
