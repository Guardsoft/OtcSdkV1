package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils;

import android.content.Context;
import android.util.Log;

import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clssjspeedy.model.Clss_JcbAidParam;
import com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clsspure.trans.model.Clss_PureAidParam;
import com.pax.jemv.amex.model.CLSS_AEAIDPARAM;
import com.pax.jemv.clcommon.ClssTmAidList;
import com.pax.jemv.clcommon.Clss_MCAidParam;
import com.pax.jemv.clcommon.Clss_PreProcInfo;
import com.pax.jemv.clcommon.EMV_CAPK;
import com.pax.jemv.clcommon.KernType;
import com.pax.jemv.qpboc.model.Clss_PbocAidParam;

import org.dtools.ini.BasicIniFile;
import org.dtools.ini.IniFile;
import org.dtools.ini.IniFileReader;
import org.dtools.ini.IniSection;

import java.io.File;
import java.io.IOException;

import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils.str2Bcd;


/**
 * Created by Administrator on 2017/3/16 0016.
 */

public class FileParse {
    private static final String TAG = "FileParse";
    private static final String RID_VISA = "A000000003";
    private static final String RID_MC = "A000000004";
    private static final String RID_PBOC = "A000000333";
    private static final String RID_AE = "A000000025";
    private static final String RID_DPAS1 = "A000000152";
    private static final String RID_DPAS2 = "A000000324";
    private static final String RID_JCB = "A000000065";
    private static final String RID_PURE = "D999999999";

    public static final int PARSE_ERR = -1;
    public static final int PARSE_SUCCESS = 0;

    private static ClssTmAidList[] tmAidLists;
    private static Clss_PreProcInfo[] preProcInfos;
    private static Clss_MCAidParam[] mcAidParams;
    private static CLSS_AEAIDPARAM[] aeAidParams;
    private static Clss_PbocAidParam[] pbocAidParams;
    private static Clss_JcbAidParam[] jcbAidParams;
    private static Clss_PureAidParam[] pureAidParams;
    private static EMV_CAPK[] mEmvCapk;

    public static ClssTmAidList[] getTmAidLists() {
        return tmAidLists;
    }

    public static EMV_CAPK[] getmEmvCapk() {
        return mEmvCapk;
    }

    public static Clss_MCAidParam[] getMcAidParams() {
        return mcAidParams;
    }

    public static CLSS_AEAIDPARAM[] getAeAidParams() {
        return aeAidParams;
    }

    public static Clss_PbocAidParam[] getPbocAidParams() {
        return pbocAidParams;
    }

    public static Clss_JcbAidParam[] getJcbAidParams() {
        return jcbAidParams;
    }

    public static Clss_PureAidParam[] getPureAidParams() {
        return pureAidParams;
    }


    public static Clss_PreProcInfo[] getPreProcInfos() {
        return preProcInfos;
    }

