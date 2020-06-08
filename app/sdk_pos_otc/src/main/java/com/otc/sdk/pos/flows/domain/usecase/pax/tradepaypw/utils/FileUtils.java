package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2017/3/15 0015.
 */

public class FileUtils {

    private static final String TAG = "FileUtils";

    public static boolean copyFileFromAssert(Context ctx, String srcFile, String descFile) {
        Log.d(TAG, "准备拷贝ASSERT/" + srcFile + "文件至" + descFile);

        boolean bIsSuc = true;
        InputStream iptStm = null;
        OutputStream optStm = null;

        try {
            iptStm = ctx.getAssets().open(srcFile);
            Log.d(TAG, "AssetsFilePath:" + srcFile + " FileSize:" + (iptStm == null ? 0 : iptStm.available()));
            Log.d(TAG, "strDesFilePath:" + descFile);

            if (iptStm == null) {
                Log.d(TAG, "文件[" + srcFile + "]不存在于ASSERT下,无需拷贝!");
                return true;
            }

            File file = new File(descFile);
            if (!file.exists()) {// 目标文件不存在需要copy
                if (!file.createNewFile()) {
                    Log.i(TAG, "createNewFile fail");
                    //return false;
                }
                Runtime.getRuntime().exec("chmod 766 " + file);
            } else {
                if (file.length() == iptStm.available()) {
                    Log.d(TAG, "文件一致,无需拷贝!");
                    iptStm.close();
                    return true;
                }
                if (!file.delete()) {
                    Log.i(TAG, "delete fail");
                }
                if (!file.createNewFile()) {
                    Log.i(TAG, "re-createNewFile fail");
                    //return false;
                }
                Runtime.getRuntime().exec("chmod 766 " + file);
            }
            optStm = new FileOutputStream(file);

            int nLen = 0;

            byte[] buff = new byte[1024 * 1];
            while ((nLen = iptStm.read(buff)) > 0) {
                optStm.write(buff, 0, nLen);
            }

            if (iptStm != null) {
                optStm.close();
            }

            if (iptStm != null) {
                iptStm.close();
            }
        } catch (FileNotFoundException e) {
            Log.e("File", e.getMessage());
        } catch (IOException e) {
            Log.e("File", e.getMessage());
        } catch (Exception e) {
            Log.e("File", e.getMessage());
            bIsSuc = false;
        } finally {
            try {
                if (optStm != null)
                    optStm.close();
            } catch (IOException e) {
                Log.e("File", e.getMessage());
            }
        }
        return bIsSuc;
    }
}
