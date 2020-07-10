package com.otc.sdk.pos.flows.sources.server.rest;

import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.otc.sdk.pos.flows.ConfSdk;
import com.otc.sdk.pos.flows.sources.config.CustomError;
import com.otc.sdk.pos.flows.sources.config.ObjectResponseHandler;
import com.otc.sdk.pos.flows.sources.config.StringResponseHandler;
import com.otc.sdk.pos.flows.sources.local.storage.Storage;
import com.otc.sdk.pos.flows.sources.server.models.request.cancel.VoidRequest;
import com.otc.sdk.pos.flows.sources.server.models.response.authorize.AuthorizeResponse;
import com.otc.sdk.pos.flows.sources.server.repository.VoidOrderApi;
import com.otc.sdk.pos.flows.util.SdkLog;

/**
 * Created by foxit on 11/24/17.
 */

public class VoidOrderRestImpl implements VoidOrderApi {

    private static final String TAG = "VoidCancelRestImpl";

    @Override
    public <T> void voidOrder(Context context, VoidRequest request, StringResponseHandler handler) {

        String dominio = "https://culqimpos.quiputech.com/";
        String tenant = "culqi";

        if (!ConfSdk.endpoint.equals("")) {
            dominio = ConfSdk.endpoint;
        }

        if (!ConfSdk.tenant.equals("")) {
            tenant = ConfSdk.tenant;
        }

        AndroidNetworking.post(dominio + "api.authorization/v3/" + tenant + "/void")
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", Storage.getToken(context))
                .addApplicationJsonBody(request)
                .setTag(this)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString( new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        handler.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
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
