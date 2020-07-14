package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.pay.trans.callback;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.ConditionVariable;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.otc.sdk.pax.a920.crypto.device.Device;
import com.otc.sdk.pos.R;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.device.myCardReaderHelper;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.FileParse;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils.ToastUtil;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.view.dialog.CustomAlertDialog;
import com.pax.dal.entity.EReaderType;
import com.pax.dal.entity.PollingResult;
import com.pax.dal.exceptions.IccDevException;
import com.pax.dal.exceptions.MagDevException;
import com.pax.dal.exceptions.PiccDevException;
import com.pax.jemv.clcommon.CLSS_TORN_LOG_RECORD;
import com.pax.jemv.clcommon.EMV_CAPK;

import java.util.Arrays;

//import com.pax.ipp.service.aidl.Exceptions;

/**
 * Created by chenld on 2017/3/18.
 */

public class TradeCallback extends TransCallback {
    public static final int CALLBACK_ERR = -1;
    public static final int CALLBACK_CANCEL = -2;
    public static final int CALLBACK_SUCCESS = 0;
    private static TradeCallback tradeCallback;
    private Activity activity;
    private CustomAlertDialog promptDialog;
    private ConditionVariable cv;

    public TradeCallback(Activity activity) {
        this.activity = activity;
    }

    public static synchronized TradeCallback getInstance(Activity activity) {
        if (tradeCallback == null) {
            tradeCallback = new TradeCallback(activity);
        }
        return tradeCallback;
    }


