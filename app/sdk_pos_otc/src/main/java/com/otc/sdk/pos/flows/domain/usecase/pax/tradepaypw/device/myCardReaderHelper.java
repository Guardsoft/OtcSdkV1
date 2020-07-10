package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.device;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.otc.sdk.pax.a920.OtcApplication;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.ReadCardActivity;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.service.serviceReadType;
import com.pax.dal.IIcc;
import com.pax.dal.IMag;
import com.pax.dal.IPicc;
import com.pax.dal.entity.EDetectMode;
import com.pax.dal.entity.EPiccType;
import com.pax.dal.entity.EReaderType;
import com.pax.dal.entity.PiccCardInfo;
import com.pax.dal.entity.PollingResult;
import com.pax.dal.exceptions.IccDevException;
import com.pax.dal.exceptions.MagDevException;
import com.pax.dal.exceptions.PiccDevException;

/**
 * Created by yanglj on 2017-11-02.
 */

public class myCardReaderHelper {
    private static myCardReaderHelper instance;
    private IMag mag;
    private IIcc icc;
    private IPicc piccInternal;
    private IPicc piccExternal;

    private boolean isStop = false;
    private boolean isPause = false;
    private Context context;
    //private byte readType;
    //private static Object lock;

    private static final byte SLOT_ICC = (byte) 0x00;


    private myCardReaderHelper() {
        //lock = new Object();
    }

    public static synchronized myCardReaderHelper getInstance() {
        if (instance == null) {
            instance = new myCardReaderHelper();
        }
        //instance.setContext(context);
        return instance;
    }

    public void setIsPause(boolean flg) {
        isPause = flg;
    }

//    public PollingResult polling(EReaderType readerType, int timeout) throws MagDevException, PiccDevException,
//            IccDevException {
//        //synchronized (lock) {
//            isStop = false;
//            isPause = false;
//            icc = OtcApplication.getDal().getIcc();
//            byte mode = readerType.getEReaderType();
//            if ((readerType.getEReaderType() & EReaderType.MAG.getEReaderType()) == EReaderType.MAG.getEReaderType()) {
//                mag = OtcApplication.getDal().getMag();
//                mag.close();
//                mag.open();
//                mag.reset();
//            }
//            if ((readerType.getEReaderType() & EReaderType.PICC.getEReaderType()) == EReaderType.PICC.getEReaderType()) {
//                // picc.close();
//                if (piccInternal == null) {
//                    piccInternal = OtcApplication.getDal().getPicc(EPiccType.INTERNAL);
//                }
//                // picc = piccInternal;
////                if (readerType.getEReaderType() == EReaderType.PICC.getEReaderType()) {
////                    mag = OtcApplication.getDal().getMag();
////                    mag.close();
////                    icc.close(SLOT_ICC);
////                }
//                piccInternal.close();
//                SwingCardActivity.prnTime("polling piccInternal.open start timse = ");
//                piccInternal.open();
//                SwingCardActivity.prnTime("polling piccInternal.open timse = ");
//            }
//            if ((readerType.getEReaderType() & EReaderType.PICCEXTERNAL.getEReaderType()) == EReaderType.PICCEXTERNAL
//                    .getEReaderType()) {
//                // picc.close();
//                if (piccExternal == null) {
//                    piccExternal = OtcApplication.getDal().getPicc(EPiccType.EXTERNAL);
//                }
//                // picc = piccExternal;
//                piccExternal.close();
//                piccExternal.open();
//            }
//            //SwingCardActivity.prnTime("Device Open diff = ");
//            long startTime = System.currentTimeMillis();
//            while (!isStop) {
//                if (timeout > 0) {
//                    long endTime = System.currentTimeMillis();
//                    if (endTime - startTime > timeout) {
//                        PollingResult result = new PollingResult();
//                        result.setOperationType(PollingResult.EOperationType.TIMEOUT);
//                        result.setReaderType(readerType);
//                        closeReader(mode);
//                        return result;
//                    }
//                }
//                // MAG
//                if ((mode & EReaderType.MAG.getEReaderType()) == EReaderType.MAG.getEReaderType()) {
//                    if (mag.isSwiped()) {
//                        PollingResult result = new PollingResult();
//                        TrackData info = mag.read();
//                        result.setOperationType(PollingResult.EOperationType.OK);
//                        result.setReaderType(EReaderType.MAG);
//                        result.setTrack1(info.getTrack1());
//                        result.setTrack2(info.getTrack2());
//                        result.setTrack3(info.getTrack3());
//                        if (info.getTrack1().equals("") && info.getTrack2().equals("") && info.getTrack3().equals("")) {
//                            //AppLog.d(ELogModule.IPPS, TAG, "mag.read no trackdata");
//                            continue;
//                        }
//                        // closeReader((byte) 0x07); // close all
//                        closeReader(mode);
//                        return result;
//                    }
//                }
//                // ICC
//                if ((mode & EReaderType.ICC.getEReaderType()) == EReaderType.ICC.getEReaderType()) {
//                    //SwingCardActivity.prnTime("icc.detect start ");
//                    boolean ret = icc.detect(SLOT_ICC);
//                    //SwingCardActivity.prnTime("icc.detect diff = ");
//                    if (ret) {
//                        PollingResult result = new PollingResult();
//                        result.setOperationType(PollingResult.EOperationType.OK);
//                        result.setReaderType(EReaderType.ICC);
//                        // closeReader((byte) 0x05); // close mag + picc
//                        closeReader((byte) (mode & 0x05));
//                        return result;
//                    }
//                }
//                // PICC
//                if ((mode & EReaderType.PICC.getEReaderType()) == EReaderType.PICC.getEReaderType()) {
//
//                    //SwingCardActivity.prnTime("piccInternal.detect start ");
//                    SwingCardActivity.prnTime("piccInternal.detect start timse = ");
//                    PiccCardInfo info = piccInternal.detect(EDetectMode.EMV_AB);
//                    SwingCardActivity.prnTime("piccInternal.detect diff = ");
//                    if (info != null) {
//                        PollingResult result = new PollingResult();
//                        result.setOperationType(PollingResult.EOperationType.OK);
//                        result.setReaderType(EReaderType.PICC);
//                        result.setSerialInfo(info.getSerialInfo());
//                          //closeReader((byte) (mode & 0x03)); // close mag + icc
//
//                        return result;
//                    }
//                }
//                // PICCEXTERNAL
//                if ((mode & EReaderType.PICCEXTERNAL.getEReaderType()) == EReaderType.PICCEXTERNAL.getEReaderType()) {
//                    PiccCardInfo info = piccExternal.detect(EDetectMode.EMV_AB);
//                    if (info != null) {
//                        PollingResult result = new PollingResult();
//                        result.setOperationType(PollingResult.EOperationType.OK);
//                        result.setReaderType(EReaderType.PICCEXTERNAL);
//                        result.setSerialInfo(info.getSerialInfo());
//                        // picc.close();
//                        // closeReader((byte) 0x03); // close mag + icc
//                        closeReader((byte) (mode & 0x03));
//                        return result;
//                    }
//                }
//            }
//            // SystemClock.sleep(100);
//            PollingResult result = new PollingResult();
//            if (isPause) {
//                result.setOperationType(PollingResult.EOperationType.PAUSE);
//            } else {
//                result.setOperationType(PollingResult.EOperationType.CANCEL);
//            }
//            // closeReader((byte) 0x07); // close all
//            closeReader(mode);
//            return result;
//      // }
//    }

