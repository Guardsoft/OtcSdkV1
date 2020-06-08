package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw;

import android.content.Context;
import android.os.ConditionVariable;
import android.util.Log;
import android.util.SparseArray;

import com.otc.sdk.pos.flows.domain.usecase.pax.app.TradeApplication;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.model.TransResult;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.trans.ClssEntryPoint;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssquickpass.trans.ClssQuickPass;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.abl.core.AAction;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.abl.core.ActionResult;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.pay.trans.action.ActionEnterPin;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils;
import com.pax.jemv.clcommon.ACType;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.EMV_APPLIST;
import com.pax.jemv.clcommon.EMV_CAPK;
import com.pax.jemv.clcommon.OnlineResult;
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.device.DeviceManager;
import com.pax.jemv.emv.api.EMVCallback;
import com.pax.jemv.emv.model.EmvEXTMParam;
import com.pax.jemv.emv.model.EmvMCKParam;
import com.pax.jemv.emv.model.EmvParam;

import java.util.Arrays;
import java.util.List;

import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils.bcd2Str;
import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils.bytes2String;


/**
 * Created by yanglj on 2017-06-07.
 */

public class ImplEmv {

    private static SparseArray<byte[]> tags = new SparseArray<>();
    private static final String TAG = "ImplEmv";
    public String operation;
    private EMVCallback emvCallback;
    private static Context emvContext;
    private static ConditionVariable cv;

    String amount;
    long ulAmntAuth;
    long ulAmntOther;
    long ulTransNo;
    byte ucTransType;
    byte[] aucTransDate = new byte[4];
    byte[] aucTransTime = new byte[4];

    private EmvParam emvParam;
    private EmvMCKParam mckParam;
    private boolean statePin = true;

    public ImplEmv(Context context) {
        emvParam = new EmvParam();
        mckParam = new EmvMCKParam();
        mckParam.extmParam = new EmvEXTMParam();
        emvCallback = EMVCallback.getInstance();
        emvCallback.setCallbackListener(emvCallbackListener);
        emvContext = context;
    }

    public ImplEmv(Context context, boolean statePin) {
        emvParam = new EmvParam();
        mckParam = new EmvMCKParam();
        mckParam.extmParam = new EmvEXTMParam();
        emvCallback = EMVCallback.getInstance();
        emvCallback.setCallbackListener(emvCallbackListener);
        emvContext = context;

        this.statePin = statePin;
    }

    private int addCapkIntoEmvLib() {
        int ret;
        ByteArray dataList = new ByteArray();

        ret = EMVCallback.EMVGetTLVData((short) 0x4F, dataList);
        if (ret != 0) {
            ret = EMVCallback.EMVGetTLVData((short) 0x84, dataList);
        }
        if (ret == 0) {
            byte[] rid = new byte[5];
            System.arraycopy(dataList.data, 0, rid, 0, 5);
            ret = EMVCallback.EMVGetTLVData((short) 0x8F, dataList);
            if (ret == 0) {
                byte keyId = dataList.data[0];
                Log.i("log", "keyID=" + keyId);
                for (EMV_CAPK capk : EmvTestCAPK.genCapks()) {
                    if (bytes2String(capk.rID).equals(new String(rid))) {
                        //if (keyId < 0 || capk.keyID == keyId) {
                        if (keyId == -1 || capk.keyID == keyId) {
                            // ret = EMVCallback.EMVAddCAPK(capk);
                            EMV_CAPK emv_capk = new EMV_CAPK();
                            Log.i("log", "EMVAddCAPK rid=" + bcd2Str(capk.rID));
                            Log.i("log", "EMVAddCAPK keyID=" + capk.keyID);
                            Log.i("log", "EMVAddCAPK exponentLen=" + capk.exponentLen);
                            Log.i("log", "EMVAddCAPK hashInd=" + capk.hashInd);
                            Log.i("log", "EMVAddCAPK arithInd=" + capk.arithInd);
                            Log.i("log", "EMVAddCAPK modulLen=" + capk.modulLen);
                            Log.i("log", "EMVAddCAPK checkSum=" + bcd2Str(capk.checkSum));
                            emv_capk.rID = capk.rID;
                            emv_capk.keyID = capk.keyID;
                            emv_capk.hashInd = capk.hashInd;
                            emv_capk.arithInd = capk.arithInd;
                            emv_capk.modul = capk.modul;
                            emv_capk.modulLen = (short) capk.modulLen;
                            emv_capk.exponent = capk.exponent;
                            emv_capk.exponentLen = (byte) capk.exponentLen;
                            emv_capk.expDate = capk.expDate;
                            emv_capk.checkSum = capk.checkSum;
                            ret = EMVCallback.EMVAddCAPK(emv_capk);
                            Log.i("log", "EMVAddCAPK ret=" + ret);
                        }
                    }
                }
            }
        }
        return ret;
    }

