package com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssquickpass.trans;

import android.util.Log;

import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.model.EntryOutParam;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.model.TransResult;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.trans.ClssEntryPoint;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.pay.trans.callback.TransCallback;
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
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.clcommon.TransactionPath;
import com.pax.jemv.qpboc.api.ClssPbocApi;
import com.pax.jemv.qpboc.model.Clss_PbocAidParam;
import com.pax.jemv.qpboc.model.Clss_PbocTornConfig;

import java.util.Arrays;

import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils.bcd2Str;
import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils.str2Bcd;


/**
 * Created by yanglj on 2017-08-17.
 */

public class ClssQuickPass {
    private static final String TAG = "ClssQuickPass";

    private Clss_TransParam transParam;
    private TransactionPath transPath = new TransactionPath();
    private DDAFlag ddaFlag = new DDAFlag();
    private CvmType cvmType = new CvmType();
    private Clss_PreProcInfo procInfo;
    private Clss_PbocAidParam aidParam;
    private Clss_ReaderParam szReaderParam;
    private static ClssQuickPass instance;
    private boolean isOffline;
    private long qpsLimit = 5999; // 59.99 until 59.99 it will not ask

    private TransCallback callback;
    //private ConditionVariable cv;
    private ClssEntryPoint entryPoint = ClssEntryPoint.getInstance();

    public void setCallback(TransCallback callback) {
        this.callback = callback;
    }

    public static ClssQuickPass getInstance() {
        if (instance == null) {
            instance = new ClssQuickPass();
        }
        return instance;
    }

    public int getDDAFlag() {
        return ddaFlag.flag;
    }

    public int getCVMType() {
        return cvmType.type;
    }

    public int getTransPath() {
        return transPath.path;
    }

    public Clss_ReaderParam getClss_ReaderParam() {
        return szReaderParam;
    }

    public int getClss_GPOData(ByteArray gpoData) {
        return ClssPbocApi.Clss_GetGPOData_Pboc(gpoData);
    }

    /**
     * @return
     */
    public int coreInit() {
        return ClssPbocApi.Clss_CoreInit_Pboc();
    }

    /**
     * @return
     */
    public String readVersion() {
        ByteArray version = new ByteArray();
        ClssPbocApi.Clss_ReadVerInfo_Pboc(version);
        String entryVer = Arrays.toString(version.data);
        return entryVer.substring(0, version.length);
    }

    void ClearExpiredTornLog() {
        ByteArray delTornFlag = new ByteArray();
        while (true) {
            int ret = ClssPbocApi.Clss_ClearTornLog_Pboc((byte) 2, delTornFlag);
            if (ret == RetCode.EMV_NO_DATA && delTornFlag.data[0] != 1) {
                return;
            }
        }
    }

    private int clssReaderParameterSet() {
        int ret;
        szReaderParam = new Clss_ReaderParam();
        szReaderParam.ulReferCurrCon = 0L;
        szReaderParam.aucMchNameLoc = "TEST DEMO MERCHANT".getBytes();
        szReaderParam.usMchLocLen = (short) "TEST DEMO MERCHANT".length();
        szReaderParam.aucMerchCatCode = str2Bcd("0000");
        szReaderParam.aucMerchantID = "123456789012345".getBytes();
        szReaderParam.acquierId = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56};

