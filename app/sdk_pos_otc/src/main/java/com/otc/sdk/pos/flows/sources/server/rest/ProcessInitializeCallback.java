package com.otc.sdk.pos.flows.sources.server.rest;

import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.otc.sdk.pax.a920.OtcApplication;
import com.otc.sdk.pax.a920.crypto.device.Device;
import com.otc.sdk.pos.flows.ConfSdk;
import com.otc.sdk.pos.flows.sources.config.CustomError;
import com.otc.sdk.pos.flows.sources.config.InitializeResponseHandler;
import com.otc.sdk.pos.flows.sources.config.ObjectResponseHandler;
import com.otc.sdk.pos.flows.sources.config.StringResponseHandler;
import com.otc.sdk.pos.flows.sources.local.storage.Storage;
import com.otc.sdk.pos.flows.sources.server.models.request.DeviceIni;
import com.otc.sdk.pos.flows.sources.server.repository.AccessTokenApi;
import com.otc.sdk.pos.flows.sources.server.repository.InitializeApi;
import com.otc.sdk.pos.flows.sources.server.repository.ProcessInitializeApi;
import com.otc.sdk.pos.flows.util.OtcUtil;
import com.otc.sdk.pos.flows.sources.server.models.request.Header;
import com.otc.sdk.pos.flows.sources.server.models.request.InitializeRequest;
import com.otc.sdk.pos.flows.sources.server.models.response.initialize.InitializeResponse;

import java.util.UUID;

import static com.otc.sdk.pax.a920.IConvert.*;

public class ProcessInitializeCallback extends CallbackRest implements ProcessInitializeApi {

    private static final String TAG = "ProcessInitialize";

    private AccessTokenApi tokenApi;
    private InitializeApi initializeApi;

    public ProcessInitializeCallback() {
        tokenApi = new AccessTokenRestImpl();
        initializeApi = new InitializeRestImpl();
    }

    @Override
    public void initialization(final Context context, final InitializeResponseHandler handler) {

        initializeNetWorkLibrary(context);

        if (OtcUtil.connectionNetwork(context)) {
            processInitialization(context, handler);
        }else{
            CustomError error = new CustomError(466, "sin conexión");
            handler.onError(error);
        }

    }

    private void processInitialization(final Context context, final InitializeResponseHandler handler) {

        Header header = new Header();
        header.setExternalId(UUID.randomUUID().toString());

        String serialNumber = OtcUtil.getSerialNumber();

        DeviceIni device = new DeviceIni();
        device.setSerialNumber(serialNumber);

        if (ConfSdk.initializeKeys) {
            device.setReloadKeys(true);
        }else{
            device.setReloadKeys(false);
        }

        if(!ConfSdk.serialNumberTest.equals("")){
            device.setSerialNumber(ConfSdk.serialNumberTest);
        }

        final InitializeRequest request = new InitializeRequest();
        request.setHeader(header);
        request.setDevice(device);

        tokenApi.accessToken(new StringResponseHandler() {
            @Override
            public void onSuccess(String response) {
                //save token
                Storage.saveToken(context, response);

                initializeApi.initialize(context, request, new ObjectResponseHandler<InitializeResponse>() {
                    @Override
                    public void onSuccess(InitializeResponse response) {

                        if (response.getKeys() != null) {

                            writeKeysWork(
                                    response.getKeys().getEwkDataHex(), ConfSdk.keyData,
                                    response.getKeys().getEwkPinHex(), ConfSdk.keyPin,
                                    response.getKeys().getEwkMacSignature(), ConfSdk.keyMac);
                        }

                        Storage.saveMerchantId(context, response.getMerchant().getMerchantId());
                        Storage.saveTerminalId(context, response.getDevice().getTerminalId());

                        com.otc.sdk.pos.flows.sources.server.models.response.initialize.Header header
                                = new com.otc.sdk.pos.flows.sources.server.models.response.initialize.Header();
                        header.setResponseCode(response.getHeader().getResponseCode());
                        header.setResponseMessage(response.getHeader().getResponseMessage());

                        response.setHeader(header);
                        response.setKeys(null);
                        response.setDevice(null);
                        handler.onSuccess(response);
                    }

                    @Override
                    public void onError(CustomError error) {
                        handler.onError(error);
                    }

                    @Override
                    public Class getResponseClass() {
                        return InitializeResponse.class;
                    }
                });
            }

            @Override
            public void onError(CustomError error) {
                handler.onError(error);
            }
        });

    }

    private void writeKeysWork(String data, int slotData,
                               String pin, int slotPin,
                               String signature, int slotMac) {

        //slotData TdkSlot
        //slotPin TpkSlot
        //slotSignature TakSlot

        byte[] bytesTdkData = OtcApplication
                .getConvert()
                .strToBcd(data, EPaddingPosition.PADDING_LEFT);

        Device.writeTDK2(ConfSdk.keyTmk, slotData, bytesTdkData);
        //******************************************************************************************

        byte[] bytesTpkPin = OtcApplication
                .getConvert()
                .strToBcd(pin, EPaddingPosition.PADDING_LEFT);

        Device.writeTPK2(ConfSdk.keyTmk, slotPin, bytesTpkPin);
        //******************************************************************************************

        byte[] bytesTakSignature = OtcApplication
                .getConvert()
                .strToBcd(signature, EPaddingPosition.PADDING_LEFT);

        Device.writeTAK(ConfSdk.keyTmk, slotMac, bytesTakSignature);
    }


    @Override
    public void cancel() {
        AndroidNetworking.cancel(this);
    }


}
