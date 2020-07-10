package com.otc.sdk.pos.flows.util;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.otc.sdk.pax.a920.OtcApplication;
import com.otc.sdk.pos.flows.ConfSdk;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.trans.ClssEntryPoint;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssquickpass.trans.ClssQuickPass;
import com.otc.sdk.pos.flows.domain.usecase.pax.signature.MacRetailUtil;
import com.otc.sdk.pos.flows.domain.usecase.pax.signature.RequestToSign;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.ImplEmv;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.ReadCardActivity;
import com.otc.sdk.pos.flows.sources.server.models.request.authorize.AuthorizeRequest;
import com.pax.dal.entity.EReaderType;
import com.pax.jemv.amex.api.ClssAmexApi;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.KernType;
import com.pax.jemv.clcommon.TransactionPath;
import com.pax.jemv.dpas.api.ClssDPASApi;
import com.pax.jemv.emv.api.EMVCallback;
import com.pax.jemv.jcb.api.ClssJCBApi;
import com.pax.jemv.paypass.api.ClssPassApi;
import com.pax.jemv.paywave.api.ClssWaveApi;
import com.pax.jemv.pure.api.ClssPUREApi;
import com.pax.jemv.qpboc.api.ClssPbocApi;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Map;

import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils.bcd2Str;

public class OtcUtil extends Application {

    private static final String TAG = "OtcUtil";

    private static OtcUtil otcUtil;
    private ClssEntryPoint entryPoint = ClssEntryPoint.getInstance();


    public OtcUtil() {
    }

    public  static OtcUtil getInstance() {
        if (otcUtil == null) {
            otcUtil = new OtcUtil();
        }
        return otcUtil;
    }

