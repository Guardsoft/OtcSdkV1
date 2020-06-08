package com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clsspure.trans;

import android.util.Log;

import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.model.EntryOutParam;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.model.TransResult;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.trans.ClssEntryPoint;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clsspure.trans.model.Clss_PureAidParam;
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
import com.pax.jemv.clcommon.OutcomeParam;
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.clcommon.TransactionPath;
import com.pax.jemv.entrypoint.api.ClssEntryApi;
import com.pax.jemv.pure.api.ClssPUREApi;

import java.util.Arrays;

import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils.bcd2Str;
import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils.str2Bcd;


/**
 * Created by yanglj on 2017-11-21.
 */

public class CassPure {
    private static final String TAG = "CassPure";

    private Clss_TransParam transParam;
    private TransactionPath transPath = new TransactionPath();
    private DDAFlag ddaFlag = new DDAFlag();
    private CvmType cvmType = new CvmType();
    private Clss_PreProcInfo procInfo;
    private Clss_ReaderParam szReaderParam;
    private Clss_PureAidParam aidParam;
    private ClssTransData_Pure outComeParm = new ClssTransData_Pure();


    private static CassPure instance;
    private TransCallback callback;
    //private ConditionVariable cv;
    private ClssEntryPoint entryPoint = ClssEntryPoint.getInstance();

    public void setCallback(TransCallback callback) {
        this.callback = callback;
    }

    public static CassPure getInstance() {
        if (instance == null) {
            instance = new CassPure();
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
        return ClssPUREApi.Clss_CoreInit_PURE();
    }

    /**
     * @return
     */
    public String readVersion() {
        ByteArray version = new ByteArray();
        ClssPUREApi.Clss_ReadVerInfo_PURE(version);
        String entryVer = Arrays.toString(version.data);
        return entryVer.substring(0, version.length);
    }

    /**
     * @param procInfo
     * @return
     */
    public int setConfigParam(Clss_PureAidParam aidParam, Clss_PreProcInfo procInfo) {

        this.transParam = entryPoint.getTransParam();
        this.procInfo = procInfo;
        this.aidParam = aidParam;

        return RetCode.EMV_OK;
    }


    public int pureProcess(TransResult transResult) {
        int ret;

        EntryOutParam outParam = entryPoint.getOutParam();
        Clss_PreProcInterInfo interInfo = entryPoint.getInterInfo();
        ret = purFlowBegin(outParam, interInfo);
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
        ret = FlowAfterGPO(transResult);
        return ret;
    }

    private int FlowAfterGPO(TransResult transResult) {
        int ret;
        int iLen = 0;
        ACType ucOutACType = new ACType();

        ret = getCardAuthResult(ucOutACType, cvmType);
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


    public int purFlowBegin(EntryOutParam outParam, Clss_PreProcInterInfo preProcIntInfo) {
        int ret;

        // ret = clssBaseParameterSet();
        Clss_ReaderParam szReaderParam = new Clss_ReaderParam();
        //Gillian 20170511
        szReaderParam.acquierId = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56};
        szReaderParam.ucTmType = 0x22;
        szReaderParam.aucTmCap = new byte[]{(byte) 0xD0, (byte) 0x68, (byte) 0xC8};
        szReaderParam.aucTmCntrCode = new byte[]{(byte) 0x08, (byte) 0x40};
        szReaderParam.aucTmTransCur = new byte[]{(byte) 0x08, (byte) 0x40};
        szReaderParam.ucTmTransCurExp = 0x02;
        szReaderParam.aucMchNameLoc = new byte[]{0x00};
        szReaderParam.usMchLocLen = 1;
        szReaderParam.aucMerchCatCode = new byte[]{0x00, 0x00};

        //Log.i(TAG, "outParam.sDataOut = "+ bcd2Str(outParam.sDataOut, outParam.iDataLen)); //Gillian debug
        //Log.i(TAG, "outParam.iDataLen = "+  outParam.iDataLen); //Gillian debug
        ret = ClssPUREApi.Clss_SetFinalSelectData_PURE(outParam.sDataOut, outParam.iDataLen);
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssPUREApi.Clss_SetFinalSelectData_PURE(outParam.sDataOut, outParam.iDataLen) error, ret = " + ret);
            outComeParm.sendTransDataOutput((byte) 0x17);
            return ret;
        }
        ;

        setTransParamPure(transParam);
        setReaderParam(szReaderParam);
        setParamByAidPure(aidParam, procInfo);

//        ByteArray byteArray= new ByteArray();
//        int iret =  ClssJCBApi.Clss_GetTLVDataList_JCB(new byte[]{(byte)0xFF, (byte)0x81,0x30},(byte) 3,2, byteArray);
//        if (iret == 0) {
//            byte[] a = new byte[byteArray.length];
//            System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
//            String tmp = bcd2Str(a);
//            Log.i(TAG, "Clss_GetTLVDataList_JCB 1 0xFF8130 = " + tmp);
//        }
        ;

        ret = ClssPUREApi.Clss_InitiateApp_PURE(preProcIntInfo);
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssPUREApi.Clss_InitiateApp_PURE(transPath) error, ret = " + ret);
            outComeParm.sendTransDataOutput((byte) 0x17);
            return ret;
        }

