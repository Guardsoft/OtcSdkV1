package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class Utils {

    /**
     * 得到设备屏幕的宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 得到设备屏幕的高度
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 得到设备的密度
     */
    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 把密度转换为像素
     */
    public static int dip2px(Context context, float px) {
        final float scale = getScreenDensity(context);
        return (int) (px * scale + 0.5);
    }

    public static void install(Context context, String name, String path) {
        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = context.getAssets().open(name);
            File file = new File(path + name);
            out = new FileOutputStream(file);
            int count = 0;
            byte[] tmp = new byte[1024];
            while ((count = in.read(tmp)) != -1) {
                out.write(tmp, 0, count);
            }
            Runtime.getRuntime().exec("chmod 777 " + path + name);
        } catch (FileNotFoundException e) {
            Log.e("install", e.getMessage());
        } catch (IOException ex) {
            Log.e("install", ex.getMessage());
        } catch (Exception e) {
            Log.e("install", e.getMessage());
        } finally {
            try {
                if (out != null) {
                    //out.flush();
                    out.close();
                    in.close();
                }
            } catch (IOException e) {
                Log.e("install", e.getMessage());
            }
        }
    }

    /**
     * 获取主秘钥索引
     *
     * @param index 0~99的主秘钥索引值
     * @return 1~100的主秘钥索引值
     */
    public static int getMainKeyIndex(int index) {
        return index + 1;
    }

    /**
     * ASCII码字符串转数字字符串
     *
     * @param content ASCII字符串
     * @return 字符串
     */
    public static String asciiStringToString(String content) {
        StringBuffer result = new StringBuffer();
        int length = content.length() / 2;
        for (int i = 0; i < length; i++) {
            String c = content.substring(i * 2, i * 2 + 2);
            long a = hexStringToAlgorism(c);
            char b = (char) a;
            String d = String.valueOf(b);
            result.append(d);
        }
        return result.toString();
    }


    /**
     * 十六进制字符串装十进制
     *
     * @param hex 十六进制字符串
     * @return 十进制数值
     */
    public static long hexStringToAlgorism(String hex) {
        hex = hex.toUpperCase();
        int max = hex.length();
        long result = 0;
        for (int i = max; i > 0; i--) {
            char c = hex.charAt(i - 1);
            int algorism = 0;
            if (c >= '0' && c <= '9') {
                algorism = c - '0';
            } else {
                algorism = c - 55;
            }
            int k = max - i;
            result += Math.pow(16, k) * Double.valueOf(algorism);
        }
        return result;
    }

    public static String bcd2Str(byte[] bytes) {
        StringBuilder temp = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            byte left = (byte) ((bytes[i] & 0xf0) >>> 4);
            byte right = (byte) (bytes[i] & 0x0f);
            if (left >= 0x0a && left <= 0x0f) {
                left -= 0x0a;
                left += 'A';
            } else {
                left += '0';
            }

            if (right >= 0x0a && right <= 0x0f) {
                right -= 0x0a;
                right += 'A';
            } else {
                right += '0';
            }

            temp.append(String.format("%c", left));
            temp.append(String.format("%c", right));
        }
        return temp.toString();
    }

    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String bcd2Str(byte[] b, int len) {
        if (b == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(len * 2);
        int minLen = Math.min(b.length, len);
        for (int i = 0; i < minLen; ++i) {
            sb.append(HEX_DIGITS[((b[i] & 0xF0) >>> 4)]);
            sb.append(HEX_DIGITS[(b[i] & 0xF)]);
        }

        return sb.toString();
    }

    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        if (len >= 2) {
            len /= 2;
        }
        byte[] bbt = new byte[len];
        byte[] abt = asc.getBytes();

        for (int p = 0; p < asc.length() / 2; p++) {
            int j;
            if ((abt[(2 * p)] >= 97) && (abt[(2 * p)] <= 122)) {
                j = abt[(2 * p)] - 97 + 10;
            } else {
                if ((abt[(2 * p)] >= 65) && (abt[(2 * p)] <= 90))
                    j = abt[(2 * p)] - 65 + 10;
                else
                    j = abt[(2 * p)] - 48;
            }
            int k;
            if ((abt[(2 * p + 1)] >= 97) && (abt[(2 * p + 1)] <= 122)) {
                k = abt[(2 * p + 1)] - 97 + 10;
            } else {
                if ((abt[(2 * p + 1)] >= 65) && (abt[(2 * p + 1)] <= 90))
                    k = abt[(2 * p + 1)] - 65 + 10;
                else {
                    k = abt[(2 * p + 1)] - 48;
                }
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    public static String bytes2String(byte[] source) {
        String result = "";
        try {
            if (source.length > 0)
                result = new String(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("byte2Str", e.getMessage());
            //e.printStackTrace();
        }
        return result;
    }

    public static byte[] int2ByteArray(int i) {
        byte[] to = new byte[4];
        int offset = 0;
        to[offset] = (byte) (i >>> 24 & 0xFF);
        to[(offset + 1)] = (byte) (i >>> 16 & 0xFF);
        to[(offset + 2)] = (byte) (i >>> 8 & 0xFF);
        to[(offset + 3)] = (byte) (i & 0xFF);
        for (int j = 0; j < to.length; ++j) {
            if (to[j] != 0) {
                return Arrays.copyOfRange(to, j, to.length);
            }
        }
        return new byte[]{0x00};
    }

    public static long bytes2Long(byte[] byteNum) {
        long num = 0;
        for (int ix = 0; ix < byteNum.length; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }

    public static byte[] LongToBytes(long values) {
        byte[] buffer = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = 64 - (i + 1) * 8;
            buffer[i] = (byte) ((values >> offset) & 0xff);
        }
        return buffer;
    }

    /**
     * bytes字符串转换为Byte值
     *
     * @param String src Byte字符串，每个Byte之间没有分隔符
     * @return byte[]
     */
    public static byte[] hexStr2Bytes(String src) {
        int m = 0, n = 0;
        int l = src.length() / 2;

        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = Byte.decode("0x" + src.substring(i * 2, m) + src.substring(m, n));
        }
        return ret;
    }
}
