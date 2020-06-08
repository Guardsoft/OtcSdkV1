package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.view;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;

import com.otc.sdk.pos.R;

import java.lang.reflect.Method;

/**
 * Created by chenld on 2017/3/10.
 */

public class CustomEditText extends EditText {

    /**
     * 模拟系统输入按键事件等
     */
    Instrumentation in;

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        in = new Instrumentation();
        this.setLongClickable(false);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyEditText);
        a.recycle();
        setLongClickable(false);
        setTextIsSelectable(false);
        setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }

    public void setIMEEnabled(boolean enable, boolean showCursor) {
        if (!enable) {
            if (showCursor) {
                ((Activity) getContext()).getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                try {
                    Class<CustomEditText> cls = CustomEditText.class;
                    Method setSoftInputShownOnFocus;
                    setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                    setSoftInputShownOnFocus.setAccessible(true);
                    setSoftInputShownOnFocus.invoke(this, false);
                } catch (Exception e) {
                    Log.e("setIMEEnabled", e.getMessage());
                    //e.printStackTrace();
                }
            } else {
                this.setInputType(InputType.TYPE_NULL);
            }

        }
    }
}
