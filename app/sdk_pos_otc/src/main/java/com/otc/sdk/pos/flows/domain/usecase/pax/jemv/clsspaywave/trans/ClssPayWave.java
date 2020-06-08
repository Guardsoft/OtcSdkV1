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
 * 10:54:05 2017-3-8  	           HuangJs           	    Create
 * ============================================================================
 */
package com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clsspaywave.trans;

import android.util.Log;

import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.model.EntryOutParam;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.model.TransResult;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.trans.ClssEntryPoint;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.SwingCardActivity;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.pay.trans.callback.TransCallback;
import com.pax.jemv.clcommon.ACType;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.Clss_PreProcInfo;
import com.pax.jemv.clcommon.Clss_PreProcInterInfo;
import com.pax.jemv.clcommon.Clss_ProgramID;
import com.pax.jemv.clcommon.Clss_ReaderParam;
import com.pax.jemv.clcommon.Clss_SchemeID_Info;
import com.pax.jemv.clcommon.Clss_TransParam;
import com.pax.jemv.clcommon.Clss_VisaAidParam;
import com.pax.jemv.clcommon.CvmType;
import com.pax.jemv.clcommon.DDAFlag;
import com.pax.jemv.clcommon.EMV_CAPK;
import com.pax.jemv.clcommon.EMV_REVOCLIST;
import com.pax.jemv.clcommon.KernType;
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.clcommon.TransactionPath;
import com.pax.jemv.entrypoint.api.ClssEntryApi;
import com.pax.jemv.paywave.api.ClssWaveApi;

import java.util.Arrays;

import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils.bcd2Str;
import static com.pax.jemv.clcommon.VisaSchemeID.SCHEME_VISA_MSD_20;
import static com.pax.jemv.clcommon.VisaSchemeID.SCHEME_VISA_WAVE_2;
import static com.pax.jemv.clcommon.VisaSchemeID.SCHEME_VISA_WAVE_3;

/**
 *
 */

public class ClssPayWave {

    private static final String TAG = "ClssPayWave";

    private Clss_TransParam transParam;
    private TransactionPath transPath = new TransactionPath();
    private DDAFlag ddaFlag = new DDAFlag();
    private CvmType cvmType = new CvmType();
    private Clss_PreProcInfo procInfo;
    private Clss_VisaAidParam visaAidParam;

    private static ClssPayWave instance;

    private TransCallback callback;
    //private ConditionVariable cv;
    private ClssEntryPoint entryPoint = ClssEntryPoint.getInstance();

    public void setCallback(TransCallback callback) {
        this.callback = callback;
    }

    public static ClssPayWave getInstance() {
        if (instance == null) {
            instance = new ClssPayWave();
        }
        return instance;
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
        return ClssWaveApi.Clss_CoreInit_Wave();
    }

    /**
     * @return
     */
    public String readVersion() {
        ByteArray version = new ByteArray();
        ClssWaveApi.Clss_ReadVerInfo_Wave(version);
        String entryVer = Arrays.toString(version.data);
        return entryVer.substring(0, version.length);
    }

    /**
     * @param visaAidParam
     * @param procInfo
     * @return
     */
    public int setConfigParam(Clss_VisaAidParam visaAidParam, Clss_PreProcInfo procInfo) {

        this.transParam = entryPoint.getTransParam();
        this.procInfo = procInfo;
        this.visaAidParam = visaAidParam;

        return RetCode.EMV_OK;
    }