    public static String getSerialNumber() {
        String serialNumber;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            serialNumber = (String) get.invoke(c, "gsm.sn1");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ril.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ro.serialno");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "sys.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = Build.SERIAL;

            // If none of the methods above worked
            if (serialNumber.equals(""))
                serialNumber = null;
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = null;
        }
        return serialNumber;
    }

    public static boolean connectionNetwork(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public static String formatAmount(double amount){
        NumberFormat formatter = new DecimalFormat("#0.00");
        return formatter.format(amount);
    }

    public static Map<String, String> getSignatureRequest(AuthorizeRequest authorizeRequest) throws Exception {

        String accessKey = String.format("%s-%s",
                authorizeRequest.getMerchant().getMerchantId(),
                authorizeRequest.getDevice().getTerminalId());

        String dominio = "https://culqimpos.quiputech.com/";
        String tenant = "culqi";

        if (!ConfSdk.endpoint.equals("")) {
            dominio = ConfSdk.endpoint;
        }

        if (!ConfSdk.tenant.equals("")) {
            tenant = ConfSdk.tenant;
        }

        String host = dominio.replace("https://", "");
        host = host.replace("/", "");
        String path = "/api.authorization/v3/" + tenant + "/authorize";

        Log.i(TAG, "getSignatureRequest: " + host);
        Log.i(TAG, "getSignatureRequest: " + path);

        String payload = toJsonPretty(authorizeRequest);

        RequestToSign request = RequestToSign.builder()
                .withAccessKey(accessKey)
                .withHost(host)
                .withMethod("POST")
                .withPath(path)
                .withRegion("global")
                .withService("authentication")
                .withPayload(payload)
                .withQueryParams(null)
                .build();

        return MacRetailUtil.sign("", request);
    }

    public static String toJsonPretty(Object value){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(value);
    }

    public static String getTrack2(String track) {

        // debe tener un formato de 38 caracteres
        track = track.split("F")[0];
//        track = track.split("D")[0];
//        track = track.split("=")[0];
        if (track.length() > 38) {
            track = track.substring(0,37);
        }

        track = String.format("%-38s", track ).replace(' ', '0');
        return track;
    }

    public static String getEmv(int type, String name, int point) {
        String arqc = null;
        String tvr = null;
        String aid = null;
        String appLable = null;
        String appName = null;
        String tsi = null;
        String tc = null;
        String atc = null;
        int iRet;

        EReaderType readerType = null;

        switch (type) {
            case 0 :  readerType = EReaderType.DEFAULT ;break;
            case 1 :  readerType = EReaderType.MAG ;break;
            case 2 :  readerType = EReaderType.ICC ;break;
            case 3 :  readerType = EReaderType.MAG_ICC ;break;
            case 4 :  readerType = EReaderType.PICC ;break;
            case 5 :  readerType = EReaderType.MAG_PICC ;break;
            case 6 :  readerType = EReaderType.ICC_PICC ;break;
            case 7 :  readerType = EReaderType.MAG_ICC_PICC ;break;
            case 8 :  readerType = EReaderType.PICCEXTERNAL ;break;
            case 9 :  readerType = EReaderType.MAG_PICCEXTERNAL ;break;
            case 10 :  readerType = EReaderType.ICC_PICCEXTERNAL ;break;
            case 11 :  readerType = EReaderType.MAG_ICC_PICCEXTERNAL ;break;
        }

        Log.i(TAG, "------------------------ READER TYPE ---------------------------");
        Log.i(TAG, readerType.name());


        String emvReturn = "";

        ByteArray byteArray = new ByteArray();

        if (readerType == EReaderType.PICC) {

            Log.i(TAG, "entryPoint: " + point);

            if (point == KernType.KERNTYPE_MC) {

                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                iRet = ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x95}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                Log.i("Clss_TLV_MC iRet 0x95", Integer.toString(iRet));
                Log.i("Clss_GetTLV_MC TVR 0x95", tvr + "");
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x4F}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x50}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x9F, 0x12}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x9B}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x9F, 0x36}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);


                //*****************************************************
                String emv1 = printEmvMC_Contactless(0x5F,0x2A, "0x5F2A");
                String emv2 = printEmvMC_Contactless(0x82,0, "0x82");
                String emv3 = printEmvMC_Contactless(0x95, 0,"0x95");
                String emv4 = printEmvMC_Contactless(0x9A, 0,"0x9A");
                String emv5 = printEmvMC_Contactless(0x9C, 0,"0x9C");
                String emv6 = printEmvMC_Contactless(0x9F,0x02, "0x9F02");
                String emv7 = printEmvMC_Contactless(0x9F03,0x03, "0x9F03");
                String emv8 = printEmvMC_Contactless(0x9F, 0x10, "0x9F10");
                String emv9 = printEmvMC_Contactless(0x9F, 0x1A, "0x9F1A");
                String emv10 = printEmvMC_Contactless(0x9F, 0x26,"0x9F26");
                String emv11 = printEmvMC_Contactless(0x9F, 0x27,"0x9F27");
                String emv12 = printEmvMC_Contactless(0x9F, 0x33,"0x9F33");
                String emv13 = printEmvMC_Contactless(0x9F, 0x34,"0x9F34");
                String emv14 = printEmvMC_Contactless(0x9F, 0x35,"0x9F35");
                String emv15 = printEmvMC_Contactless(0x9F, 0x36,"0x9F36");
                String emv16 = printEmvMC_Contactless(0x9F, 0x37,"0x9F37");
                String emv17 = printEmvMC_Contactless(0x9F, 0x40,"0x9F40");

                String emv18 = printEmvMC_Contactless(0x5F, 0x34,"0x5F34");
                String emv19 = printEmvMC_Contactless(0x84, 0,"0x84");

                emvReturn =
                        emv1 + emv2 +emv3 +emv4 +emv5
                                +emv6 +emv7 +emv8 +emv9 +emv10
                                +emv11 +emv12 +emv13 +emv14 +emv15
                                +emv16 +emv17 +emv18 +emv19;


            } else if (point == KernType.KERNTYPE_VIS) {

                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x9F26, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x95, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x4F, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x50, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x9F12, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x9B, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x9F26, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x9F36, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);


                //*****************************************************
                String emv1 = printEmvWape(0x5F2A, "0x5F2A");
                String emv2 = printEmvWape(0x82, "0x82");
                String emv3 = printEmvWape(0x95, "0x95");
                String emv4 = printEmvWape(0x9A, "0x9A");
                String emv5 = printEmvWape(0x9C, "0x9C");
                String emv6 = printEmvWape(0x9F02, "0x9F02");
                String emv7 = printEmvWape(0x9F03, "0x9F03");
                String emv8 = printEmvWape(0x9F10, "0x9F10");
                String emv9 = printEmvWape(0x9F1A, "0x9F1A");
                String emv10 = printEmvWape(0x9F26, "0x9F26");
                String emv11 = printEmvWape(0x9F27, "0x9F27");
                String emv12 = printEmvWape(0x9F33, "0x9F33");
                String emv13 = printEmvWape(0x9F34, "0x9F34");
                String emv14 = printEmvWape(0x9F35, "0x9F35");
                String emv15 = printEmvWape(0x9F36, "0x9F36");
                String emv16 = printEmvWape(0x9F37, "0x9F37");
                String emv17 = printEmvWape(0x9F40, "0x9F40");

                String emv18 = printEmvWape(0x5F34, "0x5F34");
                String emv19 = printEmvWape(0x84, "0x84");

                emvReturn =
                        emv1 + emv2 +emv3 +emv4 +emv5
                                +emv6 +emv7 +emv8 +emv9 +emv10
                                +emv11 +emv12 +emv13 +emv14 +emv15
                                +emv16 +emv17 +emv18 +emv19;

                //**********************************************************************************

            } else if (point == KernType.KERNTYPE_AE) {

                ClssAmexApi.Clss_GetTLVData_AE((short) 0x9F26, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x95, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x4F, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x50, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x9F12, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x9B, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x9F26, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x9F36, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);

            } else if (point == KernType.KERNTYPE_ZIP) {

                Log.i(TAG, "initData: entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_ZIP");

                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x95}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x4F}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x50}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x9F, 0x12}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x9B}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x9F, 0x36}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);

            } else if ((point == KernType.KERNTYPE_PBOC) &&
                    (ClssQuickPass.getInstance().getTransPath() == TransactionPath.CLSS_VISA_QVSDC)) {

                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x9F26, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x95, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x4F, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x50, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x9F12, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x9B, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x9F26, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x9F36, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);

            } else if (point == KernType.KERNTYPE_JCB) {


                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                byteArray = new ByteArray();
                iRet = ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x95}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                Log.i("Clss_TLV_MC iRet 0x95", Integer.toString(iRet));
                Log.i("Clss_GetTLV_MC TVR 0x95", tvr + "");
                byteArray = new ByteArray();
                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x4F}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                byteArray = new ByteArray();
                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x50}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                byteArray = new ByteArray();
                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x9F, 0x12}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                byteArray = new ByteArray();
                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x9B}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                byteArray = new ByteArray();
                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                byteArray = new ByteArray();
                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x9F, 0x36}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);

            } else if (point == KernType.KERNTYPE_PURE) {

                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                byteArray = new ByteArray();
                iRet = ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x95}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                Log.i("Clss_TLV_MC iRet 0x95", Integer.toString(iRet));
                Log.i("Clss_GetTLV_MC TVR 0x95", tvr + "");
                byteArray = new ByteArray();
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x4F}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                byteArray = new ByteArray();
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x50}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                byteArray = new ByteArray();
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x9F, 0x12}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                byteArray = new ByteArray();
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x9B}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                byteArray = new ByteArray();
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                byteArray = new ByteArray();
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x9F, 0x36}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);
            }
        }

        if ((readerType == EReaderType.ICC) ||
                ((point == KernType.KERNTYPE_PBOC) && (ClssQuickPass.getInstance().getTransPath() == TransactionPath.CLSS_VISA_VSDC))) { // contact

            EMVCallback.EMVGetTLVData((short) 0x9F26, byteArray);
            byte[] a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            arqc = bcd2Str(a);

            EMVCallback.EMVGetTLVData((short) 0x95, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            tvr = bcd2Str(a);
            EMVCallback.EMVGetTLVData((short) 0x4F, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            aid = bcd2Str(a);
            EMVCallback.EMVGetTLVData((short) 0x50, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            appLable = bcd2Str(a);
            EMVCallback.EMVGetTLVData((short) 0x9F12, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            appName = bcd2Str(a);
            EMVCallback.EMVGetTLVData((short) 0x9B, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            tsi = bcd2Str(a);
            EMVCallback.EMVGetTLVData((short) 0x9F26, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            tc = bcd2Str(a);
            EMVCallback.EMVGetTLVData((short) 0x9F36, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            atc = bcd2Str(a);


            //*****************************************************
            String emv1 = printEmv2(0x5F2A, "0x5F2A");
            String emv2 = printEmv2(0x82, "0x82");
            String emv3 = printEmv2(0x95, "0x95");
            String emv4 = printEmv2(0x9A, "0x9A");
            String emv5 = printEmv2(0x9C, "0x9C");
            String emv6 = printEmv2(0x9F02, "0x9F02");
            String emv7 = printEmv2(0x9F03, "0x9F03");
            String emv8 = printEmv2(0x9F10, "0x9F10");
            String emv9 = printEmv2(0x9F1A, "0x9F1A");
            String emv10 = printEmv2(0x9F26, "0x9F26");
            String emv11 = printEmv2(0x9F27, "0x9F27");
            String emv12 = printEmv2(0x9F33, "0x9F33");
            String emv13 = printEmv2(0x9F34, "0x9F34");
            String emv14 = printEmv2(0x9F35, "0x9F35");
            String emv15 = printEmv2(0x9F36, "0x9F36");
            String emv16 = printEmv2(0x9F37, "0x9F37");
            String emv17 = printEmv2(0x9F40, "0x9F40");

            String emv18 = printEmv2(0x5F34, "0x5F34");
            String emv19 = printEmv2(0x84, "0x84");


            emvReturn =
                    emv1 + emv2 +emv3 +emv4 +emv5
                            +emv6 +emv7 +emv8 +emv9 +emv10
                            +emv11 +emv12 +emv13 +emv14 +emv15
                            +emv16 +emv17 +emv18 +emv19 ;
            //**************************************************************************************

        }
//
//        tvArqc.setText(arqc);
//        tvApplable.setText(appLable);
//        tvAid.setText(aid);
//        tvAppname.setText(appName);
//        tvTsi.setText(tsi);
//        tvTc.setText(tc);
//        tvAtc.setText(atc);
//        tvTvr.setText(tvr);

        return emvReturn;

    }

    public String getEmv() {
        String arqc = null;
        String tvr = null;
        String aid = null;
        String appLable = null;
        String appName = null;
        String tsi = null;
        String tc = null;
        String atc = null;
        int iRet;

        String emvJoined ="";


        ByteArray byteArray = new ByteArray();

        Log.i(TAG, "entryPoint.getOutParam().ucKernType = " + entryPoint.getOutParam().ucKernType);

        if (ReadCardActivity.getReadType() == EReaderType.PICC) {

            Log.i(TAG, "SwingCardActivity.getReadType() == EReaderType.PICC");

            if (entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_MC) {

                Log.i(TAG, "initData: entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_MC");

                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                iRet = ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x95}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                Log.i("Clss_TLV_MC iRet 0x95", Integer.toString(iRet));
                Log.i("Clss_GetTLV_MC TVR 0x95", tvr + "");
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x4F}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x50}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x9F, 0x12}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x9B}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x9F, 0x36}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);


                //*****************************************************
                String emv1 = printEmvMC_Contactless(0x5F,0x2A, "0x5F2A");
                String emv2 = printEmvMC_Contactless(0x82,0, "0x82");
                String emv3 = printEmvMC_Contactless(0x95, 0,"0x95");
                String emv4 = printEmvMC_Contactless(0x9A, 0,"0x9A");
                String emv5 = printEmvMC_Contactless(0x9C, 0,"0x9C");
                String emv6 = printEmvMC_Contactless(0x9F,0x02, "0x9F02");
                String emv7 = printEmvMC_Contactless(0x9F03,0x03, "0x9F03");
                String emv8 = printEmvMC_Contactless(0x9F, 0x10, "0x9F10");
                String emv9 = printEmvMC_Contactless(0x9F, 0x1A, "0x9F1A");
                String emv10 = printEmvMC_Contactless(0x9F, 0x26,"0x9F26");
                String emv11 = printEmvMC_Contactless(0x9F, 0x27,"0x9F27");
                String emv12 = printEmvMC_Contactless(0x9F, 0x33,"0x9F33");
                String emv13 = printEmvMC_Contactless(0x9F, 0x34,"0x9F34");
                String emv14 = printEmvMC_Contactless(0x9F, 0x35,"0x9F35");
                String emv15 = printEmvMC_Contactless(0x9F, 0x36,"0x9F36");
                String emv16 = printEmvMC_Contactless(0x9F, 0x37,"0x9F37");
                String emv17 = printEmvMC_Contactless(0x9F, 0x40,"0x9F40");

                String emv18 = printEmvMC_Contactless(0x5F, 0x34,"0x5F34");
                String emv19 = printEmvMC_Contactless(0x84, 0,"0x84");

                emvJoined =
                        emv1 + emv2 +emv3 +emv4 +emv5
                                +emv6 +emv7 +emv8 +emv9 +emv10
                                +emv11 +emv12 +emv13 +emv14 +emv15
                                +emv16 +emv17 +emv18 +emv19;

                Log.i(TAG, "successProcess: " + emvJoined);
                //**********************************************************************************

            } else if (entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_VIS) {

                Log.i(TAG, "initData: entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_VIS");

                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x9F26, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x95, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x4F, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x50, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x9F12, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x9B, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x9F26, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                ClssWaveApi.Clss_GetTLVData_Wave((short) 0x9F36, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);

                //*****************************************************
                String emv1 = printEmvWape(0x5F2A, "0x5F2A");
                String emv2 = printEmvWape(0x82, "0x82");
                String emv3 = printEmvWape(0x95, "0x95");
                String emv4 = printEmvWape(0x9A, "0x9A");
                String emv5 = printEmvWape(0x9C, "0x9C");
                String emv6 = printEmvWape(0x9F02, "0x9F02");
                String emv7 = printEmvWape(0x9F03, "0x9F03");
                String emv8 = printEmvWape(0x9F10, "0x9F10");
                String emv9 = printEmvWape(0x9F1A, "0x9F1A");
                String emv10 = printEmvWape(0x9F26, "0x9F26");
                String emv11 = printEmvWape(0x9F27, "0x9F27");
                String emv12 = printEmvWape(0x9F33, "0x9F33");
                String emv13 = printEmvWape(0x9F34, "0x9F34");
                String emv14 = printEmvWape(0x9F35, "0x9F35");
                String emv15 = printEmvWape(0x9F36, "0x9F36");
                String emv16 = printEmvWape(0x9F37, "0x9F37");
                String emv17 = printEmvWape(0x9F40, "0x9F40");

                String emv18 = printEmvWape(0x5F34, "0x5F34");
                String emv19 = printEmvWape(0x84, "0x84");

                emvJoined =
                        emv1 + emv2 +emv3 +emv4 +emv5
                                +emv6 +emv7 +emv8 +emv9 +emv10
                                +emv11 +emv12 +emv13 +emv14 +emv15
                                +emv16 +emv17 +emv18 +emv19;

                Log.i(TAG, "successProcess: " + emvJoined);
                //**********************************************************************************

            } else if (entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_AE) {

                Log.i(TAG, "initData: entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_AE");

                ClssAmexApi.Clss_GetTLVData_AE((short) 0x9F26, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x95, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x4F, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x50, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x9F12, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x9B, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x9F26, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                ClssAmexApi.Clss_GetTLVData_AE((short) 0x9F36, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);

            } else if (entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_ZIP) {

                Log.i(TAG, "initData: entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_ZIP");

                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x95}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x4F}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x50}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x9F, 0x12}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x9B}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                ClssDPASApi.Clss_GetTLVDataList_DPAS(new byte[]{(byte) 0x9F, 0x36}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);

            } else if ((entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_PBOC) &&
                    (ClssQuickPass.getInstance().getTransPath() == TransactionPath.CLSS_VISA_QVSDC)) {

                Log.i(TAG, "initData: (entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_PBOC) &&\n" +
                        "                    (ClssQuickPass.getInstance().getTransPath() == TransactionPath.CLSS_VISA_QVSDC)");

                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x9F26, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x95, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x4F, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x50, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x9F12, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x9B, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x9F26, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x9F36, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);

            } else if (entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_JCB) {


                Log.i(TAG, "initData: entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_JCB");

                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                byteArray = new ByteArray();
                iRet = ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x95}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                Log.i("Clss_TLV_MC iRet 0x95", Integer.toString(iRet));
                Log.i("Clss_GetTLV_MC TVR 0x95", tvr + "");
                byteArray = new ByteArray();
                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x4F}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                byteArray = new ByteArray();
                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x50}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                byteArray = new ByteArray();
                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x9F, 0x12}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                byteArray = new ByteArray();
                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x9B}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                byteArray = new ByteArray();
                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                byteArray = new ByteArray();
                ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x9F, 0x36}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);

            } else if (entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_PURE) {

                Log.i(TAG, "initData: entryPoint.getOutParam().ucKernType == KernType.KERNTYPE_PURE");

                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                byte[] a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                arqc = bcd2Str(a);
                byteArray = new ByteArray();
                iRet = ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x95}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tvr = bcd2Str(a);
                Log.i("Clss_TLV_MC iRet 0x95", Integer.toString(iRet));
                Log.i("Clss_GetTLV_MC TVR 0x95", tvr + "");
                byteArray = new ByteArray();
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x4F}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                aid = bcd2Str(a);
                byteArray = new ByteArray();
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x50}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appLable = new String(a);
                byteArray = new ByteArray();
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x9F, 0x12}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                appName = new String(a);
                byteArray = new ByteArray();
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x9B}, (byte) 1, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tsi = bcd2Str(a);
                byteArray = new ByteArray();
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x9F, 0x26}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                tc = bcd2Str(a);
                byteArray = new ByteArray();
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x9F, 0x36}, (byte) 2, 10, byteArray);
                a = new byte[byteArray.length];
                System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
                atc = bcd2Str(a);
            }
        }

        if ((ReadCardActivity.getReadType() == EReaderType.ICC) ||
                ((ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_PBOC) && (ClssQuickPass.getInstance().getTransPath() == TransactionPath.CLSS_VISA_VSDC))) { // contact

            Log.i(TAG, "initData: (SwingCardActivity.getReadType() == EReaderType.ICC) ||\n" +
                    "                ((ClssEntryPoint.getInstance().getOutParam().ucKernType == KernType.KERNTYPE_PBOC) && (ClssQuickPass.getInstance().getTransPath() == TransactionPath.CLSS_VISA_VSDC))");

            EMVCallback.EMVGetTLVData((short) 0x9F26, byteArray);
            byte[] a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            arqc = bcd2Str(a);

            EMVCallback.EMVGetTLVData((short) 0x95, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            tvr = bcd2Str(a);
            EMVCallback.EMVGetTLVData((short) 0x4F, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            aid = bcd2Str(a);
            EMVCallback.EMVGetTLVData((short) 0x50, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            appLable = bcd2Str(a);
            EMVCallback.EMVGetTLVData((short) 0x9F12, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            appName = bcd2Str(a);
            EMVCallback.EMVGetTLVData((short) 0x9B, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            tsi = bcd2Str(a);
            EMVCallback.EMVGetTLVData((short) 0x9F26, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            tc = bcd2Str(a);
            EMVCallback.EMVGetTLVData((short) 0x9F36, byteArray);
            a = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
            atc = bcd2Str(a);


            //*****************************************************
            String emv1 = printEmv2(0x5F2A, "0x5F2A");
            String emv2 = printEmv2(0x82, "0x82");
            String emv3 = printEmv2(0x95, "0x95");
            String emv4 = printEmv2(0x9A, "0x9A");
            String emv5 = printEmv2(0x9C, "0x9C");
            String emv6 = printEmv2(0x9F02, "0x9F02");
            String emv7 = printEmv2(0x9F03, "0x9F03");
            String emv8 = printEmv2(0x9F10, "0x9F10");
            String emv9 = printEmv2(0x9F1A, "0x9F1A");
            String emv10 = printEmv2(0x9F26, "0x9F26");
            String emv11 = printEmv2(0x9F27, "0x9F27");
            String emv12 = printEmv2(0x9F33, "0x9F33");
            String emv13 = printEmv2(0x9F34, "0x9F34");
            String emv14 = printEmv2(0x9F35, "0x9F35");
            String emv15 = printEmv2(0x9F36, "0x9F36");
            String emv16 = printEmv2(0x9F37, "0x9F37");
            String emv17 = printEmv2(0x9F40, "0x9F40");

            String emv18 = printEmv2(0x5F34, "0x5F34");
            String emv19 = printEmv2(0x84, "0x84");


            emvJoined =
                    emv1 + emv2 +emv3 +emv4 +emv5
                            +emv6 +emv7 +emv8 +emv9 +emv10
                            +emv11 +emv12 +emv13 +emv14 +emv15
                            +emv16 +emv17 +emv18 +emv19 ;
            //**************************************************************************************

        }


        return emvJoined;

    }

    private static String printEmvWape(int tag, String tagName) {

        tagName = tagName.replace("0x", "");
        String concatEmv = "";
        String tagValue;

        try {
            ByteArray dataArray = new ByteArray();
            ClssWaveApi.Clss_GetTLVData_Wave((short) tag, dataArray);
            byte[] dataCard = Arrays.copyOfRange(dataArray.data, 0, dataArray.length);

            if (dataArray.length == 256) {

                if (tagName.equals("9F03")) {
                    concatEmv = tagName + "06" + "000000000000";
                }else if (tagName.equals("9F34")){
                    concatEmv = tagName + "03" + "000000";
                }else if (tagName.equals("9F35")){
                    concatEmv = tagName + "01" + "00";
                }else if (tagName.equals("9F40")){
                    concatEmv = tagName + "05" + "0000000000";
                }

                Log.i(TAG, "printEmvWape : * "  + tagName + " = vacio");
            }else{
                tagValue = OtcApplication.getConvert().bcdToStr(dataCard);
                Log.i(TAG, "printEmvWape : "  + tagName + " = " + calculateSizeEmv(tagValue.length()) + " - "+ tagValue);
                concatEmv = tagName + calculateSizeEmv(tagValue.length()) + tagValue;
            }
        }catch(Exception e) {
            Log.i(TAG, "printEmvWape : * "  + tagName + " = vacio");

        }
        return concatEmv;
    }


    private static String printEmv2(int tag, String tagName) {
        tagName = tagName.replace("0x", "");
        String concatEmv;
        String tagValue;

        try {
            tagValue = OtcApplication.getConvert().bcdToStr(ImplEmv.getTlv(tag));
            Log.i(TAG, "printEmv2 : "  + tagName + " = " + calculateSizeEmv(tagValue.length()) + " - "+ tagValue);
            concatEmv = tagName + calculateSizeEmv(tagValue.length()) + tagValue;
        }catch(Exception e) {

            if (tagName.equals("9F03")) {
                concatEmv = tagName + "06" + "000000000000";
            }else{
                concatEmv = "";
            }
            Log.i(TAG, "printEmv2 : * "  + tagName + " = vacio");
        }
        return concatEmv;
    }

    private static String printEmvMC_Contactless(int tag1, int tag2, String tagName) {

        tagName = tagName.replace("0x", "");
        String concatEmv;
        String tagValue = "";
        byte[] byteArrayNull = new byte[256];

        try {
            ByteArray dataArray = new ByteArray();
            if (tag2 == 0) {
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) tag1}, (byte) 1, 10, dataArray);
            }else{
                ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) tag1, (byte)tag2}, (byte) 2, 10, dataArray);
            }

            byte[] dataCard = new byte[dataArray.length];
            System.arraycopy(dataArray.data, 0, dataCard, 0, dataArray.length);

            if (Arrays.equals(byteArrayNull,dataCard)) {

                if (tagName.equals("9F03")) {
                    tagValue = "000000000000";
                }else if (tagName.equals("9F10")){
                    tagValue = "000000000000000000000000000000000000";
                }

            }else{
                tagValue = OtcApplication.getConvert().bcdToStr(dataCard);
            }

        }catch(Exception e) {
            Log.e(TAG, "printEmvMC_Contactless: ", e);

        }

        concatEmv = tagName + calculateSizeEmv(tagValue.length()) + tagValue;
        return concatEmv;
    }

    private static String calculateSizeEmv(int value){

        value = value/2;
        String temp = Integer.toHexString(value);
        value = Integer.parseInt(temp);
        String result = "";
        if(value < 10){
            result = "0" +  value;
        }else{
            result = value + "";
        }
        return result;
    }


}
