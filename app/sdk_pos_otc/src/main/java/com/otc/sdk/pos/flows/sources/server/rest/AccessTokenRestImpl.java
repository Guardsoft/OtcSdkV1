package com.otc.sdk.pos.flows.sources.server.rest;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.otc.sdk.pos.flows.App;
import com.otc.sdk.pos.flows.sources.config.CustomError;
import com.otc.sdk.pos.flows.sources.config.StringResponseHandler;
import com.otc.sdk.pos.flows.sources.server.repository.AccessTokenApi;
import com.otc.sdk.pos.flows.util.SdkLog;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Route;

/**
 * Created by foxit on 11/24/17.
 */

public class AccessTokenRestImpl implements AccessTokenApi {

    private static final String TAG = "AccessTokenRestImpl";


    @Override
    public void accessToken(final StringResponseHandler handler) {

        String dominio = "https://culqimpos.quiputech.com/";
        String tenant = "culqi";

        if (!App.endpoint.equals("")) {
            dominio = App.endpoint;
        }

        if (!App.tenant.equals("")) {
            tenant = App.tenant;
        }

        OkHttpClient client = new OkHttpClient.Builder().authenticator(new Authenticator() {
            @Override
            public Request authenticate(Route route, okhttp3.Response response) {
                String credential = Credentials.basic(App.username, App.password);
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        }).build();
        
        dominio = dominio + "api.security/v2/"+ tenant + "/security/accessToken";

        Log.i(TAG, "accessToken: " + dominio);
        
        AndroidNetworking.get(dominio)
                .setOkHttpClient(client)
                .setTag(this)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {

                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "api accessToken: OK");

                        handler.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.i(TAG, "api accessToken: ERROR");

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
