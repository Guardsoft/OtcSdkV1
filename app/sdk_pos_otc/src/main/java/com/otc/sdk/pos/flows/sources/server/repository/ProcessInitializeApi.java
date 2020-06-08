package com.otc.sdk.pos.flows.sources.server.repository;

import android.content.Context;

import com.otc.sdk.pos.flows.sources.config.InitializeResponseHandler;

public interface ProcessInitializeApi extends  BaseApi {

    void initialization(Context context, InitializeResponseHandler handler);
}
