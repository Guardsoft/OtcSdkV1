package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.view;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.otc.sdk.pos.R;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.device.Device;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.pay.Constants;

import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * Created by chenld on 2017/3/10.
 */

public class SoftKeyboardPasswordStyle extends ViewGroup {

    int count;
    int textSize;

    @SuppressLint("Recycle")
    public SoftKeyboardPasswordStyle(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
    }

    @SuppressLint("Recycle")
    public SoftKeyboardPasswordStyle(Context context, AttributeSet attrs) {

        this(context, attrs, 0);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MySoftKey);
            textSize = (int) a.getDimension(R.styleable.MySoftKey_textSize, 25);
        }
    }

    public SoftKeyboardPasswordStyle(Context context) {

        this(context, null);
    }

    private boolean isFistInitView = true;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (isFistInitView)
            initView();
        isFistInitView = false;
        count = getChildCount();
        if (count == 0) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
            return;
        }

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int height = b - t;// 布局区域高度
        int width = r - l;// 布局区域宽度
        int rows = 4;
        int colums = 4;
        int gridW = width / colums;// 格子宽度
        int gridH = height / rows;// 格子高度
        int left = 0;
        int top = 1;

        for (int i = 0; i < rows; i++) {// 遍历行
            for (int j = 0; j < colums; j++) {// 遍历每一行的元素
                if ((i == rows - 1) && (j == colums - 1))
                    break;

                View child = this.getChildAt(i * colums + j);
                if (child == null)
                    return;
                left = j * gridW + j * 1;
                // 如果当前布局宽度和测量宽度不一样，就直接用当前布局的宽度重新测量
                if (gridW != child.getMeasuredWidth() || gridH != child.getMeasuredHeight()) {
                    child.measure(makeMeasureSpec(gridW, EXACTLY), makeMeasureSpec(gridH, EXACTLY));
                }
                if ((i == rows - 2) && (j == colums - 1)) {
                    child.measure(makeMeasureSpec(gridW, EXACTLY), makeMeasureSpec(gridH * 2, EXACTLY));
                    child.layout(left, top, left + gridW, top + gridH * 2);
                } else {
                    child.layout(left, top, left + gridW, top + gridH);
                }

            }
            top += gridH + 1;
        }
    }

    public interface GridAdatper {

        View getView(int index);

        int getCount();
    }

    private void initView() {

        for (int i = 1; i < 16; i++) {
            Button button = new Button(getContext());
            button.setWidth(android.view.WindowManager.LayoutParams.MATCH_PARENT);
            button.setHeight(android.view.WindowManager.LayoutParams.MATCH_PARENT);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Instrumentation inst = new Instrumentation();
                            Device.beepPromt();
                            int tag = Integer.parseInt((String) v.getTag());
                            switch (tag) {
                                case 1:
                                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_1);
                                    break;
                                case 2:
                                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_2);
                                    break;
                                case 3:
                                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_3);
                                    break;
                                case 4:
                                    // inst.sendKeyDownUpSync(KeyEvent.KEYCODE_UNKNOWN);
                                    if (listener != null)
                                        listener.onItemClick(v, Constants.KEY_EVENT_CANCEL);
                                    break;
                                case 5:
                                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_4);
                                    break;
                                case 6:
                                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_5);
                                    break;
                                case 7:
                                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_6);
                                    break;
                                case 8:
                                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DEL);
                                    break;
                                case 9:
                                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_7);
                                    break;
                                case 10:
                                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_8);
                                    break;
                                case 11:
                                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_9);
                                    break;
                                case 12:
                                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
                                    if (listener != null)
                                        listener.onItemClick(v, KeyEvent.KEYCODE_ENTER);
                                    break;
                                case 13:
//                                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_0);
//                                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_0);
//                                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_0);
                                    break;
                                case 14:
                                    //inst.sendKeyDownUpSync(KeyEvent.KEYCODE_0);
                                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_0);
                                    break;
                                case 15:
                                    //inst.sendKeyDownUpSync(KeyEvent.KEYCODE_0);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }).start();

                }
            });
            if (i == 15) {
                //button.setBackgroundResource(R.drawable.selection_button_0);
                button.setBackgroundResource(R.drawable.jp);
            } else if (i == 14) {
                //button.setBackgroundResource(R.drawable.selection_button_00);
                button.setBackgroundResource(R.drawable.selection_button_0);
            } else if (i == 13) {
                //button.setBackgroundResource(R.drawable.selection_button_000);
                button.setBackgroundResource(R.drawable.jp);
            } else if (i == 12) {
                button.setBackgroundResource(R.drawable.selection_button_confirm);
                button.setText(R.string.dialog_ok);
                button.setTextSize(25);
                button.setTextColor(Color.WHITE);
            } else if (i == 11) {
                button.setBackgroundResource(R.drawable.selection_button_9);
            } else if (i == 10) {
                button.setBackgroundResource(R.drawable.selection_button_8);
            } else if (i == 9) {
                button.setBackgroundResource(R.drawable.selection_button_7);
            } else if (i == 8) {
                button.setBackgroundResource(R.drawable.selection_button_delete);
                button.setText(R.string.dialog_delete);
                button.setTextColor(0xff666666);
                button.setTextSize(23);
            } else if (i == 7) {
                button.setBackgroundResource(R.drawable.selection_button_6);
            } else if (i == 6) {
                button.setBackgroundResource(R.drawable.selection_button_5);
            } else if (i == 5) {
                button.setBackgroundResource(R.drawable.selection_button_4);
            } else if (i == 4) {
                button.setBackgroundResource(R.drawable.selection_button_cancel);
                button.setText(R.string.dialog_cancel);
                button.setTextColor(0xff666666);
                button.setTextSize(23);
            } else if (i == 3) {
                button.setBackgroundResource(R.drawable.selection_button_3);
            } else if (i == 2) {
                button.setBackgroundResource(R.drawable.selection_button_2);
            } else if (i == 1) {
                button.setBackgroundResource(R.drawable.selection_button_1);
            }

            button.setTag("" + i);
            button.setFocusable(false);
            button.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            button.setGravity(Gravity.CENTER);
            addView(button);
        }

    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {

        void onItemClick(View v, int index);
    }

}