    public static int parseAidFromAssets(Context context, String fileName) {
        String file = context.getFilesDir().getPath() + "/" + fileName;
        boolean flag = false;

        flag = FileUtils.copyFileFromAssert(context, fileName, file);

        if (flag) {
            IniFile iniFile = new BasicIniFile();
            File aidFile = new File(file);
            IniFileReader rad = new IniFileReader(iniFile, aidFile);
            try {
                //读取item
                rad.read();
                IniSection iniSection = iniFile.getSection(0);
                String aidNum = iniSection.getItem(0).getValue();
                tmAidLists = new ClssTmAidList[Integer.parseInt(aidNum)];
                preProcInfos = new Clss_PreProcInfo[Integer.parseInt(aidNum)];
                mcAidParams = new Clss_MCAidParam[Integer.parseInt(aidNum)];
                aeAidParams = new CLSS_AEAIDPARAM[Integer.parseInt(aidNum)];
                pbocAidParams = new Clss_PbocAidParam[Integer.parseInt(aidNum)];
                jcbAidParams = new Clss_JcbAidParam[Integer.parseInt(aidNum)];
                pureAidParams = new Clss_PureAidParam[Integer.parseInt(aidNum)];

                for (int i = 0; i < Integer.parseInt(aidNum); i++) {
                    IniSection iniSection1 = iniFile.getSection(i + 1);
                    //for (int j = 0; j < iniSection1.getNumberOfItems(); j++) {
                    tmAidLists[i] = new ClssTmAidList();
                    tmAidLists[i].ucAidLen = (byte) Integer.parseInt(iniSection1.getItem(2).getValue());
                    if (iniSection1.getItem(1).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(1).getValue()), 0,
                                tmAidLists[i].aucAID, 0, tmAidLists[i].ucAidLen);
                        //Log.d(TAG, "aid: " + Utils.bcd2Str(tmAidLists[i].aucAID));
                    }

                    String ridBuf = iniSection1.getItem(1).getValue().substring(0, 10);
                    //Log.d(TAG, "ridBuf: " + ridBuf);
                    if (ridBuf.equals(RID_PBOC))
                        tmAidLists[i].ucKernType = KernType.KERNTYPE_PBOC;
                    else if (ridBuf.equals(RID_VISA))
                        tmAidLists[i].ucKernType = KernType.KERNTYPE_VIS;
                    else if (ridBuf.equals(RID_MC))
                        tmAidLists[i].ucKernType = KernType.KERNTYPE_MC;
                    else if (ridBuf.equals(RID_AE))
                        tmAidLists[i].ucKernType = KernType.KERNTYPE_AE;
                    else if (ridBuf.equals(RID_DPAS1))
                        tmAidLists[i].ucKernType = KernType.KERNTYPE_ZIP;
                    else if (ridBuf.equals(RID_DPAS2))
                        tmAidLists[i].ucKernType = KernType.KERNTYPE_ZIP;
                    else if (ridBuf.equals(RID_JCB))
                        tmAidLists[i].ucKernType = KernType.KERNTYPE_JCB;
                    else if (ridBuf.equals(RID_PURE))
                        tmAidLists[i].ucKernType = KernType.KERNTYPE_PURE;
                    else
                        tmAidLists[i].ucKernType = KernType.KERNTYPE_DEF;
                    tmAidLists[i].ucSelFlg = (byte) Integer.parseInt(iniSection1.getItem(3).getValue());

                    preProcInfos[i] = new Clss_PreProcInfo();
                    preProcInfos[i].ucAidLen = (byte) Integer.parseInt(iniSection1.getItem(2).getValue());
                    if (iniSection1.getItem(1).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(1).getValue()), 0,
                                preProcInfos[i].aucAID, 0, tmAidLists[i].ucAidLen);
                    }
                    //????
//                    System.arraycopy(DeviceImpl.str2Bcd(iniSection1.getItem(29).getValue()), 0,
//                            preProcInfos[i].aucReaderTTQ, 0, 4);
//????????????????????????????????????????????????
//                  preProcInfos[j].ucCrypto17Flg = ;

                    if (ridBuf.equals(RID_PBOC))
                        preProcInfos[i].ucKernType = KernType.KERNTYPE_PBOC;
                    else if (ridBuf.equals(RID_VISA))
                        preProcInfos[i].ucKernType = KernType.KERNTYPE_VIS;
                    else if (ridBuf.equals(RID_MC))
                        preProcInfos[i].ucKernType = KernType.KERNTYPE_MC;
                    else if (ridBuf.equals(RID_AE))
                        preProcInfos[i].ucKernType = KernType.KERNTYPE_AE;
                    else if (ridBuf.equals(RID_DPAS1))
                        preProcInfos[i].ucKernType = KernType.KERNTYPE_ZIP;
                    else if (ridBuf.equals(RID_DPAS2))
                        preProcInfos[i].ucKernType = KernType.KERNTYPE_ZIP;
                    else if (ridBuf.equals(RID_JCB))
                        preProcInfos[i].ucKernType = KernType.KERNTYPE_JCB;
                    else if (ridBuf.equals(RID_PURE))
                        preProcInfos[i].ucKernType = KernType.KERNTYPE_PURE;
                    else
                        preProcInfos[i].ucKernType = KernType.KERNTYPE_DEF;
                    preProcInfos[i].ucRdClssFLmtFlg = (byte) Integer.parseInt(iniSection1.getItem(11).getValue());
                    preProcInfos[i].ucRdClssTxnLmtFlg = (byte) Integer.parseInt(iniSection1.getItem(12).getValue());
                    preProcInfos[i].ucRdCVMLmtFlg = (byte) Integer.parseInt(iniSection1.getItem(13).getValue());
                    //????
                    preProcInfos[i].ucStatusCheckFlg = (byte) Integer.parseInt(iniSection1.getItem(29).getValue());
                    preProcInfos[i].ucTermFLmtFlg = (byte) Integer.parseInt(iniSection1.getItem(10).getValue());
                    //?????
                    preProcInfos[i].ucZeroAmtNoAllowed = (byte) Integer.parseInt(iniSection1.getItem(30).getValue());
                    preProcInfos[i].ulRdClssFLmt = Long.parseLong(iniSection1.getItem(9).getValue());
                    preProcInfos[i].ulRdClssTxnLmt = Long.parseLong(iniSection1.getItem(8).getValue());
                    preProcInfos[i].ulRdCVMLmt = Long.parseLong(iniSection1.getItem(7).getValue());
                    preProcInfos[i].ulTermFLmt = Long.parseLong(iniSection1.getItem(6).getValue());
                    //preProcInfos[i].ucOnlinePin = (byte) Integer.parseInt(iniSection1.getItem(5).getValue());

