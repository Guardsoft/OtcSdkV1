/*
 * ============================================================================
 * = COPYRIGHT
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2017-? PAX Technology, Inc. All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                           Author	                Action
 * 10:53:38 2017-3-8  	           HuangJs           	    Create
 * ============================================================================
 */
package com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clsspaypass.trans;

import android.util.Log;

import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.model.EntryOutParam;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.model.TransResult;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.trans.ClssEntryPoint;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.pay.trans.callback.TransCallback;
import com.pax.jemv.clcommon.ACType;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.CLSS_TORN_LOG_RECORD;
import com.pax.jemv.clcommon.Clss_MCAidParam;
import com.pax.jemv.clcommon.Clss_PreProcInfo;
import com.pax.jemv.clcommon.Clss_ReaderParam;
import com.pax.jemv.clcommon.Clss_TransParam;
import com.pax.jemv.clcommon.CvmType;
import com.pax.jemv.clcommon.EMV_CAPK;
import com.pax.jemv.clcommon.EMV_REVOCLIST;
import com.pax.jemv.clcommon.OutcomeParam;
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.clcommon.TransactionPath;
import com.pax.jemv.device.DeviceManager;
import com.pax.jemv.paypass.api.ClssPassApi;
import com.pax.jemv.paypass.listener.ClssPassCBFunApi;

import java.util.Arrays;

import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils.bcd2Str;
import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils.str2Bcd;


//import com.pax.commonlib.dataformat.Tlv.TlvItem;

/**
 *
 */

public class ClssPayPass {

    private static final String TAG = "ClssPayPass";

    private static final int OC_APPROVED = 0x10;
    private static final int OC_DECLINED = 0x20;
    private static final int OC_ONLINE_REQUEST = 0x30;
    private static final int OC_END_APPLICATION = 0x40;
    private static final int OC_SELECT_NEXT = 0x50;
    private static final int OC_TRY_ANOTHER_INTERFACE = 0x60;
    private static final int OC_TRY_AGAIN = 0x70;
    private static final int OC_NA = 0xF0;
    private static final int OC_A = 0x00;
    private static final int OC_B = 0x10;
    private static final int OC_C = 0x20;
    private static final int OC_D = 0x30;
    private static final int OC_NO_CVM = 0x00;
    private static final int OC_OBTAIN_SIGNATURE = 0x10;
    private static final int OC_ONLINE_PIN = 0x20;
    private static final int OC_CONFIRM_CODE_VER = 0x30;

    private static final int MI_CARD_READ_OK = 0x17;
    private static final int MI_TRY_AGAIN = 0x21;
    private static final int MI_APPROVED = 0x03;
    private static final int MI_APPROVED_SIGN = 0x1A;
    private static final int MI_DECLINED = 0x07;
    private static final int MI_ERROR_OTHER_CARD = 0x1C;
    private static final int MI_INSERT_CARD = 0x1D;
    private static final int MI_SEE_PHONE = 0x20;
    private static final int MI_AUTHORISING_PLS_WAIT = 0x1B;
    private static final int MI_CLEAR_DISPLAY = 0x1E;
    private static final int MI_NA = 0xFF;
    private static final int MI_NOT_READY = 0x00;
    private static final int MI_IDLE = 0x01;
    private static final int MI_READY_TO_READ = 0x02;
    private static final int MI_PROCESSING = 0x03;
    private static final int MI_CARD_READ_SUCC = 0x04;
    private static final int MI_PROC_ERROR = 0x05;


    private int appTornLogNum = 0;//number of Tornlog
    private CLSS_TORN_LOG_RECORD[] tornLogRecords = new CLSS_TORN_LOG_RECORD[5];
    private CvmType cvmType = new CvmType();
    private TransactionPath transPath = new TransactionPath();
    //	private static KernType kernType = new KernType();
//	private static DDAFlag ddaFlag = new DDAFlag();
    private Clss_TransParam transParam;
    //	public static Clss_ReaderParam szReaderParam;
    private Clss_MCAidParam aidParam;
    private Clss_PreProcInfo procInfo;

    private static ClssPayPass instance;
    private Test_ClssPassCBFunApi clssPassCBFun = new Test_ClssPassCBFunApi();
    private TransCallback callback;
    private ClssEntryPoint entryPoint = ClssEntryPoint.getInstance();

    public void setCallback(TransCallback callback) {
        this.callback = callback;
    }

