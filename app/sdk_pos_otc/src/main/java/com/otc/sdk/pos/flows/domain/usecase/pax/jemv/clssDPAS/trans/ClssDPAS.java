package com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssDPAS.trans;

import android.util.Log;

import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.model.EntryOutParam;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.model.TransResult;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssentrypoint.trans.ClssEntryPoint;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.DeviceImplNeptune;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.TagsTable;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.pay.trans.callback.TransCallback;
import com.pax.jemv.clcommon.ACType;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.Clss_PreProcInterInfo;
import com.pax.jemv.clcommon.Clss_TransParam;
import com.pax.jemv.clcommon.CvmType;
import com.pax.jemv.clcommon.EMV_CAPK;
import com.pax.jemv.clcommon.EMV_REVOCLIST;
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.clcommon.TransactionPath;
import com.pax.jemv.device.model.ApduRespL2;
import com.pax.jemv.device.model.ApduSendL2;
import com.pax.jemv.dpas.api.ClssDPASApi;
import com.pax.jemv.entrypoint.api.ClssEntryApi;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils.bcd2Str;
import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils.int2ByteArray;
import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils.str2Bcd;


/**
 * Created by xionggd on 2017/8/15.
 */
public class ClssDPAS {

    private static final String TAG = "ClssDPAS";

    // private static DDAFlag ddaFlag = new DDAFlag();
    private static CvmType cvmType = new CvmType();
    // private  Clss_PreProcInfo procInfo;

    private Clss_PreProcInterInfo clssPreProcInterInfo = new Clss_PreProcInterInfo();
    private Clss_TransParam transParam;
    TransactionPath transactionPath = new TransactionPath();
    private ClssDpassSendOutcome sendOutcome = new ClssDpassSendOutcome();

    private static ClssDPAS instance;
    private TransCallback callback;
    private ClssEntryPoint entryPoint = ClssEntryPoint.getInstance();

    public void setCallback(TransCallback callback) {
        this.callback = callback;
    }

    public static ClssDPAS getInstance() {
        if (instance == null) {
            instance = new ClssDPAS();
        }
        return instance;
    }

    //  public  int getDDAFlag() {return ddaFlag.flag;}

    public int getCVMType() {
        return cvmType.type;
    }

    public int coreInit() {
        return ClssDPASApi.Clss_CoreInit_DPAS();
    }

    public int setConfigParam() {

        this.transParam = entryPoint.getTransParam();
        // this.aidParam = aidParam;
        // this.procInfo = procInfo;

        return RetCode.EMV_OK;
    }

    public int DPASProcess(TransResult transResult) {

        int ret;
        ACType acType = new ACType();

        EntryOutParam outParam = entryPoint.getOutParam();
        clssPreProcInterInfo = entryPoint.getInterInfo();
        ret = DpasFlowBegin(outParam, acType);
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
        DpasFlowAfterGPO(acType, transResult);

        return ret;

    }

    private int DpasFlowBegin(EntryOutParam outParam, ACType actype) {

        int ret;

        ret = ClssDPASApi.Clss_SetFinalSelectData_DPAS(outParam.sDataOut, outParam.iDataLen);
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssDPASApi.Clss_SetFinalSelectData_DPAS(outParam.sDataOut, outParam.iDataLen) error, ret = " + ret);
            return ret;
        }

        // ret = ClssEntryApi.Clss_GetPreProcInterFlg_Entry(clssPreProcInterInfo);
        //clssPreProcInterInfo = entryPoint.getInterInfo();
      /*  if(ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssEntryApi.Clss_GetPreProcInterFlg_Entry(clssPreProcInterInfo) error, ret = "+ ret);
            return ret;
        }*/

        clssBaseParameterSet();