                    mcAidParams[i] = new Clss_MCAidParam();
                    aeAidParams[i] = new CLSS_AEAIDPARAM();
                    pbocAidParams[i] = new Clss_PbocAidParam();
                    jcbAidParams[i] = new Clss_JcbAidParam();
                    pureAidParams[i] = new Clss_PureAidParam();

                    if (iniSection1.getItem(24).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(24).getValue()), 0,
                                mcAidParams[i].acquierId, 0, str2Bcd(iniSection1.getItem(24).getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem(24).getValue()), 0,
                                aeAidParams[i].AcquierId, 0, str2Bcd(iniSection1.getItem(24).getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem(24).getValue()), 0,
                                jcbAidParams[i].acquierId, 0, str2Bcd(iniSection1.getItem(24).getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem(24).getValue()), 0,
                                pureAidParams[i].acquierId, 0, str2Bcd(iniSection1.getItem(24).getValue()).length);
                    }
                    //Log.d(TAG, "AcquierId: " + str2Bcd(iniSection1.getItem(24).getValue()));

                    if (iniSection1.getItem(25).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(25).getValue()), 0,
                                mcAidParams[i].dDOL, 0, str2Bcd(iniSection1.getItem(25).getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem(25).getValue()), 0,
                                aeAidParams[i].dDOL, 0, str2Bcd(iniSection1.getItem(25).getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem(25).getValue()), 0,
                                pureAidParams[i].dDOL, 0, str2Bcd(iniSection1.getItem(25).getValue()).length);
                        pureAidParams[i].dDolLen = str2Bcd(iniSection1.getItem(25).getValue()).length;
                    }

                    mcAidParams[i].floorLimit = Long.parseLong(iniSection1.getItem(14).getValue());
                    aeAidParams[i].FloorLimit = Long.parseLong(iniSection1.getItem(14).getValue());
                    pbocAidParams[i].ulTermFLmt = Long.parseLong(iniSection1.getItem(14).getValue());

                    mcAidParams[i].floorLimitCheck = (byte) Integer.parseInt(iniSection1.getItem(15).getValue());
                    aeAidParams[i].FloorLimitCheck = (byte) Integer.parseInt(iniSection1.getItem(15).getValue());
                    //Log.d(TAG, "record 1 ");

                    //mcAidParams[j].forceOnline;
                    //mcAidParams[j].magAvn;
                    mcAidParams[i].maxTargetPer = (byte) Integer.parseInt(iniSection1.getItem(18).getValue());
                    jcbAidParams[i].maxTargetPer = (byte) Integer.parseInt(iniSection1.getItem(18).getValue());

                    mcAidParams[i].randTransSel = (byte) Integer.parseInt(iniSection1.getItem(19).getValue());

                    if (iniSection1.getItem(23).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(23).getValue()), 0,
                                mcAidParams[i].tacDefault, 0, str2Bcd(iniSection1.getItem(23).getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem(23).getValue()), 0,
                                aeAidParams[i].TACDefault, 0, str2Bcd(iniSection1.getItem(23).getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem(23).getValue()), 0,
                                jcbAidParams[i].tacDefault, 0, str2Bcd(iniSection1.getItem(23).getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem(23).getValue()), 0,
                                pureAidParams[i].tacDefault, 0, str2Bcd(iniSection1.getItem(23).getValue()).length);
                    }

                    if (iniSection1.getItem(21).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(21).getValue()), 0,
                                mcAidParams[i].tacDenial, 0, str2Bcd(iniSection1.getItem(21).getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem(21).getValue()), 0,
                                aeAidParams[i].TACDenial, 0, str2Bcd(iniSection1.getItem(21).getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem(21).getValue()), 0,
                                jcbAidParams[i].tacDenial, 0, str2Bcd(iniSection1.getItem(21).getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem(21).getValue()), 0,
                                pureAidParams[i].tacDenial, 0, str2Bcd(iniSection1.getItem(21).getValue()).length);
                    }

                    if (iniSection1.getItem(22).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(22).getValue()), 0,
                                mcAidParams[i].tacOnline, 0, str2Bcd(iniSection1.getItem(22).getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem(22).getValue()), 0,
                                aeAidParams[i].TACOnline, 0, str2Bcd(iniSection1.getItem(22).getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem(22).getValue()), 0,
                                jcbAidParams[i].tacOnline, 0, str2Bcd(iniSection1.getItem(22).getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem(22).getValue()), 0,
                                pureAidParams[i].tacOnline, 0, str2Bcd(iniSection1.getItem(22).getValue()).length);
                    }
                    //Log.d(TAG, "record 2 ");
                    if (ridBuf.equals(RID_AE) || ridBuf.equals(RID_JCB)) {
                        mcAidParams[i].targetPer = (byte) Integer.parseInt(iniSection1.getItem(17).getValue());
                        jcbAidParams[i].targetPer = (byte) Integer.parseInt(iniSection1.getItem(17).getValue());
                    } else if (ridBuf.equals(RID_PURE)) {
                        if (!iniSection1.getItem(17).getValue().equals("")) {
                            pureAidParams[i].ioOption[0] = str2Bcd(iniSection1.getItem(17).getValue())[0];//16进制
                        }
                    }

                    if (ridBuf.equals(RID_AE) || ridBuf.equals(RID_JCB)) {
                        mcAidParams[i].threshold = (byte) Integer.parseInt(iniSection1.getItem(16).getValue());
                        jcbAidParams[i].threshold = (byte) Integer.parseInt(iniSection1.getItem(16).getValue());
                    } else if (ridBuf.equals(RID_PURE)) {
                        if (!iniSection1.getItem(16).getValue().equals("")) {
                            if (iniSection1.getItem(16).getValue().length() == 2)
                                pureAidParams[i].appAuthType[0] = str2Bcd(iniSection1.getItem(16).getValue())[0];//16进制
                        }
                    }

                    if (iniSection1.getItem(26).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(26).getValue()), 0,
                                mcAidParams[i].tDOL, 0, str2Bcd(iniSection1.getItem(26).getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem(26).getValue()), 0,
                                aeAidParams[i].tDOL, 0, str2Bcd(iniSection1.getItem(26).getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem(26).getValue()), 0,
                                pureAidParams[i].mtDOL, 0, str2Bcd(iniSection1.getItem(26).getValue()).length);
                        pureAidParams[i].mtDolLen = str2Bcd(iniSection1.getItem(26).getValue()).length;
                    }

                    //Log.d(TAG, "record 4 ");
                    //mcAidParams[j].ucMagSupportFlg;
                    //mcAidParams[j].uDOL;
                    //mcAidParams[j].usUDOLLen;
                    mcAidParams[i].velocityCheck = (byte) Integer.parseInt(iniSection1.getItem(20).getValue());

                    if (iniSection1.getItem(27).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(27).getValue()), 0,
                                mcAidParams[i].version, 0, str2Bcd(iniSection1.getItem(27).getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem(27).getValue()), 0,
                                aeAidParams[i].Version, 0, str2Bcd(iniSection1.getItem(27).getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem(27).getValue()), 0,
                                pureAidParams[i].Version, 0, str2Bcd(iniSection1.getItem(27).getValue()).length);
                    }
                    //Log.d(TAG, "record 3 ");
                    if (iniSection1.getItem(28).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(28).getValue()), 0,
                                pureAidParams[i].aTOL, 0, str2Bcd(iniSection1.getItem(28).getValue()).length);
                        pureAidParams[i].aTolLen = str2Bcd(iniSection1.getItem(28).getValue()).length;
                    }

                    if (!iniSection1.getItem(31).getValue().equals("")) {
                        aeAidParams[i].ucAETermCap = str2Bcd(iniSection1.getItem(31).getValue())[0];//16进制
                        System.arraycopy(str2Bcd(iniSection1.getItem(31).getValue()), 0,
                                pureAidParams[i].atdTOL, 0, str2Bcd(iniSection1.getItem(31).getValue()).length);
                        pureAidParams[i].atdTolLen = str2Bcd(iniSection1.getItem(31).getValue()).length;
                    }
                    //Log.d(TAG, "aid param:  " + i + "Finish");

                    //}
                }
