package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.app.quickclick;

import android.os.SystemClock;

/**
 * 快速点击保护， 主要用在界面功能选择
 *
 * @author Steven.W
 */
public class AutoRecoveredValueSetter<T> {

    T value;
    T recoveredTo;
    long timeoutMs;

    protected void setValue(T value) {
        this.value = value;
    }

    protected T getValue() {
        return value;
    }

    protected void setRecoverTo(T value) {
        this.recoveredTo = value;
    }

    protected void setTimeoutMs(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    protected void recover() {
        this.value = recoveredTo;
    }

    protected void autoRecover() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(timeoutMs);
                setValue(recoveredTo);
            }
        }).start();
    }

}