    public int startContactEmvTrans() {
        int ret = EMVCallback.EMVCoreInit();
        if (ret != RetCode.EMV_OK) {
            return ret;
        }

        EMVCallback.EMVSetCallback();


        EMVCallback.EMVGetParameter(emvParam);
        emvParam.capability = Utils.str2Bcd("E0F8C8");
        //emvParam.countryCode = Utils.str2Bcd("0840");
        emvParam.countryCode = Utils.str2Bcd("0156"); // soles
        emvParam.exCapability = Utils.str2Bcd("E000F0A001");
        emvParam.forceOnline = 0;
        emvParam.getDataPIN = ((byte) 1);
        //emvParam.merchCateCode = Utils.str2Bcd("0840");
        emvParam.merchCateCode = Utils.str2Bcd("0156");
        //emvParam.referCurrCode = Utils.str2Bcd("0840");
        emvParam.referCurrCode = Utils.str2Bcd("0156");
        emvParam.referCurrCon = 1000;
        emvParam.referCurrExp = (byte) 2;
        emvParam.surportPSESel = (byte) 1;
        emvParam.terminalType = ((byte) 0x22);
        //emvParam.transCurrCode = Utils.str2Bcd("0840");
        emvParam.transCurrCode = Utils.str2Bcd("0156");
        emvParam.transCurrExp = (byte) 2;
        emvParam.transType = ucTransType;
        emvParam.termId = "12345678".getBytes();
        emvParam.merchId = "123456789012345".getBytes();
        emvParam.merchName = "abcd".getBytes();

        EMVCallback.EMVSetParameter(emvParam);


        EMVCallback.EMVGetMCKParam(mckParam);
        mckParam.ucBypassPin = 1;
        mckParam.ucBatchCapture = 1;
        mckParam.extmParam.aucTermAIP = Utils.str2Bcd("0800");
        mckParam.extmParam.ucUseTermAIPFlg = 1;
        mckParam.extmParam.ucBypassAllFlg = 1;
        EMVCallback.EMVSetMCKParam(mckParam);

        EMVCallback.EMVSetPCIModeParam((byte) 1, "0,4,5,6,7,8,9,10,11,12".getBytes(), 1000 * 120);//Set no PCI mode. for input PIN

        EMVCallback.EMVDelAllApp();
        for (EMV_APPLIST i : EmvTestAID.genApplists()) {
            ret = EMVCallback.EMVAddApp(i);
            if (ret != RetCode.EMV_OK) {
                Log.i(TAG, "EMVAddApp");
                return ret;
            }
            //Log.i(TAG, "EMVAddApp " + Utils.bcd2Str(i.aid));
        }

        EMV_APPLIST test = new EMV_APPLIST();
        for (int i = 0; i < EmvTestAID.genApplists().size(); ++i) {
            ret = EMVCallback.EMVGetApp(i, test);
            Log.i(TAG, "EmvApiGetApp " + bcd2Str(test.aid));
            if (ret != RetCode.EMV_OK) {
                Log.i(TAG, "EMVGetApp err=" + ret);
                return ret;
            }
        }

        ret = EMVCallback.EMVAppSelect(0, 1);
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVAppSelect");
            return ret;
        }

