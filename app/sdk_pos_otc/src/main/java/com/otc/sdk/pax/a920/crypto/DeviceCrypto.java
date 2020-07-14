package com.otc.sdk.pax.a920.crypto;

import android.util.Log;

import com.otc.sdk.pax.a920.IConvert;
import com.otc.sdk.pax.a920.OtcApplication;
import com.otc.sdk.pax.a920.crypto.device.Device;
import com.pax.dal.IPed;
import com.pax.dal.entity.EPedMacMode;
import com.pax.dal.entity.EPedType;
import com.pax.dal.exceptions.PedDevException;

public class DeviceCrypto {

    private static DeviceCrypto deviceCrypto;

    public DeviceCrypto() {
    }

    public  static DeviceCrypto getInstance() {
        if (deviceCrypto == null) {
            deviceCrypto = new DeviceCrypto();
        }
        return deviceCrypto;
    }

    public static void writeKeysDataPin(String data, String pin) {

        String decryptInit = Device.decrypt3DesCBC(data, 1);
        String llaveReencriptada = Device.encrypt3DesEBC(decryptInit, 1);

        int slotTMK = 2;
        int slotDataTAESK10 = 10;

        byte[] bytesTDKData10 = OtcApplication
                .getConvert()
                .strToBcd(llaveReencriptada, IConvert.EPaddingPosition.PADDING_LEFT);

        Device.writeTAESK2(slotTMK,slotDataTAESK10, bytesTDKData10);
        //*****************************************************************************************

        String decryptInitPin = Device.decrypt3DesCBC(pin, 1);

        String llaveTpkClaro = decryptInit.substring(0,32);

        String llaveReencriptadaPin = Device.encrypt3DesEBC(decryptInitPin, 1);

        int slotDataTAESK11 = 11;

        byte[] bytesTDKPin11 = OtcApplication
                .getConvert()
                .strToBcd(llaveReencriptadaPin, IConvert.EPaddingPosition.PADDING_LEFT);

        Device.writeTAESK2(slotTMK,slotDataTAESK11, bytesTDKPin11);

        //******************************************************************************************

//      llave pin en claro =  B4BBC1FB914C1D69D907A4B6F069B375
//        Key:			703D6EAE86355C9B17E50BE4E20B7121
//        Key length:		32

        int slotTPK = 2;

        String llaveTPK = Device.encrypt3DesEBC(llaveTpkClaro, 1);

        byte[] bytesTPK = OtcApplication
                .getConvert()
                .strToBcd(llaveTPK, IConvert.EPaddingPosition.PADDING_LEFT);

        // usar para capturar el pin
        Device.writeTPK2(slotTMK,slotTPK, bytesTPK);

        Device.getKCV_TPK((byte)slotTPK);


        //**************************************************************************

        int slotTDK = 5;
        Device.writeTDK(slotTMK,slotTDK, bytesTPK);
        Device.getKCV_TDK((byte)slotTDK);
    }

    public static byte[] getMacRetail(int keyindexTak, byte[] value){

        IPed ped = OtcApplication.getDal().getPed(EPedType.INTERNAL);
        try {
            byte[] bytesMode02 = ped.getMac((byte) keyindexTak, value, EPedMacMode.MODE_02);
            Log.i("getMacRetail:MODE_02 = " + keyindexTak, OtcApplication.getConvert().bcdToStr(bytesMode02));

            return bytesMode02;
        } catch (PedDevException e) {
            e.printStackTrace();
            Log.e("getMacRetail:slot"+ keyindexTak, e.toString());
        }
        return null;
    }

}
