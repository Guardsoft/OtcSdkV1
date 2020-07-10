package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.device;

import android.util.Log;

import com.otc.sdk.pax.a920.IConvert;
import com.otc.sdk.pax.a920.OtcApplication;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.pay.Constants;
import com.pax.dal.IPed;
import com.pax.dal.entity.DUKPTResult;
import com.pax.dal.entity.EAesCheckMode;
import com.pax.dal.entity.EBeepMode;
import com.pax.dal.entity.ECheckMode;
import com.pax.dal.entity.ECryptOperate;
import com.pax.dal.entity.ECryptOpt;
import com.pax.dal.entity.EDUKPTPinMode;
import com.pax.dal.entity.EPedKeyType;
import com.pax.dal.entity.EPedType;
import com.pax.dal.entity.EPinBlockMode;
import com.pax.dal.exceptions.PedDevException;

public class Device {
    private static final String TAG = "Device";
    private static byte[] byte_test = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

    private Device() {

    }

    /**
     * beep 成功
     */
    public static void beepOk() {
        OtcApplication.getDal().getSys().beep(EBeepMode.FREQUENCE_LEVEL_3, 100);
        OtcApplication.getDal().getSys().beep(EBeepMode.FREQUENCE_LEVEL_4, 100);
        OtcApplication.getDal().getSys().beep(EBeepMode.FREQUENCE_LEVEL_5, 100);
    }

    /**
     * beep 失败
     */
    public static void beepErr() {
        OtcApplication.getDal().getSys().beep(EBeepMode.FREQUENCE_LEVEL_6, 200);
    }

    /**
     * beep 提示音
     */

    public static void beepPromt() {
        OtcApplication.getDal().getSys().beep(EBeepMode.FREQUENCE_LEVEL_6, 50);
    }


    public static boolean writeTMK(byte[] tmkValue) {
        // write TMK
        try {
            OtcApplication.getDal().getPed(EPedType.INTERNAL).writeKey(EPedKeyType.TLK, (byte) 0,
                    EPedKeyType.TMK, Constants.INDEX_TMK,
                    tmkValue, ECheckMode.KCV_NONE, null);
            return true;
        } catch (PedDevException e) {
            Log.w("writeTMK", e);
        }
        return false;
    }

    public static boolean writeTPK(byte[] tpkValue, byte[] tpkKcv) {
        try {
            //int mKeyIndex = Utils.getMainKeyIndex(FinancialApplication.getSysParam().get(SysParam.NumberParam.MK_INDEX));
            ECheckMode checkMode = ECheckMode.KCV_ENCRYPT_0;
            if (tpkKcv == null || tpkKcv.length == 0) {
                checkMode = ECheckMode.KCV_NONE;
            }
            OtcApplication.getDal().getPed(EPedType.INTERNAL).writeKey(EPedKeyType.TMK, Constants.INDEX_TMK,
                    EPedKeyType.TPK, Constants.INDEX_TPK, tpkValue, checkMode, tpkKcv);
            return true;
        } catch (PedDevException e) {
            Log.w("writeTPK", e);
        }
        return false;
    }

    public static boolean writeTIKFuc(byte[] keyValue, byte[] ksn) {
        // write TIK
        try {
            OtcApplication.getDal().getPed(EPedType.INTERNAL).writeTIK(Constants.INDEX_TIK, (byte) 0,
                    keyValue, ksn, ECheckMode.KCV_NONE, null);
            return true;
        } catch (PedDevException e) {
            Log.w("writeTIKFuc", e);
        }
        return false;
    }


