package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils;

import android.text.Editable;
import android.text.TextWatcher;

public class EnterAmountTextWatcher implements TextWatcher {

    private boolean mEditing;
    private String strPre = "";
    private final int MAX_DIGITS = 9;

    public EnterAmountTextWatcher() {
        mEditing = false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!mEditing) {
            mEditing = true;
            String digits = s.toString().replace(".", "").trim().replaceAll("[^(0-9)]", "");
            String str = "";
            if (digits.length() > MAX_DIGITS) {
                str = strPre;
            } else {
                if (digits == null || digits.length() == 0)
                    str = "0.00";
                else {
                    str = String.format("%d.%02d", Long.parseLong(digits) / 100, Long.parseLong(digits) % 100);
                }
            }
            try {
                s.replace(0, s.length(), str);
                strPre = str;
            } catch (NumberFormatException nfe) {
                s.clear();
            }
            mEditing = false;
        }

    }

}
