package com.otc.sdk.pos.flows.sources.config;

public interface StringResponseHandler {
    void onSuccess(String response);
    void onError(CustomError error);
}
