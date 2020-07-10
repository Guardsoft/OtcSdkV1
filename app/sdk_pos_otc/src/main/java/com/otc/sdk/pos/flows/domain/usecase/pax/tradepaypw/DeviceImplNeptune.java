package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw;

import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import com.otc.sdk.pax.a920.OtcApplication;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.service.serviceReadType;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.TickTimer;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.view.dialog.InputPwdDialog;
import com.pax.dal.IDAL;
import com.pax.dal.IIcc;
import com.pax.dal.IMag;
import com.pax.dal.IPed;
import com.pax.dal.IPicc;
import com.pax.dal.entity.ApduRespInfo;
import com.pax.dal.entity.ApduSendInfo;
import com.pax.dal.entity.EPedType;
import com.pax.dal.entity.EPiccType;
import com.pax.dal.entity.EReaderType;
import com.pax.dal.entity.ETermInfoKey;
import com.pax.dal.entity.PiccPara;
import com.pax.dal.entity.RSAPinKey;
import com.pax.dal.exceptions.EPedDevException;
import com.pax.dal.exceptions.EPiccDevException;
import com.pax.dal.exceptions.IccDevException;
import com.pax.dal.exceptions.PedDevException;
import com.pax.dal.exceptions.PiccDevException;
import com.pax.jemv.device.IDevice;
import com.pax.jemv.device.model.ApduRespL2;
import com.pax.jemv.device.model.ApduSendL2;
import com.pax.jemv.device.model.DeviceRetCode;
import com.pax.jemv.device.model.RsaPinKeyL2;
import com.pax.jemv.device.model.TransactionInterface;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils.bcd2Str;
import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.Utils.str2Bcd;

/**
 * Created by linhb on 4/25/2017.
 */

public class DeviceImplNeptune implements IDevice, TickTimer.TickTimerListener {
    private static final String TAG = "DeviceImplNeptune";
    private static final byte RET_RF_ERR_USER_CANCEL = 39;

    private String expectPinLen = "0,4,5,6,7,8,9,10,11,12";
    private int timeOut = 30000;
    private byte iccSlot = 0;
    private boolean cancelKeyFlag = false;
    private byte cancelKeyValue = 27;
    private int transInterface = 0;

    private IDAL dal;
    private IPed ped;
    private IIcc icc;
    private IPicc picc;
    private IMag mag;
    private TextView pwdTv;//Gillian 20170516

    private static DeviceImplNeptune instance = null;

    private DeviceImplNeptune() {
        //dal = TradeApplication.getDal();

        dal = OtcApplication.getDal();
        ped = dal.getPed(EPedType.INTERNAL);
        icc = dal.getIcc();
        picc = dal.getPicc(EPiccType.INTERNAL);
        mag = dal.getMag();
    }

    public static DeviceImplNeptune getInstance() {
        if (instance == null) {
            instance = new DeviceImplNeptune();
        }
        return instance;
    }

    @Override
    public void getTime(byte[] dateTime) {
        String date = dal.getSys().getDate();
        System.arraycopy(str2Bcd(date), 0, dateTime, 0, 7);
    }

    @Override
    public void readSN(byte[] serialNo) {
        Map<ETermInfoKey, String> info = dal.getSys().getTermInfo();
        String sn = info.get(ETermInfoKey.SN);
        if (sn != null) {
            System.arraycopy(sn.getBytes(), 0, serialNo, 0, sn.length());
        }
    }


    @Override
    public void getRand(byte[] buf, int len) {
        byte[] random = dal.getSys().getRandom(len);
        System.arraycopy(random, 0, buf, 0, len);
    }

    private long leftTime = 0;
    //    private TickTimer tickTimer = new TickTimer(new TickTimer.OnTickTimerListener() {
//        @Override
//        public void onTick(long leftTime) {
//            DeviceImplNeptune.this.leftTime = leftTime;
//            Log.i("TAG", "onTick:" + leftTime);
//        }
//
//        @Override
//        public void onFinish() {
//            DeviceImplNeptune.this.leftTime = 0;
//        }
//    });
    private TickTimer tickTimer;

    //@Override
    public void onTick(long leftTime) {
        this.leftTime = leftTime;
        Log.i(TAG, "onTick:" + leftTime);
    }


