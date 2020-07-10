package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.otc.sdk.pax.a920.OtcApplication;
import com.pax.dal.IDAL;
import com.pax.dal.IIcc;
import com.pax.dal.IMag;
import com.pax.dal.IPicc;
import com.pax.dal.entity.EPiccType;
import com.pax.dal.entity.EReaderType;
import com.pax.dal.entity.TrackData;
import com.pax.dal.exceptions.IccDevException;
import com.pax.dal.exceptions.MagDevException;
import com.pax.dal.exceptions.PiccDevException;

/**
 * Created by yanglj on 2017-11-27.
 */

public class OtherDetectCard extends Service {
    private static final String TAG = "OtherDetectCard";
    private IDAL dal;
    private IIcc icc;
    private IPicc picc;
    private IMag mag;
    boolean running = false;
    byte readType;
    byte iccSlot = 0;
    String process;
    private Intent intent;
    //public int mOSPriority = android.os.Process.setThreadPriority();

    @Override
    public void onCreate() {

        super.onCreate();
        dal = OtcApplication.getDal();
        icc = dal.getIcc();
        mag = dal.getMag();
        picc = dal.getPicc(EPiccType.INTERNAL);
        running = true;

        Log.i(TAG, "onCreate readType= " + readType);

        new Thread() {
            public void run() {
                while (running) {
                    //System.out.println(Service中获取到的数据： + data);
                    //Process.setThreadPriority(mOSPriority);
                    //android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                    detectOtherCard(readType);
                    //Thread.sleep(1000);
                }
            }
        }.start();

    }

    @Override
    public void onStart(Intent intent, int startId) {
        // Log.i(TAG, "onStart");
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //执行文件的下载或者播放等操作
        //Log.i(TAG, "onStartCommand");
        /*
         * 这里返回状态有三个值，分别是:
         * 1、START_STICKY：当服务进程在运行时被杀死，系统将会把它置为started状态，但是不保存其传递的Intent对象，之后，系统会尝试重新创建服务;
         * 2、START_NOT_STICKY：当服务进程在运行时被杀死，并且没有新的Intent对象传递过来的话，系统将会把它置为started状态，
         *   但是系统不会重新创建服务，直到startService(Intent intent)方法再次被调用;
         * 3、START_REDELIVER_INTENT：当服务进程在运行时被杀死，它将会在隔一段时间后自动创建，并且最后一个传递的Intent对象将会再次传递过来。
         */
        readType = intent.getByteExtra("readType", (byte) 0x00);
        iccSlot = intent.getByteExtra("iccSlot", (byte) 0x00);
        process = intent.getStringExtra("process");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        //Log.i(TAG, "onBind");
        // return new MyIBinder();
        return null;
    }

    @Override
    public void onDestroy() {
        // Log.i(TAG, "onDestroy");
        super.onDestroy();
        running = false;
    }

    //代理人
    public class MyIBinder extends Binder {

    }

    public void detectOtherCard(byte readerType) {
        Log.i(TAG, "detectOtherCard readerType = " + readerType);
        if ((readType & EReaderType.MAG.getEReaderType()) == EReaderType.MAG.getEReaderType()) {
            // mag = OtcApplication.getDal().getMag();
            try {
                mag.close();
                mag.open();
                mag.reset();
            } catch (MagDevException e) {
                e.printStackTrace();
            }
        }
        while (true) {
            try {
                if ((readerType & EReaderType.ICC.getEReaderType()) == EReaderType.ICC.getEReaderType()) {
                    if (icc.detect(iccSlot)) {
                        picc.close();
                        intent = new Intent();
                        intent.setAction("ACTION_DETECT");
                        intent.putExtra("process", process);
                        intent.putExtra("TYPE", EReaderType.ICC.getEReaderType());
                        sendBroadcast(intent);
                        //return DeviceRetCode.DEVICE_PICC_INSERTED_ICCARD;
                        Log.i(TAG, "icc.detect = " + readerType);
                        break;
                    }
                    SystemClock.sleep(5);//5ms????,????????
                }
                if ((readerType & EReaderType.MAG.getEReaderType()) == EReaderType.MAG.getEReaderType()) {
                    if (mag.isSwiped()) {
                        TrackData info = mag.read();
                        if (info.getTrack1().equals("") && info.getTrack2().equals("") && info.getTrack3().equals("")) {
                            //AppLog.d(ELogModule.IPPS, TAG, "mag.read no trackdata");
                            //Device.beepErr();
                            continue;
                        }

                        picc.close();
                        intent = new Intent();
                        intent.setAction("ACTION_DETECT");
                        intent.putExtra("process", process);
                        intent.putExtra("TYPE", EReaderType.MAG.getEReaderType());
                        intent.putExtra("TRK1", info.getTrack1());
                        intent.putExtra("TRK2", info.getTrack2());
                        intent.putExtra("TRK3", info.getTrack3());
                        sendBroadcast(intent);
                        Log.i(TAG, "mag.isSwiped = " + readerType);
                        break;
                        //return DeviceRetCode.DEVICE_PICC_SWIPED_MAGCARD;
                    }
                    SystemClock.sleep(5);//5ms????,????????
                } else {
                    SystemClock.sleep(1);
                }
            } catch (MagDevException e) {
                e.printStackTrace();
            } catch (IccDevException e) {
                e.printStackTrace();
            } catch (PiccDevException e) {
                e.printStackTrace();
            }
        }
        //return DeviceRetCode.DEVICE_PICC_OK;
    }


}
