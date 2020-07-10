package com.otc.sdk.pos.flows.sources.server.repository;

import android.content.Context;

import com.otc.sdk.pos.flows.sources.config.StringResponseHandler;
import com.otc.sdk.pos.flows.sources.server.models.request.cancel.Order;

public interface ProcessVoidOrderApi extends  BaseApi {

    void voidOrder(Context context, Order order, String track2, String type, StringResponseHandler handler);
}
