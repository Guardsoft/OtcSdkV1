package com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssjspeedy;

import android.util.Log;

import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.model.EntryOutParam;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.model.TransResult;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.trans.ClssEntryPoint;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssjspeedy.model.Clss_JcbAidParam;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.pay.trans.callback.TransCallback;
import com.pax.jemv.clcommon.ACType;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.Clss_PreProcInfo;
import com.pax.jemv.clcommon.Clss_ReaderParam;
import com.pax.jemv.clcommon.Clss_TransParam;
import com.pax.jemv.clcommon.CvmType;
import com.pax.jemv.clcommon.DDAFlag;
import com.pax.jemv.clcommon.EMV_CAPK;
import com.pax.jemv.clcommon.EMV_REVOCLIST;
import com.pax.jemv.clcommon.OutcomeParam;
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.clcommon.TransactionPath;
import com.pax.jemv.entrypoint.api.ClssEntryApi;
import com.pax.jemv.jcb.api.ClssJCBApi;

import java.util.Arrays;

import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils.bcd2Str;
import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils.str2Bcd;


/**
 * Created by yanglj on 2017-11-21.
 */

public class ClssJSpeedy {
    private static final String TAG = "ClssJSpeedy";

    private Clss_TransParam transParam;
    private TransactionPath transPath = new TransactionPath();
    private DDAFlag ddaFlag = new DDAFlag();
    private CvmType cvmType = new CvmType();
    private Clss_PreProcInfo procInfo;
    private Clss_ReaderParam szReaderParam;
    private Clss_JcbAidParam aidParam;

    private static ClssJSpeedy instance;

    private TransCallback callback;
    //private ConditionVariable cv;
    private ClssEntryPoint entryPoint = ClssEntryPoint.getInstance();

    public void setCallback(TransCallback callback) {
        this.callback = callback;
    }

    public static ClssJSpeedy getInstance() {
        if (instance == null) {
            instance = new ClssJSpeedy();
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

    public int coreInit() {
        return ClssJCBApi.Clss_CoreInit_JCB();
    }

    /**
     * @return
     */
    public String readVersion() {
        ByteArray version = new ByteArray();
        ClssJCBApi.Clss_ReadVerInfo_JCB(version);
        String entryVer = Arrays.toString(version.data);
        return entryVer.substring(0, version.length);
    }

    /**
     * @param procInfo
     * @return
     */
    public int setConfigParam(Clss_JcbAidParam aidParam, Clss_PreProcInfo procInfo) {

        this.transParam = entryPoint.getTransParam();
        this.procInfo = procInfo;
        this.aidParam = aidParam;

        return RetCode.EMV_OK;
    }


    public int jspeedyProcess(TransResult transResult) {
        int ret;

        EntryOutParam outParam = entryPoint.getOutParam();
        //Clss_PreProcInterInfo interInfo = entryPoint.getInterInfo();
        ret = jcbFlowBegin(outParam);
        if (ret != RetCode.EMV_OK) {
            if (ret == RetCode.CLSS_RESELECT_APP) {
                ret = ClssEntryApi.Clss_DelCurCandApp_Entry();
                if (ret != RetCode.EMV_OK) {
                    return ret;
                }
                ret = RetCode.CLSS_TRY_AGAIN;
                return ret;
            } else if (ret == RetCode.CLSS_REFER_CONSUMER_DEVICE) {//see phone
                if (callback != null) {
                    callback.removeCardPrompt();      //DEVICE_Beep("6:200",BEEP)
                    ret = callback.displaySeePhone();
                    if (ret != 0) {
                        ret = RetCode.EMV_USER_CANCEL;
                    } else
                        ret = RetCode.CLSS_REFER_CONSUMER_DEVICE;
                }
                return ret;
            } else if (ret == (RetCode.EMV_OK + 1)) {
                return RetCode.EMV_OK;
            }
            return ret;
        }
        ret = JcbFlowAfterGPO(transResult);
        return ret;
    }

    private int JcbFlowAfterGPO(TransResult transResult) {
        int ret;
        int iLen = 0;
        ACType ucOutACType = new ACType();

        ret = getCardAuthResultJcb(ucOutACType, cvmType);
        if (ret == RetCode.EMV_OK) {
            if (ucOutACType.type == ACType.AC_ARQC) {
                transResult.result = TransResult.EMV_ARQC;
                //return EMV_PayPassOnlineProc(pucTransResult);
            } else {
                transResult.result = TransResult.EMV_OFFLINE_APPROVED;
                // return EMV_PayPassOfflineProc(pucTransResult);
            }
        }
        return ret;
    }


    public int jcbFlowBegin(EntryOutParam outParam) {
        int ret;

        // ret = clssBaseParameterSet();
        Clss_ReaderParam szReaderParam = new Clss_ReaderParam();
        //Gillian 20170511
        szReaderParam.acquierId = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56};
        szReaderParam.ucTmType = 0x22;
        szReaderParam.aucTmCap = new byte[]{(byte) 0xE0, (byte) 0x68, (byte) 0xC8};
        szReaderParam.aucTmCntrCode = new byte[]{(byte) 0x08, (byte) 0x40};
        szReaderParam.aucTmTransCur = new byte[]{(byte) 0x08, (byte) 0x40};
        szReaderParam.ucTmTransCurExp = 0x02;
        szReaderParam.aucMchNameLoc = new byte[]{0x00};
        szReaderParam.usMchLocLen = 1;
        szReaderParam.aucMerchCatCode = new byte[]{0x00, 0x00};

        Log.i(TAG, "outParam.sDataOut = " + bcd2Str(outParam.sDataOut, outParam.iDataLen)); //Gillian debug
        Log.i(TAG, "outParam.iDataLen = " + outParam.iDataLen); //Gillian debug
        ret = ClssJCBApi.Clss_SetFinalSelectData_JCB(outParam.sDataOut, outParam.iDataLen);
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssJCBApi.Clss_SetFinalSelectData_JCB(outParam.sDataOut, outParam.iDataLen) error, ret = " + ret);
            return ret;
        }
        ;

        setTransParamJcb(transParam);
        setReaderParam(szReaderParam);
        setParamByAidJcb(aidParam, procInfo);

//        ByteArray byteArray= new ByteArray();
//        int iret =  ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte)0xFF, (byte)0x81,0x30},(byte) 3,2, byteArray);
//        if (iret == 0) {
//            byte[] a = new byte[byteArray.length];
//            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
//            String tmp = bcd2Str(a);
//            Log.i(TAG, "Clss_GetTLVDataList_JCB 1 0xFF8130 = " + tmp);
//        }


        ret = ClssJCBApi.Clss_InitiateApp_JCB(transPath);
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssJCBApi.Clss_InitiateApp_JCB(transPath) error, ret = " + ret);
            return ret;
        }

