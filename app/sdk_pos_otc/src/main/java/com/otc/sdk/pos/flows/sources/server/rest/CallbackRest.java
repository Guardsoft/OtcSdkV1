package com.otc.sdk.pos.flows.sources.server.rest;

import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.otc.sdk.pos.flows.sources.config.HttpConfiguration;

public class CallbackRest {

    public void initializeNetWorkLibrary(Context context){
        AndroidNetworking
                .initialize(context, HttpConfiguration.getInstance(context)
                        .buildCustomHttpClient());
    }
}
