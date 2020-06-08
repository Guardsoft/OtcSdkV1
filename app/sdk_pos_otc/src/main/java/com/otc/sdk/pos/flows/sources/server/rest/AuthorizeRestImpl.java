package com.otc.sdk.pos.flows.sources.server.rest;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.otc.sdk.pos.flows.App;
import com.otc.sdk.pos.flows.sources.config.CustomError;
import com.otc.sdk.pos.flows.sources.config.ObjectResponseHandler;
import com.otc.sdk.pos.flows.sources.server.models.request.AuthorizeRequest;
import com.otc.sdk.pos.flows.sources.server.models.response.authorize.AuthorizeResponse;
import com.otc.sdk.pos.flows.sources.server.repository.AuthorizeApi;
import com.otc.sdk.pos.flows.util.SdkLog;

/**
 * Created by foxit on 11/24/17.
 */

public class AuthorizeRestImpl implements AuthorizeApi {

    @Override
    public <T> void authorize(AuthorizeRequest request, final ObjectResponseHandler handler) {

        String dominio = "https://culqimpos.quiputech.com/";
        String tenant = "culqi";

        if (!App.endpoint.equals("")) {
            dominio = App.endpoint;
        }

        if (!App.tenant.equals("")) {
            tenant = App.tenant;
        }

        AndroidNetworking.post(dominio + "api.authorization/v3/" + tenant + "/authorize")
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", "storage")
                .addApplicationJsonBody(request)
                .setTag("Authorize")
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
