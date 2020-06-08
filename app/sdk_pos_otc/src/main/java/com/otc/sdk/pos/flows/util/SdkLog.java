package com.otc.sdk.pos.flows.util;

import android.util.Log;
import com.otc.sdk.pos.BuildConfig;

public class SdkLog {

    private static final String TAG = "OTC-LOG";

    public static void log(Object object) {
        if (BuildConfig.DEBUG_LOG) {
            Log.d (TAG, (""+object));
        }
    }

    public static void logError(Object object) {
        if (BuildConfig.DEBUG_LOG) {
            Log.e(TAG, ("" + object));
        }
    }
}
