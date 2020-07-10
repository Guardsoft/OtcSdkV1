package com.otc.sdk.pos.flows.sources.local.storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by foxit on 11/24/17.
 */

public class Storage {

    private static final String STORAGE_NAME = "otc_storage";
    private static final String PARAM_TOKEN = "access_token";
    private static final String PARAM_MERCHANT_ID = "merchantId";
    private static final String PARAM_TERMINAL_ID = "terminalId";

    public static void saveMerchantId(Context context, String token){
        SharedPreferences.Editor editor = context. getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(PARAM_MERCHANT_ID, token);
        editor.apply();
    }

    public static String getMerchantId(Context context){
        SharedPreferences preferences = context. getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(PARAM_MERCHANT_ID, null);
    }

    public static void saveTerminalId(Context context, String token){
        SharedPreferences.Editor editor = context. getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(PARAM_TERMINAL_ID, token);
        editor.apply();
    }

    public static String getTerminalId(Context context){
        SharedPreferences preferences = context. getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(PARAM_TERMINAL_ID, null);
    }

    public static void saveToken(Context context, String token){
        SharedPreferences.Editor editor = context. getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(PARAM_TOKEN, token);
        editor.apply();
    }

    public static String getToken(Context context){
        SharedPreferences preferences = context. getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(PARAM_TOKEN, null);
    }
}
