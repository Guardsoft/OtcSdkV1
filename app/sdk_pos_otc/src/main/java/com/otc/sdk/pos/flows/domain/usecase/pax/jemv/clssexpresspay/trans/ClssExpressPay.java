package com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssexpresspay.trans;

import android.os.ConditionVariable;
import android.util.Log;

import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.model.EntryOutParam;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.model.TransResult;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.trans.ClssEntryPoint;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.pay.trans.callback.TransCallback;
import com.pax.jemv.amex.api.ClssAmexApi;
import com.pax.jemv.amex.model.CLSS_AEAIDPARAM;
import com.pax.jemv.amex.model.Clss_AddReaderParam_AE;
import com.pax.jemv.amex.model.Clss_ReaderParam_AE;
import com.pax.jemv.amex.model.ONLINE_PARAM;
import com.pax.jemv.amex.model.TransactionMode;
import com.pax.jemv.clcommon.ACType;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.Clss_PreProcInfo;
import com.pax.jemv.clcommon.Clss_PreProcInterInfo;
import com.pax.jemv.clcommon.Clss_ReaderParam;
import com.pax.jemv.clcommon.Clss_TransParam;
import com.pax.jemv.clcommon.CvmType;
import com.pax.jemv.clcommon.DDAFlag;
import com.pax.jemv.clcommon.EMV_CAPK;
import com.pax.jemv.clcommon.EMV_REVOCLIST;
import com.pax.jemv.clcommon.OnlineResult;
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.clcommon.TransactionPath;
import com.pax.jemv.entrypoint.api.ClssEntryApi;

import java.util.Arrays;

import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils.str2Bcd;


//import com.pax.jemv.clcommon.Clss_VisaAidParam;
//import com.pax.jemv.clssexpresspay.trans.ClssExpressPay;

/**
 * Created by yanglj on 2017-07-17.
 */

public class ClssExpressPay {
    private static final String TAG = "ClssExpressPay";

    private Clss_TransParam transParam;
    private TransactionPath transPath = new TransactionPath();
    private DDAFlag ddaFlag = new DDAFlag();
    private CvmType cvmType = new CvmType();
    private Clss_PreProcInfo procInfo;
    private CLSS_AEAIDPARAM aidParam;
    private ONLINE_PARAM ptOnlineParam;
    private TransactionMode transactionMode;

    private static ClssExpressPay instance;

    private TransCallback callback;
    private ConditionVariable cv;
    private boolean isFullOnline = false;
    private ClssEntryPoint entryPoint = ClssEntryPoint.getInstance();

    public void setCallback(TransCallback callback) {
        this.callback = callback;
    }

    public void setResult(int ret) {
        if (cv != null) {
            cv.open();
        }
    }

    public static ClssExpressPay getInstance() {
        if (instance == null) {
            instance = new ClssExpressPay();
        }
        return instance;
    }

    //    public  int getTransPath() {
//        return transPath.path;
//    }
    public int getTransPath() {
        return transactionMode.mode;
    }

    public int getDDAFlag() {
        return ddaFlag.flag;
    }

    public int getCVMType() {
        return cvmType.type;
    }

    /**
     * @return
     */
    public int coreInit() {
        return ClssAmexApi.Clss_CoreInit_AE();
    }

    public String readVersion() {
        ByteArray version = new ByteArray();
        ClssAmexApi.Clss_ReadVerInfo_AE(version);
        String entryVer = Arrays.toString(version.data);
        return entryVer.substring(0, version.length);
    }

    public int setConfigParam(CLSS_AEAIDPARAM aidParam, Clss_PreProcInfo procInfo) {

        int ret = RetCode.EMV_OK;

        transParam = entryPoint.getTransParam();

        this.aidParam = aidParam;
        this.procInfo = procInfo;

//        if (callback != null) {
//            appTornLogNum = callback.appLoadTornLog(tornLogRecords);
//            if (appTornLogNum > 5) {
//                appTornLogNum = 5;
//            }
//        }
        return ret;
    }

