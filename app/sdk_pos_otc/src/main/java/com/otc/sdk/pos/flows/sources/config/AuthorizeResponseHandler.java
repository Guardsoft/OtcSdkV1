package com.otc.sdk.pos.flows.sources.config;

import com.otc.sdk.pos.flows.sources.server.models.response.authorize.AuthorizeResponse;

public interface AuthorizeResponseHandler {
    void onSuccess(AuthorizeResponse response);
    void onError(CustomError error);
}
