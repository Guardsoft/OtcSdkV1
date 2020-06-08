package com.otc.sdk.pos.flows.sources.server.rest;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.otc.sdk.pos.flows.sources.config.CustomError;
import com.otc.sdk.pos.flows.sources.config.ObjectResponseHandler;
import com.otc.sdk.pos.flows.sources.config.StringResponseHandler;
import com.otc.sdk.pos.flows.sources.server.models.request.AuthorizeRequest;
import com.otc.sdk.pos.flows.sources.server.repository.AuthorizeApi;
import com.otc.sdk.pos.flows.sources.server.repository.InitializeApi;
import com.otc.sdk.pos.flows.sources.server.repository.ProcessAuthorizeApi;
import com.otc.sdk.pos.flows.util.OtcUtil;

public class ProcessAuthorizeCallback extends CallbackRest implements ProcessAuthorizeApi {

    private static final String TAG = "ProcessAuthorizeCallbac";

    private AuthorizeApi authorizeApi;
    private InitializeApi initializeApi;

    public ProcessAuthorizeCallback() {
        authorizeApi = new AuthorizeRestImpl();
        initializeApi = new InitializeRestImpl();
    }


    @Override
    public void authorization(Context context, AuthorizeRequest request, StringResponseHandler handler) {

        if (OtcUtil.connectionNetwork(context)) {
            processAuthorize(context, request, handler);
        }else{
            CustomError error = new CustomError(466, "sin conexión");
            handler.onError(error);
        }

    }

    private void processAuthorize(final Context context, AuthorizeRequest request, final StringResponseHandler handler) {

        authorizeApi.authorize(request, new ObjectResponseHandler<String>() {
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
