package com.otc.sdk.pos.flows.sources.server.rest;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.otc.sdk.pax.a920.OtcApplication;
import com.otc.sdk.pos.flows.sources.config.AuthorizeResponseHandler;
import com.otc.sdk.pos.flows.sources.config.CustomError;
import com.otc.sdk.pos.flows.sources.config.ObjectResponseHandler;
import com.otc.sdk.pos.flows.sources.config.StringResponseHandler;
import com.otc.sdk.pos.flows.sources.local.storage.Storage;
import com.otc.sdk.pos.flows.sources.server.models.request.authorize.AuthorizeRequest;
import com.otc.sdk.pos.flows.sources.server.models.request.authorize.Card;
import com.otc.sdk.pos.flows.sources.server.models.request.authorize.Cryptography;
import com.otc.sdk.pos.flows.sources.server.models.request.authorize.Device;
import com.otc.sdk.pos.flows.sources.server.models.request.authorize.Header;
import com.otc.sdk.pos.flows.sources.server.models.request.authorize.Merchant;
import com.otc.sdk.pos.flows.sources.server.models.request.authorize.Order;
import com.otc.sdk.pos.flows.sources.server.models.response.authorize.AuthorizeResponse;
import com.otc.sdk.pos.flows.sources.server.repository.AuthorizeApi;
import com.otc.sdk.pos.flows.sources.server.repository.ProcessAuthorizeApi;
import com.otc.sdk.pos.flows.util.OtcUtil;

import java.util.UUID;

public class ProcessAuthorizeCallback extends CallbackRest implements ProcessAuthorizeApi {

    private static final String TAG = "ProcessAuthorizeCallbac";

    private AuthorizeApi authorizeApi;

    public ProcessAuthorizeCallback() {
        authorizeApi = new AuthorizeRestImpl();
    }


    @Override
    public void authorizationV1(Context context, Intent intentData, Order request, AuthorizeResponseHandler handler) {
        initializeNetWorkLibrary(context);

        if (OtcUtil.connectionNetwork(context)) {
            processAuthorize(context, intentData, request, handler);
        }else{
            CustomError error = new CustomError(466, "sin conexión");
            handler.onError(error);
        }
    }

    @Override
    public void authorizationV2(Context context, String track2, String type, String emv, Order order, AuthorizeResponseHandler handler) {
        initializeNetWorkLibrary(context);

        if (OtcUtil.connectionNetwork(context)) {
            processAuthorize(context, track2, type, emv, order, handler);
        }else{
            CustomError error = new CustomError(466, "sin conexión");
            handler.onError(error);
        }
    }

    private void processAuthorize(final Context context, Intent intentData, Order order, final AuthorizeResponseHandler handler) {

        //aca leer tarjeta
        String track2 = intentData.getStringExtra("track2");
        String type = intentData.getStringExtra("type");
        String emv = intentData.getStringExtra("emv");

        Header header = new Header();
        header.setExternalId(UUID.randomUUID().toString());

        Cryptography crypt = null;

        Merchant merchant = new Merchant();
        merchant.setMerchantId(Storage.getMerchantId(context));

        Device deviceAut = new Device();
        deviceAut.setTerminalId(Storage.getTerminalId(context));
        deviceAut.setCaptureType(type);//chip contactless / band
        deviceAut.setUnattended(false);

        Card card = new Card();
        card.setSequenceNumber("001");
        card.setTrack2(track2);
        card.setPinBlock("");

        if (emv != null) {
            card.setEmv(emv);
        }

        AuthorizeRequest request = new AuthorizeRequest();
        request.setHeader(header);
        request.setCryptography(crypt);
        request.setMerchant(merchant);
        request.setDevice(deviceAut);
        request.setOrder(order);
        request.setCard(card);

        authorizeApi.authorize(request, new ObjectResponseHandler<AuthorizeResponse>() {
            @Override
            public void onSuccess(AuthorizeResponse response) {

                com.otc.sdk.pos.flows.sources.server.models.response.authorize.Header header
                        = new com.otc.sdk.pos.flows.sources.server.models.response.authorize.Header();

                header.setResponseCode(response.getHeader().getResponseCode());
                header.setResponseMessage(response.getHeader().getResponseMessage());

                response.setHeader(header);
                response.setDevice(null);
                response.setDevice(null);
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
                return AuthorizeResponse.class;
            }
        });

    }

    private void processAuthorize(final Context context, String track2, String type, String emv, Order order, final AuthorizeResponseHandler handler) {


        Header header = new Header();
        header.setExternalId(UUID.randomUUID().toString());

        Cryptography crypt = null;

        Merchant merchant = new Merchant();
        merchant.setMerchantId(Storage.getMerchantId(context));

        Device deviceAut = new Device();
        deviceAut.setTerminalId(Storage.getTerminalId(context));
        deviceAut.setCaptureType(type);//chip contactless / band
        deviceAut.setUnattended(false);

        Card card = new Card();
        card.setSequenceNumber("001");
        card.setTrack2(track2);
        card.setPinBlock("");

        if (emv != null) {
            card.setEmv(emv);
        }

        AuthorizeRequest request = new AuthorizeRequest();
        request.setHeader(header);
        request.setCryptography(crypt);
        request.setMerchant(merchant);
        request.setDevice(deviceAut);
        request.setOrder(order);
        request.setCard(card);

        Log.i(TAG, "processAuthorize: " + request.toString());

        authorizeApi.authorize(request, new ObjectResponseHandler<AuthorizeResponse>() {
            @Override
            public void onSuccess(AuthorizeResponse response) {

                com.otc.sdk.pos.flows.sources.server.models.response.authorize.Header header
                        = new com.otc.sdk.pos.flows.sources.server.models.response.authorize.Header();

                header.setResponseCode(response.getHeader().getResponseCode());
                header.setResponseMessage(response.getHeader().getResponseMessage());

                response.setHeader(header);
                response.setDevice(null);
                response.setDevice(null);
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
                return AuthorizeResponse.class;
            }
        });

    }




    @Override
    public void cancel() {
        AndroidNetworking.cancel(this);
    }


}
