package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.app.quickclick;

/**
 * 快速点击保护， 主要用在界面功能选择
 *
 * @author Steven.W
 */
public class QuickClickProtection extends AutoRecoveredValueSetter<Boolean> {

    public QuickClickProtection(long timeoutMs) {
        setTimeoutMs(timeoutMs);
        setValue(false);
        setRecoverTo(false);
    }

    /**
     * 默认500ms
     */
    public QuickClickProtection() {
        setTimeoutMs(500);
        setValue(false);
        setRecoverTo(false);
    }

    public boolean isStarted() {
        return getValue();
    }

    public void start() {
        setValue(true);
        autoRecover();
    }

    public void stop() {
        recover();
    }
}
