package com.otc.sdk.pos.flows.sources.server.repository;

import android.content.Context;

import com.otc.sdk.pos.flows.sources.config.StringResponseHandler;
import com.otc.sdk.pos.flows.sources.server.models.request.AuthorizeRequest;

public interface ProcessAuthorizeApi extends  BaseApi {

    void authorization(Context context, AuthorizeRequest request, StringResponseHandler handler);
}