    public static ClssPayPass getInstance() {
        if (instance == null) {
            instance = new ClssPayPass();
        }
        return instance;
    }

    private ClssPayPass() {
        tornLogRecords = new CLSS_TORN_LOG_RECORD[5];
    }

//	public static int getACType() {
//		return acType.type;
//	}

    public int getCVMType() {
        return cvmType.type;
    }

    public int getTransPath() {
        return transPath.path;
    }

//	public static int getKernType() {
//		return kernType.kernType;
//	}

//	public static int getDDAFlag() {
//		return ddaFlag.flag;
//	}

    public int coreInit(byte deSupportFlag) {
        int ret;
        ClssPassApi.Clss_CoreInit_MC(deSupportFlag);
        ClssPassCBFunApi.getInstance().setICBFun(clssPassCBFun);
        ret = ClssPassApi.Clss_SetCBFun_SendTransDataOutput_MC();
        Log.i(TAG, "Clss_SetCBFun_SendTransDataOutput_MC = " + ret);
        return ret;
    }

    public String readVersion() {
        ByteArray version = new ByteArray();
        ClssPassApi.Clss_ReadVerInfo_MC(version);
        String entryVer = Arrays.toString(version.data);
        return entryVer.substring(0, version.length);
    }

    public int setConfigParam(Clss_MCAidParam aidParam, Clss_PreProcInfo procInfo) {

        int ret = RetCode.EMV_OK;

        transParam = entryPoint.getTransParam();

        this.aidParam = aidParam;
        this.procInfo = procInfo;

        if (callback != null) {
            appTornLogNum = callback.appLoadTornLog(tornLogRecords);
            if (appTornLogNum > 5) {
                appTornLogNum = 5;
            }
        }

		//SetTagPresent();

        return ret;
    }

    public int passProcess(TransResult transResult) {

        int ret;
        ACType acType = new ACType();
        EntryOutParam outParam = entryPoint.getOutParam();
        //Clss_PreProcInterInfo interInfo = entryPoint.getInterInfo();

        appCleanTornLog();

        ret = passFlowBegin(outParam, acType);
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "passFlowBegin ret = " + ret);