        ret = ClssPUREApi.Clss_ReadData_PURE();
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssPUREApi.Clss_ReadData_PURE() error, ret = " + ret);
            outComeParm.sendTransDataOutput((byte) 0x17);
            return ret;
        }

        ret = appTransProc();
        Log.i(TAG, "appTransProc ret = " + ret);

        return ret;
    }

    private void setTransParamPure(Clss_TransParam transParam) {

        byte[] amtAuth = str2Bcd(String.format("%012d", transParam.ulAmntAuth));
        byte[] amtOther = str2Bcd(String.format("%012d", transParam.ulAmntOther));
        //byte[] transNo = str2Bcd(String.format("%08d", transParam.ulTransNo));
        //Log.i(TAG, "transParam.ulAmntAuth = "+ String.format("%012d", transParam.ulAmntAuth));
        //Log.i(TAG, "transParam.ulAmntOther = "+ String.format("%012d", transParam.ulAmntOther));
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
                new TransParamTable(new byte[]{(byte) 0x9F, 0x33}, 2, readerParam.aucTmCap, 3),//E0F0C8
                new TransParamTable(new byte[]{(byte) 0xFF, (byte) 0x81, 0x3A}, 3, new byte[]{0x01}, 1),//00
        };

        for (TransParamTable transParamTable : table) {
            ret = setDETData(transParamTable.tag, transParamTable.tag_len, transParamTable.value, transParamTable.value_len);
            if (ret != RetCode.EMV_OK) {
                break;
            }
        }

        return ret;
    }

    private int setParamByAidPure(Clss_PureAidParam aidParam, Clss_PreProcInfo procInfo) {
        if (aidParam != null) {

            setDETData(new byte[]{(byte) 0xDF, (byte) 0x81, 0x20}, 3, aidParam.tacDefault, 5);//TAC Default
            if (transParam != null && transParam.ucTransType == 0x20) {//refund rquired AAC
                setDETData(new byte[]{(byte) 0xDF, (byte) 0x81, 0x21}, 3, new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, 5);//TAC Denial
            } else {
                setDETData(new byte[]{(byte) 0xDF, (byte) 0x81, 0x21}, 3, aidParam.tacDenial, 5);//TAC Denial
            }
            setDETData(new byte[]{(byte) 0xDF, (byte) 0x81, 0x22}, 3, aidParam.tacOnline, 5);//TAC Online
            setDETData(new byte[]{(byte) 0x9F, 0x09}, 2, aidParam.Version, 2);
            setDETData(new byte[]{(byte) 0xFF, (byte) 0x81, 0x36}, 3, aidParam.dDOL, aidParam.dDolLen); //Default DDOL
            //setDETData(new byte[]{(byte) 0xFF,(byte) 0x81,0x31}, 3,	new byte[]{(byte) 0x8C, 0x57}, 2);//      MTOL
            setDETData(new byte[]{(byte) 0xFF, (byte) 0x81, 0x31}, 3, aidParam.mtDOL, aidParam.mtDolLen);//      MTOL
//            byte[] tmp= new byte[]{(byte)0x9F,0x02, (byte)0x9F,0x03, (byte)0x9F,0x26, (byte)0x82, (byte)0x9F,0x36, (byte)0x9F,0x27, (byte)0x9F,0x10, (byte)0x9F,0x1A, (byte)0x95, (byte)0x5F,0x2A,
//                    (byte)0x9A, (byte)0x9C, (byte)0x9F,0x37, (byte)0x9F,0x35, (byte)0x57, (byte)0x9F,0x34, (byte)0x84, (byte)0x5F,0x34, (byte)0x5A, (byte)0xC7, (byte)0x9F,0x33, (byte)0x9F,0x73, (byte)0x9F,0x77, (byte)0x9F,0x45};
//            setDETData(new byte[]{(byte) 0xFF,(byte) 0x81,0x30}, 3,	tmp, 40);//      ATOL
//            setDETData(new byte[]{(byte) 0xFF,(byte) 0x81,0x32}, 3,	new byte[]{(byte) 0x82, (byte)0x95, (byte)0x9F, 0x77, (byte)0x84}, 5);//      ATDTOL
            setDETData(new byte[]{(byte) 0xFF, (byte) 0x81, 0x30}, 3, aidParam.aTOL, aidParam.aTolLen);//      ATOL
            setDETData(new byte[]{(byte) 0xFF, (byte) 0x81, 0x32}, 3, aidParam.atdTOL, aidParam.atdTolLen);//      ATDTOL
            setDETData(new byte[]{(byte) 0xFF, (byte) 0x81, 0x34}, 3, aidParam.ioOption, 1);//     Contactless POS Implementation Option
            setDETData(new byte[]{(byte) 0xFF, (byte) 0x81, 0x35}, 3, aidParam.appAuthType, 1); //  Transaction Type value for Application Authentication Transaction
        }

        setDETData(new byte[]{(byte) 0xDF, (byte) 0x81, 0x27}, 3, new byte[]{(byte) 0x10, 0x00}, 2);//TAC Default
        setDETData(new byte[]{(byte) 0xFF, (byte) 0x81, 0x33}, 3, new byte[]{(byte) 0x26, 0x00, 0x60, 0x43, (byte) 0xF9}, 5);

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
        ret = ClssPUREApi.Clss_SetTLVDataList_PURE(aucBuff, ucBuffLen);
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "aucBuff = " + bcd2Str(aucBuff, ucBuffLen) + ", ucBuffLen = " + ucBuffLen);
            Log.e(TAG, " ClssPUREApi.Clss_SetTLVDataList_PURE(aucBuff, ucBuffLen), ret = " + ret);
        }

        return ret;
    }

    private int appTransProc() {
        int ret = 0;
        ByteArray ucPkIndex = new ByteArray();
        ByteArray aucAid = new ByteArray();
        //ACType ACTypeOut = new ACType();

        EMV_REVOCLIST tRevocList = new EMV_REVOCLIST();
        byte ucExceptFileFlg = 0;

        ClssPUREApi.Clss_DelAllRevocList_PURE();
        ClssPUREApi.Clss_DelAllCAPK_PURE();

        if (ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x8F}, (byte) 1, 1, ucPkIndex) == 0) {
            if (ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{0x4F}, (byte) 1, 17, aucAid) == 0) {

                setCAPK(ucPkIndex, aucAid);//修改放到检测卡前
                System.arraycopy(aucAid.data, 0, tRevocList.ucRid, 0, 5);
                tRevocList.ucIndex = ucPkIndex.data[0];
                System.arraycopy(new byte[]{0x00, 0x10, 0x00}, 0, tRevocList.ucCertSn, 0, 3);
                ret = ClssPUREApi.Clss_AddRevocList_PURE(tRevocList);
                if (ret != RetCode.EMV_OK) {
                    Log.e(TAG, "ClssPUREApi.Clss_AddRevocList_PURE error, ret = " + ret);
                    return ret;
                }
            }
        }
        ret = CheckExceptionFile();
        if (ret != RetCode.EMV_OK) {
            ucExceptFileFlg = 1;
        }

        ret = ClssPUREApi.Clss_StartTrans_PURE(ucExceptFileFlg);
        outComeParm.sendTransDataOutput((byte) 0x17);
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "EMV Clss_StartTrans_PURE error, ret = " + ret);
//            if ((outComeParm.outcomeParamSet.data[1] == OutcomeParam.CLSS_OC_B ) ||
//                (outComeParm.outcomeParamSet.data[0] == OutcomeParam.CLSS_OC_END_APPLICATION ) ||
//                (outComeParm.outcomeParamSet.data[0] == OutcomeParam.CLSS_OC_TRY_ANOTHER_INTERFACE ))
            return ret;
        }

        ClssPUREApi.Clss_CardAuth_PURE();
        //Log.i(TAG, "ClssPUREApi.Clss_CardAuth_PURE ret = "+ ret);
        outComeParm.sendTransDataOutput((byte) 0x17);

