package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.pay.trans.action;

import android.content.Context;
import android.content.Intent;

import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.ReadCardActivity;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.abl.core.AAction;
import com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.pay.constant.EUIParamKeys;

public class ActionSearchCard extends AAction {

    public ActionSearchCard(AAction.ActionStartListener listener) {
        super(listener);
        // TODO Auto-generated constructor stub
    }

    /**
     * 寻卡类型定义
     *
     * @author Steven.W
     */
    public static class SearchMode {
        /**
         * 刷卡
         */
        public static final byte SWIPE = 0x01;
        /**
         * 插卡
         */
        public static final byte INSERT = 0x02;
        /**
         * 挥卡
         */
        public static final byte TAP = 0x04;
        /**
         * 支持插卡/挥卡
         */
        public static final byte INSERT_TAP = 0x06;
        /**
         * 支持手输
         */
        public static final byte KEYIN = 0x08;
    }

    public static class CardInformation {
        private byte searchMode;
        private String track1;
        private String track2;
        private String track3;
        private String pan;
        private String ExpDate;

        public CardInformation(byte mode, String track1, String track2, String track3, String pan) {
            this.searchMode = mode;
            this.track1 = track1;
            this.track2 = track2;
            this.track3 = track3;
            this.pan = pan;
        }

        public CardInformation(byte mode) {
            this.searchMode = mode;
        }

        public CardInformation(byte mode, String pan) {
            this.searchMode = mode;
            this.pan = pan;
        }

        public CardInformation() {

        }

        public byte getSearchMode() {
            return searchMode;
        }

        public void setSearchMode(byte searchMode) {
            this.searchMode = searchMode;
        }

        public String getTrack1() {
            return track1;
        }

        public void setTrack1(String track1) {
            this.track1 = track1;
        }

        public String getTrack2() {
            return track2;
        }

        public void setTrack2(String track2) {
            this.track2 = track2;
        }

        public String getTrack3() {
            return track3;
        }

        public void setTrack3(String track3) {
            this.track3 = track3;
        }

        public String getPan() {
            return pan;
        }

        public void setPan(String pan) {
            this.pan = pan;
        }

        public String getExpDate() {
            return ExpDate;
        }

        public void setExpDate(String expDate) {
            ExpDate = expDate;
        }
    }

    public static enum ESearchCardUIType {
        DEFAULT,
        QUICKPASS,
        EC,
    }

    private Context context;
    private byte mode;

    private String title;
    private String amount;
    private String date;
    private String code;
    private String searchCardPrompt;

    private ESearchCardUIType searchCardUIType;

    /**
     * 设置参数
     *
     * @param context ：上下文
     * @param mode    ：读卡模式
     * @param amount  ：交易模式
     */
    public void setParam(Context context, String title, byte mode, String amount, String code, String date,
                         ESearchCardUIType searchCardUIType) {
        this.context = context;
        this.title = title;
        this.mode = mode;
        this.amount = amount;
        this.code = code;
        this.date = date;
        this.searchCardUIType = searchCardUIType;
    }

    public void setParam(Context context, String title, byte mode, String amount, String code, String date,
                         ESearchCardUIType searchCardUIType, String searchCardPrompt) {
        this.context = context;
        this.title = title;
        this.mode = mode;
        this.amount = amount;
        this.code = code;
        this.date = date;
        this.searchCardUIType = searchCardUIType;
        this.searchCardPrompt = searchCardPrompt;
    }

    @Override
    protected void process() {
        Intent intent = new Intent(context, ReadCardActivity.class);
        intent.putExtra(EUIParamKeys.NAV_TITLE.toString(), title);
        intent.putExtra(EUIParamKeys.NAV_BACK.toString(), true);
        intent.putExtra(EUIParamKeys.TRANS_AMOUNT.toString(), amount);
        intent.putExtra(EUIParamKeys.CARD_SEARCH_MODE.toString(), mode);
        intent.putExtra(EUIParamKeys.AUTH_CODE.toString(), code);
        intent.putExtra(EUIParamKeys.TRANS_DATE.toString(), date);
        intent.putExtra(EUIParamKeys.SEARCH_CARD_UI_TYPE.toString(), searchCardUIType);
        intent.putExtra(EUIParamKeys.SEARCH_CARD_PROMPT.toString(), searchCardPrompt);
        context.startActivity(intent);
    }

}