    /**
     * 计算pinblock(包括国密)
     *
     * @param panBlock
     * @return
     * @throws PedDevException
     */
    public static byte[] getPinBlock(String panBlock) throws PedDevException {
        IPed ped = OtcApplication.getDal().getPed(EPedType.INTERNAL);
//        String supportSm = TradeApplication.sysParam.get(SysParam.SUPPORT_SM);
//        if (supportSm.equals(SysParam.Constant.YES)) { // 国密
//            return ped.getPinBlockSM4(Constants.INDEX_TPK, "0,4,5,6,7,8,9,10,11,12", panBlock.getBytes(),
//                    EPinBlockMode.ISO9564_0, 60 * 1000);
//        } else {
//            return ped.getPinBlock(Constants.INDEX_TPK, "0,4,5,6,7,8,9,10,11,12", panBlock.getBytes(),
//                    EPinBlockMode.ISO9564_0, 60 * 1000);
//        }
        //IPed ped = FinancialApplication.getDal().getPed(EPedType.INTERNAL);

        //ped.setKeyboardLayoutLandscape(true);
        return ped.getPinBlock(Constants.INDEX_TPK2, "0,4,5,6,7,8,9,10,11,12", panBlock.getBytes(),
                EPinBlockMode.ISO9564_0, 60 * 1000);
    }

    public static DUKPTResult getDUKPTPin(String panBlock) throws PedDevException {
        IPed ped = OtcApplication.getDal().getPed(EPedType.INTERNAL);

        return ped.getDUKPTPin(Constants.INDEX_TIK, "0,4,5,6,7,8,9,10,11,12", panBlock.getBytes(),
                EDUKPTPinMode.ISO9564_0_INC, 60 * 1000);
    }

    public static String decrypt3DesECB(String value, int indexTDK) {
        byte [] initVector = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}; //init vector of CBC


        int DECRYPT_ECB = 0;
        int ENCRYPT_ECB = 1;
        int DECRYPT_CBC = 2;
        int ENCRYPT_CBC = 3;

