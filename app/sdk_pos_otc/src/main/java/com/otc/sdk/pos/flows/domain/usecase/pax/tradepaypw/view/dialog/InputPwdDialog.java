package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ReplacementTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.otc.sdk.pax.a920.OtcApplication;
import com.otc.sdk.pos.R;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.GetPinEmv;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.ImplEmv;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.device.Device;
import com.pax.dal.IPed;
import com.pax.dal.IPed.IPedInputPinListener;
import com.pax.dal.entity.DUKPTResult;
import com.pax.dal.entity.EKeyCode;
import com.pax.dal.entity.EPedType;
import com.pax.dal.exceptions.EPedDevException;
import com.pax.dal.exceptions.PedDevException;
import com.pax.jemv.clcommon.RetCode;

public class InputPwdDialog extends Dialog {
    private static final String TAG = "InputPwdDialog";

    private String title; // 标题
    private String prompt; // 提示信息

    private Handler handler;

    private TextView titleTv;
    private TextView subtitleTv;
    //private EditText pwdEdt;
    private TextView pwdTv;
    private int maxLength;
    private Boolean isFirstStart = true;
    private Boolean isOnline;

    private Boolean isTPKMod;
    private Boolean isDUPKTMod;
    private byte[] pindata = null;
    private String pindataEncrypt = "";
    private byte[] ksn = null;
    private DUKPTResult dukptResult;
    //private static InputPwdDialog instance;

    //    private FrameLayout mFrameLayout;
//    private SoftKeyboardPosStyle softKeyboard;
//    private static final byte ICC_SLOT = 0x00;
//    public static final String OFFLINE_EXP_PIN_LEN = "0,4,5,6,7,8,9,10,11,12";
    private IPed ped = OtcApplication.getDal().getPed(EPedType.INTERNAL);

    public InputPwdDialog(Context context, Handler handler, int length, String title, String prompt) {
        this(context, R.style.popup_dialog);
        this.handler = handler;
        this.maxLength = length;
        this.title = title;
        this.prompt = prompt;
    }

    /**
     * 输联机密码时调用次构造方法
     *
     * @param context
     * @param handler
     * @param title
     * @param prompt
     */
    public InputPwdDialog(Context context, Handler handler, String title, String prompt) {
        this(context, R.style.popup_dialog);
        this.handler = handler;
        this.title = title;
        this.prompt = prompt;
    }

    public InputPwdDialog(Context context, int theme) {
        super(context, theme);

    }

    public interface OnPwdListener {
        public void onSucc(String data, String dataEncrypt);

        public void onErr();
    }

    public static OnPwdListener listener;

    public static OnPwdListener getPwdListener() {
        return listener;
    }

    public static void setPwdListener(OnPwdListener pwdListener) {
        listener = pwdListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View convertView = getLayoutInflater().inflate(R.layout.activity_inner_pwd_layout, null);
        setContentView(convertView);
        getWindow().setGravity(Gravity.BOTTOM); // 显示在底部
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setWindowAnimations(R.style.popup_anim_style); // 添加动画
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = 880;
        isFirstStart = true;
        getWindow().setAttributes(lp);
        Log.i("InputPwdDialog", "onCreate");

        initViews(convertView);
        Log.i("InputPwdDialog", "initViews");
        //TickTimer.cancle();

    }

