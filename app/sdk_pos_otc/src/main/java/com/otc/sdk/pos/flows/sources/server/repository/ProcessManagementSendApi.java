package com.otc.sdk.pos.flows.sources.server.repository;

import android.content.Context;

import com.otc.sdk.pos.flows.sources.config.ObjectResponseHandler;
import com.otc.sdk.pos.flows.sources.server.models.request.retrieve.RetrieveRequest;
import com.otc.sdk.pos.flows.sources.server.models.request.send.SendSmsRequest;

public interface ProcessManagementSendApi extends BaseApi {

    void managementSend(Context context, SendSmsRequest request, ObjectResponseHandler handler);
}
