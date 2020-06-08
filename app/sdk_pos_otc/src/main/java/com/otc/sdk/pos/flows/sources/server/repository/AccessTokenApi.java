package com.otc.sdk.pos.flows.sources.server.repository;


import com.otc.sdk.pos.flows.sources.config.StringResponseHandler;

public interface AccessTokenApi extends BaseApi{
    void accessToken(final StringResponseHandler handler);
}