        ret = EMVCallback.EMVReadAppData();
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVReadAppData");
            return ret;
        }

        for (EMV_CAPK i : EmvTestCAPK.genCapks())
            EMVCallback.EMVDelCAPK(i.keyID, i.rID);

        addCapkIntoEmvLib();

        ret = EMVCallback.EMVCardAuth();
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVCardAuth");
            return ret;
        }

        byte[] errCode = new byte[10];
        ret = EMVCallback.EMVGetDebugInfo(0, errCode);
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVGetDebugInfo1 ret=" + ret);
            //return ret;
        } else {
            Log.i(TAG, "EMVGetDebugInfo1 ok .ret=" + ret);
        }


        Log.i("EmvApi", "before EMVStartTrans");
        ACType acType = new ACType();
        Log.i(TAG, "AcType 1=" + acType.type + "");
        ret = EMVCallback.EMVStartTrans(ulAmntAuth, 0, acType);
        Log.i("EmvApi", "after EMVStartTrans");
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVStartTrans err = " + ret);
            return ret;
        }
        Log.i(TAG, "AcType =" + acType.type + "");
        if (acType.type == ACType.AC_TC)
            return TransResult.EMV_OFFLINE_APPROVED;
        else if (acType.type == ACType.AC_AAC)
            return TransResult.EMV_OFFLINE_DENIED;
        else if (acType.type == ACType.AC_ARQC)
            return TransResult.EMV_ARQC;


//        if(acType.type == ACType.AC_TC)
//        {
//            return RetCode.EMV_OK;
//        }
//        else if(acType.type == ACType.AC_AAC)
//        {
//            return RetCode.EMV_DENIAL;
//        }

//        String authCode = "123456";
//        EMVCallback.EMVSetTLVData((short) 0x89, authCode.getBytes(), 6);
//        EMVCallback.EMVSetTLVData((short) 0x8A, "00".getBytes(), 2);