            if (RetCode.CLSS_RESELECT_APP == ret) {
                ret = RetCode.CLSS_TRY_AGAIN;
                return ret;
            } else if ((ret == RetCode.CLSS_REFER_CONSUMER_DEVICE)) {//see phone
                if (callback != null) {

                    callback.removeCardPrompt(); //DEVICE_Beep("6:200",BEEP);
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

        ret = passFlowAfterGPO(acType, transResult);

        return ret;


    }

    private int passFlowAfterGPO(ACType acType, TransResult transResult) {
        int ret;
        int iLen = 0;
        ACType ucOutACType = new ACType();

        if (acType.type == ACType.AC_AAC) {
            transResult.result = TransResult.EMV_OFFLINE_DENIED;
            return RetCode.EMV_DENIAL;
        }

        ret = getCardAuthResultPass(ucOutACType, cvmType);
        if (ret == RetCode.EMV_OK) {
//			ret = ClssPassApi.clssMcGetTLVDataList(arg0, arg1, arg2, arg3)AdkEMV_GetTLV(0x9F10 ,sBuf,&iLen);
//			if(ret == RetCode.EMV_OK) {
////	
//			}
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

    private int getCardAuthResultPass(ACType ucOutACType, CvmType g_ucCvmType) {

        int ret;
        byte[] szBuff = new byte[]{(byte) 0xDF, (byte) 0x81, 0x29};//Outcome Parameter
        ByteArray aucOutcomeParamSet_MC = new ByteArray();

        ret = ClssPassApi.Clss_GetTLVDataList_MC(szBuff, (byte) 3, 24, aucOutcomeParamSet_MC);
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
                    g_ucCvmType.type = CvmType.RD_CVM_OFFLINE_PIN;
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

    private int passFlowBegin(EntryOutParam outParam, ACType acType) {

        Clss_ReaderParam szReaderParam = new Clss_ReaderParam();
        //Gillian 20170511
        szReaderParam.acquierId = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56};
        szReaderParam.ucTmType = 0x22;
        szReaderParam.aucTmCap = new byte[]{(byte) 0xE0, (byte) 0x60, (byte) 0xC8};
        //szReaderParam.aucTmCntrCode = new byte[]{(byte) 0x08, (byte) 0x40}; //dolare
        szReaderParam.aucTmCntrCode = new byte[]{(byte) 0x06, (byte) 0x04}; // soles
        szReaderParam.aucTmTransCur = new byte[]{(byte) 0x08, (byte) 0x40}; //dolares
        szReaderParam.aucTmTransCur = new byte[]{(byte) 0x06, (byte) 0x04}; //soles
        szReaderParam.ucTmTransCurExp = 0x02;
        //Gillian end
        int ret;
        ByteArray aucOutcomeParamSet_MC = new ByteArray();
        byte[] szBuff = new byte[]{(byte) 0xDF, (byte) 0x81, 0x29};    //Outcome Parameter

        ret = ClssPassApi.Clss_SetFinalSelectData_MC(outParam.sDataOut, outParam.iDataLen);
        if (ret != RetCode.EMV_OK) {
            ret = ClssPassApi.Clss_GetTLVDataList_MC(szBuff, (byte) 3, 24, aucOutcomeParamSet_MC);
            if ((aucOutcomeParamSet_MC.data[1] & 0xF0) == 0x20) {//Start : C
                return RetCode.CLSS_RESELECT_APP;
            }
            return ret;
        }
        setTransParamPass(transParam);
        setReaderParam(szReaderParam);
        setParamByAidPass(aidParam, procInfo);

        ret = ClssPassApi.Clss_InitiateApp_MC();
        if (ret != RetCode.EMV_OK) {
            ret = ClssPassApi.Clss_GetTLVDataList_MC(szBuff, (byte) 3, 24, aucOutcomeParamSet_MC);
            if ((aucOutcomeParamSet_MC.data[1] & 0xF0) == 0x20) {//Start : C
                return RetCode.CLSS_RESELECT_APP;
            }
            return ret;
        }
        ret = ClssPassApi.Clss_ReadData_MC(transPath);
        if (ret != RetCode.EMV_OK) {
            return ret;
        }
        Log.i(TAG, "outcomeParamSet = " + bcd2Str(clssPassCBFun.outcomeParamSet.data));


        ret = appTransProc((byte) transPath.path, acType);
        Log.i(TAG, "appTransProc ret = " + ret);

        Log.i(TAG, "userInterReqData = " + bcd2Str(clssPassCBFun.userInterReqData.data, 22));
        if (clssPassCBFun.userInterReqData.data[0] == (byte) 0x20) {//MI_SEE_PHONE
            return RetCode.CLSS_REFER_CONSUMER_DEVICE;
        }
        if ((clssPassCBFun.outcomeParamSet.data[0] == (byte) OC_TRY_AGAIN) || (clssPassCBFun.outcomeParamSet.data[1] != (byte) OC_NA)) {//OC_TRY_AGAIN , OC_NA
            //Log.i(TAG, "outcomeParamSet[0] = "+ clssPassCBFun.outcomeParamSet.data[0] +  "outcomeParamSet[1] = " + clssPassCBFun.outcomeParamSet.data[1]);
            return RetCode.CLSS_TRY_AGAIN;
        }

        return ret;

    }

    private int appTransProc(byte transPath, ACType acType) {
        int ret = 0;
        ByteArray ucPkIndex = new ByteArray();
        ByteArray aucAid = new ByteArray();
        //ACType ACTypeOut = new ACType();
        CLSS_TORN_LOG_RECORD atAppTornTransLog[] = new CLSS_TORN_LOG_RECORD[5];//Torn Transaction Log
        int nAppTornLogNum = 0;//number of Tornlog

        EMV_REVOCLIST tRevocList = new EMV_REVOCLIST();


        if (transPath == TransactionPath.CLSS_MC_MCHIP) {// 0x06)
            ClssPassApi.Clss_DelAllRevocList_MC_MChip();
            ClssPassApi.Clss_DelAllCAPK_MC_MChip();

            if (ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x8F}, (byte) 1, 1, ucPkIndex) == 0) {
                if (ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{0x4F}, (byte) 1, 17, aucAid) == 0) {
                    ByteArray temp = new ByteArray();
                    ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0xDF, (byte) 0x81, 0x1F}, (byte) 3, 1, temp);
                    temp = new ByteArray();
                    ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x9F, 0x33}, (byte) 2, 3, temp);

