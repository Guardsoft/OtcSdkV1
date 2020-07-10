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
import com.otc.sdk.pos.flows.sources.server.models.request.retrieve.RetrieveRequest;
import com.otc.sdk.pos.flows.sources.server.models.response.retrieve.RetrieveResponse;
import com.otc.sdk.pos.flows.sources.server.repository.RetrieveListApi;
import com.otc.sdk.pos.flows.util.SdkLog;

/**
 * Created by foxit on 11/24/17.
 */

public class RetrieveListRestImpl implements RetrieveListApi {

    private static final String TAG = "RetrieveListRestImpl";

    @Override
    public void cancel() {
        AndroidNetworking.cancel(this);
    }

    @Override
    public <T> void retrieveList(Context context, RetrieveRequest request, ObjectResponseHandler handler) {
        String dominio = "https://culqimpos.quiputech.com/";
        String tenant = "culqi";

        if (!ConfSdk.endpoint.equals("")) {
            dominio = ConfSdk.endpoint;
        }

        if (!ConfSdk.tenant.equals("")) {
            tenant = ConfSdk.tenant;
        }

        AndroidNetworking.post(dominio + "api.authorization/v3/" + tenant + "/retrieve/list")
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", Storage.getToken(context))
                .addApplicationJsonBody(request)
                .setTag(this)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(RetrieveResponse.class, new ParsedRequestListener<RetrieveResponse>() {
                    @Override
                    public void onResponse(RetrieveResponse response) {
                        Log.i(TAG, "api retrieveList: OK");
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
}