    //@Override
    public void onFinish() {
        tickTimer.cancel();
        this.leftTime = 0;
    }

    @Override
    public void timerSet(byte[] timerNo, short timeMS) {
        Log.i(TAG, "timerSet:" + leftTime);
        leftTime = timeMS;
        tickTimer = new TickTimer(timeMS / 10, 1);
        tickTimer.setTimeCountListener(this);
        tickTimer.start();
    }

    @Override
    public short timerCheck(byte timerNo) {
        return (short) (leftTime * 10);
    }

    @Override
    public void delayMs(short timeMS) {
        SystemClock.sleep(timeMS);
    }

    @Override
    public int setPinInputParam(final byte[] expectPinLen, long timeoutMs) {
        this.expectPinLen = new String(expectPinLen);
        this.timeOut = (int) timeoutMs;
        return DeviceRetCode.DEVICE_PED_OK;
    }

    @Override
    public int pedVerifyPlainPin(byte[] iccRespOut, byte mode) {
        try {
            Log.i("log", "pedVerifyPlainPin expectPinLen=" + this.expectPinLen);
            Log.i("log", "pedVerifyPlainPin mode=" + mode);
            Log.i("log", "pedVerifyPlainPin timeOut=" + timeOut);
            //ped.setKeyboardLayoutLandscape(!ViewUtils.isScreenOrientationPortrait(ActivityStack.getInstance().top()));
            byte[] e = ped.verifyPlainPin(iccSlot, expectPinLen, mode, timeOut);
            Log.i("log", "pedVerifyPlainPin iccRespOut=" + bcd2Str(e));
            System.arraycopy(e, 0, iccRespOut, 0, 2);
            return DeviceRetCode.DEVICE_PROC_OK;
        } catch (PedDevException e) {
            //e.printStackTrace();
            int code = e.getErrCode();

            if (code == EPedDevException.PED_ERR_INPUT_CANCEL.getErrCodeFromBasement()) {
                return DeviceRetCode.DEVICE_PEDERR_INPUT_CANCEL;
            }
            //pop enter pin activity unless cancel
            //ActivityStack.getInstance().pop();
            InputPwdDialog.getPwdListener().onErr();
            if (code == EPedDevException.PED_ERR_INPUT_TIMEOUT.getErrCodeFromBasement()) {
                return DeviceRetCode.DEVICE_PEDERR_INPUT_TIMEOUT;
            } else if (code == EPedDevException.PED_ERR_PIN_BYPASS_BYFUNKEY.getErrCodeFromBasement()) { //Gillian 20170607
                Log.i("log", "pedVerifyPlainPin DEVICE_PEDERR_NO_PIN_INPUT");
                return DeviceRetCode.DEVICE_PEDERR_NO_PIN_INPUT;
            } else if (code == EPedDevException.PED_ERR_NO_ICC.getErrCodeFromBasement()) { //Gillian 20170607
                Log.i("log", "pedVerifyPlainPin DEVICE_PEDERR_NO_ICC");
                return DeviceRetCode.DEVICE_PEDERR_NO_ICC;
            } else if (code == EPedDevException.PED_ERR_ICC_NO_INIT.getErrCodeFromBasement()) { //Gillian 20170607
                Log.i("log", "pedVerifyPlainPin DEVICE_PEDERR_ICC_NO_INIT");
                return DeviceRetCode.DEVICE_PEDERR_ICC_NO_INIT;
            } else if (code == EPedDevException.PED_ERR_NO_PIN_INPUT.getErrCodeFromBasement()) { // Reserved for NO_PIN_INPUT
                //Log.i("log", "pedVerifyPlainPin DEVICE_PEDERR_NO_PIN_INPUT");
                return DeviceRetCode.DEVICE_PEDERR_NO_PIN_INPUT;
            } else if (code == EPedDevException.PED_ERR_WAIT_INTERVAL.getErrCodeFromBasement()) { //Gillian 20170607
                Log.i("log", "pedVerifyPlainPin DEVICE_PEDERR_NO_PIN_INPUT");
                return DeviceRetCode.DEVICE_PEDERR_WAIT_INTERVAL;
            } else {
                return DeviceRetCode.DEVICE_PEDERR_OTHER;
            }
        }
        //return -1;
    }

