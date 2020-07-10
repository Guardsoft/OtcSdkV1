package com.otc.sdk.pos.flows.sources.server.rest;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.otc.sdk.pos.flows.ConfSdk;
import com.otc.sdk.pos.flows.sources.config.CustomError;
import com.otc.sdk.pos.flows.sources.config.ObjectResponseHandler;
import com.otc.sdk.pos.flows.sources.local.storage.Storage;
import com.otc.sdk.pos.flows.sources.server.models.request.InitializeRequest;
import com.otc.sdk.pos.flows.sources.server.models.response.initialize.InitializeResponse;
import com.otc.sdk.pos.flows.sources.server.repository.InitializeApi;
import com.otc.sdk.pos.flows.util.SdkLog;

/**
 * Created by foxit on 11/24/17.
 */

public class InitializeRestImpl implements InitializeApi {

    private static final String TAG = "InitializeRestImpl";

    @Override
    public <T> void initialize(Context context, InitializeRequest request, final ObjectResponseHandler handler) {

        String dominio = "https://culqimpos.quiputech.com/";
        String tenant = "culqi";

        if (!ConfSdk.endpoint.equals("")) {
            dominio = ConfSdk.endpoint;
        }

        if (!ConfSdk.tenant.equals("")) {
            tenant = ConfSdk.tenant;
        }

        Log.i(TAG, "api initialize: " + request.toString());

        AndroidNetworking.post(dominio + "api.terminal/v3/" + tenant + "/management/initialize")
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", Storage.getToken(context))
                .addApplicationJsonBody(request)
                .setTag("initialize")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(InitializeResponse.class, new ParsedRequestListener<InitializeResponse>() {
                    @Override
                    public void onResponse(InitializeResponse response) {
                        Log.i(TAG, "api initialize: OK");
                        handler.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.i(TAG, "api initialize: ERROR");
                        CustomError error;
                        if (anError.getErrorCode() != 0) {
                            SdkLog.log("onError errorCode : " + anError.getErrorCode());
                            SdkLog.log("onError errorBody : " + anError.getErrorBody());
                            SdkLog.log("onError errorDetail : " + anError.getErrorDetail());
                            error = new CustomError(anError.getErrorCode(), anError.getErrorBody());
                        } else {
                            SdkLog.log("onError  : " + anError);
                            error = new CustomError(404, anError.getErrorDetail());
                        }
                        handler.onError(error);
                    }
                });
    }

    @Override
    public void cancel() {
        AndroidNetworking.cancel(this);
    }

}