//        //Gillian debug
//        ByteArray byteArray = new ByteArray();
//        byte[] a;
//        int iRet;
//        String TVR = null;
//        iRet = ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte)0x95},(byte) 1,10,byteArray);
//        a = new byte[byteArray.length];
//        System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
//        TVR = bcd2Str(a);
//        Log.i("Clss_TLV_MC iRet 0x95", iRet + "");
//        Log.i("Clss_GetTLV_MC TVR 0x95", TVR + "");
        return ret;
    }

    private int getCardAuthResult(ACType ucOutACType, CvmType g_ucCvmType) {
        int ret;
        switch (outComeParm.outcomeParamSet.data[3] & 0xF0) {
            case OutcomeParam.CLSS_OC_NO_CVM:
                g_ucCvmType.type = CvmType.RD_CVM_NO;
                break;
            case OutcomeParam.CLSS_OC_OBTAIN_SIGNATURE:
                g_ucCvmType.type = CvmType.RD_CVM_SIG;
                break;
            case OutcomeParam.CLSS_OC_ONLINE_PIN:
                g_ucCvmType.type = CvmType.RD_CVM_ONLINE_PIN;
                break;
            default:
                g_ucCvmType.type = CvmType.RD_CVM_NO;
                break;
        }
        switch (outComeParm.outcomeParamSet.data[0] & 0xF0) {
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
            case OutcomeParam.CLSS_OC_END_APPLICATION:
                ret = RetCode.CLSS_FAILED;
                break;
            case OutcomeParam.CLSS_OC_SELECT_NEXT:
                ret = RetCode.CLSS_RESELECT_APP;
                break;
            case OutcomeParam.CLSS_OC_TRY_AGAIN:
                ret = RetCode.CLSS_TRY_AGAIN;
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
            //Log.i(TAG,"ucKeyIndex.data[0] = "+bcd2Str(ucKeyIndex.data));
            iRet = callback.getCapk(sAid.data, ucKeyIndex.data[0], stEMVCapk);
            if (iRet != RetCode.EMV_OK) {
                Log.e(TAG, "callback.getCapk error, ret = " + iRet);
                return iRet;
            }
        }

        iRet = ClssPUREApi.Clss_AddCAPK_PURE(stEMVCapk);

        if (RetCode.EMV_OK != iRet) {
            Log.e(TAG, "ClssPassApi.clssMcAddCAPKMChip(stEMVCapk) error, ret = " + iRet);
        }
        //Log.e(TAG, "ClssPassApi.clssMcAddCAPKMChip(stEMVCapk) success");
        return iRet;
    }

    private int CheckExceptionFile() {
        ByteArray ucPan = new ByteArray(12);
        ByteArray ucPanSeq = new ByteArray(1);
        int ret, ret2;
        byte[] bPan, bPanSeq;

        ret = ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x5A}, (byte) 1, 12, ucPan);
        ret2 = ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0x5F, 0x34}, (byte) 2, 1, ucPanSeq);
        if (ret < 0 || ret2 < 0)
            return RetCode.EMV_OK;
        bPan = new byte[ucPan.length];
        System.arraycopy(ucPan.data, 0, bPan, 0, ucPan.length);
        bPanSeq = new byte[ucPanSeq.length];
        System.arraycopy(ucPanSeq.data, 0, bPanSeq, 0, ucPanSeq.length);
        if (Arrays.equals(bPan, new byte[]{0x22, 0x22, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x90, (byte) 0xFF, (byte) 0xFF}) && bPanSeq[0] == 0x00)
            return -1;
        return 0;
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

    private class ClssTransData_Pure {
        ByteArray outcomeParamSet = new ByteArray(8);
        ByteArray userInterReqData = new ByteArray(22);
        ByteArray errIndication = new ByteArray(6);
        ByteArray dataRecord = new ByteArray(25);

        void sendTransDataOutput(byte b) {
            if ((b & 0x01) != 0) {
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0xDF, (byte) 0x81, 0x29}, (byte) 3, 8, outcomeParamSet);
            }

            if ((b & 0x04) != 0) {
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0xDF, (byte) 0x81, 0x16}, (byte) 3, 22, userInterReqData);
            }

            if ((b & 0x02) != 0) {
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0xDF, (byte) 0x81, 0x15}, (byte) 3, 2, errIndication);
            }

            if ((b & 0x10) != 0) {
                ClssPUREApi.Clss_GetTLVDataList_PURE(new byte[]{(byte) 0xDF, (byte) 0x81, 0x05}, (byte) 3, 25, dataRecord);
            }
        }
    }


}