    public PollingResult polling(EReaderType readerType, int timeout) throws MagDevException, PiccDevException,
            IccDevException {
        //synchronized (lock) {
        isStop = false;
        isPause = false;
        icc = OtcApplication.getDal().getIcc();
        byte mode = readerType.getEReaderType();
        mag = OtcApplication.getDal().getMag();
//        if ((readerType.getEReaderType() & EReaderType.MAG.getEReaderType()) == EReaderType.MAG.getEReaderType()) {
//            mag = OtcApplication.getDal().getMag();
//            mag.close();
//            mag.open();
//            mag.reset();
//        }
        //Log.i(TAG, "readerType.getEReaderType() = " + readerType.getEReaderType());
        if ((readerType.getEReaderType() & EReaderType.PICC.getEReaderType()) == EReaderType.PICC.getEReaderType()) {
            // picc.close();
            if (piccInternal == null) {
                piccInternal = OtcApplication.getDal().getPicc(EPiccType.INTERNAL);
            }
            // picc = piccInternal;
//                if (readerType.getEReaderType() == EReaderType.PICC.getEReaderType()) {
//                    mag = OtcApplication.getDal().getMag();
//                    mag.close();
//                    icc.close(SLOT_ICC);
//                }
            piccInternal.close();
            //SwingCardActivity.prnTime("polling piccInternal.open start timse = ");
            piccInternal.open();
            //SwingCardActivity.prnTime("polling piccInternal.open timse = ");
            //Log.i(TAG, "piccInternal.open ok mode = " + mode);
        }
        if ((readerType.getEReaderType() & EReaderType.PICCEXTERNAL.getEReaderType()) == EReaderType.PICCEXTERNAL
                .getEReaderType()) {
            // picc.close();
            if (piccExternal == null) {
                piccExternal = OtcApplication.getDal().getPicc(EPiccType.EXTERNAL);
            }
            // picc = piccExternal;
            piccExternal.close();
            piccExternal.open();
        }
        //SwingCardActivity.prnTime("Device Open diff = ");
        serviceReadType.getInstance().setrReadType(EReaderType.DEFAULT.getEReaderType());
        long startTime = System.currentTimeMillis();
        while (!isStop) {
            if (timeout > 0) {
                long endTime = System.currentTimeMillis();
                if (endTime - startTime > timeout) {
                    PollingResult result = new PollingResult();
                    result.setOperationType(PollingResult.EOperationType.TIMEOUT);
                    result.setReaderType(readerType);
                    closeReader(mode);
                    return result;
                }
            }
            // MAG
            if ((mode & EReaderType.MAG.getEReaderType()) == EReaderType.MAG.getEReaderType()) {
//                if (mag.isSwiped()) {
                if (serviceReadType.getInstance().getrReadType() == EReaderType.MAG.getEReaderType()) {
                    PollingResult result = new PollingResult();
                    //TrackData info = mag.read();
                    result.setOperationType(PollingResult.EOperationType.OK);
                    result.setReaderType(EReaderType.MAG);
                    closeReader(mode);
                    return result;
                }
            }
            // ICC
            if ((mode & EReaderType.ICC.getEReaderType()) == EReaderType.ICC.getEReaderType()) {
                //boolean ret = icc.detect(SLOT_ICC);
                //if (ret) {
                byte type = serviceReadType.getInstance().getrReadType();
                //Log.i(TAG, "serviceReadType.getInstance().getrReadType = " + type);
                if (type == EReaderType.ICC.getEReaderType()) {
                    PollingResult result = new PollingResult();
                    result.setOperationType(PollingResult.EOperationType.OK);
                    result.setReaderType(EReaderType.ICC);
                    // closeReader((byte) 0x05); // close mag + picc
                    closeReader((byte) (mode & 0x05));
                    //Log.i(TAG, "closeReader (mode & 0x05) OK" );
                    return result;
                }
            }
            // PICC
            if ((mode & EReaderType.PICC.getEReaderType()) == EReaderType.PICC.getEReaderType()) {

                //SwingCardActivity.prnTime("piccInternal.detect start ");
                // for A920C+ terminal, if picc reader closed, then here will throw exception, so catch this exception, and keep thread alive.
                try {
                    ReadCardActivity.prnTime("piccInternal.detect start timse = ");
                    PiccCardInfo info = piccInternal.detect(EDetectMode.EMV_AB);
                    ReadCardActivity.prnTime("piccInternal.detect diff = ");
                    //Log.i(TAG, "piccInternal.detect Finish " );
                    if (info != null) {
                        PollingResult result = new PollingResult();
                        result.setOperationType(PollingResult.EOperationType.OK);
                        result.setReaderType(EReaderType.PICC);
                        result.setSerialInfo(info.getSerialInfo());
                        //closeReader((byte) (mode & 0x03)); // close mag + icc
                        return result;
                    }
                } catch (PiccDevException e) {
                    e.printStackTrace();
                }
            }
            // PICCEXTERNAL
            if ((mode & EReaderType.PICCEXTERNAL.getEReaderType()) == EReaderType.PICCEXTERNAL.getEReaderType()) {
                PiccCardInfo info = piccExternal.detect(EDetectMode.EMV_AB);
                if (info != null) {
                    PollingResult result = new PollingResult();
                    result.setOperationType(PollingResult.EOperationType.OK);
                    result.setReaderType(EReaderType.PICCEXTERNAL);
                    result.setSerialInfo(info.getSerialInfo());
                    // picc.close();
                    // closeReader((byte) 0x03); // close mag + icc
                    closeReader((byte) (mode & 0x03));
                    return result;
                }
            }
        }
        // SystemClock.sleep(100);
        PollingResult result = new PollingResult();
        if (isPause) {
            result.setOperationType(PollingResult.EOperationType.PAUSE);
        } else {
            result.setOperationType(PollingResult.EOperationType.CANCEL);
        }
        // closeReader((byte) 0x07); // close all
        closeReader(mode);
        return result;
    }


    public void stopPolling() {
        // synchronized (lock) {
        // 避免极端情况：在polling之后马上调用stopPolling，因线程还未初始化导致stopPolling不起作用。
        SystemClock.sleep(30);
        isStop = true;
        // }
        // SystemClock.sleep(100);
    }

    private void closeReader(byte flag) {

        if ((flag & 0x01) != 0) {
            try {
                mag.close();
            } catch (Exception e) {
                Log.e("mag close error : ", e.getMessage());
                //e.printStackTrace();
            }
        }

        if ((flag & 0x02) != 0) {
            try {
                icc.close(SLOT_ICC);
            } catch (Exception e) {
                Log.e("icc close error : ", e.getMessage());
                //e.printStackTrace();
            }
        }

        if ((flag & 0x04) != 0) {

            if (piccExternal != null) {
                try {
                    piccExternal.close();
                } catch (Exception e) {
                    Log.e("piccExt close err: ", e.getMessage());
                    //e.printStackTrace();
                }
            }

//            try {
//                piccInternal.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

    }


}