    @Override
    public int pedVerifyCipherPin(final RsaPinKeyL2 rsaPinKeyIn, byte[] iccRespOut, byte mode) {
        Log.i("log", "pedVerifyCipherPin start");
        RSAPinKey pinkey = new RSAPinKey();
        System.arraycopy(rsaPinKeyIn.exp, 0, pinkey.getExponent(), 0, 4);
        System.arraycopy(rsaPinKeyIn.iccrandom, 0, pinkey.getIccRandom(), 0, rsaPinKeyIn.iccrandomlen);
        System.arraycopy(rsaPinKeyIn.mod, 0, pinkey.getModulus(), 0, pinkey.getModulus().length);
        pinkey.setModulusLen(rsaPinKeyIn.modlen);

        Log.i("log", "pedVerifyCipherPin pinkey set ok");

        try {
            //ped.setKeyboardLayoutLandscape(!ViewUtils.isScreenOrientationPortrait(ActivityStack.getInstance().top()));
            Log.i("log", "pedVerifyCipherPin expectPinLen=" + this.expectPinLen);
            Log.i("log", "pedVerifyCipherPin mode=" + mode);
            Log.i("log", "pedVerifyCipherPin timeOut=" + timeOut);
            //Log.i("log", "pedVerifyCipherPin pinkey=" + bcd2Str(pinkey.));
            // SystemClock.sleep(300);
            byte[] e = ped.verifyCipherPin(iccSlot, expectPinLen, pinkey, mode, timeOut);
            Log.i("log", "pedVerifyCipherPin iccRespOut=" + bcd2Str(e));
            System.arraycopy(e, 0, iccRespOut, 0, 2);
            return 0;
        } catch (PedDevException e) {
            Log.i("log", "pedVerifyCipherPin err=" + e.getErrCode());
            int code = e.getErrCode();
            if (code == EPedDevException.PED_ERR_INPUT_CANCEL.getErrCodeFromBasement()) {
                return DeviceRetCode.DEVICE_PEDERR_INPUT_CANCEL;
            }
            //pop enter pin activity unless cancel
            //ActivityStack.getInstance().pop();
            InputPwdDialog.getPwdListener().onErr();
            if (code == EPedDevException.PED_ERR_INPUT_TIMEOUT.getErrCodeFromBasement()) {
                return DeviceRetCode.DEVICE_PEDERR_INPUT_TIMEOUT;
            } else if (code == EPedDevException.PED_ERR_PIN_BYPASS_BYFUNKEY.getErrCodeFromBasement()) {
                return DeviceRetCode.DEVICE_PEDERR_NO_PIN_INPUT;
            } else if (code == EPedDevException.PED_ERR_NO_ICC.getErrCodeFromBasement()) {
                return DeviceRetCode.DEVICE_PEDERR_NO_ICC;
            } else if (code == EPedDevException.PED_ERR_ICC_NO_INIT.getErrCodeFromBasement()) {
                return DeviceRetCode.DEVICE_PEDERR_ICC_NO_INIT;
            } else if (code == EPedDevException.PED_ERR_NO_PIN_INPUT.getErrCodeFromBasement()) {    // Reserved for NO_PIN_INPUT
                return DeviceRetCode.DEVICE_PEDERR_NO_PIN_INPUT;
            } else if (code == EPedDevException.PED_ERR_WAIT_INTERVAL.getErrCodeFromBasement()) {
                return DeviceRetCode.DEVICE_PEDERR_WAIT_INTERVAL;
            } else {
                return DeviceRetCode.DEVICE_PEDERR_OTHER;
            }
        }
        //return -1;
    }