                    setCAPK(ucPkIndex, aucAid);//修改放到检测卡前
                    System.arraycopy(aucAid.data, 0, tRevocList.ucRid, 0, 5);
                    tRevocList.ucIndex = ucPkIndex.data[0];
                    System.arraycopy(new byte[]{0x00, 0x07, 0x11}, 0, tRevocList.ucCertSn, 0, 3);
                    ret = ClssPassApi.Clss_AddRevocList_MC_MChip(tRevocList);
                    if (ret != RetCode.EMV_OK) {
                        Log.e(TAG, "callback.getCapk error, ret = " + ret);
                        return ret;
                    }
                }
            }
            if (callback != null) {
                appTornLogNum = callback.appLoadTornLog(atAppTornTransLog);
            }
            if (appTornLogNum > 0) {
                ClssPassApi.Clss_SetTornLog_MC_MChip(tornLogRecords, appTornLogNum);
            }
            ret = ClssPassApi.Clss_TransProc_MC_MChip(acType);
            //if (acType.type < RetCode.EMV_OK) {
            //	ret = acType.type;
            //}
            int dataOut[] = new int[2];
            ClssPassApi.Clss_GetTornLog_MC_MChip(atAppTornTransLog, dataOut);
            nAppTornLogNum = dataOut[0];
            if (dataOut[1] == 1) {   // TornLog record need to update
                if (callback != null) {
                    callback.appSaveTornLog(atAppTornTransLog, nAppTornLogNum);
                }
            }
        } else if (transPath == TransactionPath.CLSS_MC_MAG) {// 0x05)
            ret = ClssPassApi.Clss_TransProc_MC_Mag(acType);
            //if (acType.type < RetCode.EMV_OK) {
            //	ret = acType.type;
            //} else {
            //	ret = RetCode.EMV_OK;
            //}
        } else {
            ret = RetCode.CLSS_TERMINATE;
        }
        //Gillian debug
        ByteArray byteArray = new ByteArray();
        byte[] a;
        int iRet;
        String TVR = null;
        iRet = ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0x95}, (byte) 1, 10, byteArray);
        a = new byte[byteArray.length];
        System.arraycopy(byteArray.data, 0, a, 0, byteArray.length);
        TVR = bcd2Str(a);
        Log.i("Clss_TLV_MC iRet 0x95", iRet + "");
        Log.i("Clss_GetTLV_MC TVR 0x95", TVR + "");
        return ret;
    }

    private int appCleanTornLog() {
        byte[] time = new byte[7];
        byte[] time_tmp = new byte[6];
        int ret = 0;
        DeviceManager.getInstance().getTime(time);
        if (appTornLogNum == 0) {
            return 0;
        }
        ClssPassApi.Clss_SetTornLog_MC_MChip(tornLogRecords, appTornLogNum);
        System.arraycopy(time, 1, time_tmp, 0, 6);
        ret = ClssPassApi.Clss_CleanTornLog_MC_MChip(time_tmp, 6, (byte) 0);
        if (ret != 0) {
            return 0;
        }
        tornLogRecords = new CLSS_TORN_LOG_RECORD[5];
        int dataOut[] = new int[2];
        ret = ClssPassApi.Clss_GetTornLog_MC_MChip(tornLogRecords, dataOut);
        if (ret == 0) {
            appTornLogNum = dataOut[0];
        }
        return 0;
    }

    private void setTransParamPass(Clss_TransParam transParam) {

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
                new TransParamTable(new byte[]{(byte) 0x9F, 0x4E}, 2, readerParam.aucMchNameLoc, readerParam.usMchLocLen),//00
                new TransParamTable(new byte[]{(byte) 0x9F, 0x15}, 2, readerParam.aucMerchCatCode, 2),//00
                new TransParamTable(new byte[]{(byte) 0x9F, 0x16}, 2, readerParam.aucMerchantID, 15),//00
                new TransParamTable(new byte[]{(byte) 0x9F, 0x01}, 2, readerParam.acquierId, 6),//"\x00\x00\x00\x12\x34\x56"
                new TransParamTable(new byte[]{(byte) 0x9F, 0x1C}, 2, readerParam.aucTmID, 8),//0
                new TransParamTable(new byte[]{(byte) 0x9F, 0x35}, 2, new byte[]{readerParam.ucTmType}, 1),
                new TransParamTable(new byte[]{(byte) 0x9F, 0x33}, 2, readerParam.aucTmCap, 3),//E0F0C8
                new TransParamTable(new byte[]{(byte) 0xDF, (byte) 0x81, 0x17}, 3, new byte[]{readerParam.aucTmCap[0]}, 1),//\xE0
                new TransParamTable(new byte[]{(byte) 0xDF, (byte) 0x81, 0x18}, 3, new byte[]{readerParam.aucTmCap[1]}, 1),//60
                new TransParamTable(new byte[]{(byte) 0xDF, (byte) 0x81, 0x19}, 3, new byte[]{(byte) 0x08}, 1),//NO CVM Gillian 20170606
                new TransParamTable(new byte[]{(byte) 0xDF, (byte) 0x81, 0x1F}, 3, new byte[]{readerParam.aucTmCap[2]}, 1),//c8
                new TransParamTable(new byte[]{(byte) 0xDF, (byte) 0x81, 0x1A}, 3, new byte[]{(byte) 0x9F, (byte) 0x6A, (byte) 0x04}, 3),//\x9F\x6A\x04 //Default UDOL Gillian 20170606
                new TransParamTable(new byte[]{(byte) 0x9F, 0x6D}, 2, new byte[]{(byte) 0x00, (byte) 0x01}, 2),
                new TransParamTable(new byte[]{(byte) 0xDF, (byte) 0x81, 0x1E}, 3, new byte[]{(byte) 0x20}, 1),
                new TransParamTable(new byte[]{(byte) 0xDF, (byte) 0x81, 0x2C}, 3, new byte[]{(byte) 0x00}, 1),

                new TransParamTable(new byte[]{(byte) 0x9F, 0x40}, 2, readerParam.aucTmCapAd, 5),//00
                new TransParamTable(new byte[]{(byte) 0x9F, 0x1A}, 2, readerParam.aucTmCntrCode, 2),//0344
                new TransParamTable(new byte[]{0x5F, 0x2A}, 2, readerParam.aucTmTransCur, 2),//0344
                new TransParamTable(new byte[]{0x5F, 0x36}, 2, new byte[]{readerParam.ucTmTransCurExp}, 1),//02
                new TransParamTable(new byte[]{(byte) 0x9F, 0x3C}, 2, readerParam.aucTmRefCurCode, 2),
                new TransParamTable(new byte[]{(byte) 0x9F, 0x3D}, 2, new byte[]{readerParam.ucTmRefCurExp}, 1),

                //new TransParamTable(new byte[]{(byte) 0x9F,0x5C}, 2,  new byte[] {(byte) 0x7A, (byte) 0x45, (byte) 0x12, (byte) 0x3E, (byte) 0xE5, (byte) 0x9C, (byte) 0x7E, (byte) 0x40}, 8),
                //new TransParamTable(new byte[]{(byte) 0x9F,0x5C}, 2,  new byte[] {(byte) 0}, 1), //don't support IDS 9f5c not present.
                new TransParamTable(new byte[]{(byte) 0xDF, (byte) 0x81, 0x0D}, 3, new byte[]{(byte) 0x00}, 1),
                new TransParamTable(new byte[]{(byte) 0x9F, 0x70}, 2, new byte[]{(byte) 0x00}, 1),
                new TransParamTable(new byte[]{(byte) 0x9F, 0x75}, 2, new byte[]{(byte) 0x00}, 1),
                new TransParamTable(new byte[]{(byte) 0xDF, (byte) 0x81, 0x30}, 3, new byte[]{(byte) 0x00}, 1),
                new TransParamTable(new byte[]{(byte) 0xDF, (byte) 0x81, 0x1C}, 3, new byte[]{(byte) 0x00, (byte) 0x00}, 2),
                new TransParamTable(new byte[]{(byte) 0xDF, (byte) 0x81, 0x1D}, 3, new byte[]{(byte) 0x00}, 1),
                new TransParamTable(new byte[]{(byte) 0xDF, (byte) 0x81, 0x0C}, 3, new byte[]{(byte) 0x02}, 1),
                new TransParamTable(new byte[]{(byte) 0xDF, (byte) 0x81, 0x2D}, 3, new byte[]{(byte) 0x00}, 1),

                new TransParamTable(new byte[]{(byte) 0xDF, (byte) 0x81, 0x1B}, 3, new byte[]{(byte) 0x20}, 1), //MCD

                //9f1D
                new TransParamTable(new byte[]{(byte) 0x9F, 0x1D}, 2, new byte[]{(byte) 0x6C, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}, 8),
        };

        for (TransParamTable transParamTable : table) {
            ret = setDETData(transParamTable.tag, transParamTable.tag_len, transParamTable.value, transParamTable.value_len);
            if (ret != RetCode.EMV_OK) {
                break;
            }
        }

        return ret;
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
        ret = ClssPassApi.Clss_SetTLVDataList_MC(aucBuff, ucBuffLen);
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "aucBuff = " + bcd2Str(aucBuff) + ", ucBuffLen = " + ucBuffLen);
            Log.e(TAG, "ClssPassApi.clssMcSetTLVDataList(aucBuff, ucBuffLen), ret = " + ret);
        }

        return ret;
    }

    private void setParamByAidPass(Clss_MCAidParam aidParam, Clss_PreProcInfo procInfo) {

        byte[] aucBuff = new byte[64];

        if (aidParam != null) {
            setDETData(new byte[]{(byte) 0x9F, 0x09}, 2, aidParam.version, 2);

            setDETData(new byte[]{(byte) 0xDF, (byte) 0x81, 0x20}, 3, aidParam.tacDefault, 5);//TAC Default
            if (transParam != null && transParam.ucTransType == 0x20) {//refund rquired AAC
                setDETData(new byte[]{(byte) 0xDF, (byte) 0x81, 0x21}, 3, new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, 5);//TAC Denial
            } else {
                setDETData(new byte[]{(byte) 0xDF, (byte) 0x81, 0x21}, 3, aidParam.tacDenial, 5);//TAC Denial
            }
            setDETData(new byte[]{(byte) 0xDF, (byte) 0x81, 0x22}, 3, aidParam.tacOnline, 5);//TAC Online

            setDETData(new byte[]{(byte) 0x9F, 0x01}, 2, aidParam.acquierId, 6);
        }


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

            System.arraycopy(str2Bcd(String.format("%012d", procInfo.ulRdClssTxnLmt)), 0, aucBuff, 0, 6);
            setDETData(new byte[]{(byte) 0xDF, (byte) 0x81, 0x25}, 3, aucBuff, 6);
            Log.i(TAG, "ulRdClssTxnLmt = " + String.format("%012d", procInfo.ulRdClssTxnLmt));

            System.arraycopy(str2Bcd(String.format("%012d", procInfo.ulRdCVMLmt)), 0, aucBuff, 0, 6);
            setDETData(new byte[]{(byte) 0xDF, (byte) 0x81, 0x26}, 3, aucBuff, 6);
            Log.i(TAG, "ulRdCVMLmt = " + procInfo.ulRdCVMLmt);

        }

    }

    private int setCAPK(ByteArray ucKeyIndex, ByteArray sAid) {

        int iRet = -1;
        EMV_CAPK stEMVCapk = new EMV_CAPK();

//	    iRet = ClssPassApi.clssMcGetTLVDataList(new byte[]{0x4F}, (byte) 1, sAid.data.length, sAid);
//	    if(iRet != RetCode.EMV_OK) {
//	        Log.e(TAG, "ClssPassApi.clssMcGetTLVDataList(new byte[]{0x4F}, (byte) 1, sAid.data.length, sAid) error, ret = "+iRet);
//	    	return iRet;
//	    }
//
//	    iRet = ClssPassApi.clssMcGetTLVDataList(new byte[]{(byte) 0x8F},(byte) 1, ucKeyIndex.data.length, ucKeyIndex);
//	    if(iRet != RetCode.EMV_OK) {
//	    	Log.e(TAG, "ClssPassApi.clssMcGetTLVDataList(new byte[]{(byte) 0x8F},(byte) 1, ucKeyIndex.data.length, ucKeyIndex) error, ret = "+iRet);
//	    	return iRet;
//	    }

        if (callback != null) {
            Log.i(TAG, "ucKeyIndex.data[0] = " + bcd2Str(ucKeyIndex.data));
            iRet = callback.getCapk(sAid.data, ucKeyIndex.data[0], stEMVCapk);
            if (iRet != RetCode.EMV_OK) {
                Log.e(TAG, "callback.getCapk error, ret = " + iRet);
                return iRet;
            }
        }

        iRet = ClssPassApi.Clss_AddCAPK_MC_MChip(stEMVCapk);

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

}