    public int expressProcess(TransResult transResult) {
        int ret;
        ACType acType = new ACType();

        EntryOutParam outParam = entryPoint.getOutParam();
        Clss_PreProcInterInfo interInfo = entryPoint.getInterInfo();
        ret = aeFlowBegin(outParam, interInfo, acType);
        if (ret != RetCode.EMV_OK) {
            if (RetCode.CLSS_RESELECT_APP == ret) {
                ret = ClssEntryApi.Clss_DelCurCandApp_Entry();
                if (ret != RetCode.EMV_OK) {
                    return ret;
                }
                ret = RetCode.CLSS_TRY_AGAIN;
                return ret;
            } else if ((ret == RetCode.CLSS_REFER_CONSUMER_DEVICE)) {//see phone
                return ret;
            } else if (ret == (RetCode.EMV_OK + 1)) {
                return RetCode.EMV_OK;
            }
            return ret;
        }
        amexFlowAfterGPO(acType, transResult);
        return ret;
    }


    private int amexFlowAfterGPO(ACType acType, TransResult transResult) {
        cvmType.type = ClssAmexApi.Clss_GetCvmType_AE();

        if (acType.type == ACType.AC_AAC) {
            transResult.result = TransResult.EMV_OFFLINE_DENIED;
            return RetCode.EMV_DENIAL;
        }

        if (acType.type == ACType.AC_ARQC) {
            transResult.result = TransResult.EMV_ARQC;
        } else {
            transResult.result = TransResult.EMV_OFFLINE_APPROVED;
        }
        return RetCode.EMV_OK;
    }