        ret = ClssDPASApi.Clss_InitiateApp_DPAS(transactionPath);
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssDPASApi.Clss_InitiateApp_DPAS error, ret = " + ret);
            return ret;
        }

        ret = processDpas(transactionPath);
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "processDpas error, ret = " + ret);
            return ret;
        }

        genTransResult(actype);
        if (actype.type == ACType.AC_ARQC) {
            ByteArray track1 = new ByteArray();
            ByteArray track2 = new ByteArray();

            if ((transactionPath.path == TransactionPath.CLSS_DPAS_MAG)
                    || (transactionPath.path == TransactionPath.CLSS_DPAS_ZIP)) {
                ClssDPASApi.Clss_GetTrackMapData_DPAS((byte) 0x01, track1);
                ClssDPASApi.Clss_GetTrackMapData_DPAS((byte) 0x02, track2);
            }
        }

        return ret;
    }

    private int DpasFlowAfterGPO(ACType acType, TransResult transResult) {
        int ret;

        ret = processTranResult(acType, transResult);
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "processTranResult, ret = " + ret);
            return ret;
        }

        //fix PED-11
        if (transactionPath.path == TransactionPath.CLSS_DPAS_EMV) {
    /*        ret = onIssScrCon();
            if (ret != RetCode.EMV_OK) {
                Log.e(TAG, "onIssScrCon, ret = " + ret);
                return RetCode.EMV_RSP_ERR;
            }*/

            byte[] tag91 = new byte[16];
            byte[] tag71 = new byte[256];
            byte[] tag72 = new byte[256];
            ret = completeTrans(transResult, tag91, tag71, tag72);
            if (ret != RetCode.EMV_OK) {
                Log.e(TAG, "completeTrans, ret = " + ret);
                return ret;
            }

            genTransResult(acType);
            ret = processTranResult(acType, transResult);
            if (ret != RetCode.EMV_OK) {
                Log.e(TAG, "processTranResult, ret = " + ret);
                return ret;
            }
        }

        return RetCode.EMV_OK;

    }

    private void getCVMResultDPAS(CvmType g_ucCvmType) {
        switch (sendOutcome.outcomeParamSet.data[3] & 0x30) {
            case 0x10:
                g_ucCvmType.type = CvmType.RD_CVM_SIG;
                Log.i(TAG, "CVM = signature");
                break;
            case 0x20:
                g_ucCvmType.type = CvmType.RD_CVM_ONLINE_PIN;
                Log.i(TAG, "CVM = online pin");
                break;
            default:
                g_ucCvmType.type = CvmType.RD_CVM_NO;
                Log.i(TAG, "CVM = no cvm");
                break;
        }
    }

    private void clssBaseParameterSet() {
        setTlv(TagsTable.APP_VER, new byte[]{0x00, 0x01}, 2);
        setTlv(TagsTable.TERM_DEFAULT, new byte[]{0x04, 0x00, 0x00, 0x00, 0x00}, 5);
        setTlv(TagsTable.TERM_DENIAL, new byte[]{0x04, 0x40, 0x00, (byte) 0x80, 0x00}, 5);
        setTlv(TagsTable.TERM_ONLINE, new byte[]{(byte) 0xF8, 0x50, (byte) 0xAC, (byte) 0xF8, 0x00}, 5);
        setTlv(TagsTable.FLOOR_LIMIT, new byte[]{0x00, 0x00, 0x00, 0x00, 0x50, 0x00}, 6);
        setTlv(TagsTable.TRANS_LIMIT, new byte[]{0x00, 0x00, 0x00, 0x10, 0x00, 0x00}, 6);
        setTlv(TagsTable.CVM_LIMIT, new byte[]{0x00, 0x00, 0x00, 0x00, 0x30, 0x00}, 6);
        setTlv(TagsTable.TERMINAL_CAPABILITY, new byte[]{(byte) 0xE0, (byte) 0xE1, (byte) 0xC8}, 3);
        setTlv(TagsTable.ACQUIRER_ID, new byte[]{0x00, 0x00, 0x00, 0x12, 0x34, 0x56}, 6);
        setTlv(TagsTable.MERCHANT_CATEGORY_CODE, new byte[]{0x00, 0x00}, 2);
        setTlv(TagsTable.MERCHANT_NAME_LOCATION, new byte[]{0x00}, 1);
        setTlv(TagsTable.COUNTRY_CODE, new byte[]{0x08, 0x40}, 2);
        setTlv(0x9F35, new byte[]{0x22}, 1);//TerminalType
        setTlv(TagsTable.CURRENCY_CODE, new byte[]{0x08, 0x40}, 2);
        setTlv(0x5F36, new byte[]{0x02}, 1);//Transaction Currency Exponent
        setTlv(0x9F66, clssPreProcInterInfo.aucReaderTTQ, 5);
        Log.i(TAG, "clssBaseParameterSet  clssPreProcInterInfo.aucReaderTTQ = " + bcd2Str(clssPreProcInterInfo.aucReaderTTQ));
        Log.i(TAG, "clssBaseParameterSet  clssPreProcInterInfo.ucRdCLFLmtExceed = " + clssPreProcInterInfo.ucRdCLFLmtExceed);
        Log.i(TAG, "clssBaseParameterSet  clssPreProcInterInfo.aucAID = " + bcd2Str(clssPreProcInterInfo.aucAID));

        if (clssPreProcInterInfo.ucRdCLFLmtExceed == 1) {
            ByteArray TVR = new ByteArray();
            getTlv(0x95, TVR);
            TVR.data[3] = (byte) (TVR.data[3] | 0x80);
            setTlv(0x95, Arrays.copyOfRange(TVR.data, 0, 5), 5);
        }

        byte[] tmp = str2Bcd(String.valueOf(transParam.ulAmntAuth));
        byte[] amount = new byte[6];
        System.arraycopy(tmp, 0, amount, 6 - tmp.length, tmp.length);
        setTlv(TagsTable.AMOUNT, amount, 6);

        tmp = str2Bcd(Long.toString(transParam.ulAmntOther));
        amount = new byte[6];
        System.arraycopy(tmp, 0, amount, 6 - tmp.length, tmp.length);
        setTlv(TagsTable.AMOUNT_OTHER, amount, 6);

        setTlv(TagsTable.TRANS_TYPE, new byte[]{transParam.ucTransType}, 1);
        //setDETData(new byte[]{(byte) 0x9A}, 1, transParam.aucTransDate, 3);
        //setDETData(new byte[]{(byte) 0x9F,(byte) 0x21}, 2, transParam.aucTransTime, 3);
        setTlv(TagsTable.TRANS_DATE, transParam.aucTransDate, 3);
        setTlv(TagsTable.TRANS_TIME, transParam.aucTransTime, 3);
    }

    public int setTlv(int tag, byte[] value, int ValueLen) {
        byte[] bcdTag = int2ByteArray(tag);
        byte[] buf = new byte[bcdTag.length + 1 + (value != null ? ValueLen : 0)];

        System.arraycopy(bcdTag, 0, buf, 0, bcdTag.length);
        if (value != null) {
            buf[bcdTag.length] = (byte) ValueLen;
            System.arraycopy(value, 0, buf, bcdTag.length + 1, ValueLen);
        } else {
            buf[bcdTag.length] = 0x00;
        }

        int ret = ClssDPASApi.Clss_SetTLVDataList_DPAS(buf, buf.length);
        Log.i(TAG, "ClssDPASApi.Clss_SetTLVDataList_DPAS() ret = " + ret + " buf = " + bcd2Str(buf));
        return ret;
    }

