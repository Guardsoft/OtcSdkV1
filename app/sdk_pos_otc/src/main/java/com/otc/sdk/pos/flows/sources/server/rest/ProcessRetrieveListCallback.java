package com.otc.sdk.pos.flows.sources.server.rest;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.otc.sdk.pos.flows.sources.config.CustomError;
import com.otc.sdk.pos.flows.sources.config.ObjectResponseHandler;
import com.otc.sdk.pos.flows.sources.config.QueryResponseHandler;
import com.otc.sdk.pos.flows.sources.local.storage.Storage;
import com.otc.sdk.pos.flows.sources.server.models.request.retrieve.Card;
import com.otc.sdk.pos.flows.sources.server.models.request.retrieve.Cryptography;
import com.otc.sdk.pos.flows.sources.server.models.request.retrieve.Device;
import com.otc.sdk.pos.flows.sources.server.models.request.retrieve.Header;
import com.otc.sdk.pos.flows.sources.server.models.request.retrieve.Merchant;
import com.otc.sdk.pos.flows.sources.server.models.request.retrieve.Paging;
import com.otc.sdk.pos.flows.sources.server.models.request.retrieve.RetrieveRequest;
import com.otc.sdk.pos.flows.sources.server.models.response.retrieve.RetrieveResponse;
import com.otc.sdk.pos.flows.sources.server.repository.ProcessRetrieveListApi;
import com.otc.sdk.pos.flows.sources.server.repository.RetrieveListApi;
import com.otc.sdk.pos.flows.util.OtcUtil;

import java.util.UUID;

public class ProcessRetrieveListCallback extends CallbackRest implements ProcessRetrieveListApi {

    private static final String TAG = "ProcessRetrieveListCall";

    private RetrieveListApi retrieveListApi;

    public ProcessRetrieveListCallback() {
        retrieveListApi = new RetrieveListRestImpl();
    }

    @Override
    public void retrieveList(Context context, String track2, int pageNumber, int pageSize, QueryResponseHandler handler) {
        if (OtcUtil.connectionNetwork(context)) {
            processRetrieveList(context, track2, pageNumber, pageSize, handler);
        } else {
            CustomError error = new CustomError(466, "sin conexión");
            handler.onError(error);
        }
    }

    @Override
    public void retrieveList(Context context, int pageNumber, int pageSize, QueryResponseHandler handler) {
        if (OtcUtil.connectionNetwork(context)) {
            processRetrieveList(context, pageNumber, pageSize, handler);
        } else {
            CustomError error = new CustomError(466, "sin conexión");
            handler.onError(error);
        }
    }

    private void processRetrieveList(final Context context, String track2, int pageNumber, int pageSize, final QueryResponseHandler handler) {

        Header header = new Header();
        header.setExternalId(UUID.randomUUID().toString());

        Merchant merchant = new Merchant();
        merchant.setMerchantId(Storage.getMerchantId(context));

        Device device = new Device();
        device.setTerminalId(Storage.getTerminalId(context));

        Paging paging = new Paging();
        paging.setPageNumber(pageNumber);
        paging.setPageSize(pageSize);

        RetrieveRequest request = new RetrieveRequest();
        request.setHeader(header);
        request.setMerchant(merchant);
        request.setDevice(device);
        request.setPaging(paging);

        Cryptography crypt = null;
        request.setCryptography(crypt);

        // card --------------------------------------------------------------------------------
        Card card = new Card();
        card.setSequenceNumber("001");
        card.setTrack2(track2);

        request.setCard(card);


        retrieveListApi.retrieveList(context, request, new ObjectResponseHandler<RetrieveResponse>() {
            @Override
            public void onSuccess(RetrieveResponse response) {

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
                return RetrieveResponse.class;
            }
        });

    }

    private void processRetrieveList(final Context context, int pageNumber, int pageSize, final QueryResponseHandler handler) {

        Header header = new Header();
        header.setExternalId(UUID.randomUUID().toString());

        Merchant merchant = new Merchant();
        merchant.setMerchantId(Storage.getMerchantId(context));

        Device device = new Device();
        device.setTerminalId(Storage.getTerminalId(context));

        Paging paging = new Paging();
        paging.setPageNumber(pageNumber);
        paging.setPageSize(pageSize);

        RetrieveRequest request = new RetrieveRequest();
        request.setHeader(header);
        request.setMerchant(merchant);
        request.setDevice(device);
        request.setPaging(paging);

        Cryptography crypt = null;
        request.setCryptography(crypt);

        retrieveListApi.retrieveList(context, request, new ObjectResponseHandler<RetrieveResponse>() {
            @Override
            public void onSuccess(RetrieveResponse response) {

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
                return RetrieveResponse.class;
            }
        });

    }


    @Override
    public void cancel() {
        AndroidNetworking.cancel(this);
    }


}
