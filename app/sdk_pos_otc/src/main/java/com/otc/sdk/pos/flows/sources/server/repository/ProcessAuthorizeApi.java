package com.otc.sdk.pos.flows.sources.server.repository;

import android.content.Context;
import android.content.Intent;

import com.otc.sdk.pos.flows.sources.config.AuthorizeResponseHandler;
import com.otc.sdk.pos.flows.sources.server.models.request.authorize.Order;

public interface ProcessAuthorizeApi extends  BaseApi {

    void authorizationV1(Context context, Intent intentData, Order request, AuthorizeResponseHandler handler);

    void authorizationV2(Context context, String track2, String type, String emv, Order request, AuthorizeResponseHandler handler);


    }
