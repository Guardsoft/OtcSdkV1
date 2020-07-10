package com.otc.sdk.pos.flows.sources.server.rest;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.otc.sdk.pos.flows.ConfSdk;
import com.otc.sdk.pos.flows.sources.config.CustomError;
import com.otc.sdk.pos.flows.sources.config.ObjectResponseHandler;
import com.otc.sdk.pos.flows.sources.server.models.request.authorize.AuthorizeRequest;
import com.otc.sdk.pos.flows.sources.server.models.response.authorize.AuthorizeResponse;
import com.otc.sdk.pos.flows.sources.server.repository.AuthorizeApi;
import com.otc.sdk.pos.flows.util.OtcUtil;
import com.otc.sdk.pos.flows.util.SdkLog;

import java.util.Map;

/**
 * Created by foxit on 11/24/17.
 */

public class AuthorizeRestImpl implements AuthorizeApi {

    private static final String TAG = "AuthorizeRestImpl";

    @Override
    public <T> void authorize(AuthorizeRequest request, final ObjectResponseHandler handler) {

        String dominio = "https://culqimpos.quiputech.com/";
        String tenant = "culqi";

        if (!ConfSdk.endpoint.equals("")) {
            dominio = ConfSdk.endpoint;
        }

        if (!ConfSdk.tenant.equals("")) {
            tenant = ConfSdk.tenant;
        }

        // ------------------------------------  HEADERS -------------------------------------------
        Map<String, String> headerMap = null;
        try {
            headerMap = OtcUtil.getSignatureRequest(request);
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
        // ------------------------------------  HEADERS -------------------------------------------

        AndroidNetworking.post(dominio + "api.authorization/v3/" + tenant + "/authorize")
                .setContentType("application/json;charset=utf-8")
                .addHeaders(headerMap)
                .addStringBody(OtcUtil.toJsonPretty(request))
                .setTag(this)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(AuthorizeResponse.class, new ParsedRequestListener<AuthorizeResponse>() {
                    @Override
                    public void onResponse(AuthorizeResponse response) {
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
