package com.otc.sdk.pos.flows.sources.config;

import com.otc.sdk.pos.flows.sources.server.models.response.initialize.InitializeResponse;

public interface InitializeResponseHandler {
    void onSuccess(InitializeResponse response);
    void onError(CustomError error);
}
