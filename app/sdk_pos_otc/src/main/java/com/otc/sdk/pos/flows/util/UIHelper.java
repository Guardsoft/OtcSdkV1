package com.otc.sdk.pos.flows.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.core.content.ContextCompat;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by foxit on 11/24/17.
 */

public class UIHelper {

    private static final int MAX_YEAR = 2030;

    public static String randomAlphanumeric() {
        Random rnd = new Random(System.currentTimeMillis());
        char c = (char) (rnd.nextInt(26) + 'a');
        int intAletorio = rnd.nextInt(999);
        return intAletorio + "" + c;
    }

    @SuppressLint("DefaultLocale")
    public static String formatWithZeros(int value, int numberOfZeros) {
        String pattern = "%0" + numberOfZeros + "d";
        return String.format(pattern, value);
    }

    public static boolean isValidName(String s) {
        String patternExp = "([a-zA-Z\\p{L}'´]\\s*)+";
        return s.matches(patternExp);
    }

    public static boolean isValidAlphanumeric(String s) {
        String patternExp = "([a-zA-Z0-9\\p{L}'´]\\s*)+";
        return s.matches(patternExp);
    }

    public static int dpToPx(Context ctx, int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        //Log.d(TAG, String.format("Density: %s - DP: %s", density, dp));
        return (int) (dp * density);
    }

    public static boolean isValidEmail(String s) {
        return Patterns.EMAIL_ADDRESS.matcher(s).matches();
    }

