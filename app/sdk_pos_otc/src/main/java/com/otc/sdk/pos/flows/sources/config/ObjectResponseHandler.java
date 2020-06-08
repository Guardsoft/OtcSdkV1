package com.otc.sdk.pos.flows.sources.config;

public interface ObjectResponseHandler<T> {
    void onSuccess(T response);
    void onError(CustomError error);
    Class getResponseClass();
}
