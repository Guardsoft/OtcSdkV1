package com.otc.sdk.pos.flows.sources.server.rest;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.otc.sdk.pos.flows.sources.config.CustomError;
import com.otc.sdk.pos.flows.sources.config.ObjectResponseHandler;
import com.otc.sdk.pos.flows.sources.server.models.request.send.SendSmsRequest;
import com.otc.sdk.pos.flows.sources.server.repository.ManagementSendApi;
import com.otc.sdk.pos.flows.sources.server.repository.ProcessManagementSendApi;
import com.otc.sdk.pos.flows.util.OtcUtil;

public class ProcessManagementSendCallback extends CallbackRest implements ProcessManagementSendApi {

    private static final String TAG = "ProcessManagementSendCa";

    private ManagementSendApi managementSendApi;

    public ProcessManagementSendCallback() {
        managementSendApi = new ManagementSendRestImpl();
    }

    @Override
    public void managementSend(Context context, SendSmsRequest request, ObjectResponseHandler handler) {
        if (OtcUtil.connectionNetwork(context)) {
            processSendSMS(context, request, handler);
        }else{
            CustomError error = new CustomError(466, "sin conexión");
            handler.onError(error);
        }
    }

    private void processSendSMS(final Context context, SendSmsRequest request, final ObjectResponseHandler handler) {

        managementSendApi.managementSend(context, request, new ObjectResponseHandler<String>() {
            @Override
            public void onSuccess(String response) {

                Log.i(TAG, "onSuccess: " + response);
                handler.onSuccess(response);
            }

            @Override
            public void onError(CustomError error) {

                Log.i(TAG, "onError: " + error.getMessage());
                handler.onError(error);
            }

            @Override
            public Class getResponseClass() {
                return String.class;
            }
        });

    }


    @Override
    public void cancel() {
        AndroidNetworking.cancel(this);
    }



}
