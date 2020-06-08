package com.otc.sdk.pos.flows.sources.server.repository;

import android.content.Context;

import com.otc.sdk.pos.flows.sources.config.ObjectResponseHandler;
import com.otc.sdk.pos.flows.sources.server.models.request.InitializeRequest;

public interface InitializeApi extends BaseApi{

    <T> void initialize(Context Context, InitializeRequest request, ObjectResponseHandler handler);
}