    public static float pixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    public static Locale getCurrentLocale(Context context) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }


    public static CharSequence noTrailingwhiteLines(CharSequence text) {

        while (text.charAt(text.length() - 1) == '\n') {
            text = text.subSequence(0, text.length() - 1);
        }
        return text;
    }

    public static CharSequence htmlToString(String htmlString) {

        CharSequence trimmed;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            trimmed = noTrailingwhiteLines(Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY));
        } else {
            trimmed = noTrailingwhiteLines(Html.fromHtml(htmlString));
        }
        return trimmed;
    }


    public static boolean luhnCheck(String card) {
        if (card == null)
            return false;
        char checkDigit = card.charAt(card.length() - 1);
        String digit = calculateCheckDigit(card.substring(0, card.length() - 1));
        return checkDigit == digit.charAt(0);
    }

    public static boolean isValidExpiration(String s) {
        if (s.length() == 5) {
            String[] data = s.split("/");
            if (data.length == 2) {
                String monthStr = data[0];
                String yearStr = data[1];
                int month = Integer.parseInt(monthStr);
                int year = Integer.parseInt(yearStr) + 2000;
                Calendar calendar = Calendar.getInstance();
                int currentMonth = calendar.get(Calendar.MONTH) + 1;
                int currentYear = calendar.get(Calendar.YEAR);

                // invalid month
                if (month < 1 || month > 12) {
                    return false;
                }
                // invalid year
                if (year < currentYear || year > MAX_YEAR) {
                    return false;
                }
                // month/year not valid
                if (year == currentYear && month < currentMonth) {
                    return false;
                }

                // after all
                return true;
            }
        }
        return false;
    }


    public static boolean isValidExpirationMount(String monthStr) {
        if (monthStr.length() == 2) {

            int month = Integer.parseInt(monthStr);

            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(Calendar.MONTH) + 1;

            // invalid month
            if (month < 1 || month > 12) {
                return false;
            }


            // after all
            return true;
        }

        return false;
    }

    public static boolean isValidExpirationYear(String yearStr) {
        if (yearStr.length() == 2) {
            int year = Integer.parseInt(yearStr) + 2000;
            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            int currentYear = calendar.get(Calendar.YEAR);

            // invalid year
            if (year < currentYear || year > MAX_YEAR) {
                return false;
            }
            // after all
            return true;
        }
        return false;
    }

    private static boolean checkLuhn(int[] digits) {
        int sum = 0;
        int length = digits.length;
        for (int i = 0; i < length; i++) {

            // get digits in reverse order
            int digit = digits[length - i - 1];

            // every 2nd number multiply with 2
            if (i % 2 == 1) {
                digit *= 2;
            }
            sum += digit > 9 ? digit - 9 : digit;
        }
        return sum % 10 == 0;
    }


    public static String formatCard(String cardNumber) {
        if (cardNumber == null) return null;
        char delimiter = '-';
        return cardNumber.replaceAll(".{4}(?!$)", "$0" + delimiter);
    }

    public static String colorResourceString(Context context, int color) {
        return "#" + Integer.toHexString(ContextCompat.getColor(context, color));
    }

    public static String calculateCheckDigit(String card) {
        if (card == null)
            return null;
        String digit;
        /* convert to array of int for simplicity */
        int[] digits = new int[card.length()];
        for (int i = 0; i < card.length(); i++) {
            digits[i] = Character.getNumericValue(card.charAt(i));
        }

        /* double every other starting from right - jumping from 2 in 2 */
        for (int i = digits.length - 1; i >= 0; i -= 2) {
            digits[i] += digits[i];

            /* taking the sum of digits grater than 10 - simple trick by substract 9 */
            if (digits[i] >= 10) {
                digits[i] = digits[i] - 9;
            }
        }
        int sum = 0;
        for (int i = 0; i < digits.length; i++) {
            sum += digits[i];
        }
        /* multiply by 9 step */
        sum = sum * 9;

        /* convert to string to be easier to take the last digit */
        digit = sum + "";
        return digit.substring(digit.length() - 1);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String noNullString(String text) {
        text = text != null ? text : "";
        return text;
    }

    public static Bitmap stringToBitmap(String text) {
        byte[] decodedString = Base64.decode(text, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public static String menuInstallment(String installment) {

        String resultado = "";

        if (installment.equals("Sin Cuotas") || installment.equals("No Installments")) {
            resultado = "1";
        } else if (installment.equals("2 Cuotas") || installment.equals("2 Installments")) {
            resultado = "2";
        } else if (installment.equals("3 Cuotas") || installment.equals("3 Installments")) {
            resultado = "3";
        } else if (installment.equals("4 Cuotas") || installment.equals("4 Installments")) {
            resultado = "4";
        } else if (installment.equals("5 Cuotas") || installment.equals("5 Installments")) {
            resultado = "5";
        } else if (installment.equals("6 Cuotas") || installment.equals("6 Installments")) {
            resultado = "6";
        } else if (installment.equals("7 Cuotas") || installment.equals("7 Installments")) {
            resultado = "7";
        } else if (installment.equals("8 Cuotas") || installment.equals("8 Installments")) {
            resultado = "8";
        } else if (installment.equals("9 Cuotas") || installment.equals("9 Installments")) {
            resultado = "9";
        } else if (installment.equals("10 Cuotas") || installment.equals("10 Installments")) {
            resultado = "10";
        } else if (installment.equals("11 Cuotas") || installment.equals("11 Installments")) {
            resultado = "11";
        } else if (installment.equals("12 Cuotas") || installment.equals("12 Installments")) {
            resultado = "12";
        } else if (installment.equals("13 Cuotas") || installment.equals("13 Installments")) {
            resultado = "13";
        } else if (installment.equals("14 Cuotas") || installment.equals("14 Installments")) {
            resultado = "14";
        } else if (installment.equals("15 Cuotas") || installment.equals("15 Installments")) {
            resultado = "15";
        } else if (installment.equals("16 Cuotas") || installment.equals("16 Installments")) {
            resultado = "16";
        } else if (installment.equals("17 Cuotas") || installment.equals("17 Installments")) {
            resultado = "17";
        } else if (installment.equals("18 Cuotas") || installment.equals("18 Installments")) {
            resultado = "18";
        } else if (installment.equals("19 Cuotas") || installment.equals("19 Installments")) {
            resultado = "19";
        } else if (installment.equals("20 Cuotas") || installment.equals("20 Installments")) {
            resultado = "20";
        } else if (installment.equals("21 Cuotas") || installment.equals("21 Installments")) {
            resultado = "21";
        } else if (installment.equals("22 Cuotas") || installment.equals("22 Installments")) {
            resultado = "22";
        } else if (installment.equals("23 Cuotas") || installment.equals("23 Installments")) {
            resultado = "23";
        } else if (installment.equals("24 Cuotas") || installment.equals("24 Installments")) {
            resultado = "24";
        } else if (installment.equals("25 Cuotas") || installment.equals("25 Installments")) {
            resultado = "25";
        } else if (installment.equals("26 Cuotas") || installment.equals("26 Installments")) {
            resultado = "26";
        } else if (installment.equals("27 Cuotas") || installment.equals("27 Installments")) {
            resultado = "27";
        } else if (installment.equals("28 Cuotas") || installment.equals("28 Installments")) {
            resultado = "28";
        } else if (installment.equals("29 Cuotas") || installment.equals("29 Installments")) {
            resultado = "29";
        } else if (installment.equals("30 Cuotas") || installment.equals("30 Installments")) {
            resultado = "30";
        } else if (installment.equals("31 Cuotas") || installment.equals("31 Installments")) {
            resultado = "31";
        } else if (installment.equals("32 Cuotas") || installment.equals("32 Installments")) {
            resultado = "32";
        } else if (installment.equals("33 Cuotas") || installment.equals("33 Installments")) {
            resultado = "33";
        } else if (installment.equals("34 Cuotas") || installment.equals("34 Installments")) {
            resultado = "34";
        } else if (installment.equals("35 Cuotas") || installment.equals("35 Installments")) {
            resultado = "35";
        } else if (installment.equals("36 Cuotas") || installment.equals("36 Installments")) {
            resultado = "36";
        }else if (installment.equals("48 Cuotas") || installment.equals("48 Installments")) {
            resultado = "48";
        }

        return resultado;
    }

    public void slideToRight(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, view.getWidth(), 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    // To animate view slide out from right to left
    public void slideToLeft(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, -view.getWidth(), 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    // To animate view slide out from top to bottom
    public void slideToBottom(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    // To animate view slide out from bottom to top
    public void slideToTop(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, -view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    public static void errorVibrate(Vibrator vibrator, long milliseconds){
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
            }else{
                vibrator.vibrate(milliseconds);
            }
        }
    }



}