//			            iniItem.setValue("Konan");
//			            iniSection.addItem(iniItem);
//			            iniFile.addSection(iniSection);
//			            wir.write();
            } catch (IOException e) {// TODO Auto-generated catch block
                Log.e("parseAidFromAssets", e.getMessage());
                //e.printStackTrace();
                return PARSE_ERR;
            }
            return PARSE_SUCCESS;
        }
        return PARSE_ERR;
    }

    public static int parseCapkFromAssets(Context context, String fileName) {
        String file = context.getFilesDir().getPath() + "/" + fileName;
        boolean flag = false;

        flag = FileUtils.copyFileFromAssert(context, fileName, file);

        if (flag) {
            IniFile iniFile = new BasicIniFile();
            File capkFile = new File(file);
            IniFileReader rad = new IniFileReader(iniFile, capkFile);
            try {
                //读取item
                rad.read();
                IniSection iniSection = iniFile.getSection(0);
                String capkNum = iniSection.getItem(0).getValue();
                mEmvCapk = new EMV_CAPK[Integer.parseInt(capkNum)];

                for (int i = 0; i < Integer.parseInt(capkNum); i++) {
                    IniSection iniSection1 = iniFile.getSection(i + 1);
                    mEmvCapk[i] = new EMV_CAPK();

                    if (iniSection1.getItem(0).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(0).getValue()), 0,
                                mEmvCapk[i].rID, 0, str2Bcd(iniSection1.getItem(0).getValue()).length);
                    }
                    if (!iniSection1.getItem(1).getValue().equals("")) {
                        //mEmvCapk[i].keyID = (byte) Integer.parseInt(iniSection1.getItem(1).getValue());
                        mEmvCapk[i].keyID = str2Bcd(iniSection1.getItem(1).getValue())[0];//16进制
                    }

                    if (!iniSection1.getItem(2).getValue().equals("")) {
                        mEmvCapk[i].hashInd = str2Bcd(iniSection1.getItem(2).getValue())[0];
                    }

                    if (!iniSection1.getItem(3).getValue().equals("")) {
                        mEmvCapk[i].arithInd = str2Bcd(iniSection1.getItem(3).getValue())[0];
                    }

                    if (!iniSection1.getItem(4).getValue().equals("")) {
                        mEmvCapk[i].modulLen = (short) Integer.parseInt(iniSection1.getItem(4).getValue());
                    }


                    if (iniSection1.getItem(5).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(5).getValue()), 0,
                                mEmvCapk[i].modul, 0, str2Bcd(iniSection1.getItem(5).getValue()).length);
                    }

                    if (!iniSection1.getItem(6).getValue().equals("")) {
                        mEmvCapk[i].exponentLen = (byte) Integer.parseInt(iniSection1.getItem(6).getValue());
                    }

                    if (iniSection1.getItem(7).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(7).getValue()), 0,
                                mEmvCapk[i].exponent, 0, str2Bcd(iniSection1.getItem(7).getValue()).length);
                    }

                    if (iniSection1.getItem(8).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(8).getValue()), 0,
                                mEmvCapk[i].expDate, 0, str2Bcd(iniSection1.getItem(8).getValue()).length);
                    }

                    if (iniSection1.getItem(9).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(9).getValue()), 0, mEmvCapk[i].checkSum, 0,
                                str2Bcd(iniSection1.getItem(9).getValue()).length);
                    }

                }
//			            iniItem.setValue("Konan");
//			            iniSection.addItem(iniItem);
//			            iniFile.addSection(iniSection);
//			            wir.write();
            } catch (IOException e) {// TODO Auto-generated catch block
                Log.e("parseCapkFromAssets", e.getMessage());
                //e.printStackTrace();
                return PARSE_ERR;
            }
            return PARSE_SUCCESS;
        }
        return PARSE_ERR;
    }

}