    public int waveProcess(TransResult transResult) {

        int ret;
        ACType actype = new ACType();
        EntryOutParam outParam = entryPoint.getOutParam();
        Clss_PreProcInterInfo interInfo = entryPoint.getInterInfo();

        ret = waveFlowBegin(outParam, interInfo, actype, visaAidParam);

        if (ret != RetCode.EMV_OK) {
            if (RetCode.CLSS_RESELECT_APP == ret) {
                ret = ClssEntryApi.Clss_DelCurCandApp_Entry();
                if (ret != RetCode.EMV_OK) {
                    return ret;
                }
                ret = RetCode.CLSS_TRY_AGAIN;
                return ret;
            } else if ((ret == RetCode.CLSS_REFER_CONSUMER_DEVICE)
                    && ((interInfo.aucReaderTTQ[0] & 0x20) == 0x20)) {//see phone
                if (callback != null) {
                    callback.removeCardPrompt();
                    ret = callback.displaySeePhone();
                    if (ret != 0) {
                        ret = RetCode.EMV_USER_CANCEL;
                    } else
                        ret = RetCode.CLSS_REFER_CONSUMER_DEVICE;
                }
                //Application selection
                return ret;
            } else if (ret == (RetCode.EMV_OK + 1)) {
                return RetCode.EMV_OK;
            }
            return ret;
        }

        if (callback != null) {
            //cv = new ConditionVariable();
            callback.removeCardPrompt();
            //cv.block();
        }

        ret = waveFlowAfterGPO(actype, transResult);

        return ret;
    }

    public int waveFlowBegin(EntryOutParam outParam, Clss_PreProcInterInfo interInfo, ACType actype, Clss_VisaAidParam visaAidParam) {

        int ret;

        SwingCardActivity.prnTime("waveFlowBegin Clss_SetFinalSelectData_Wave strat time = ");
        ret = ClssWaveApi.Clss_SetFinalSelectData_Wave(outParam.sDataOut, outParam.iDataLen);
        SwingCardActivity.prnTime("waveFlowBegin Clss_SetFinalSelectData_Wave time = ");
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssWaveApi.clssWaveSetFinalSelectData(outParam.sDataOut, outParam.iDataLen) error, ret = " + ret);
            return ret;
        }

        Clss_ReaderParam szReaderParam = new Clss_ReaderParam();

        SwingCardActivity.prnTime("waveFlowBegin Clss_GetReaderParam_Wave strat time = ");
        ret = ClssWaveApi.Clss_GetReaderParam_Wave(szReaderParam);
        SwingCardActivity.prnTime("waveFlowBegin Clss_GetReaderParam_Wave time = ");
        //Log.i("WaveGetReaderParam", "ret = " + ret);
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssWaveApi.WaveGetReaderParam(szReaderParam) error, ret = " + ret);
            return ret;
        }

        //Gillian 20170511
        szReaderParam.acquierId = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56};
        szReaderParam.ucTmType = 0x22;
        szReaderParam.aucTmCap = new byte[]{(byte) 0xE0, (byte) 0xF8, (byte) 0xE8};
        szReaderParam.aucTmCntrCode = new byte[]{(byte) 0x08, (byte) 0x40};
        szReaderParam.aucTmTransCur = new byte[]{(byte) 0x08, (byte) 0x40};
        szReaderParam.ucTmTransCurExp = 0x02;
        //Gillian end
        SwingCardActivity.prnTime("waveFlowBegin Clss_SetReaderParam_Wave strat time = ");
        ret = ClssWaveApi.Clss_SetReaderParam_Wave(szReaderParam);
        SwingCardActivity.prnTime("waveFlowBegin Clss_SetReaderParam_Wave time = ");
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssWaveApi.Clss_SetReaderParam_Wave(szReaderParam) error, ret = " + ret);
            return ret;
        }

        //Gillian 20170613
        Clss_SchemeID_Info[] szSchemeIDInfo = new Clss_SchemeID_Info[3];
        szSchemeIDInfo[0] = new Clss_SchemeID_Info();
        szSchemeIDInfo[1] = new Clss_SchemeID_Info();
        szSchemeIDInfo[2] = new Clss_SchemeID_Info();

        szSchemeIDInfo[0].ucSchemeID = (byte) SCHEME_VISA_WAVE_2;/*VisaSchemeID.SCHEME_VISA_WAVE_2*/

        szSchemeIDInfo[0].ucSupportFlg = (byte) 1;
        szSchemeIDInfo[1].ucSchemeID = (byte) SCHEME_VISA_WAVE_3;
        szSchemeIDInfo[1].ucSupportFlg = (byte) 1;
        szSchemeIDInfo[2].ucSchemeID = (byte) SCHEME_VISA_MSD_20;
        szSchemeIDInfo[2].ucSupportFlg = (byte) 1;

        SwingCardActivity.prnTime("waveFlowBegin Clss_SetRdSchemeInfo_Wave strat time = ");
        ClssWaveApi.Clss_SetRdSchemeInfo_Wave((byte) 3, szSchemeIDInfo);
        SwingCardActivity.prnTime("waveFlowBegin Clss_SetRdSchemeInfo_Wave time = ");

        //TODO:for test
        ByteArray proID = new ByteArray();
