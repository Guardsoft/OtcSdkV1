package com.otc.sdk.pos.flows.sources.server.repository;

import android.content.Context;

import com.otc.sdk.pos.flows.sources.config.ObjectResponseHandler;
import com.otc.sdk.pos.flows.sources.config.StringResponseHandler;
import com.otc.sdk.pos.flows.sources.server.models.request.InitializeRequest;
import com.otc.sdk.pos.flows.sources.server.models.request.authorize.AuthorizeRequest;
import com.otc.sdk.pos.flows.sources.server.models.request.cancel.VoidRequest;

public interface VoidOrderApi extends BaseApi{

    <T> void voidOrder(Context context, VoidRequest request, StringResponseHandler handler);
}