        szReaderParam.aucTmID = "12345678".getBytes();
        szReaderParam.ucTmType = 0x22;
        szReaderParam.aucTmCap = str2Bcd("E068C8");
        szReaderParam.aucTmCapAd = str2Bcd("E000F0A001");
        szReaderParam.aucTmCntrCode = new byte[]{(byte) 0x01, (byte) 0x56};
        szReaderParam.aucTmTransCur = new byte[]{(byte) 0x01, (byte) 0x56};
        szReaderParam.ucTmTransCurExp = 0x02;
        szReaderParam.aucTmRefCurCode = new byte[]{(byte) 0x08, (byte) 0x40};
        szReaderParam.ucTmRefCurExp = 0x02;
        ret = ClssPbocApi.Clss_SetReaderParam_Pboc(szReaderParam);
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssPbocApi.Clss_SetReaderParam_Pboc(szReaderParam) error, ret = " + ret);
            return ret;
        }
        return ret;
    }

    public int setConfigParam(Clss_PbocAidParam aidParam, Clss_PreProcInfo procInfo) {
        int ret;

        transParam = entryPoint.getTransParam();
        this.aidParam = aidParam;
        this.procInfo = procInfo;

        readVersion();

        ClssPbocApi.Clss_SetQUICSFlag_Pboc((byte) 2);
        Clss_PbocTornConfig pbocTornConfig = new Clss_PbocTornConfig(300, (short) 3, (short) 1, new byte[4]);
        ret = ClssPbocApi.Clss_TornSetConfig_Pboc(pbocTornConfig);
        Log.i(TAG, "Pboc_ornSetConfig ret = " + ret);
        if (ret != RetCode.EMV_OK) {
            return ret;
        }
        ClearExpiredTornLog();

        clssReaderParameterSet();

        //Set Additional Param
        if (aidParam != null) {
            ret = ClssPbocApi.Clss_SetPbocAidParam_Pboc(aidParam);
            Log.i(TAG, "Clss_SetPbocAidParam_Pboc = " + ret);
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

        ClssPbocApi.Clss_DelAllRevocList_Pboc();
        ClssPbocApi.Clss_DelAllCAPK_Pboc();

        iRet = ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x4F, sAid);
        if (iRet != RetCode.EMV_OK) {
            Log.e(TAG, "ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x4F, sAid) error, ret = " + iRet);
            return iRet;
        }
        Log.i(TAG, "ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x4F, sAid) = " + bcd2Str(sAid.data, 10));

        iRet = ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x8F, ucKeyIndex);
        if (iRet != RetCode.EMV_OK) {
            Log.e(TAG, "ClssPbocApi.Clss_GetTLVData_Pboc((short) 0x8F, ucKeyIndex) error, ret = " + iRet);
            return iRet;
        }
        Log.i(TAG, "ucKeyIndex.data[0] = " + bcd2Str(ucKeyIndex.data, 1));

        if (callback != null) {
            iRet = callback.getCapk(sAid.data, ucKeyIndex.data[0], stEMVCapk);
            if (iRet != RetCode.EMV_OK) {
                Log.e(TAG, "callback.getCapk error, ret = " + iRet);
                return iRet;
            }
        }

        iRet = ClssPbocApi.Clss_AddCAPK_Pboc(stEMVCapk);

        if (RetCode.EMV_OK != iRet) {
            Log.e(TAG, "ClssWaveApi.clssWaveAddCapk(stEMVCapk) error, ret = " + iRet);
        }
        System.arraycopy(sAid.data, 0, tRevocList.ucRid, 0, 5);
        tRevocList.ucIndex = ucKeyIndex.data[0];
        System.arraycopy(new byte[]{0x00, 0x07, 0x11}, 0, tRevocList.ucCertSn, 0, 3);
        iRet = ClssPbocApi.Clss_AddRevocList_Pboc(tRevocList);
        if (RetCode.EMV_OK != iRet) {
            Log.e(TAG, "ClssWaveApi.Clss_AddRevocList_Wave(stEMVCapk) , ret = " + iRet);
        }

        return iRet;
    }


    private int wholeTornProcess(ACType acType) {
        byte[] tornBuff = new byte[4];
        int ret = ClssPbocApi.Clss_TornProcessing_Pboc((byte) 0, tornBuff);
        Log.i(TAG, "tornBuff: " + bcd2Str(tornBuff));
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "Clss_TornProcessing_Pboc return " + ret);
            return ret;
        } else if (tornBuff[1] == 0) {
            ByteArray failFlag = new ByteArray(1);
            ret = ClssPbocApi.Clss_GetTornFailFlag_Pboc(failFlag);
            if (ret == RetCode.EMV_OK) {
                if (failFlag.data[0] == 2) {
                    ByteArray clearFlag = new ByteArray(1);
                    clearFlag.data[0] = 1;
                    while (clearFlag.data[0] == 1) {
                        ClssPbocApi.Clss_ClearTornLog_Pboc((byte) 0, clearFlag);
                    }
                }
            }
            return RetCode.EMV_OK;
        } else if (tornBuff[1] == 1) {
            isOffline = true;
            return offlineTornProcess(acType);
        }
        return RetCode.EMV_OK;
    }

    boolean isNeedSaveTorn = false;
    boolean isNeedDelTorn = false;

    private int offlineTornProcess(ACType acType) {
        int ret = 0;

        setCAPK();
        DDAFlag flag = new DDAFlag();
        ret = ClssPbocApi.Clss_CardAuth_Pboc(acType, flag);
        Log.i(TAG, "clssPbocCardAuth ret = " + ret + "acType = " + acType.type + "fddaFlag" + flag.flag);
        if (ret != RetCode.EMV_OK) {
            if (ret == RetCode.CLSS_USE_CONTACT) {
                return ret;
            }
            ByteArray tornFailFlag = new ByteArray(1);
            int ret1 = ClssPbocApi.Clss_GetTornFailFlag_Pboc(tornFailFlag);
            if (ret1 == RetCode.EMV_OK) {
                if (tornFailFlag.data[0] == 2) { //Application should delete the fail torn log
                    isNeedDelTorn = true;
                } else if (tornFailFlag.data[0] == 1) {//There is a fail torn log deleted and the data of the deleted torn log are saved in the
                    isNeedSaveTorn = true;                            // TLV database.
                }
            }
            return ret;
        } else if (flag.flag == DDAFlag.FAIL) {
            return RetCode.CLSS_TERMINATE;
        }

        if (acType.type == ACType.AC_AAC) {
            isNeedSaveTorn = false;
        } else if (acType.type == ACType.AC_TC) {
            isNeedDelTorn = true;
        } else if (acType.type == ACType.AC_ARQC) {
            isNeedSaveTorn = false;
        }

        ByteArray clearFlag = new ByteArray(1);
        clearFlag.data[0] = 1;
        while (clearFlag.data[0] == 1) {
            if (isNeedSaveTorn) {
                ClssPbocApi.Clss_ClearTornLog_Pboc((byte) 1, clearFlag);
            }
            if (isNeedDelTorn) {
                ClssPbocApi.Clss_ClearTornLog_Pboc((byte) 0, clearFlag);
            }
        }

        return RetCode.EMV_OK;
    }

    public int qPbocProcess(TransResult transResult) {

        Clss_PreProcInterInfo interInfo = entryPoint.getInterInfo();
        Log.i(TAG, "transParam.ucTransType ret = " + Integer.toHexString(transParam.ucTransType));
        Log.i(TAG, "transParam.ulAmntAuth ret = " + String.format("%012d", transParam.ulAmntAuth));
        int ret = ClssPbocApi.Clss_SetTransData_Pboc(transParam, interInfo);
        Log.i(TAG, "PbocSetTransData ret = " + ret);
        if (ret != RetCode.EMV_OK) {
            return ret;
        }

        EntryOutParam outParam = entryPoint.getOutParam();
        ret = ClssPbocApi.Clss_SetFinalSelectData_Pboc(outParam.sDataOut, outParam.iDataLen);
        Log.i(TAG, "PbocSetFinalSelectData ret = " + ret);
        if (ret != RetCode.EMV_OK) {
            return ret;
        }

        ACType acType = new ACType();
        isOffline = false;
        ret = wholeTornProcess(acType);
        if (isOffline) {
            if (callback != null) {
                callback.removeCardPrompt();
            }

            if ((ret == RetCode.CLSS_USE_CONTACT) || (ret == RetCode.CLSS_TERMINATE)) {
                return ret;
            }

            if (acType.type == ACType.AC_AAC) {
                return RetCode.CLSS_DECLINE;
            } else if ((acType.type == ACType.AC_TC) || (acType.type == ACType.AC_ARQC))
                return ret;
        }

        ret = ClssPbocApi.Clss_Proctrans_Pboc(transPath, acType);
        Log.i(TAG, "Clss_Proctrans_Pboc ret = " + ret);
        if (ret != RetCode.EMV_OK) {
            if (ret == RetCode.CLSS_LAST_CMD_ERR) {
                // TODO current tornProcess
                return ret;
            }
            if (ret == RetCode.CLSS_USE_CONTACT) {
                return ret;
            }
        }
        Log.i(TAG, "Clss_Proctrans_Pboc TransPath = " + transPath.path + ", ACType = " + acType.type);

        ret = qPbocFlowAfterGPO(acType, transResult);
        Log.i(TAG, "qPbocFlowAfterGPO ret = " + ret);

        if (transPath.path == TransactionPath.CLSS_VISA_VSDC) {
            if (callback != null) {
                callback.dontRemoveCard();
            }
            return ret;
        }

        if (callback != null) {
            callback.removeCardPrompt();
        }

        int rret = ClssPbocApi.Clss_GetCvmType_Pboc(cvmType);
        Log.i(TAG, "Clss_GetCvmType_Pboc CVMType = " + cvmType.type);
        if (rret < 0) {
            if (rret == RetCode.CLSS_PARAM_ERR) {
                transResult.result = TransResult.EMV_OFFLINE_DENIED;
            }
            return rret;
        }


        String DEBIT1 = "A000000333010101";
        String DEBIT2 = "A000000333010106";
        String CREDIT1 = "A000000333010102";
        String QCREDIT = "A000000333010103";

        boolean needPin = false;
        Log.i(TAG, "transParam.ulAmntAuth = " + transParam.ulAmntAuth + " procInfo.ulRdCVMLmt = " + procInfo.ulRdCVMLmt);
        String sAid = bcd2Str(ClssEntryPoint.getInstance().getOutParam().sAID, ClssEntryPoint.getInstance().getOutParam().iAIDLen);
        if (sAid.equals(CREDIT1) || sAid.equals(QCREDIT)) {
            if (cvmType.type == CvmType.RD_CVM_ONLINE_PIN) {
                //PED-10 support QPS
                if (transParam.ulAmntAuth > procInfo.ulRdCVMLmt || transParam.ulAmntAuth > qpsLimit) {
                    cvmType.type = CvmType.RD_CVM_ONLINE_PIN;
                    needPin = true;
                    Log.i(TAG, "Credit card need PIN");
                } else {
                    cvmType.type = CvmType.RD_CVM_NO;
                    needPin = false;
                    Log.i(TAG, "Credit card NO need PIN because of LIMIT");
                }
            }
        }

        if (sAid.equals(DEBIT1) || sAid.equals(DEBIT2)) {
            if (cvmType.type == CvmType.RD_CVM_ONLINE_PIN) {
                cvmType.type = CvmType.RD_CVM_ONLINE_PIN;
                needPin = true;
                Log.i(TAG, "Debit card need PIN");
            }
        }

        if (sAid.equals(CREDIT1) || sAid.equals(QCREDIT)) {
            if (cvmType.type == CvmType.RD_CVM_ONLINE_PIN
                    || cvmType.type == CvmType.RD_CVM_SIG) {
                //PED-10 support QPS
                if (transParam.ulAmntAuth > procInfo.ulRdCVMLmt || transParam.ulAmntAuth > qpsLimit) {
                    if (needPin) {
                        cvmType.type = CvmType.RD_CVM_ONLINE_PIN + CvmType.RD_CVM_SIG;
                    } else
                        cvmType.type = CvmType.RD_CVM_SIG;
                } else {
                    if (needPin) {
                        cvmType.type = CvmType.RD_CVM_ONLINE_PIN;
                    } else {
                        cvmType.type = CvmType.RD_CVM_NO;
                    }
                }
            }
        }
        if (sAid.equals(DEBIT1) || sAid.equals(DEBIT2)) {
            if (cvmType.type == CvmType.RD_CVM_SIG) {
                if (transParam.ulAmntAuth > procInfo.ulRdCVMLmt) {
                    if (needPin) {
                        cvmType.type = CvmType.RD_CVM_ONLINE_PIN + CvmType.RD_CVM_SIG;
                    } else
                        cvmType.type = CvmType.RD_CVM_SIG;
                }
            }
        }

        return ret;
    }

    int qPbocFlowAfterGPO(ACType acType, TransResult transResult) {
        int ret;

        if (transPath.path == TransactionPath.CLSS_VISA_QVSDC) {
            if (acType.type == ACType.AC_AAC) {
                return RetCode.CLSS_DECLINE;
            }
            ret = processQVSDC(acType, transResult);
            if (ret != RetCode.EMV_OK) {
                return ret;
            }
        } else if (transPath.path == TransactionPath.CLSS_VISA_VSDC) {
            ret = processVSDC(acType, transResult);
            if (ret != RetCode.EMV_OK) {
                return ret;
            }
        } else {
            return RetCode.CLSS_FAILED;
        }

        if (acType.type == ACType.AC_TC) {
            transResult.result = TransResult.EMV_OFFLINE_APPROVED;
        } else if (acType.type == ACType.AC_ARQC) {
            transResult.result = TransResult.EMV_ARQC;
        }
        return ret;
    }

    private int processQVSDC(ACType acType, TransResult transResult) {
        int ret = 0;

        if ((acType.type == com.pax.jemv.clcommon.ACType.AC_TC)
                && transParam.ucTransType != 0x20) { //no refund
            //according to EDC
            setCAPK();

            DDAFlag flag = new DDAFlag();
            ret = ClssPbocApi.Clss_CardAuth_Pboc(acType, flag);
            Log.i(TAG, "clssPbocCardAuth ret = " + ret);
            if (ret != RetCode.EMV_OK) {
                if (ret == RetCode.CLSS_USE_CONTACT) {
                    transResult.result = TransResult.EMV_ABORT_TERMINATED;
                    return ret;
                }

            } else if (flag.flag == DDAFlag.FAIL) {
                transResult.result = TransResult.EMV_ABORT_TERMINATED;
                return RetCode.CLSS_FAILED;
            }
        }
        return RetCode.EMV_OK;
    }

    private int processVSDC(ACType acType, TransResult transResult) {
//transResult        int ret = 0;
//        if ((acType.type == com.pax.jemv.clcommon.ACType.AC_TC)
//                && transParam.ucTransType != 0x20) { //no refund
//            //according to EDC
//            setCAPK();
//
//            DDAFlag flag = new DDAFlag();
//            ret = ClssPbocApi.Clss_CardAuth_Pboc(acType, flag);
//            Log.i(TAG, "clssPbocCardAuth ret = " + ret);
//            if (ret != RetCode.EMV_OK) {
//                transResult.result = TransResult.EMV_OFFLINE_DENIED;
//                return ret;
//            }
//        }
        return RetCode.EMV_OK;
    }

}