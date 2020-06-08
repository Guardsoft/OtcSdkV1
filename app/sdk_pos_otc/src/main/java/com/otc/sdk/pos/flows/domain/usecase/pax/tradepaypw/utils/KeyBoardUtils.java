package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.otc.sdk.pos.R;

/**
 * Created by chenld on 2017/3/14.
 */

public class KeyBoardUtils {
    /**
     * 显示输入键盘
     *
     * @param softKeyboard
     */
    public static void show(Context context, FrameLayout softKeyboard) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_slide_in_from_bottom);
        softKeyboard.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        int visibility = softKeyboard.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            softKeyboard.clearAnimation();
            softKeyboard.startAnimation(animation);
            softKeyboard.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏输入键盘
     *
     * @param softKeyboard
     */
    public static void hide(Context context, FrameLayout softKeyboard) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_slide_out_to_bottom);
        int visibility = softKeyboard.getVisibility();
        if (visibility == View.VISIBLE) {
            softKeyboard.clearAnimation();
            softKeyboard.startAnimation(animation);
            softKeyboard.setVisibility(View.INVISIBLE);
        }
    }

}