    //当页面加载完成之后再执行弹出键盘的动作
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (!isOnline) {
                if (isFirstStart) {
                    ImplEmv.pinEnterReady();
                }
                isFirstStart = false;
            }
        }
    }

    private void initViews(View view) {

        titleTv = (TextView) view.findViewById(R.id.prompt_title);
        titleTv.setText(title);

        subtitleTv = (TextView) view.findViewById(R.id.prompt_no_pwd);
        if (prompt != null) {
            subtitleTv.setText(prompt);
        } else {
            subtitleTv.setVisibility(View.INVISIBLE);
        }

        pwdTv = (TextView) view.findViewById(R.id.pwd_input_text);
        pwdTv.setVisibility(View.GONE);
//        pwdEdt = (EditText) view.findViewById(R.id.pwd_input_et);
//        pwdEdt.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLength) });

        //InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(pwdEdt.getWindowToken(), 0);
//        pwdEdt.setInputType(InputType.TYPE_NULL);
//        pwdEdt.setFocusable(true);
//        pwdEdt.setTransformationMethod(new WordReplacement());

        //mFrameLayout = (FrameLayout) view.findViewById(R.id.fl_trans_softkeyboard);
        //softKeyboard = (SoftKeyboardPosStyle) view.findViewById(R.id.soft_keyboard_view);
//        softKeyboard.setOnItemClickListener(new SoftKeyboardPosStyle.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(View v, int index) {
//                if (index == KeyEvent.KEYCODE_ENTER) {
//                    String content = pwdEdt.getText().toString().trim();
//                    if (listener != null) {
//                        listener.onSucc(content);
//                    }
//                } else if (index == Constants.KEY_EVENT_CANCEL) {
//                    if (listener != null) {
//                        listener.onErr();
//                    }
//                }
//            }
//        });
    }

    public void setContentText(final String content) {
        if (handler != null) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    if (pwdTv != null) {
                        pwdTv.setText(content);
                        //content.getDimension(R.dimen.font_size_key)
                        pwdTv.setTextSize(25);
                    }
                }
            });
        }

    }

    public String getContentText() {
        StringBuffer buffer = new StringBuffer();
        if (pwdTv != null) {
            buffer.append(pwdTv.getText().toString());
        }
        return buffer.toString();
    }

    CustomAlertDialog promptDialog = null;


    //here2
    public void inputOfflinePlainPin() throws PedDevException {

        pwdTv.setVisibility(View.VISIBLE);
        //pwdEdt.setVisibility(View.GONE);
        //mFrameLayout.setVisibility(View.INVISIBLE);
        try {
            ped.setIntervalTime(1, 1);

            ped.setInputPinListener(new IPedInputPinListener() {

                @Override
                public void onKeyEvent(final EKeyCode arg0) {
                    String temp = "";
                    if (arg0 == EKeyCode.KEY_CLEAR) {
                        temp = "";
                    } else if (arg0 == EKeyCode.KEY_ENTER) {
                        ped.setInputPinListener(null);
                        listener.onSucc(null, "");
                        return;
                    } else if (arg0 == EKeyCode.KEY_CANCEL) {
                        ped.setInputPinListener(null);
                        listener.onErr();
                        return;
                    } else {
                        temp = getContentText();
                        temp += "*";
                    }
                    setContentText(temp);
                }
            });
        } catch (PedDevException e) {
            Log.e(TAG, "setInputPinListener", e);
            listener.onErr();
            throw (e);
        }

        // sleep 200ms to keep the pop time of soft keyboard and text view at same time
        //SystemClock.sleep(200);
        ImplEmv.pinEnterReady();
        Log.i("InputPwdDialog", "pinEnterReady");
    }


    //here3
    public void inputOnlinePin(final String panBlock) {
        isTPKMod = true;
        isDUPKTMod = false;

        pwdTv.setVisibility(View.VISIBLE);
        //pwdEdt.setVisibility(View.GONE);
        //mFrameLayout.setVisibility(View.INVISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //IPed ped = TradeApplication.dal.getPed(EPedType.INTERNAL);
                    ped.setIntervalTime(1, 1);
                    ped.setInputPinListener(new IPedInputPinListener() {

                        @Override
                        public void onKeyEvent(final EKeyCode arg0) {
                            String temp = "";
                            if (arg0 == EKeyCode.KEY_CLEAR) {
                                temp = "";
                            } else if (arg0 == EKeyCode.KEY_ENTER || arg0 == EKeyCode.KEY_CANCEL) {
                                // do nothing
                                return;
                            } else {
                                temp = getContentText();
                                temp += "*";
                            }
                            setContentText(temp);
                        }
                    });
                    // 有时密码框出不来， 有时键盘出不来， 目前真没找到更好的方法处理， 暂时用延时试试看， 也不一定起作用
                    // 后面那个大神有更好的方式再改正吧
                    //SystemClock.sleep(200);

                    if (isTPKMod) {
                        pindata = Device.getPinBlock(panBlock);
                        pindataEncrypt = pinBlockToAes(pindata);


                    } else if (isDUPKTMod) {
                        dukptResult = Device.getDUKPTPin(panBlock);
                        pindata = dukptResult.getResult();
                        ksn = dukptResult.getKsn();
                    }
                    //byte[] pindata = Device.getPinBlock(panBlock);
                    if (listener != null) {
                        if (pindata == null || pindata.length == 0)
                            listener.onSucc(null, "");
                        else {
                            listener.onSucc(OtcApplication.getConvert().bcdToStr(pindata), pindataEncrypt);
                        }
                    }
                } catch (final PedDevException e) {
                    e.printStackTrace();
                    setPwdResult(e.getErrCode());
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            Log.i(TAG, "e:" + e.getErrCode());
                            Device.beepErr();
                            promptDialog = new CustomAlertDialog(getContext(), CustomAlertDialog.ERROR_TYPE);
                            promptDialog.setTimeout(3);
                            //只显示超时和用户取消两种情况
                            //if (e.getErrCode() == EPedDevException.PED_ERR_INPUT_CLEAR.getErrCodeFromBasement())
                            if (e.getErrCode() == EPedDevException.PED_ERR_INPUT_CANCEL.getErrCodeFromBasement()) {
                                promptDialog.setContentText(getContext().getString(R.string.user_cancel));
                            } else if (e.getErrCode() == EPedDevException.PED_ERR_INPUT_TIMEOUT.getErrCodeFromBasement()) {
                                promptDialog.setContentText(getContext().getString(R.string.password_time_out));
                            }

                            promptDialog.show();
                            promptDialog.showConfirmButton(true);
                            promptDialog.setOnDismissListener(new OnDismissListener() {

                                @Override
                                public void onDismiss(DialogInterface arg0) {
                                    if (promptDialog != null)
                                        promptDialog.dismiss();
                                    if (listener != null) {
                                        listener.onErr();
                                    }
                                }
                            });
                        }
                    });

                }
                /*finally {
                    try {
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                dismiss();
                            }
                        });

                    } catch (Exception e2) {
                        // TODO: handle exception
                        e2.printStackTrace();
                    }
                }*/
            }
        }).start();
    }

    private String pinBlockToAes(byte[] pindata) {

        Log.i(TAG, "run pindata: " + OtcApplication.getConvert().bcdToStr(pindata));
        String pinBlockClaro = Device.decrypt3DesECB(OtcApplication.getConvert().bcdToStr(pindata), 5);

        Log.i(TAG, "pinBlockClaro: " + pinBlockClaro);

        pinBlockClaro = pinBlockClaro +"0D0D0D0D0D0D0D0D0D0D0D0D0D0D0D0D0D0D0D0D0D0D0D0D";

        return Device.encryptAES_CBC(pinBlockClaro, 11);
    }

    private void setPwdResult(int errCode) {

        if (errCode == EPedDevException.PED_ERR_INPUT_CANCEL.getErrCodeFromBasement()) {
            GetPinEmv.getInstance().setPinResult(RetCode.EMV_USER_CANCEL);
        } else if (errCode == EPedDevException.PED_ERR_INPUT_TIMEOUT.getErrCodeFromBasement()) {
            GetPinEmv.getInstance().setPinResult(RetCode.EMV_TIME_OUT);
        } else if (errCode == EPedDevException.PED_ERR_PIN_BYPASS_BYFUNKEY.getErrCodeFromBasement()) {
            GetPinEmv.getInstance().setPinResult(RetCode.EMV_NO_PASSWORD);
        } else if (errCode == EPedDevException.PED_ERR_NO_PIN_INPUT.getErrCodeFromBasement()) {    // Reserved for NO_PIN_INPUT
            GetPinEmv.getInstance().setPinResult(RetCode.EMV_NO_PASSWORD);
        } else {
            GetPinEmv.getInstance().setPinResult(RetCode.EMV_NO_PINPAD);
        }
    }

    class WordReplacement extends ReplacementTransformationMethod {

        private String word;

        @Override
        protected char[] getOriginal() {
            // 循环ASCII值 字符串形式累加到String
            for (char i = 0; i < 256; i++) {
                word += String.valueOf(i);
            }
            // strWord转换为字符形式的数组
            char[] charOriginal = word.toCharArray();
            return charOriginal;
        }

        @Override
        protected char[] getReplacement() {
            char[] charReplacement = new char[255];
            // 输入的字符在ASCII范围内，将其转换为*
            for (int i = 0; i < 255; i++) {
                charReplacement[i] = '*';
            }

            return charReplacement;
        }
    }

    public void setcvmType(int isonline) {
        if (isonline == 1)
            isOnline = true;
        else
            isOnline = false;
    }

}
