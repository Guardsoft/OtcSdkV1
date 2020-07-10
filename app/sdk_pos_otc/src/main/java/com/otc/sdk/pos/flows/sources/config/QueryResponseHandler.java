package com.otc.sdk.pos.flows.sources.config;

import com.otc.sdk.pos.flows.sources.server.models.response.retrieve.RetrieveResponse;

public interface QueryResponseHandler {
    void onSuccess(RetrieveResponse response);
    void onError(CustomError error);
}
