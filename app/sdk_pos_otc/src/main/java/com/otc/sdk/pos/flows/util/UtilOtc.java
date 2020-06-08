package com.otc.sdk.pos.flows.util;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Scroller;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.Locale;

public class UtilOtc extends Application {

    private static final String TAG = "UtilOtc";


    private static UtilOtc otcUtil;

    public UtilOtc() {
    }

    public  static UtilOtc getInstance() {
        if (otcUtil == null) {
            otcUtil = new UtilOtc();
        }
        return otcUtil;
    }

    public void dialogResult(Context context, String msg){

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("MENSAJE")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, (dialog1, which) -> dialog1.dismiss())
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();

        TextView textView = dialog.findViewById(android.R.id.message);
        textView.setScroller(new Scroller(context));
        textView.setVerticalScrollBarEnabled(true);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }

    public static String getSerialNumber() {
        String serialNumber;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            serialNumber = (String) get.invoke(c, "gsm.sn1");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ril.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ro.serialno");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "sys.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = Build.SERIAL;

            // If none of the methods above worked
            if (serialNumber.equals(""))
                serialNumber = null;
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = null;
        }

        return serialNumber;
    }


    public static String getTrack2(String track) {

        Log.i(TAG, "getTrack2: " + track);

        // debe tener un formato de 38 caracteres
        track = track.split("F")[0];
//        track = track.split("D")[0];
//        track = track.split("=")[0];
        if (track.length() > 38) {
            track = track.substring(0,37);
        }

        track = String.format("%-38s", track ).replace(' ', '0');
        return track;
    }

    public static String getCardNumber(String track) {

        // debe tener un formato de 38 caracteres
        track = track.split("D")[0];
        track = track.split("F")[0];
        track = track.split("=")[0];

        track = track.substring(0,6) + "******" + track.substring(12);


        return track;
    }

    public static String formatAmount(double amount){
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.UK);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        nf.setGroupingUsed(true);
        return nf.format(amount);
    }


}