/*    private int setDETData(byte[] pucTag, int ucTagLen, byte[] pucData, int ucDataLen) {
        byte[] aucBuff = new byte[256];
        int ucBuffLen;
        int ret;

        if (pucTag == null || pucData == null)
        {
            return RetCode.CLSS_PARAM_ERR;
        }

        System.arraycopy(pucTag, 0, aucBuff, 0, ucTagLen);//Terminal Country Code
        ucBuffLen = ucTagLen;
        aucBuff[ucBuffLen++] = (byte) ucDataLen;
        System.arraycopy(pucData, 0, aucBuff, ucBuffLen, ucDataLen);
        ucBuffLen += ucDataLen;
        ret = ClssDPASApi.Clss_SetTLVDataList_DPAS(aucBuff, ucBuffLen);
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "aucBuff = "+bcd2Str(aucBuff)+", ucBuffLen = "+ucBuffLen);
       }
        Log.i(TAG, "ClssDPASApi.Clss_SetTLVDataList_DPAS(aucBuff, ucBuffLen), ret = "+ret);

        return ret;
    }*/

    public int getTlv(int tag, ByteArray value) {
        int ret;
        byte[] bcdTag = int2ByteArray(tag);

        ret = ClssDPASApi.Clss_GetTLVDataList_DPAS(bcdTag, (byte) bcdTag.length, value.length, value);
        Log.i(TAG, "Dpas getTlv  tag :" + tag
                + " value: " + bcd2Str(value.data).substring(0, 2 * value.length) + " ret :" + ret + "value.length = " + value.length);
        return ret;
    }

    private int processDpas(TransactionPath pathType) {
        int ret;

        ret = ClssDPASApi.Clss_ReadData_DPAS();
        if (ret != RetCode.EMV_OK) {
            Log.e(TAG, "ClssDPASApi.Clss_ReadData_DPAS error, ret = " + ret);
            return ret;
        }

        if (pathType.path == TransactionPath.CLSS_DPAS_EMV) {
            ret = setCAPK();
            if (ret != RetCode.EMV_OK) {
                Log.i(TAG, "setCAPK() ret = " + ret);
            }
        }

        ret = ClssDPASApi.Clss_TransProc_DPAS((byte) 0x00);
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "ClssDPASApi.Clss_TransProc_DPAS ret = " + ret);
            return ret;
        }

        sendOutcome.sendTransDataOutput((byte) 0x07);

        return ret;
    }

    private int setCAPK() {

        int iRet;
        EMV_CAPK stEMVCapk = new EMV_CAPK();
        ByteArray sAid = new ByteArray();
        ByteArray ucKeyIndex = new ByteArray();
        EMV_REVOCLIST tRevocList = new EMV_REVOCLIST();

        ClssDPASApi.Clss_DelAllRevocList_DPAS();
        ClssDPASApi.Clss_DelAllCAPK_DPAS();

        iRet = getTlv(0x4F, sAid);
        if (iRet != RetCode.EMV_OK) {
            Log.e(TAG, "ClssDPASApi.Clss_GetTLVDataList_DPAS( 0x4F, sAid) error, ret = " + iRet);
            return iRet;
        }

        iRet = getTlv(0x8F, ucKeyIndex);
        if (iRet != RetCode.EMV_OK) {
            Log.e(TAG, "ClssDPASApi.Clss_GetTLVDataList_DPAS (0x8F, ucKeyIndex) error, ret = " + iRet);
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

        iRet = ClssDPASApi.Clss_AddCAPK_DPAS(stEMVCapk);

        if (RetCode.EMV_OK != iRet) {
            Log.e(TAG, "ClssDPASApi.Clss_AddCAPK_DPAS(stEMVCapk) error, ret = " + iRet);
        }
        System.arraycopy(sAid.data, 0, tRevocList.ucRid, 0, 5);
        tRevocList.ucIndex = ucKeyIndex.data[0];
        System.arraycopy(new byte[]{0x00, 0x07, 0x11}, 0, tRevocList.ucCertSn, 0, 3);
        iRet = ClssDPASApi.Clss_AddRevocList_DPAS(tRevocList);
        if (RetCode.EMV_OK != iRet) {
            Log.e(TAG, "ClssDPASApi.Clss_AddRevocList_DPAS(tRevocList) , ret = " + iRet);
        }

        return iRet;
    }

    private void genTransResult(ACType acType) {
        switch (sendOutcome.outcomeParamSet.data[0] & 0xF0) {
            case 0x10:
                Log.i(TAG, "genTransResult CLSS_OC_APPROVED");
                acType.type = ACType.AC_TC;
                break;
            case 0x30:
                Log.i(TAG, "genTransResult CLSS_OC_ONLINE_REQUEST");
                acType.type = ACType.AC_ARQC;
                break;
            case 0x60:
                Log.i(TAG, "genTransResult CLSS_OC_TRY_ANOTHER_INTERFACE");
                acType.type = ACType.AC_AAC;
                break;
            case 0x20:
                Log.i(TAG, "genTransResult CLSS_OC_DECLINED");
                acType.type = ACType.AC_AAC;
                break;
            default:
                Log.i(TAG, "default genTransResult CLSS_OC_DECLINED");
                acType.type = ACType.AC_AAC;
                break;
        }
    }

    private int completeTrans(TransResult transResult, byte[] tag91, byte[] tag71, byte[] tag72) {
        int ret;

        byte[] script = combine917172(tag91, tag71, tag72);
        if (script == null)
            script = new byte[0];

        ret = ClssDPASApi.Clss_IssuerUpdateProc_DPAS(transResult.result, script, script.length);
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "ClssProcDpas Clss_IssuerUpdateProc_DPAS transResult.result = " + transResult.result + "ret =" + ret);
            return ret;
        }

        return RetCode.EMV_OK;

    }

    private byte[] combine917172(byte[] f91, byte[] f71, byte[] f72) {
        if (f91 == null || f91.length == 0)
            return combine7172(f71, f72);
        if (f71 == null || f71.length == 0)
            return f72;
        if (f72 == null || f72.length == 0)
            return f71;

        ByteBuffer bb = ByteBuffer.allocate(f91.length + f71.length + f72.length + 8);

        bb.put((byte) 0x91);
        bb.put((byte) f91.length); //fix 16
        bb.put(f91, 0, f91.length);

        byte[] f7172 = combine7172(f71, f72);
        bb.put(f7172, 0, f7172.length);

        int len = bb.position();
        bb.position(0);

        byte[] script = new byte[len];
        bb.get(script, 0, len);

        return script;
    }

    private byte[] combine7172(byte[] f71, byte[] f72) {
        if (f71 == null || f71.length == 0)
            return f72;
        if (f72 == null || f72.length == 0)
            return f71;

        ByteBuffer bb = ByteBuffer.allocate(f71.length + f72.length + 6);

        bb.put((byte) 0x71);
        if (f71.length > 127)
            bb.put((byte) 0x81);
        bb.put((byte) f71.length);
        bb.put(f71, 0, f71.length);

        bb.put((byte) 0x72);
        if (f72.length > 127)
            bb.put((byte) 0x81);
        bb.put((byte) f72.length);
        bb.put(f72, 0, f72.length);

        int len = bb.position();
        bb.position(0);

        byte[] script = new byte[len];
        bb.get(script, 0, len);

        return script;
    }

    private int onIssScrCon() {
        ApduSendL2 apduSendL2 = new ApduSendL2();
        ApduRespL2 apduRespL2 = new ApduRespL2();
        byte[] sendCommand = new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00};
        System.arraycopy(sendCommand, 0, apduSendL2.command, 0, sendCommand.length);
        apduSendL2.lc = 14;
        String sendDataIn = "1PAY.SYS.DDF01";
        System.arraycopy(sendDataIn.getBytes(), 0, apduSendL2.dataIn, 0, sendDataIn.getBytes().length);
        apduSendL2.le = 256;
        int ret = (int) DeviceImplNeptune.getInstance().iccCommand(apduSendL2, apduRespL2);
        if (ret != RetCode.EMV_OK)
            return ret;

        if (apduRespL2.swa != (byte) 0x90 || apduRespL2.swb != 0x00)
            return RetCode.EMV_RSP_ERR;

        apduSendL2 = new ApduSendL2();
        apduRespL2 = new ApduRespL2();
        System.arraycopy(sendCommand, 0, apduSendL2.command, 0, sendCommand.length);
        apduSendL2.lc = 14;
        // System.arraycopy(transData.getAid().getBytes(), 0, apduSendL2.dataIn, 0, transData.getAid().getBytes().length);
        apduSendL2.le = 256;
        ret = (int) DeviceImplNeptune.getInstance().iccCommand(apduSendL2, apduRespL2);
        if (ret != RetCode.EMV_OK)
            return ret;

        if (apduRespL2.swa != (byte) 0x90 || apduRespL2.swb != 0x00)
            return RetCode.EMV_RSP_ERR;

        return RetCode.EMV_OK;
    }

    private int processTranResult(ACType acType, TransResult transResult) {
        getCVMResultDPAS(cvmType);

        if (acType.type == ACType.AC_ARQC) {             //ARQC
            transResult.result = TransResult.EMV_ARQC;
            return RetCode.EMV_OK;

        } else if (acType.type == ACType.AC_TC) {       //TC
            transResult.result = TransResult.EMV_OFFLINE_APPROVED;
            return RetCode.EMV_OK;
        } else if (acType.type == ACType.AC_AAC) {     //AAC
            transResult.result = TransResult.EMV_OFFLINE_DENIED;
            return RetCode.CLSS_DECLINE;

        } else {
            transResult.result = TransResult.EMV_ABORT_TERMINATED;
            return RetCode.CLSS_TERMINATE;
        }
    }

    private class ClssDpassSendOutcome {
        ByteArray outcomeParamSet = new ByteArray(8);
        ByteArray userInterReqData = new ByteArray(22);
        ByteArray errIndication = new ByteArray(6);

        void sendTransDataOutput(byte b) {
            if ((b & 0x01) != 0) {
                getTlv(TagsTable.LIST, outcomeParamSet);
            }

            if ((b & 0x04) != 0) {
                getTlv(0xDF8116, userInterReqData);
            }

            if ((b & 0x02) != 0) {
                getTlv(0xDF8115, errIndication);
            }
        }
    }

}
