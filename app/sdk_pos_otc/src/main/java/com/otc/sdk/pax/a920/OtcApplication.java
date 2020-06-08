package com.otc.sdk.pax.a920;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.otc.sdk.pax.a920.crypto.ICrypted;
import com.pax.dal.IDAL;
import com.pax.neptunelite.api.NeptuneLiteUser;

import static com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.FileParse.*;

public class OtcApplication extends Application {

    private static final String TAG = "TradeApplication";
    private static OtcApplication otcApplication;
    public final static String APP_VERSION = "V1.00.00";

    private static IDAL dal;
    private static IConvert convert;
    private static ICrypted crypted;

    private boolean isDalEnabled = true;

    public static IDAL getDal() {
        return dal;
    }

    public static IConvert getConvert() {
        return convert;
    }

    public static ICrypted getCrypted() {
        return crypted;
    }

    public boolean isDalEnabled() {
        return isDalEnabled;
    }

    public void setDalEnabled(boolean dalEnabled) {
        isDalEnabled = dalEnabled;
    }

    public static OtcApplication getInstance() {
        return otcApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        OtcApplication.otcApplication = this;
        init();
    }

    public void init() {
        NeptuneLiteUser neptuneLiteUser = NeptuneLiteUser.getInstance();
        try {
            if (dal == null) {
                dal = neptuneLiteUser.getDal(otcApplication);
                Log.i("FinancialApplication:", "dalProxyClient finished.");
            }
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e("dalProxyClient", e.getMessage());
        }

        convert = new ConverterImp();

        parseAidFromAssets(this, "aid.ini");
        parseCapkFromAssets(this, "capk.ini");
        Log.i(TAG, "init: ");
    }

    public void init(Activity activity) {
        NeptuneLiteUser neptuneLiteUser = NeptuneLiteUser.getInstance();
        try {
            if (dal == null) {
                dal = neptuneLiteUser.getDal(activity);
                Log.i("FinancialApplication:", "dalProxyClient finished.");
            }
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e("dalProxyClient", e.getMessage());
        }

        convert = new ConverterImp();


        parseAidFromAssets(this, "aid.ini");
        parseCapkFromAssets(this, "capk.ini");
        Log.i(TAG, "init: ");
    }

    static {
        System.loadLibrary("F_DEVICE_LIB_PayDroid");
        System.loadLibrary("F_PUBLIC_LIB_PayDroid");
        System.loadLibrary("F_EMV_LIB_PayDroid");
        System.loadLibrary("F_ENTRY_LIB_PayDroid");
        System.loadLibrary("F_MC_LIB_PayDroid");
        System.loadLibrary("F_WAVE_LIB_PayDroid");
        System.loadLibrary("F_AE_LIB_PayDroid");
        System.loadLibrary("F_QPBOC_LIB_PayDroid");
        System.loadLibrary("F_DPAS_LIB_PayDroid");
        System.loadLibrary("F_JCB_LIB_PayDroid");
        System.loadLibrary("F_PURE_LIB_PayDroid");
        System.loadLibrary("JNI_EMV_v101");
        System.loadLibrary("JNI_ENTRY_v103");
        System.loadLibrary("JNI_MC_v100");
        System.loadLibrary("JNI_WAVE_v100");
        System.loadLibrary("JNI_AE_v101");
        System.loadLibrary("JNI_QPBOC_v100");
        System.loadLibrary("JNI_DPAS_v100");
        System.loadLibrary("JNI_JCB_v100");
        System.loadLibrary("JNI_PURE_v100");

    }
}