    private int aeFlowBegin(EntryOutParam outParam, Clss_PreProcInterInfo interInfo, ACType actype) {

        int ret;

        //setReaderParam and setAddReaderParam should call before setFinalSelectData.--added by Barret.
        ret = clssBaseParameterSet();
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "clssBaseParameterSet error, ret = " + ret);
            return ret;
        }

        ret = ClssAmexApi.Clss_SetFinalSelectData_AE(outParam.sDataOut, outParam.iDataLen);
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssAmexApi.Clss_SetFinalSelectData_AE(outParam.sDataOut, outParam.iDataLen) error, ret = " + ret);
            return ret;
        }

        ret = ClssAmexApi.Clss_SetTransData_AE(transParam, interInfo);
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssAmexApi.Clss_SetTransData_AE(transParam, interInfo) error, ret = " + ret);
            return ret;
        }

        transactionMode = new TransactionMode();
        ret = ClssAmexApi.Clss_Proctrans_AE(transactionMode);
        if (ret != RetCode.EMV_OK) {
            if (ret == RetCode.CLSS_RESELECT_APP) { // GPO
                ret = ClssEntryApi.Clss_DelCurCandApp_Entry();
                if (ret == RetCode.EMV_OK) {
                    ret = RetCode.CLSS_TRY_AGAIN;
                }
            }
            Log.e(TAG, "ClssAmexApi.Clss_DelCurCandApp_Entry() error, ret = " + ret);
            return ret;
        }

        ByteArray optimizeFlag = new ByteArray();
        ret = ClssAmexApi.Clss_ReadRecord_AE(optimizeFlag);
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssAmexApi.Clss_ReadRecord_AE(optimizeFlag) error, ret = " + ret);
            return ret;
        }

        ret = setCAPK();
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssAmexApi.Clss_ReadRecord_AE(optimizeFlag) error, ret = " + ret);
            return ret;
        }

        ret = ClssAmexApi.Clss_CardAuth_AE();
        //returns	EMV_OK	EMV_DATA_ERR
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssAmexApi.Clss_CardAuth_AE() error, ret = " + ret);
            return ret;
        }

        ByteArray adviceFlag = new ByteArray();
        ByteArray onlineFlagByte = new ByteArray();
        ret = ClssAmexApi.Clss_StartTrans_AE((byte) 0, adviceFlag, onlineFlagByte);
        Log.i(TAG, "ClssAmexApi.Clss_StartTrans_AE() ret = " + ret);

        ByteArray tmTransCapa = new ByteArray();
        ClssAmexApi.Clss_GetTLVData_AE((short) 0x9F6E, tmTransCapa);

        boolean onlineFlag = onlineFlagByte.data[0] == 1;
        if (ret == RetCode.EMV_OK) {
            if (!onlineFlag || (transactionMode.mode == TransactionMode.AE_MAGMODE) || ((tmTransCapa.data[0] & 0x20) == 0)) {
                if (callback != null) {
                    callback.removeCardPrompt();
                }
            }
            actype.type = onlineFlag ? ACType.AC_ARQC : ACType.AC_TC;
        } else if ((ret == RetCode.CLSS_CVMDECLINE) || (ret == RetCode.EMV_DENIAL)) {
            if (!onlineFlag || (transactionMode.mode == TransactionMode.AE_MAGMODE) || ((tmTransCapa.data[0] & 0x20) == 0)) {
                if (callback != null) {
                    callback.removeCardPrompt();
                }
            }
            actype.type = ACType.AC_AAC;
            ret = RetCode.CLSS_DECLINE;
            return ret;
        } else if (ret == RetCode.CLSS_REFER_CONSUMER_DEVICE) {
            if (!onlineFlag || (transactionMode.mode == TransactionMode.AE_MAGMODE) || ((tmTransCapa.data[0] & 0x20) == 0)) {
                // prompt see phone
                if (callback != null) {
                    callback.removeCardPrompt();
                    ret = callback.displaySeePhone();
                    if (ret != 0) {
                        actype.type = ACType.AC_AAC;
                        ret = RetCode.EMV_USER_CANCEL;
                        return ret;
                    }
                }
            }
            actype.type = ACType.AC_AAC;
            ret = RetCode.CLSS_REFER_CONSUMER_DEVICE;
            return ret;
        } else {
            if (!onlineFlag || (transactionMode.mode == TransactionMode.AE_MAGMODE) || ((tmTransCapa.data[0] & 0x20) == 0)) {
                //full online not supported
                // prompt transaction decline;
                // prompt remove card!!!!
                if (callback != null) {
                    callback.removeCardPrompt();
                }
            }
        }
        //Delayed Authorization
        if (onlineFlag) {
            Clss_AddReaderParam_AE clssAddReaderParamAE = new Clss_AddReaderParam_AE(new byte[4], (byte) 0, new byte[27]);
            ClssAmexApi.Clss_GetAddReaderParam_AE(clssAddReaderParamAE);

            //ByteArray ptOnlineParam = new ByteArray();
            if (clssAddReaderParamAE.ucDelayAuthFlag == 1) {
                ptOnlineParam = new ONLINE_PARAM();
                System.arraycopy("00", 0, ptOnlineParam.aucRspCode, 0, 2);
                ptOnlineParam.nAuthCodeLen = 6;
                ptOnlineParam.aucAuthCode = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
                ptOnlineParam.aucIAuthData = new byte[]{(byte) 0x00};
                ptOnlineParam.nIAuthDataLen = 0;
                ptOnlineParam.aucScript = new byte[]{(byte) 0x00};
                ptOnlineParam.nScriptLen = 0;
                ret = ClssAmexApi.Clss_CompleteTrans_AE((byte) OnlineResult.ONLINE_APPROVE, (byte) TransactionMode.AE_DELAYAUTH_PARTIALONLINE, ptOnlineParam, adviceFlag);
            }
            if ((tmTransCapa.data[0] & 0x20) != 0 && transactionMode.mode == TransactionMode.AE_EMVMODE) {
                if (callback != null) {
                    callback.removeCardPrompt();
                }
                isFullOnline = false;
            }
        }
        return ret;
    }

    private int clssBaseParameterSet() {
        int ret;
        Clss_ReaderParam_AE szReaderParam = new Clss_ReaderParam_AE();
//        ret = ClssAmexApi.Clss_GetReaderParam_AE(szReaderParam);
//        Log.i("AEGetReaderParam", "ret = " + ret);
//        if (ret != RetCode.EMV_OK) {
//            Log.e(TAG, "ClssAmexApi.Clss_GetReaderParam_AE(szReaderParam) error, ret = "+ ret);
//            return ret;
//        }

        szReaderParam.stReaderParam = new Clss_ReaderParam();
        szReaderParam.stReaderParam.ulReferCurrCon = 0L;
        szReaderParam.stReaderParam.aucMchNameLoc = "TEST DEMO MERCHANT".getBytes();
        szReaderParam.stReaderParam.usMchLocLen = (short) "TEST DEMO MERCHANT".length();
        szReaderParam.stReaderParam.aucMerchCatCode = str2Bcd("0000");
        szReaderParam.stReaderParam.aucMerchantID = "123456789012345".getBytes();
        szReaderParam.stReaderParam.acquierId = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56};

        szReaderParam.stReaderParam.aucTmID = "12345678".getBytes();
        szReaderParam.stReaderParam.ucTmType = 0x22;
        szReaderParam.stReaderParam.aucTmCap = str2Bcd("E068C8");
        szReaderParam.stReaderParam.aucTmCapAd = str2Bcd("E000F0A001");
        szReaderParam.stReaderParam.aucTmCntrCode = new byte[]{(byte) 0x08, (byte) 0x40};
        szReaderParam.stReaderParam.aucTmTransCur = new byte[]{(byte) 0x08, (byte) 0x40};
        szReaderParam.stReaderParam.ucTmTransCurExp = 0x02;
        szReaderParam.stReaderParam.aucTmRefCurCode = new byte[]{(byte) 0x08, (byte) 0x40};
        ;
        szReaderParam.stReaderParam.ucTmRefCurExp = 0x02;
        szReaderParam.aucUNRange = new byte[]{(byte) 0x00, (byte) 0x60};
        szReaderParam.ucTmSupOptTrans = 1;
        ret = ClssAmexApi.Clss_SetReaderParam_AE(szReaderParam);
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssAmexApi.Clss_SetReaderParam_AE(szReaderParam) error, ret = " + ret);
            return ret;
        }

        //Set Additional Param
        Clss_AddReaderParam_AE clssAddReaderParamAE = new Clss_AddReaderParam_AE(new byte[4], (byte) 0, new byte[27]);
        ClssAmexApi.Clss_GetAddReaderParam_AE(clssAddReaderParamAE);
        clssAddReaderParamAE.aucTmTransCapa = new byte[]{(byte) 58, (byte) 0xE0, (byte) 0x40, (byte) 0x00};
        clssAddReaderParamAE.ucDelayAuthFlag = 0;
        ret = ClssAmexApi.Clss_SetAddReaderParam_AE(clssAddReaderParamAE);
        Log.i(TAG, "Clss_SetAddReaderParam_AE = " + ret);

        if (aidParam != null) {
            ret = ClssAmexApi.Clss_SetAEAidParam_AE(aidParam);
            if (ret != RetCode.EMV_OK) {
                return ret;
            }
        }
        return ret;
    }

    private int setCAPK() {

        int iRet = -1;
        EMV_CAPK stEMVCapk = new EMV_CAPK();
        ByteArray sAid = new ByteArray();
        ByteArray ucKeyIndex = new ByteArray();
        EMV_REVOCLIST tRevocList = new EMV_REVOCLIST();

        ClssAmexApi.Clss_DelAllRevocList_AE();
        ClssAmexApi.Clss_DelAllCAPK_AE();

        iRet = ClssAmexApi.Clss_GetTLVData_AE((short) 0x9F06, sAid);
        if (iRet != RetCode.EMV_OK) {
            Log.e(TAG, "ClssAmexApi.Clss_GetTLVData_AE((short) 0x9F06, sAid) error, ret = " + iRet);
            return iRet;
        }
        //Log.i(TAG, "ClssAmexApi.Clss_GetTLVData_AE((short) 0x9F06, sAid) = "+ bcd2Str(sAid.data));

        iRet = ClssAmexApi.Clss_GetTLVData_AE((short) 0x8F, ucKeyIndex);
        if (iRet != RetCode.EMV_OK) {
            Log.e(TAG, "ClssWaveApi.clssWaveGetTLVData((short) 0x8F, ucKeyIndex) error, ret = " + iRet);
            return iRet;
        }
        //Log.i(TAG,"ucKeyIndex.data[0] = "+bcd2Str(ucKeyIndex.data));

        if (callback != null) {
            iRet = callback.getCapk(sAid.data, ucKeyIndex.data[0], stEMVCapk);
            if (iRet != RetCode.EMV_OK) {
                Log.e(TAG, "callback.getCapk error, ret = " + iRet);
                return iRet;
            }
        }

        iRet = ClssAmexApi.Clss_AddCAPK_AE(stEMVCapk);

        if (RetCode.EMV_OK != iRet) {
            Log.e(TAG, "ClssWaveApi.clssWaveAddCapk(stEMVCapk) error, ret = " + iRet);
        }
        System.arraycopy(sAid.data, 0, tRevocList.ucRid, 0, 5);
        tRevocList.ucIndex = ucKeyIndex.data[0];
        System.arraycopy(new byte[]{0x00, 0x07, 0x11}, 0, tRevocList.ucCertSn, 0, 3);
        iRet = ClssAmexApi.Clss_AddRevocList_AE(tRevocList);
        if (RetCode.EMV_OK != iRet) {
            Log.e(TAG, "ClssWaveApi.Clss_AddRevocList_Wave(stEMVCapk) , ret = " + iRet);
        }

        return iRet;
    }

    public int amexFlowComplete(int result, byte[] aucRspCode, byte[] aucAuthCode, byte[] sAuthData, int sgAuthDataLen, byte[] sIssuerScript, int sgScriptLen) {
        int ucOnlineMode;

        if (sgAuthDataLen == 0 && sgScriptLen == 0) {
            return RetCode.EMV_NO_DATA;
        }
        int onlineResult;
        if (result == TransResult.EMV_ABORT_TERMINATED) {
            onlineResult = OnlineResult.ONLINE_FAILED;
        } else if (result == TransResult.EMV_ONLINE_APPROVED) {
            onlineResult = OnlineResult.ONLINE_APPROVE;
        } else if (!Arrays.equals(Arrays.copyOf(aucRspCode, 2), "89".getBytes()))
            onlineResult = OnlineResult.ONLINE_ABORT;
        else
            onlineResult = OnlineResult.ONLINE_DENIAL;

        ucOnlineMode = TransactionMode.AE_PARTIALONLINE;
        ByteArray tmTransCapa = new ByteArray();
        ClssAmexApi.Clss_GetTLVData_AE((short) 0x9F6E, tmTransCapa);
        if ((tmTransCapa.data[0] & 0x20) != 0) {//full online supported
            ucOnlineMode = isFullOnline ? TransactionMode.AE_FULLONLINE : TransactionMode.AE_PARTIALONLINE;
        }
        ONLINE_PARAM onlineParam = new ONLINE_PARAM();
        System.arraycopy(aucRspCode, 0, onlineParam.aucRspCode, 0, 2);
        onlineParam.nAuthCodeLen = aucAuthCode.length;
        System.arraycopy(aucAuthCode, 0, onlineParam.aucAuthCode, 0, 6);
        System.arraycopy(sAuthData, 0, onlineParam.aucIAuthData, 0, sgAuthDataLen);
        onlineParam.nIAuthDataLen = sgAuthDataLen;
        System.arraycopy(sIssuerScript, 0, onlineParam.aucScript, 0, sgScriptLen);
        onlineParam.nScriptLen = sgScriptLen;
        ByteArray adviceFlag = new ByteArray();
        int ret = ClssAmexApi.Clss_CompleteTrans_AE((byte) onlineResult, (byte) ucOnlineMode, onlineParam, adviceFlag);
        return ret;
    }


}
