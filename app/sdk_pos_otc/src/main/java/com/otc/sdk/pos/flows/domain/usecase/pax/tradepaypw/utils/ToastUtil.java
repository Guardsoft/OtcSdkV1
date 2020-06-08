package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

/**
 * Created by chenld on 2017/3/13.
 * 单例
 */

public class ToastUtil {
    private static Toast mToast;

    public static void showToast(final Activity activity, final String text) {

        if ("main".equals(Thread.currentThread().getName())) {
            if (mToast == null) {
                mToast = Toast.makeText(activity, text, Toast.LENGTH_SHORT);
            }
            mToast.setText(text);
            mToast.show();
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mToast == null) {
                        mToast = Toast.makeText(activity, text, Toast.LENGTH_SHORT);
                    }
                    mToast.setText(text);
                    mToast.show();
                }
            });
        }

    }


    public static void showImageToast(Activity activity, Bitmap bitmap, String titulo, String mensaje){

        ImageView image = new ImageView(activity);
        image.setImageBitmap(bitmap);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(activity).
                        setTitle(titulo).
                        setMessage(mensaje).
                        setPositiveButton("OK", (dialog, which) -> dialog.dismiss()).
                        setView(image);
        builder.create().show();

    }
}