    @Override
    public void des(final byte[] input, byte[] output, final byte[] desKey, int mode) {
        try {
            byte[] in = Arrays.copyOfRange(input, 0, Math.min(input.length, 8));
            byte[] out;
            switch (mode) {
                case 1:
                    out = DES.encrypt(in, desKey);
                    System.arraycopy(out, 0, output, 0, 8);
                    break;
                case 0:
                    out = DES.decrypt(in, desKey);
                    System.arraycopy(out, 0, output, 0, 8);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e("des", e.getMessage());
        }

    }

    @Override
    public int aes(final byte[] input, byte[] output, final byte[] aesKey, int keyLen, int mode) {
        try {
            byte[] in = Arrays.copyOfRange(input, 0, Math.min(input.length, 16));
            byte[] out;
            switch (mode) {
                case 1:
                    out = AES.encrypt(in, aesKey);
                    System.arraycopy(out, 0, output, 0, 16);
                    return DeviceRetCode.DEVICE_PROC_OK;
                case 0:
                    out = AES.decrypt(in, aesKey);
                    System.arraycopy(out, 0, output, 0, 16);
                    return DeviceRetCode.DEVICE_PROC_OK;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e("aes", e.getMessage());
        }

        return DeviceRetCode.DEVICE_PROC_ERROR;
    }

    @Override
    public void hash(final byte[] dataIn, int dataInLen, byte[] dataOut) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] in = Arrays.copyOfRange(dataIn, 0, Math.min(dataIn.length, dataInLen));
            byte[] cipher = digest.digest(in);
            System.arraycopy(cipher, 0, dataOut, 0, 20);
        } catch (NoSuchAlgorithmException e) {
            Log.e("hash", e.getMessage());
        }
    }

    @Override
    public int rsaRecover(final byte[] module, int moduleLen, final byte[] exp, int expLen, final byte[] dataIn, byte[] dataOut) {
        try {
            byte[] out = RSA.recover(module, moduleLen, exp, expLen, dataIn);
            if (out != null) {
//                Log.i("log", "RSA.recover expLen=" + expLen);
//                Log.i("log", "RSA.recover moduleLen=" + moduleLen);
//                Log.i("log", "RSA.recover out.length=" + out.length);
                System.arraycopy(out, 0, dataOut, 0, out.length);
//                Log.i("log", "RSA.recover  out=" + bcd2Str(out));
//                Log.i("log", "RSA.recover  dataOut=" + bcd2Str(dataOut));
                return DeviceRetCode.DEVICE_PROC_OK;
            }
        } catch (Exception e) {
            Log.e("log", "RSA.recover  e=" + e.getMessage());
            //e.printStackTrace();
        }
        return DeviceRetCode.DEVICE_PROC_ERROR;
    }

    @Override
    public int sm2Verify(byte pubKeyIn, final byte[] msgIn, int msgInLen, final byte[] signIn, int signInLen) {
        byte[] uid = new byte[]{49, 50, 51, 52, 53, 54, 55, 56, 49, 50, 51, 52, 53, 54, 55, 56};
        byte[] byteMsgIn = new byte[msgInLen];
        byte[] byteSignIn = new byte[signInLen];
        System.arraycopy(msgIn, 0, byteMsgIn, 0, msgInLen);
        System.arraycopy(signIn, 0, byteSignIn, 0, signInLen);

        try {
            ped.SM2Verify(pubKeyIn, uid, byteMsgIn, byteSignIn);
            return DeviceRetCode.DEVICE_PROC_OK;
        } catch (PedDevException e) {
            e.printStackTrace();
        }
        return DeviceRetCode.DEVICE_PROC_ERROR;
    }

    @Override
    public int sm3(final byte[] msgIn, int msgInLen, byte[] resultOut) {
        try {
            byte[] in = new byte[msgInLen];
            System.arraycopy(msgIn, 0, in, 0, msgInLen);
            System.arraycopy(ped.SM3(in, (byte) 0x00), 0, resultOut, 0, resultOut.length);
            return DeviceRetCode.DEVICE_PROC_OK;
        } catch (PedDevException e) {
            e.printStackTrace();
        }
        return DeviceRetCode.DEVICE_PROC_ERROR;
    }

    @Override
    public byte setControlParam(byte[] param) {
        changeCancelKeyFlagDevice((param[0] & 1) == 1);
        return DeviceRetCode.DEVICE_PROC_OK;
    }

    private void changeCancelKeyFlagDevice(boolean control) {
        PiccPara piccPara;
        if (control && !cancelKeyFlag) {
            cancelKeyFlag = true;
        } else if (!control && cancelKeyFlag) {
            cancelKeyFlag = false;
        } else
            return;

//        try {
//            //FIXME not until updated Neptune Lite
//            piccPara = picc.readParam();
//            //piccPara.user_control_w = 1;
//            //piccPara.user_control_key_val = cancelKey;
//            picc.setParam(piccPara);
//        } catch (PiccDevException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public int setCancelKey(byte keyValue) {
        cancelKeyFlag = false;
        cancelKeyValue = keyValue;
        return DeviceRetCode.DEVICE_PED_OK;
    }

    @Override
    public int iccSetTxnIF(int txnIF) {
        if (txnIF != 0xFF && txnIF != 0) {
            return DeviceRetCode.DEVICE_PARAM_ERROR;
        }
        transInterface = txnIF;
        return DeviceRetCode.DEVICE_PED_OK;
    }

    @Override
    public int iccGetTxnIF() {
        return transInterface;
    }

    @Override
    public void setIccSlot(byte slot) {
        this.iccSlot = slot;
    }

    @Override
    public int iccReset() {
        try {
            dal.getIcc().init(this.iccSlot); // ignore returned ATR
            return DeviceRetCode.DEVICE_PICC_OK;
        } catch (IccDevException e) {
            e.printStackTrace();
        }

        return DeviceRetCode.DEVICE_PICC_OTHER_ERR;
    }

    @Override
    public byte iccCommand(final ApduSendL2 apduSend, ApduRespL2 apduRecv) {
        if (transInterface == TransactionInterface.DEVICE_CLSS_TXNIF) {
            //remove it due to effect the speed of contactless card processing
//            int ret = detectOtherCard();
            int ret = serviceReadType.getInstance().getrReadType();
            //Log.i(TAG, "SwingCardActivity.getrReadType = " + ret);
            if (ret != EReaderType.DEFAULT.getEReaderType()) {
                return (byte) ret;
            } else
                return (byte) piccIsoCommandDevice(apduSend, apduRecv);
            //return (byte) piccIsoCommandDevice(apduSend, apduRecv);
        } else {
            return (byte) iccIsoCommandDevice(apduSend, apduRecv);
        }
    }

    @Override
    public int fInitiate() {
        return 0;
    }

    @Override
    public int fWriteData(int fileIndex, final byte[] dataIn, int dataInLen) {
        return 0;
    }

    @Override
    public int fReadData(int fileIndex, byte[] dataOut, int dataExceptLen) {
        return 0;
    }

    @Override
    public int fRemove(int fileIndex) {
        return 0;
    }

    @Override
    public void setDebug(byte debugFlag, byte portChannel) {
    }


    private int piccIsoCommandDevice(ApduSendL2 apduSend, ApduRespL2 apduRecv) {
//         Date sDate;
//         Date eDate;
//        long diff;

        ApduSendInfo send = new ApduSendInfo();
        send.setCommand(apduSend.command);
        send.setDataIn(apduSend.dataIn);
        send.setLc(apduSend.lc);
        send.setLe(apduSend.le);

        Log.i("piccIsoCommandDevice", "apduSend = " + bcd2Str(apduSend.command));
        Log.i("piccIsoCommandDevice", "Data = " + bcd2Str(apduSend.dataIn));
        try {
            ReadCardActivity.prnTime("waveFlowBegin isoCommandByApdu strat time = ");
            ApduRespInfo resp = picc.isoCommandByApdu(iccSlot, send);
            ReadCardActivity.prnTime("waveFlowBegin isoCommandByApdu time = ");
            Log.i("piccIsoCommandDevice", "apduRecv = " + bcd2Str(resp.getDataOut()));
            System.arraycopy(resp.getDataOut(), 0, apduRecv.dataOut, 0, resp.getDataOut().length);
            apduRecv.lenOut = (short) resp.getDataOut().length;
            apduRecv.swa = resp.getSwA();
            apduRecv.swb = resp.getSwB();
            Log.i("piccIsoCommandDevice", "swa = " + bcd2Str(new byte[]{apduRecv.swa}));
            Log.i("piccIsoCommandDevice", "swb = " + bcd2Str(new byte[]{apduRecv.swb}));
            //return 0;
        } catch (PiccDevException e) {
            e.printStackTrace();
            int ret1 = e.getErrCode();
            short ret2;
            if (ret1 == RET_RF_ERR_USER_CANCEL) {//test case 3B02-9001 for paypass 3.0.1 by zhoujie   // ?
                ret2 = DeviceRetCode.DEVICE_PICC_USER_CANCEL;
            } else if (ret1 == EPiccDevException.PICC_ERR_PROTOCOL2.getErrCodeFromBasement()) {
                ret2 = DeviceRetCode.DEVICE_PICC_PROTOCOL_ERROR;
            } else if (ret1 == EPiccDevException.PICC_ERR_IO.getErrCodeFromBasement()) {
                ret2 = DeviceRetCode.DEVICE_PICC_TRANSMIT_ERROR;
            } else if (ret1 == EPiccDevException.PICC_ERR_TIMEOUT.getErrCodeFromBasement()) {
                ret2 = DeviceRetCode.DEVICE_PICC_TIME_OUT_ERROR;
            } else {
                ret2 = DeviceRetCode.DEVICE_PICC_OTHER_ERR;
            }

            return ret2;
        }


        return 0;

    }

    private int iccIsoCommandDevice(final ApduSendL2 apduSend, ApduRespL2 apduRecv) {
        ApduSendInfo send = new ApduSendInfo();
        send.setCommand(apduSend.command);
        send.setDataIn(apduSend.dataIn);
        send.setLc(apduSend.lc);
        send.setLe(apduSend.le);
        Log.i("DeviceImpl", "command = " + bcd2Str(apduSend.command));
        Log.i("DeviceImpl", "apduSend = " + bcd2Str(apduSend.dataIn));

        ApduRespInfo resp;
        try {
            resp = icc.isoCommandByApdu(this.iccSlot, send);
        } catch (IccDevException e) {
            e.printStackTrace();
            return DeviceRetCode.DEVICE_PICC_OTHER_ERR;
        }

        System.arraycopy(resp.getDataOut(), 0, apduRecv.dataOut, 0, resp.getDataOut().length);

        apduRecv.lenOut = (short) resp.getDataOut().length;
        apduRecv.swa = resp.getSwA();
        apduRecv.swb = resp.getSwB();
        Log.i("DeviceImpl", "apduRecv = " + bcd2Str(resp.getDataOut()));
        Log.i("DeviceImpl", "swa = " + bcd2Str(new byte[]{apduRecv.swa}));
        Log.i("DeviceImpl", "swb = " + bcd2Str(new byte[]{apduRecv.swb}));
        return DeviceRetCode.DEVICE_PICC_OK;
    }

    @Override
    public long getTickCount() {
        Date now = new Date();
        return now.getTime();
    }


    private static class DES {
        private static final String TRANSFORMATION = "DES/CBC/NoPadding";

        private DES() {

        }

        private static SecretKey genKey(final byte[] password) throws Exception {
            DESKeySpec desKey = new DESKeySpec(password);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            return keyFactory.generateSecret(desKey);
        }

        static byte[] encrypt(final byte[] input, final byte[] password) throws AlgoException {
            try {
                SecretKey secureKey = genKey(password);
                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                cipher.init(Cipher.ENCRYPT_MODE, secureKey);
                return cipher.doFinal(input);
            } catch (Exception e) {
                Log.i(TAG, "DES encrypt Exception");
                throw new AlgoException(e);
            }
        }

        static byte[] decrypt(final byte[] input, final byte[] password) throws AlgoException {
            try {
                SecretKey secureKey = genKey(password);
                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                cipher.init(Cipher.DECRYPT_MODE, secureKey);
                return cipher.doFinal(input);
            } catch (Exception e) {
                Log.i(TAG, "DES decrypt Exception");
                throw new AlgoException(e);
            }
        }
    }

    private static class AES {
        private static final String TRANSFORMATION = "AES/CBC/NoPadding";

        private AES() {

        }

        private static SecretKeySpec genKey(final byte[] password) throws AlgoException {
            try {
                KeyGenerator kgen = KeyGenerator.getInstance("AES");
                kgen.init(password.length * 8, new SecureRandom(password));
                SecretKey secretKey = kgen.generateKey();
                byte[] enCodeFormat = secretKey.getEncoded();
                return new SecretKeySpec(enCodeFormat, "AES");
            } catch (Exception e) {
                Log.i(TAG, "SecretKeySpec genKey Exception");
                throw new AlgoException(e);
            }
        }

        static byte[] encrypt(final byte[] input, final byte[] password) throws AlgoException {
            try {
                SecretKeySpec key = genKey(password);
                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                return cipher.doFinal(input);
            } catch (Exception e) {
                Log.i(TAG, "encrypt Exception");
                throw new AlgoException(e);
            }
        }

        static byte[] decrypt(final byte[] input, final byte[] password) throws AlgoException {
            try {
                SecretKeySpec key = genKey(password);
                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                cipher.init(Cipher.DECRYPT_MODE, key);
                return cipher.doFinal(input);
            } catch (Exception e) {
                Log.i(TAG, "decrypt Exception");
                throw new AlgoException(e);
            }
        }
    }

    private static class RSA {
        private static final String TRANSFORMATION = "RSA/ECB/NoPadding";

        private RSA() {

        }

        static byte[] recover(final byte[] modulus, int moduleLen, final byte[] exp, int expLen, final byte[] dataIn) {
            try {

                byte[] temp;
                if (moduleLen != expLen) {
                    PublicKey publicKey = genPublicKey(modulus, exp);
                    if (publicKey == null)
                        throw new IllegalArgumentException();

                    temp = encryptWithPublicKey(publicKey, dataIn);
                    if (temp == null) {
                        Log.i("log", "encryptWithPublicKey = null");
                        return null;
                    }
                } else {
                    Log.i("log", "genPrivateKey");
                    PrivateKey privateKey = genPrivateKey(modulus, exp);
                    if (privateKey == null) {
                        Log.i("log", "privateKey = null");
                        throw new IllegalArgumentException();
                    }
                    temp = decryptWithPrivateKey(privateKey, dataIn);
                    if (temp == null) {
                        Log.i("log", "decryptWithPrivateKey = null");
                        return null;
                    }
                }
                return temp;
            } catch (Exception e) {
                Log.e("log", e.getMessage());
                //e.printStackTrace();
                throw new IllegalArgumentException();
            }
        }

        private static byte[] encryptWithPublicKey(PublicKey pubKey, byte[] input) throws AlgoException {
            try {
                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                cipher.init(Cipher.ENCRYPT_MODE, pubKey);
                return cipher.doFinal(input);
            } catch (Exception e) {
                Log.i(TAG, "encryptWithPublicKey Exception");
                throw new AlgoException(e);
            }
        }

        private static byte[] decryptWithPrivateKey(PrivateKey priKey, byte[] input) throws AlgoException {
            try {
                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                cipher.init(Cipher.DECRYPT_MODE, priKey);
                return cipher.doFinal(input);
            } catch (Exception e) {
                Log.i(TAG, "decryptWithPrivateKey Exception");
                throw new AlgoException(e);
            }
        }

        private static PublicKey genPublicKey(byte[] modulus, byte[] exp) {
            try {
                BigInteger modulusInt = new BigInteger(1, modulus);
                BigInteger expInt = new BigInteger(exp);
                KeyFactory kf = KeyFactory.getInstance("RSA");
                RSAPublicKeySpec pks = new RSAPublicKeySpec(modulusInt, expInt);
                return kf.generatePublic(pks);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                //e.printStackTrace();
                return null;
            }
        }

        private static PrivateKey genPrivateKey(byte[] modulus, byte[] exp) {
            try {
                BigInteger modulusInt = new BigInteger(1, modulus);
                BigInteger expInt = new BigInteger(exp);
                KeyFactory kf = KeyFactory.getInstance("RSA");
                RSAPrivateKeySpec pks = new RSAPrivateKeySpec(modulusInt, expInt);
                return kf.generatePrivate(pks);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                //e.printStackTrace();
                return null;
            }
        }

    }


    private static class AlgoException extends Exception {
        AlgoException(Throwable cause) {
            super(cause);
        }
    }

}
