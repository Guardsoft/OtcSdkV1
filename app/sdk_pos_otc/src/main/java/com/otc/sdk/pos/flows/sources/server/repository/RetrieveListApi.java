package com.otc.sdk.pos.flows.sources.server.repository;

import android.content.Context;

import com.otc.sdk.pos.flows.sources.config.ObjectResponseHandler;
import com.otc.sdk.pos.flows.sources.server.models.request.authorize.AuthorizeRequest;
import com.otc.sdk.pos.flows.sources.server.models.request.retrieve.RetrieveRequest;

public interface RetrieveListApi extends BaseApi{

    <T> void retrieveList(Context context, RetrieveRequest request, ObjectResponseHandler handler);
}
