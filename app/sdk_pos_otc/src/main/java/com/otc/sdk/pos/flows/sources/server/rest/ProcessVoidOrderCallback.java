package com.otc.sdk.pos.flows.sources.server.rest;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.otc.sdk.pos.flows.sources.config.CustomError;
import com.otc.sdk.pos.flows.sources.config.ObjectResponseHandler;
import com.otc.sdk.pos.flows.sources.config.StringResponseHandler;
import com.otc.sdk.pos.flows.sources.local.storage.Storage;
import com.otc.sdk.pos.flows.sources.server.models.request.cancel.Card;
import com.otc.sdk.pos.flows.sources.server.models.request.cancel.Cryptography;
import com.otc.sdk.pos.flows.sources.server.models.request.cancel.Device;
import com.otc.sdk.pos.flows.sources.server.models.request.cancel.Header;
import com.otc.sdk.pos.flows.sources.server.models.request.cancel.Merchant;
import com.otc.sdk.pos.flows.sources.server.models.request.cancel.Order;
import com.otc.sdk.pos.flows.sources.server.models.request.cancel.VoidRequest;
import com.otc.sdk.pos.flows.sources.server.repository.ProcessVoidOrderApi;
import com.otc.sdk.pos.flows.sources.server.repository.VoidOrderApi;
import com.otc.sdk.pos.flows.util.OtcUtil;

import java.util.UUID;

public class ProcessVoidOrderCallback extends CallbackRest implements ProcessVoidOrderApi {

    private static final String TAG = "ProcessAuthorizeCallbac";

    private VoidOrderApi voidCancelApi;

    public ProcessVoidOrderCallback() {
        voidCancelApi = new VoidOrderRestImpl();
    }

    @Override
    public void voidOrder(Context context, Order order, String track2, String type, StringResponseHandler handler) {
        if (OtcUtil.connectionNetwork(context)) {
            processVoid(context, order, track2, type, handler);
        }else{
            CustomError error = new CustomError(466, "sin conexión");
            handler.onError(error);
        }
    }
    private void processVoid(final Context context, Order order, String track2, String type, final StringResponseHandler handler) {

        Header header = new Header();
        header.setExternalId(UUID.randomUUID().toString());

        Merchant merchant = new Merchant();
        merchant.setMerchantId(Storage.getMerchantId(context));

        Device device = new Device();
        device.setCaptureType(type);
        device.setTerminalId(Storage.getTerminalId(context));
        device.setUnattended(false);

        Card card = new Card();
        card.setTrack2(track2);

        Cryptography crypt = null;

        VoidRequest request = new VoidRequest();
        request.setHeader(header);
        request.setMerchant(merchant);
        request.setDevice(device);
        request.setOrder(order);
        request.setCard(card);
        request.setCryptography(crypt);

        voidCancelApi.voidOrder(context, request, new StringResponseHandler() {
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

        });

    }


    @Override
    public void cancel() {
        AndroidNetworking.cancel(this);
    }


}