//		ret = ClssWaveApi.clssWaveSetTLVData((short) 0x9F5A, "123".getBytes(), 3);
        ClssWaveApi.Clss_GetTLVData_Wave((short) 0x9F5A, proID);
        SwingCardActivity.prnTime("waveFlowBegin Clss_GetTLVData_Wave 0x9F5A time = ");

        if (proID.length != 0) {
            if (procInfo != null) {
                Clss_ProgramID stDRLParam = new Clss_ProgramID(procInfo.ulRdClssTxnLmt, procInfo.ulRdCVMLmt,
                        procInfo.ulRdClssFLmt, procInfo.ulTermFLmt, proID.data, (byte) proID.length,
                        procInfo.ucRdClssFLmtFlg, procInfo.ucRdClssTxnLmtFlg, procInfo.ucRdCVMLmtFlg,
                        procInfo.ucTermFLmtFlg, procInfo.ucStatusCheckFlg, (byte) 0, new byte[4]);
                ret = ClssWaveApi.Clss_SetDRLParam_Wave(stDRLParam);
                SwingCardActivity.prnTime("waveFlowBegin Clss_SetDRLParam_Wave time = ");
                if (ret != RetCode.EMV_OK) {
                    Log.e(TAG, "ClssWaveApi.clssWaveSetDRLParam(stDRLParam) error, ret = " + ret);
                    return ret;
                }
            }

        }

        if (visaAidParam != null) {
            SwingCardActivity.prnTime("waveFlowBegin Clss_SetVisaAidParam_Wave strat time = ");
            ret = ClssWaveApi.Clss_SetVisaAidParam_Wave(visaAidParam);
            SwingCardActivity.prnTime("waveFlowBegin Clss_SetVisaAidParam_Wave time = ");
            if (ret != RetCode.EMV_OK) {
                return ret;
            }
        }

        if (transParam != null) {
            ClssWaveApi.Clss_SetTLVData_Wave((short) 0x9c, new byte[]{transParam.ucTransType}, 1);
            SwingCardActivity.prnTime("waveFlowBegin Clss_SetTransData_Wave strat time = ");
            ret = ClssWaveApi.Clss_SetTransData_Wave(transParam, interInfo);
            SwingCardActivity.prnTime("waveFlowBegin Set Clss_SetTransData_Wave time = ");
            if (ret != RetCode.EMV_OK) {
                Log.e(TAG, "ClssWaveApi.Clss_SetTransData_Wave(transParam, interInfo) error, ret = " + ret);
                return ret;
            }
        }

        SwingCardActivity.prnTime("waveFlowBegin Clss_Proctrans_Wave strat time = ");
        ret = ClssWaveApi.Clss_Proctrans_Wave(transPath, actype);
        SwingCardActivity.prnTime("waveFlowBegin Clss_Proctrans_Wave time = ");
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssWaveApi.Clss_Proctrans_Wave(transPath, actype) error, ret = " + ret);
        }
        return ret;
    }

    public int waveFlowComplete(byte[] sAuthData, int sgAuthDataLen, byte[] sIssuerScript, int sgScriptLen) {
        int iRet, iDataLen;
        int nCTQLen;
        KernType ucKernType = new KernType();
        ByteArray sDataOut = new ByteArray();
        ByteArray aucCTQ = new ByteArray();

        //MSD
        if ((transPath.path == TransactionPath.CLSS_VISA_MSD)
                || (transPath.path == TransactionPath.CLSS_VISA_MSD_CVN17)
                || (transPath.path == TransactionPath.CLSS_VISA_MSD_LEGACY))
            return RetCode.EMV_OK;


        iRet = ClssWaveApi.Clss_GetTLVData_Wave((short) 0x9F6C, aucCTQ);
        if (iRet != RetCode.EMV_OK) {
            return iRet;
        }

        //TTQ are only two byte availabe,why here is TTQ[2] ?
        Clss_PreProcInterInfo interInfo = entryPoint.getInterInfo();
        if ((interInfo.aucReaderTTQ[2] & 0x80) != 0x80 || (aucCTQ.data[1] & 0x40) != 0x40) {
            return RetCode.EMV_OK;
        }

        // get card
        if (callback != null) {
            iRet = callback.detectRFCardAgain();
            if (iRet != RetCode.EMV_OK) {
                return iRet;
            }
        }

        iRet = ClssEntryApi.Clss_FinalSelect_Entry(ucKernType, sDataOut);
        if (iRet != 0) {
            return iRet;
        }
        iRet = ClssWaveApi.Clss_SetFinalSelectData_Wave(entryPoint.getOutParam().sDataOut, entryPoint.getOutParam().iDataLen);
        if (iRet != RetCode.EMV_OK) {
            return iRet;
        }

        iRet = ClssWaveApi.Clss_IssuerAuth_Wave(sAuthData, sgAuthDataLen);
        if (iRet != RetCode.EMV_OK) {
            return iRet;
        }

        iRet = ClssWaveApi.Clss_IssScriptProc_Wave(sIssuerScript, sgScriptLen);
        if (iRet != RetCode.EMV_OK) {
            return iRet;
        }

        return RetCode.EMV_OK;
    }

    private int waveFlowAfterGPO(ACType actype, TransResult transResult) {

        int ret = RetCode.EMV_OK;
        int path = transPath.path;

        if (actype.type == ACType.AC_AAC) {
            /*AAC handle*/
            transResult.result = TransResult.EMV_OFFLINE_DENIED;
            return RetCode.EMV_DENIAL;
        }
        switch (path) {
            case TransactionPath.CLSS_VISA_MSD:
            case TransactionPath.CLSS_VISA_MSD_CVN17:
            case TransactionPath.CLSS_VISA_MSD_LEGACY:
                byte msdPath = ClssWaveApi.Clss_GetMSDType_Wave();
                if (msdPath >= RetCode.EMV_OK) {
                    transPath.path = msdPath;
                    transResult.result = TransResult.EMV_ARQC;
                } else {
                    ret = msdPath;
                }
                break;
            case TransactionPath.CLSS_VISA_WAVE2:
                if (actype.type == ACType.AC_ARQC) {
                    transResult.result = TransResult.EMV_ARQC;
                    ret = RetCode.EMV_OK;
                    return ret;
                } else if (actype.type == ACType.AC_TC) {
                    //card auth
                    ClssWaveApi.Clss_DelAllRevocList_Wave();
                    ClssWaveApi.Clss_DelAllCAPK_Wave();
                    setCAPK();

                    //Card trans handle
                    //ACType cardACType = new ACType();
                    ret = ClssWaveApi.Clss_CardAuth_Wave(actype, ddaFlag);
                    if (ret == RetCode.EMV_OK) {
                        if (actype.type == ACType.AC_ARQC) {
                            transResult.result = TransResult.EMV_ARQC;
                        } else if (actype.type == ACType.AC_TC) {
                            transResult.result = TransResult.EMV_OFFLINE_APPROVED;
                        } else {
                            ClssWaveApi.Clss_GetDebugInfo_Wave();
                            transResult.result = TransResult.EMV_OFFLINE_DENIED;
                            ret = RetCode.EMV_DENIAL;
                        }
                        if (DDAFlag.FAIL == ddaFlag.flag) {
                            ret = RetCode.CLSS_TERMINATE;
                        }
                        //get cvm type
                        cvmType.type = ClssWaveApi.Clss_GetCvmType_Wave();
                        Log.i("waveFlowAfterGPO", "cvmType = " + Integer.toHexString(cvmType.type));
                    } else {
                        ret = RetCode.CLSS_TERMINATE;
                    }
                }
                break;
            case TransactionPath.CLSS_VISA_QVSDC:
                if (actype.type == ACType.AC_ARQC) {
                    transResult.result = TransResult.EMV_ARQC;
                    ret = RetCode.EMV_OK;
                    return ret;
                }

                //Terminal limit check
                ret = ClssWaveApi.Clss_ProcRestric_Wave();
                if (ret != RetCode.EMV_OK) {
                    return ret;
                }

                if ((actype.type == ACType.AC_TC)
                        && ((transParam.ucTransType != 0x20))) {
                    //card auth
                    ClssWaveApi.Clss_DelAllRevocList_Wave();
                    ClssWaveApi.Clss_DelAllCAPK_Wave();
                    ret = setCAPK();
                    if (ret != RetCode.EMV_OK) {
                        return ret;
                    }
                    //Card trans handle
                    //ACType cardACType2 = new ACType();
                    ret = ClssWaveApi.Clss_CardAuth_Wave(actype, ddaFlag);
                    if (ret == RetCode.EMV_OK) {
                        if (actype.type == ACType.AC_ARQC) {
                            transResult.result = TransResult.EMV_ARQC;
                        } else if (actype.type == ACType.AC_TC) {
                            transResult.result = TransResult.EMV_OFFLINE_APPROVED;
                        } else {
                            ClssWaveApi.Clss_GetDebugInfo_Wave();
                            transResult.result = TransResult.EMV_OFFLINE_DENIED;
                            ret = RetCode.EMV_DENIAL;
                        }
                        if (DDAFlag.FAIL == ddaFlag.flag) {
                            ret = RetCode.CLSS_TERMINATE;
                        }
                        //get cvm type
                        cvmType.type = ClssWaveApi.Clss_GetCvmType_Wave();
                    } else {
                        ret = RetCode.CLSS_TERMINATE;
                    }
                }
                break;
            default:
                ret = RetCode.CLSS_TERMINATE;
                break;
        }//switch
        return ret;
    }


    private int setCAPK() {

        int iRet = -1;
        EMV_CAPK stEMVCapk = new EMV_CAPK();
        ByteArray sAid = new ByteArray();
        ByteArray ucKeyIndex = new ByteArray();
        EMV_REVOCLIST tRevocList = new EMV_REVOCLIST();

        iRet = ClssWaveApi.Clss_GetTLVData_Wave((short) 0x4F, sAid);
        if (iRet != RetCode.EMV_OK) {
            Log.e(TAG, "ClssWaveApi.clssWaveGetTLVData((short) 0x4F, sAid) error, ret = " + iRet);
            return iRet;
        }

        iRet = ClssWaveApi.Clss_GetTLVData_Wave((short) 0x8F, ucKeyIndex);
        if (iRet != RetCode.EMV_OK) {
            Log.e(TAG, "ClssWaveApi.clssWaveGetTLVData((short) 0x8F, ucKeyIndex) error, ret = " + iRet);
            return iRet;
        }

        if (callback != null) {
            Log.i(TAG, "ucKeyIndex.data[0] = " + bcd2Str(ucKeyIndex.data));
            iRet = callback.getCapk(sAid.data, ucKeyIndex.data[0], stEMVCapk);
            if (iRet != RetCode.EMV_OK) {
                Log.e(TAG, "callback.getCapk error, ret = " + iRet);
                return iRet;
            }
        }

        iRet = ClssWaveApi.Clss_AddCAPK_Wave(stEMVCapk);

        if (RetCode.EMV_OK != iRet) {
            Log.e(TAG, "ClssWaveApi.clssWaveAddCapk(stEMVCapk) error, ret = " + iRet);
        }
        System.arraycopy(sAid.data, 0, tRevocList.ucRid, 0, 5);
        tRevocList.ucIndex = ucKeyIndex.data[0];
        System.arraycopy(new byte[]{0x00, 0x07, 0x11}, 0, tRevocList.ucCertSn, 0, 3);
        iRet = ClssWaveApi.Clss_AddRevocList_Wave(tRevocList);
        if (RetCode.EMV_OK != iRet) {
            Log.e(TAG, "ClssWaveApi.Clss_AddRevocList_Wave(stEMVCapk) , ret = " + iRet);
        }

        return iRet;
    }

}
