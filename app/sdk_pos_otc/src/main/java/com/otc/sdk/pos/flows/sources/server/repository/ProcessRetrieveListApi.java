package com.otc.sdk.pos.flows.sources.server.repository;

import android.content.Context;

import com.otc.sdk.pos.flows.sources.config.ObjectResponseHandler;
import com.otc.sdk.pos.flows.sources.config.QueryResponseHandler;
import com.otc.sdk.pos.flows.sources.config.StringResponseHandler;
import com.otc.sdk.pos.flows.sources.server.models.request.authorize.AuthorizeRequest;
import com.otc.sdk.pos.flows.sources.server.models.request.retrieve.RetrieveRequest;

public interface ProcessRetrieveListApi extends BaseApi {

    void retrieveList(Context context, String track2, int pageNumber, int pageSize, QueryResponseHandler handler);

    void retrieveList(Context context, int pageNumber, int pageSize, QueryResponseHandler handler);
}