    @Override
    public int removeCardPrompt() {
        cv = new ConditionVariable();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                while (true) {
                    try {
                        //ICardReaderHelper helper = TradeApplication.getDal().getCardReaderHelper();
                        PollingResult result = myCardReaderHelper.getInstance().polling(EReaderType.ICC_PICC, 100);
                        if (result.getReaderType() == EReaderType.ICC || result.getReaderType() == EReaderType.PICC) {
                            Device.beepErr();

                            // 提示拔卡
                            //if (result.getReaderType() == EReaderType.ICC) {
//                        showWarning(getCurrentContext().getString(R.string.pull_card));
//                            } else {
                            // 提示移卡
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (promptDialog == null) {
                                        promptDialog = new CustomAlertDialog(activity, CustomAlertDialog.WARN_TYPE);
                                        promptDialog.show();
                                        promptDialog.setImage(BitmapFactory.decodeResource(activity.getResources(),
                                                R.drawable.ic16));
                                        promptDialog.setCancelable(false);
                                        //promptDialog.setTitleText(activity.getString(R.string.trans_sale));
                                    }

                                    promptDialog.setContentText(activity.getString(R.string.remove_card));
                                }
                            });

                            //}
                            SystemClock.sleep(500);
                        } else {
                            if (promptDialog != null) {
                                promptDialog.dismiss();
                            }
                            cv.open();
                            //ClssPayWave.getInstance().setResult(0);
                            break;
                        }
                    } catch (PiccDevException e) {
                        e.printStackTrace();
                        if (promptDialog != null) {
                            promptDialog.dismiss();
                        }
                        cv.open();
                        //ClssPayWave.getInstance().setResult(0);
                        break;
                    } catch (IccDevException e) {
                        e.printStackTrace();
                        if (promptDialog != null) {
                            promptDialog.dismiss();
                        }
                        cv.open();
                        //ClssPayWave.getInstance().setResult(0);
                        break;
                    } catch (MagDevException e) {
                        e.printStackTrace();
                        if (promptDialog != null) {
                            promptDialog.dismiss();
                        }
                        cv.open();
                        //ClssPayWave.getInstance().setResult(0);
                        break;
                    }
                }
            }
        }).start();
        cv.block();
        return CALLBACK_SUCCESS;
    }

    private int callback_ret = CALLBACK_SUCCESS;

    @Override
    public int displaySeePhone() {

        cv = new ConditionVariable();
        promptDialog = null;
        Log.i("displaySeePhone", "displaySeePhone start");
        ///ToastUtil.showToast(activity, disp_msg + "\n" +activity.getString(R.string.detect_card_again));
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                while (true) {
                    try {
                        //ICardReaderHelper helper = TradeApplication.getDal().getCardReaderHelper();
                        PollingResult result = myCardReaderHelper.getInstance().polling(EReaderType.ICC_PICC, 100);
                        //helper.stopPolling();
                        if (result.getReaderType() == EReaderType.ICC || result.getReaderType() == EReaderType.PICC) {
                            Log.i("displayMsg", result.getReaderType() + "");
                            if (promptDialog != null) {
                                Log.i("displayMsg", "promptDialog != null");
                                promptDialog.dismiss();
                            }
                            cv.open();
                            break;
                        } else {
                            if (promptDialog != null) {
                                continue;
                            }
                            // 提示SEE PHONE
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (promptDialog == null) {
                                        promptDialog = new CustomAlertDialog(activity, CustomAlertDialog.WARN_TYPE);
                                        promptDialog.show();
                                        promptDialog.setImage(BitmapFactory.decodeResource(activity.getResources(),
                                                R.drawable.ic16));
                                        promptDialog.setCancelable(false);
                                        promptDialog.setTitleText(activity.getString(R.string.trans_sale));
                                    }

                                    promptDialog.setContentText(activity.getString(R.string.see_phone));
                                }
                            });
                        }
                    } catch (PiccDevException e) {
                        e.printStackTrace();
                        if (promptDialog != null) {
                            promptDialog.dismiss();
                        }
                        cv.open();
                        callback_ret = CALLBACK_ERR;
                        break;
                    } catch (IccDevException e) {
                        e.printStackTrace();
                        if (promptDialog != null) {
                            promptDialog.dismiss();
                        }
                        cv.open();
                        callback_ret = CALLBACK_ERR;
                        break;
                    } catch (MagDevException e) {
                        e.printStackTrace();
                        if (promptDialog != null) {
                            promptDialog.dismiss();
                        }
                        cv.open();
                        callback_ret = CALLBACK_ERR;
                        break;
                    }
                }
            }
        }).start();
        cv.block();

        return callback_ret;
    }

    @Override
    public int detectRFCardAgain() {
        ToastUtil.showToast(activity, activity.getString(R.string.detect_card_again));
        return CALLBACK_SUCCESS;
    }

    @Override
    public int dontRemoveCard() {
        ToastUtil.showToast(activity, activity.getString(R.string.prompt_not_remove_card));
        return CALLBACK_SUCCESS;
    }

    @Override
    public int getCapk(byte[] aid, byte index, EMV_CAPK capk) {
        byte[] rID = new byte[5];

        if (aid == null || capk == null) {
            return CALLBACK_ERR;
        }
        System.arraycopy(aid, 0, rID, 0, 5);
        for (EMV_CAPK capkTmp : FileParse.getmEmvCapk()) {

            if (capkTmp.keyID == index && Arrays.equals(rID, capkTmp.rID)) {
                System.arraycopy(capkTmp.rID, 0, capk.rID, 0, capkTmp.rID.length);

                capk.keyID = capkTmp.keyID;
                capk.hashInd = capkTmp.hashInd;
                capk.arithInd = capkTmp.arithInd;
                capk.modulLen = capkTmp.modulLen;
                System.arraycopy(capkTmp.modul, 0, capk.modul, 0, capkTmp.modul.length);
                capk.exponentLen = capkTmp.exponentLen;
                capk.exponent = capkTmp.exponent;
                System.arraycopy(capkTmp.exponent, 0, capk.exponent, 0, capkTmp.exponent.length);
                System.arraycopy(capkTmp.expDate, 0, capk.expDate, 0, capkTmp.expDate.length);
                System.arraycopy(capkTmp.checkSum, 0, capk.checkSum, 0, capkTmp.checkSum.length);
                //capk.expDate = capkTmp.expDate;
                //capk.checkSum = capkTmp.checkSum;
//                Log.i("TradeCallback","capk.idx = "+Integer.toHexString(capk.keyID));
//                Log.i("TradeCallback","capk.rID = "+DeviceImpl.bcd2Str(capk.rID));
//                Log.i("TradeCallback","capk.modul length = "+ Integer.toString(capk.exponentLen));
//                Log.i("TradeCallback","capk.modul = "+DeviceImpl.bcd2Str(capk.modul));
//                Log.i("TradeCallback","capk.checkSum = "+DeviceImpl.bcd2Str(capk.checkSum));

                return CALLBACK_SUCCESS;

            }
        }
        return CALLBACK_ERR;
    }

    @Override
    public int appLoadTornLog(CLSS_TORN_LOG_RECORD[] tornLogRecords) {
        return 0;  //return TornLog number;
    }

    @Override
    public int appSaveTornLog(CLSS_TORN_LOG_RECORD[] ptTornLog, int nTornNum) {
        return 0;
    }
}
