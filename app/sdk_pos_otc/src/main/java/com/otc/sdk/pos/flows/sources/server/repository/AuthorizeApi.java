package com.otc.sdk.pos.flows.sources.server.repository;

import com.otc.sdk.pos.flows.sources.config.ObjectResponseHandler;
import com.otc.sdk.pos.flows.sources.server.models.request.AuthorizeRequest;

public interface AuthorizeApi extends BaseApi{

    <T> void authorize(AuthorizeRequest request, final ObjectResponseHandler handler);
}