        Log.i(TAG, "ClssJCBApi transPath = " + transPath.path);

        ret = ClssJCBApi.Clss_ReadData_JCB();
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssJCBApi.Clss_ReadData_JCB() error, ret = " + ret);
            return ret;
        }

        ret = appTransProc((byte) transPath.path);
        Log.i(TAG, "appTransProc ret = " + ret);

        return ret;
    }

    private void setTransParamJcb(Clss_TransParam transParam) {

        byte[] amtAuth = str2Bcd(String.format("%012d", transParam.ulAmntAuth));
        byte[] amtOther = str2Bcd(String.format("%012d", transParam.ulAmntOther));
        //byte[] transNo = str2Bcd(String.format("%08d", transParam.ulTransNo));
        Log.i(TAG, "transParam.ulAmntAuth = " + String.format("%012d", transParam.ulAmntAuth));
        Log.i(TAG, "transParam.ulAmntOther = " + String.format("%012d", transParam.ulAmntOther));
        //Log.i(TAG, "transParam.ulTransNo = "+ String.format("%08d", transParam.ulTransNo));

        TransParamTable table[] =
                {
                        new TransParamTable(new byte[]{(byte) 0x9A}, 1, transParam.aucTransDate, 3),
                        new TransParamTable(new byte[]{(byte) 0x9F, 0x21}, 2, transParam.aucTransTime, 3),
                        new TransParamTable(new byte[]{(byte) 0x9C}, 1, new byte[]{transParam.ucTransType}, 1),
                        new TransParamTable(new byte[]{(byte) 0x9F, 0x02}, 2, amtAuth, 6),
                        new TransParamTable(new byte[]{(byte) 0x9F, 0x03}, 2, amtOther, 6),
//			new TransParamTable(new byte[]{(byte)0x9F, 0x15}, 2, transNo,    					 4),
                };

        for (TransParamTable transParamTable : table) {
            setDETData(transParamTable.tag, transParamTable.tag_len, transParamTable.value, transParamTable.value_len);
        }
    }

    private int setReaderParam(Clss_ReaderParam readerParam) {

        int ret = RetCode.EMV_OK;

        TransParamTable table[] = {
                new TransParamTable(new byte[]{(byte) 0x9F, 0x4E}, 2, readerParam.aucMchNameLoc, readerParam.usMchLocLen),//00 Merchant Name and Location
                new TransParamTable(new byte[]{(byte) 0x9F, 0x15}, 2, readerParam.aucMerchCatCode, 2),//00 00 Merchant Category Code
                new TransParamTable(new byte[]{(byte) 0x9F, 0x01}, 2, readerParam.acquierId, 6),//"\x00\x00\x00\x12\x34\x56"
                new TransParamTable(new byte[]{(byte) 0x9F, 0x1A}, 2, readerParam.aucTmCntrCode, 2),//0344
                new TransParamTable(new byte[]{(byte) 0x9F, 0x35}, 2, new byte[]{readerParam.ucTmType}, 1),
                new TransParamTable(new byte[]{0x5F, 0x2A}, 2, readerParam.aucTmTransCur, 2),//0344
                new TransParamTable(new byte[]{0x5F, 0x36}, 2, new byte[]{readerParam.ucTmTransCurExp}, 1),//02
                //new TransParamTable(new byte[]{(byte) 0x9F,0x33}, 2, readerParam.aucTmCap, 3),//E0F0C8

                new TransParamTable(new byte[]{(byte) 0x9F, 0x53}, 2, new byte[]{(byte) 0xF2, (byte) 0x80, (byte) 0x00}, 3),//F28000
                new TransParamTable(new byte[]{(byte) 0x9F, 0x52}, 2, new byte[]{(byte) 0x03}, 1),//F28000

                //new TransParamTable(new byte[]{(byte) 0xFF, (byte) 0x81, 0x30}, 3, new byte[]{0x3B, 0x00 }, 2),//00
        };

        for (TransParamTable transParamTable : table) {
            ret = setDETData(transParamTable.tag, transParamTable.tag_len, transParamTable.value, transParamTable.value_len);
            if (ret != RetCode.EMV_OK) {
                break;
            }
        }

        return ret;
    }

    private int setParamByAidJcb(Clss_JcbAidParam aidParam, Clss_PreProcInfo procInfo) {
        byte[] aucBuff = new byte[64];

        if (aidParam != null) {

            setDETData(new byte[]{(byte) 0xDF, (byte) 0x81, 0x20}, 3, aidParam.tacDefault, 5);//TAC Default
            if (transParam != null && transParam.ucTransType == 0x20) {//refund rquired AAC
                setDETData(new byte[]{(byte) 0xDF, (byte) 0x81, 0x21}, 3, new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, 5);//TAC Denial
            } else {
                setDETData(new byte[]{(byte) 0xDF, (byte) 0x81, 0x21}, 3, aidParam.tacDenial, 5);//TAC Denial
            }
            setDETData(new byte[]{(byte) 0xDF, (byte) 0x81, 0x22}, 3, aidParam.tacOnline, 5);//TAC Online
        }
        setDETData(new byte[]{(byte) 0xFF, (byte) 0x81, 0x30}, 3, new byte[]{(byte) 0x3B, 0x00}, 2);//TAC Online

//        ByteArray byteArray= new ByteArray();
//        int ret =  ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte)0xFF, (byte)0x81,0x30},(byte) 3,2, byteArray);
//        if (ret == 0) {
//            byte[] a = new byte[byteArray.length];
//            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
//            String tmp = bcd2Str(a);
//            Log.i(TAG, "Clss_GetTLVDataList_JCB 0xFF8130 = " + tmp);
//        }

        if (procInfo != null) {
            if (transParam.ucTransType == 0x20) {
                setDETData(new byte[]{(byte) 0xDF, (byte) 0x81, 0x23}, 3, new byte[6], 6);
            } else {
                System.arraycopy(str2Bcd(String.format("%012d", procInfo.ulRdClssFLmt)), 0, aucBuff, 0, 6);
                setDETData(new byte[]{(byte) 0xDF, (byte) 0x81, 0x23}, 3, aucBuff, 6);
            }
            Log.i(TAG, "ulRdClssFLmt = " + String.format("%012d", procInfo.ulRdClssFLmt));

            System.arraycopy(str2Bcd(String.format("%012d", procInfo.ulRdClssTxnLmt)), 0, aucBuff, 0, 6);
            setDETData(new byte[]{(byte) 0xDF, (byte) 0x81, 0x24}, 3, aucBuff, 6);
            Log.i(TAG, "ulRdClssTxnLmt = " + String.format("%012d", procInfo.ulRdClssTxnLmt));

            System.arraycopy(str2Bcd(String.format("%012d", procInfo.ulRdCVMLmt)), 0, aucBuff, 0, 6);
            setDETData(new byte[]{(byte) 0xDF, (byte) 0x81, 0x26}, 3, aucBuff, 6);
            Log.i(TAG, "ulRdCVMLmt = " + procInfo.ulRdCVMLmt);

        }
        return 0;
    }

    private int setDETData(byte[] pucTag, int ucTagLen, byte[] pucData, int ucDataLen) {
        byte[] aucBuff = new byte[256];
        int ucBuffLen;
        int ret;

        if (pucTag == null || pucData == null) {
            return RetCode.CLSS_PARAM_ERR;
        }

        System.arraycopy(pucTag, 0, aucBuff, 0, ucTagLen);//Terminal Country Code
        ucBuffLen = ucTagLen;
        aucBuff[ucBuffLen++] = (byte) ucDataLen;
        System.arraycopy(pucData, 0, aucBuff, ucBuffLen, ucDataLen);
        ucBuffLen += ucDataLen;
        ret = ClssJCBApi.Clss_SetTLVDataList_JCB(aucBuff, ucBuffLen);
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "aucBuff = " + bcd2Str(aucBuff, ucBuffLen) + ", ucBuffLen = " + ucBuffLen);
            Log.e(TAG, "ClssJCBApi.Clss_SetTLVDataList_JCB(aucBuff, ucBuffLen), ret = " + ret);
        }

        return ret;
    }

    private int appTransProc(byte transPath) {
        int ret = 0;
        ByteArray ucPkIndex = new ByteArray();
        ByteArray aucAid = new ByteArray();
        //ACType ACTypeOut = new ACType();

        EMV_REVOCLIST tRevocList = new EMV_REVOCLIST();
        byte ucExceptFileFlg = 0;

        if (transPath == TransactionPath.CLSS_JCB_EMV) {// 0x06)
            ClssJCBApi.Clss_DelAllRevocList_JCB();
            ClssJCBApi.Clss_DelAllCAPK_JCB();

            if (ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x8F}, (byte) 1, 1, ucPkIndex) == 0) {
                if (ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{0x4F}, (byte) 1, 17, aucAid) == 0) {

                    setCAPK(ucPkIndex, aucAid);//修改放到检测卡前
                    System.arraycopy(aucAid.data, 0, tRevocList.ucRid, 0, 5);
                    tRevocList.ucIndex = ucPkIndex.data[0];
                    System.arraycopy(new byte[]{0x00, 0x07, 0x11}, 0, tRevocList.ucCertSn, 0, 3);
                    ret = ClssJCBApi.Clss_AddRevocList_JCB(tRevocList);
                    if (ret != RetCode.EMV_OK) {
                        Log.e(TAG, "ClssJCBApi.Clss_AddRevocList_JCB error, ret = " + ret);
                        return ret;
                    }
                }
            }

            ret = ClssJCBApi.Clss_TransProc_JCB(ucExceptFileFlg);
            if (ret != RetCode.EMV_OK) {
                Log.e(TAG, "EMV Clss_TransProc_JCB error, ret = " + ret);
                return ret;
            }

            ret = ClssJCBApi.Clss_CardAuth_JCB();
            Log.i(TAG, "ClssJCBApi.Clss_CardAuth_JCB ret = " + ret);
        } else {// 0x05)
            ret = ClssJCBApi.Clss_TransProc_JCB(ucExceptFileFlg);
            Log.i(TAG, "MAG or LEGACY ClssJCBApi.Clss_TransProc_JCB ret = " + ret);
        }
        //Gillian debug
        ByteArray byteArray = new ByteArray();
        byte[] a;
        int iRet;
        String TVR = null;
        iRet = ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte) 0x95}, (byte) 1, 10, byteArray);
        a = new byte[byteArray.length];
        System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
        TVR = bcd2Str(a);
        Log.i("Clss_TLV_MC iRet 0x95", iRet + "");
        Log.i("Clss_GetTLV_MC TVR 0x95", TVR + "");
        return ret;
    }

    private int getCardAuthResultJcb(ACType ucOutACType, CvmType g_ucCvmType) {

        int ret;
        byte[] szBuff = new byte[]{(byte) 0xDF, (byte) 0x81, 0x29};//Outcome Parameter
        ByteArray aucOutcomeParamSet_MC = new ByteArray();

        ret = ClssJCBApi.Clss_GetTLVDataList_JCB(szBuff, (byte) 3, 24, aucOutcomeParamSet_MC);
        if (ret == RetCode.EMV_OK) {
            switch (aucOutcomeParamSet_MC.data[3] & 0xF0) {
                case OutcomeParam.CLSS_OC_NO_CVM:
                    g_ucCvmType.type = CvmType.RD_CVM_NO;
                    break;
                case OutcomeParam.CLSS_OC_OBTAIN_SIGNATURE:
                    g_ucCvmType.type = CvmType.RD_CVM_SIG;
                    break;
                case OutcomeParam.CLSS_OC_ONLINE_PIN:
                    g_ucCvmType.type = CvmType.RD_CVM_ONLINE_PIN;
                    break;
                case OutcomeParam.CLSS_OC_CONFIRM_CODE_VER:
                    g_ucCvmType.type = CvmType.RD_CVM_CONSUMER_DEVICE;
                    break;
                default:
                    g_ucCvmType.type = CvmType.RD_CVM_NO;
                    break;
            }
        }
        switch (aucOutcomeParamSet_MC.data[0] & 0xF0) {
            case OutcomeParam.CLSS_OC_APPROVED:
                ucOutACType.type = ACType.AC_TC;
                ret = RetCode.EMV_OK;
                break;
            case OutcomeParam.CLSS_OC_DECLINED:
                ucOutACType.type = ACType.AC_AAC;
                ret = RetCode.CLSS_DECLINE;
                break;
            case OutcomeParam.CLSS_OC_ONLINE_REQUEST:
                ucOutACType.type = ACType.AC_ARQC;
                ret = RetCode.EMV_OK;
                break;
            case OutcomeParam.CLSS_OC_TRY_ANOTHER_INTERFACE:
                ret = RetCode.CLSS_USE_CONTACT;
                break;
            default://CLSS_OC_END_APPLICATION
                ret = RetCode.CLSS_TERMINATE;
                break;
        }
        return ret;
    }


    private int setCAPK(ByteArray ucKeyIndex, ByteArray sAid) {

        int iRet = -1;
        EMV_CAPK stEMVCapk = new EMV_CAPK();


        if (callback != null) {
            Log.i(TAG, "ucKeyIndex.data[0] = " + bcd2Str(ucKeyIndex.data));
            iRet = callback.getCapk(sAid.data, ucKeyIndex.data[0], stEMVCapk);
            if (iRet != RetCode.EMV_OK) {
                Log.e(TAG, "callback.getCapk error, ret = " + iRet);
                return iRet;
            }
        }

        iRet = ClssJCBApi.Clss_AddCAPK_JCB(stEMVCapk);

        if (RetCode.EMV_OK != iRet) {
            Log.e(TAG, "ClssPassApi.clssMcAddCAPKMChip(stEMVCapk) error, ret = " + iRet);
        }
        Log.e(TAG, "ClssPassApi.clssMcAddCAPKMChip(stEMVCapk) success");
        return iRet;
    }

    class TransParamTable {

        byte[] tag;
        int tag_len;
        byte[] value;
        int value_len;

        public TransParamTable(byte[] tag, int tag_len, byte[] value, int value_len) {
            this.tag = new byte[4];
            System.arraycopy(tag, 0, this.tag, 0, tag_len);
            this.tag_len = tag_len;
            this.value = new byte[256];
            System.arraycopy(value, 0, this.value, 0, value_len);
            this.value_len = value_len;
        }

    }

    public int jcbFlowComplete(byte[] sIssuerScript, int sgScriptLen) {
        int ret;

        if (sgScriptLen == 0) {
            return RetCode.EMV_NO_DATA;
        }

        ret = ClssJCBApi.Clss_IssuerUpdateProc_JCB(sIssuerScript, sgScriptLen);
        Log.e(TAG, "ClssJCBApi.Clss_IssuerUpdateProc_JCB() ret =" + ret);
        if (ret == RetCode.EMV_OK) {
            ACType ucOutACType = new ACType();

            ret = getCardAuthResultJcb(ucOutACType, cvmType);
            Log.i(TAG, "getCardAuthResultJcb ret =" + ucOutACType.type);
            Log.i(TAG, "getCardAuthResultJcb ucOutACType =" + ucOutACType.type);
            Log.i(TAG, "getCardAuthResultJcb cvmType =" + cvmType.type);
        }
        return ret;
    }

}
