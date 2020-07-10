package com.otc.sdk.pos.flows.sources.server.repository;

import android.content.Context;

import com.otc.sdk.pos.flows.sources.config.ObjectResponseHandler;
import com.otc.sdk.pos.flows.sources.server.models.request.authorize.AuthorizeRequest;
import com.otc.sdk.pos.flows.sources.server.models.request.send.SendSmsRequest;

public interface ManagementSendApi extends BaseApi{

    <T> void managementSend(Context context, SendSmsRequest request, final ObjectResponseHandler handler);
}