        try {
            byte [] valueByte = OtcApplication.getConvert()
                    .strToBcd(value, IConvert.EPaddingPosition.PADDING_LEFT);

            byte [] result =  OtcApplication.getDal().getPed(EPedType.INTERNAL)
                    .calcDes(
                            (byte)indexTDK,
                            null,
                            valueByte,
                            (byte)DECRYPT_ECB);

            return OtcApplication.getConvert().bcdToStr(result);
        } catch (PedDevException e) {
            Log.w("writeTMK", e);
        }
        return null;
    }

    public static String encryptAES_CBC(String value, int indexTAES) {
        byte [] initVector = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}; //init vector of CBC

        try {
            byte [] valueByte = OtcApplication.getConvert()
                    .strToBcd(value, IConvert.EPaddingPosition.PADDING_LEFT);

            byte [] result =  OtcApplication.getDal()
                    .getPed(EPedType.INTERNAL)
                    .calcAes(
                            (byte)indexTAES,
                            initVector,
                            valueByte,
                            ECryptOperate.ENCRYPT,
                            ECryptOpt.CBC);

            return OtcApplication.getConvert().bcdToStr(result);
        } catch (PedDevException e) {
            Log.w("writeTMK", e);
        }
        return null;
    }

    //**************  otc


    public static String decrypt3DesCBC(String value, int indexTDK) {
        byte [] initVector = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}; //init vector of CBC


        int DECRYPT_ECB = 0;
        int ENCRYPT_ECB = 1;
        int DECRYPT_CBC = 2;
        int ENCRYPT_CBC = 3;

        try {
            byte [] valueByte = OtcApplication.getConvert()
                    .strToBcd(value, IConvert.EPaddingPosition.PADDING_LEFT);

            byte [] result =  OtcApplication.getDal().getPed(EPedType.INTERNAL)
                    .calcDes(
                            (byte)indexTDK,
                            initVector,
                            valueByte,
                            (byte)DECRYPT_CBC);

            return OtcApplication.getConvert().bcdToStr(result);
        } catch (PedDevException e) {
            Log.w("writeTMK", e);
        }
        return null;
    }

    public static String encrypt3DesEBC(String value, int indexTDK) {
        byte [] initVector = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}; //init vector of CBC


        int DECRYPT_ECB = 0;
        int ENCRYPT_ECB = 1;
        int DECRYPT_CBC = 2;
        int ENCRYPT_CBC = 3;

        try {
            byte [] valueByte = OtcApplication.getConvert()
                    .strToBcd(value, IConvert.EPaddingPosition.PADDING_LEFT);

            byte [] result =  OtcApplication.getDal().getPed(EPedType.INTERNAL)
                    .calcDes(
                            (byte)indexTDK,
                            null,
                            valueByte,
                            (byte)ENCRYPT_ECB);

            return OtcApplication.getConvert().bcdToStr(result);
        } catch (PedDevException e) {
            Log.w("writeTMK", e);
        }
        return null;
    }

    public static boolean writeTAESK2(int indexTmk, int indexTAesk , byte[] tpkValue) {
//***
        try {
            OtcApplication.getDal()
                    .getPed(EPedType.INTERNAL)
                    .writeAesKey(
                            EPedKeyType.TMK,
                            (byte)indexTmk,
                            (byte)indexTAesk,
                            tpkValue,
                            EAesCheckMode.KCV_NONE,
                            null);
            return true;
        } catch (PedDevException e) {
            Log.w("writeTPK", e);
        }
        return false;
    }

    public static boolean writeTAESK3(int indexTAesk , byte[] tpkValue) {
//***
        try {
            OtcApplication.getDal()
                    .getPed(EPedType.INTERNAL)
                    .writeAesKey(
                            EPedKeyType.TLK,
                            (byte)1,
                            (byte)indexTAesk,
                            tpkValue,
                            EAesCheckMode.KCV_NONE,
                            null);
            return true;
        } catch (PedDevException e) {
            Log.w("writeTPK", e);
        }
        return false;
    }


    public static boolean writeTPK2(int tmkIndex, int tpkIndex, byte[] tpkValue) {
        try {
            OtcApplication.getDal()
                    .getPed(EPedType.INTERNAL)
                    .writeKey(
                            EPedKeyType.TMK,
                            (byte) tmkIndex,
                            EPedKeyType.TPK,
                            (byte) tpkIndex,
                            tpkValue,
                            ECheckMode.KCV_NONE,
                            null);
            return true;
        } catch (PedDevException e) {
            Log.w("writeTPK", e);
        }
        return false;
    }

    public static byte[] getKCV_TPK(byte keyIndex) {

        IPed ped = OtcApplication.getDal().getPed(EPedType.INTERNAL);
        try {
            byte[] bytes_tdk = ped.getKCV(EPedKeyType.TPK, keyIndex, (byte) 0, byte_test);
            Log.i("getKCV_TPK:slot"+ keyIndex, OtcApplication.getConvert().bcdToStr(bytes_tdk));
            return bytes_tdk;
        } catch (PedDevException e) {
            e.printStackTrace();
            Log.e("getKCV_TPK:slot"+ keyIndex, e.toString());
        }
        return null;
    }

    public static boolean writeTDK(int tmkIndex, int tdkIndex, byte[] tdkValue) {
        // write TMK
        try {
            OtcApplication.getDal().getPed(EPedType.INTERNAL)
                    .writeKey(
                            EPedKeyType.TMK,
                            (byte) tmkIndex,
                            EPedKeyType.TDK,
                            (byte) tdkIndex,
                            tdkValue,
                            ECheckMode.KCV_NONE,
                            null);

            return true;
        } catch (PedDevException e) {
            Log.w(TAG, e);
        }
        return false;
    }

    public static byte[] getKCV_TDK(byte keyIndex) {

        IPed ped = OtcApplication.getDal().getPed(EPedType.INTERNAL);
        try {
            byte[] bytes_tdk = ped.getKCV(EPedKeyType.TDK, keyIndex, (byte) 0, byte_test);
            Log.i("getKCV_TDK:slot"+ keyIndex, OtcApplication.getConvert().bcdToStr(bytes_tdk));
            return bytes_tdk;
        } catch (PedDevException e) {
            e.printStackTrace();
            Log.e("getKCV_TDK:slot"+ keyIndex, e.toString());
        }
        return null;
    }

    public static String encryptAES(String value, int indexTAES) {
        byte [] initVector = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}; //init vector of CBC

        try {
            byte [] valueByte = OtcApplication.getConvert()
                    .strToBcd(value, IConvert.EPaddingPosition.PADDING_LEFT);

            byte [] result =  OtcApplication.getDal()
                    .getPed(EPedType.INTERNAL)
                    .calcAes(
                            (byte)indexTAES,
                            initVector,
                            valueByte,
                            ECryptOperate.ENCRYPT,
                            ECryptOpt.CBC);

            return OtcApplication.getConvert().bcdToStr(result);
        } catch (PedDevException e) {
            Log.w("writeTMK", e);
        }
        return null;
    }

    //**********************************************************************************************
    public static boolean writeTLK(byte[] tlkValue) {

        try {
            OtcApplication.getDal().getPed(EPedType.EXTERNAL_TYPEA).setExMode(0x07);

            OtcApplication.getDal().getPed(EPedType.INTERNAL).writeKey(
                    EPedKeyType.TLK,
                    (byte) 0,
                    EPedKeyType.TLK,
                    (byte) 1,
                    tlkValue,
                    ECheckMode.KCV_NONE, null);

            return true;
        } catch (PedDevException e) {
            Log.w(TAG, e);
        }
        return false;
    }

    public static boolean cleanKeys() {
        try {
            OtcApplication.getDal().getPed(EPedType.INTERNAL).erase();
            return true;
        } catch (PedDevException e) {
            Log.w(TAG, e);
        }
        return false;
    }


    public static byte[] getKCV_TLK() {
        IPed ped = OtcApplication.getDal().getPed(EPedType.INTERNAL);
        try {
            byte[] bytes_tlk = ped.getKCV(
                    EPedKeyType.TLK,
                    (byte) 1,
                    (byte) 0,
                    byte_test);
            Log.i("getKCV_TLK:slot" + 1, OtcApplication.getConvert().bcdToStr(bytes_tlk));
            return bytes_tlk;
        } catch (PedDevException e) {
            e.printStackTrace();
            Log.e("getKCV_TLK:slot"+ 1, e.toString());
        }
        return null;
    }

    public static boolean writeTMK2(int tmkIndex, byte[] tmkValue) {
        // write TMK
        try {
            OtcApplication.getDal().getPed(EPedType.EXTERNAL_TYPEA).setExMode(0x77);

            OtcApplication.getDal().getPed(EPedType.INTERNAL).writeKey(
                    EPedKeyType.TLK,
                    (byte) 1,
                    EPedKeyType.TMK,
                    (byte) tmkIndex,
                    tmkValue,
                    ECheckMode.KCV_NONE, null);

            return true;
        } catch (PedDevException e) {
            Log.w(TAG, e);
        }
        return false;
    }

    public static byte[] getKCV_TMK(byte keyIndex) {
        IPed ped = OtcApplication.getDal().getPed(EPedType.INTERNAL);
        try {
            byte[] bytes_tmk = ped.getKCV(EPedKeyType.TMK, keyIndex, (byte) 0, byte_test);
            Log.i("getKCV_TMK:slot" + keyIndex, OtcApplication.getConvert().bcdToStr(bytes_tmk));
            return bytes_tmk;
        } catch (PedDevException e) {
            e.printStackTrace();
            Log.e("getKCV_TMK:slot"+ keyIndex, e.toString());
        }
        return null;
    }

    public static boolean writeTDK2(int tmkIndex, int tdkIndex, byte[] tdkValue) {
        // write TMK
        try {
            //FinancialApplication.getDal().getPed(EPedType.EXTERNAL_TYPEA).setExMode(0x77);

            OtcApplication.getDal().getPed(EPedType.INTERNAL).writeKey(
                    EPedKeyType.TMK,
                    (byte) tmkIndex,
                    EPedKeyType.TDK,
                    (byte) tdkIndex,
                    tdkValue,
                    ECheckMode.KCV_NONE, null);

            return true;
        } catch (PedDevException e) {
            Log.w(TAG, e);
        }
        return false;
    }


}