//        onlineProc();
//
//        byte[] script = Utils.str2Bcd("9F1804AABBCCDD86098424000004AABBCCDD86098418000004AABBCCDD86098416000004AABBCCDD");
//        int rspResult = OnlineResult.ONLINE_APPROVE;
//        ret = EMVCallback.EMVCompleteTrans(rspResult, script, script.length, acType);
//        Log.i(TAG, "EMVCompleteTrans");
//        ByteArray dataList = new ByteArray(5);
//        EMVCallback.EMVGetTLVData((short) 0x95, dataList);
//        Log.i("EMVGetTLVData TVR 0x95", Utils.bcd2Str(dataList.data, 5) + "");
//        EMVCallback.EMVGetTLVData((short) 0x9B, dataList);
//        Log.i("EMVGetTLVData TVR 0x95", Utils.bcd2Str(dataList.data, 2) + "");
//
//        Log.i("AcType", acType.type + "");
//        if(acType.type == ACType.AC_TC)
//        {
//            return RetCode.EMV_OK;
//        }
//        if(acType.type == ACType.AC_AAC)
//        {
//            return RetCode.EMV_DENIAL;
//        }

        return ret;
    }

    public int CompleteContactEmvTrans() {
        int ret;

        String authCode = "123456";
        EMVCallback.EMVSetTLVData((short) 0x89, authCode.getBytes(), 6);
        EMVCallback.EMVSetTLVData((short) 0x8A, "00".getBytes(), 2);
        byte[] script = Utils.str2Bcd("9F1804AABBCCDD86098424000004AABBCCDD86098418000004AABBCCDD86098416000004AABBCCDD");
        int rspResult = OnlineResult.ONLINE_APPROVE;
        ACType acType = new ACType();
        ret = EMVCallback.EMVCompleteTrans(rspResult, script, script.length, acType);
        Log.i(TAG, "EMVCompleteTrans");
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVCompleteTrans err = " + ret);
            //return ret;
        }
        ByteArray dataList = new ByteArray(5);
        EMVCallback.EMVGetTLVData((short) 0x95, dataList);
        Log.i("EMVGetTLVData TVR 0x95", bcd2Str(dataList.data, 5) + "");
        EMVCallback.EMVGetTLVData((short) 0x9B, dataList);
        Log.i("EMVGetTLVData TVR 0x9B", bcd2Str(dataList.data, 2) + "");
        //acType.type = ACType.AC_TC;  //this is for demo only;
        Log.i("AcType", acType.type + "");
        if (acType.type == ACType.AC_TC)
            return TransResult.EMV_ONLINE_APPROVED;
        else if (acType.type == ACType.AC_AAC) {
            if (rspResult == OnlineResult.ONLINE_APPROVE)
                return TransResult.EMV_ONLINE_CARD_DENIED;
            else
                return TransResult.EMV_ONLINE_DENIED;
        }
        return ret;
    }

    private EMVCallback.EmvCallbackListener emvCallbackListener = new EMVCallback.EmvCallbackListener() {
        @Override
        public void emvWaitAppSel(int tryCnt, final EMV_APPLIST[] list, int appNum) {
            Log.i(TAG, "emvWaitAppSel : need to call app select page");
            List<EMV_APPLIST> lis = Arrays.asList(list);
            for (EMV_APPLIST i : lis) {
                Log.i(TAG, "AID :" + bcd2Str(i.aid));
            }
            emvCallback.setCallBackResult(0); //force to select the first app
        }

        @Override
        public void emvInputAmount(long[] amt) {
            amt[0] = ulAmntAuth; //dummy
            //if (amt[1] != null)
            //amt[1] = 0;
            Log.i(TAG, "emvInputAmount : need to call input amount page");
            emvCallback.setCallBackResult(RetCode.EMV_OK);
        }

        @Override
        public void emvGetHolderPwd(int tryFlag, int remainCnt, byte[] pin) {
            if (pin == null) {
                Log.i("log", "emvGetHolderPwd pin is null, tryFlag=" + tryFlag + " remainCnt:" + remainCnt);
            } else {
                Log.i("log", "emvGetHolderPwd pin is not null, tryFlag=" + tryFlag + " remainCnt:" + remainCnt);
            }

            int result = 0;
            int ret = 0;

            if ( (pin != null && pin[0] != 0) || !statePin) {

                Log.i(TAG, "emvGetHolderPwd: SIN PIN ******************************************");


                if (!statePin) {
                    ret = 1234;
                    Log.i("log", "emvGetHolderPwd ret=" + ret);
                }else{
                    ret = pin[0];
                    Log.i("log", "emvGetHolderPwd ret=" + ret);
                }


            } else {

                Log.i(TAG, "emvGetHolderPwd: CON PIN *****************************************");

                enterPin(pin == null, remainCnt);
                //enterPin(MainActivity.this, pin == null, remainCnt);
                Log.i("log", "emvGetHolderPwd enterPin finish");
                ret = GetPinEmv.getInstance().GetPinResult();
                Log.i("log", "GetPinEmv GetPinResult = " + ret);
            }



            if (ret == EEmvExceptions.EMV_OK.getErrCodeFromBasement()) {
                result = RetCode.EMV_OK;
                Log.i("log", "GetPinEmv result = EMV_OK");
            } else if (ret == EEmvExceptions.EMV_ERR_TIME_OUT.getErrCodeFromBasement()) {
                result = RetCode.EMV_TIME_OUT;
                Log.i("log", "GetPinEmv result = EMV_ERR_TIME_OUT");
            } else if (ret == EEmvExceptions.EMV_ERR_USER_CANCEL.getErrCodeFromBasement()) {
                result = RetCode.EMV_USER_CANCEL;
                Log.i("log", "GetPinEmv result = EMV_USER_CANCEL");
            } else if (ret == EEmvExceptions.EMV_ERR_NO_PASSWORD.getErrCodeFromBasement()) {
                result = RetCode.EMV_NO_PASSWORD;
                Log.i("log", "GetPinEmv result = EMV_NO_PASSWORD");
            } else {
                result = RetCode.EMV_NO_PINPAD;
                Log.i("log", "GetPinEmv result = EMV_USER_CANCEL");
            }

            emvCallback.setCallBackResult(result);
        }

        @Override
        public void emvAdviceProc() {
            Log.i(TAG, "emvAdviceProc");
        }

        @Override
        public void emvVerifyPINOK() {
            Log.i(TAG, "emvVerifyPINOK");
        }

        @Override
        public int emvVerifyPINfailed(byte[] var1) {
            Log.i(TAG, "emvVerifyPINfailed ret = " + var1);
            return 0;
        }

        @Override
        public int emvUnknowTLVData(short tag, ByteArray data) {
            //Log.i(TAG, "emvUnknowTLVData tag: "+ Integer.toHexString(tag) + " data:" + data.data.length);
            switch ((int) tag) {
                case 0x9A:
                    //String date = TradeApplication.dal.getSys().getDate();
                    //System.arraycopy(Utils.str2Bcd(date.substring(2, 8)), 0, data.data, 0, data.data.length);
                    byte[] date = new byte[7];
                    DeviceManager.getInstance().getTime(date);
                    System.arraycopy(date, 1, data.data, 0, 3);
                    break;
                case 0x9F1E:
                    //String sn = TradeApplication.dal.getSys().getTermInfo().get(ETermInfoKey.SN);
                    //System.arraycopy(sn.getBytes(), 0, data.data, 0, data.data.length);
                    byte[] sn = new byte[10];
                    DeviceManager.getInstance().readSN(sn);
                    System.arraycopy(sn, 0, data.data, 0, Math.min(data.data.length, sn.length));
                    break;
                case 0x9F21:
                    //String time = TradeApplication.dal.getSys().getDate();
                    byte[] time = new byte[7];
                    DeviceManager.getInstance().getTime(time);
                    //System.arraycopy(time, 3, data.data, 0, 3);
                    System.arraycopy(time, 4, data.data, 0, 3);
                    break;
                case 0x9F37:
                    //byte[] random = TradeApplication.dal.getSys().getRandom(data.data.length);
                    //System.arraycopy(random, 0, data.data, 0, data.data.length);
                    byte[] random = new byte[4];
                    DeviceManager.getInstance().getRand(random, 4);
                    System.arraycopy(random, 0, data.data, 0, data.data.length);
                    break;
                case 0xFF01:
                    Arrays.fill(data.data, (byte) 0x00);
                    break;
                default:
                    return RetCode.EMV_PARAM_ERR;
            }
            data.length = data.data.length;
            Log.i(TAG, "emvUnknowTLVData tag: " + Integer.toHexString(tag));
            Log.i(TAG, "emvUnknowTLVData data: " + bcd2Str(data.data) + " length:" + data.data.length);
            return RetCode.EMV_OK;
        }

        @Override
        public void certVerify() {
            Log.i(TAG, "certVerify");
            emvCallback.setCallBackResult(RetCode.EMV_OK);
        }

        @Override
        public int emvSetParam() {
            Log.i(TAG, "emvSetParam");
            return RetCode.EMV_OK;
        }

        @Override
        public int cRFU2() {
            return 0;
        }
    };

    public void enterPin(boolean isOnlinePin, int offlinePinLeftTimes) {
        final boolean onlinePin = isOnlinePin;
        Log.i("log", "enterPin offlinePinLeftTimes=" + Integer.toHexString(offlinePinLeftTimes));


        cv = new ConditionVariable();
        final String totalAmount = amount;
        final String leftTimes = Integer.toString(offlinePinLeftTimes);

        //showEnterPin(context,pan,isOnlinePin, offlinePinLeftTimes);
        ActionEnterPin actionEnterPin = new ActionEnterPin(new AAction.ActionStartListener() {

            byte[] track2 = getTlv(0x57);
            String strTrack2 = TradeApplication.getConvert().bcdToStr(track2).split("F")[0];
            //strTrack2 = strTrack2.split("F")[0];
            String pan = strTrack2.split("D")[0];

            @Override
            public void onStart(AAction action) {
                ((ActionEnterPin) action).setParam(emvContext, pan, onlinePin, totalAmount, leftTimes);
            }

        });
        actionEnterPin.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                //InputPwdDialog.OnDismissListener();
                Log.i(TAG, "setEndListener OnEnd");
//                ConsumeActivity.getInstance().onStop();
                ConsumeActivity.getInstance().onStop();
                //ActivityStack.getInstance().popTo((Activity) context);
            }
        });
        actionEnterPin.execute();
        cv.block(); // for the Offline pin case, block it for make sure the PIN activity is ready, otherwise, may get the black screen.
    }


    public static byte[] getTlv(int tag) {
        ByteArray byteArray = new ByteArray();
        if (EMVCallback.EMVGetTLVData((short) tag, byteArray) == RetCode.EMV_OK) {
            byte[] data = Arrays.copyOfRange(byteArray.data, 0, byteArray.length);
            tags.put(tag, data);
            Log.i("asd", Integer.toHexString(tag) + ":" + data.length);
            return data;
        }
        return tags.get(tag, null);
    }

    public static void pinEnterReady() {
        if (cv != null)
            cv.open();
    }

    int startClssPBOC(TransResult transResult) {

        transResult.result = TransResult.EMV_ABORT_TERMINATED;

        int ret = EMVCallback.EMVCoreInit();
        if (ret != RetCode.EMV_OK) {
            return ret;
        }
        EMVCallback.EMVSetCallback();

        EMVCallback.EMVGetParameter(emvParam);
        ClssQuickPass clssQuickPass = ClssQuickPass.getInstance();
        emvParam.capability = clssQuickPass.getClss_ReaderParam().aucTmCap;
        emvParam.countryCode = clssQuickPass.getClss_ReaderParam().aucTmCntrCode;
        Log.i(TAG, " emvParam.countryCode = " + bcd2Str(emvParam.countryCode));
        emvParam.exCapability = clssQuickPass.getClss_ReaderParam().aucTmCapAd;
        emvParam.forceOnline = 0;
        emvParam.getDataPIN = ((byte) 1);
        emvParam.merchCateCode = clssQuickPass.getClss_ReaderParam().aucMerchCatCode;
        emvParam.referCurrCode = clssQuickPass.getClss_ReaderParam().aucTmRefCurCode;
        emvParam.referCurrCon = clssQuickPass.getClss_ReaderParam().ulReferCurrCon;
        emvParam.referCurrExp = clssQuickPass.getClss_ReaderParam().ucTmRefCurExp;
        emvParam.surportPSESel = (byte) 1;
        emvParam.terminalType = clssQuickPass.getClss_ReaderParam().ucTmType;
        emvParam.transCurrCode = clssQuickPass.getClss_ReaderParam().aucTmTransCur;
        Log.i(TAG, " emvParam.transCurrCode = " + bcd2Str(emvParam.transCurrCode));
        emvParam.transCurrExp = clssQuickPass.getClss_ReaderParam().ucTmTransCurExp;
        emvParam.transType = clssQuickPass.getClss_ReaderParam().ucTmType;
        emvParam.termId = clssQuickPass.getClss_ReaderParam().acquierId;
        emvParam.merchId = clssQuickPass.getClss_ReaderParam().aucMerchantID;
        emvParam.merchName = clssQuickPass.getClss_ReaderParam().aucMchNameLoc;

        EMVCallback.EMVSetParameter(emvParam);


        EMVCallback.EMVGetMCKParam(mckParam);
        mckParam.ucBypassPin = 1;
        mckParam.ucBatchCapture = 1;
        mckParam.extmParam.aucTermAIP = Utils.str2Bcd("0800");
        mckParam.extmParam.ucUseTermAIPFlg = 1;
        mckParam.extmParam.ucBypassAllFlg = 1;
        EMVCallback.EMVSetMCKParam(mckParam);

        EMVCallback.EMVSetPCIModeParam((byte) 1, "0,4,5,6,7,8,9,10,11,12".getBytes(), 1000 * 120);//Set no PCI mode. for input PIN

        EMVCallback.EMVDelAllApp();
        for (EMV_APPLIST i : EmvTestAID.genApplists()) {
            ret = EMVCallback.EMVAddApp(i);
            if (ret != RetCode.EMV_OK) {
                Log.i(TAG, "EMVAddApp");
                return ret;
            }
            //Log.i(TAG, "EMVAddApp " + Utils.bcd2Str(i.aid));
        }

        EMV_APPLIST test = new EMV_APPLIST();
        for (int i = 0; i < EmvTestAID.genApplists().size(); ++i) {
            ret = EMVCallback.EMVGetApp(i, test);
            Log.i(TAG, "EmvApiGetApp " + bcd2Str(test.aid));
            if (ret != RetCode.EMV_OK) {
                Log.i(TAG, "EMVGetApp err=" + ret);
                return ret;
            }
        }

        EMVCallback.EMVInitTLVData();
        ClssEntryPoint entryPoint = ClssEntryPoint.getInstance();
        ByteArray gpoData = new ByteArray();
        ret = clssQuickPass.getClss_GPOData(gpoData);
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "getClss_GPOData err=" + ret);
            return ret;
        }
        ret = EMVCallback.EMVSwitchClss(entryPoint.getTransParam(), entryPoint.getOutParam().sDataOut, entryPoint.getOutParam().iDataLen, gpoData.data, gpoData.length);
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVSwitchClss err = " + ret);
            return ret;
        }

        ret = EMVCallback.EMVReadAppData();
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVReadAppData err = " + ret);
            return ret;
        }

        for (EMV_CAPK i : EmvTestCAPK.genCapks())
            EMVCallback.EMVDelCAPK(i.keyID, i.rID);

        addCapkIntoEmvLib();

        ret = EMVCallback.EMVCardAuth();
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVCardAuth");
            return ret;
        }

        byte[] errCode = new byte[10];
        ret = EMVCallback.EMVGetDebugInfo(0, errCode);
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVGetDebugInfo1 ret=" + ret);
            //return ret;
        } else {
            Log.i(TAG, "EMVGetDebugInfo1 ok .ret=" + ret);
        }


        Log.i("EmvApi", "before EMVStartTrans");
        ACType acType = new ACType();
        Log.i(TAG, "AcType 1=" + acType.type + "");
        ulAmntAuth = entryPoint.getTransParam().ulAmntAuth;

        Log.i(TAG, "ulAmntAuth = " + Long.toString(ulAmntAuth));

        ret = EMVCallback.EMVStartTrans(ulAmntAuth, 0, acType);
        Log.i("EmvApi", "after EMVStartTrans");
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVStartTrans err = " + ret);
            return ret;
        }
        Log.i(TAG, "AcType =" + acType.type + "");
        if (acType.type == ACType.AC_TC)
            transResult.result = TransResult.EMV_OFFLINE_APPROVED;
        else if (acType.type == ACType.AC_AAC)
            transResult.result = TransResult.EMV_OFFLINE_DENIED;
        else if (acType.type == ACType.AC_ARQC)
            transResult.result = TransResult.EMV_ARQC;
        return ret;
    }
}
