/*
 * ============================================================================
 * COPYRIGHT
 *              Pax CORPORATION PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with Pax Corporation and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2016 - ? Pax Corporation. All rights reserved.
 * Module Date: 2016-11-26
 * Module Author: Steven.W
 * Description:
 *
 * ============================================================================
 */
package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.utils;

import android.os.CountDownTimer;

public class TickTimer extends CountDownTimer {

    public interface TickTimerListener {
        public void onFinish();

        public void onTick(long leftTime);
    }

    private TickTimerListener listener;

    public void setTimeCountListener(TickTimerListener listener) {
        this.listener = listener;
    }

    /**
     * @param timeout      表示以毫秒(1/1000S)为单位 倒计时的总数
     * @param tickInterval 表示 间隔 多少毫秒 调用一次 onTick 方法
     */
    public TickTimer(long timeout, long tickInterval) {
        super(timeout * 1000, tickInterval * 1000);
    }

    @Override
    public void onFinish() {
        if (listener != null)
            listener.onFinish();
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (listener != null)
            listener.onTick(millisUntilFinished / 1000);
    }

}