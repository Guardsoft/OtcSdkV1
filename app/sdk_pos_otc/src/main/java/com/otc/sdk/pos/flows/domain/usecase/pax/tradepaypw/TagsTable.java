package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw;

/**
 * Created by xionggd on 2017/8/16.
 */
public class TagsTable {
    public static final int APP_VER = 0x9F09;

    public static final int CARD_DATA = 0xDF8117;
    public static final int CVM_REQ = 0xDF8118;
    public static final int CVM_NO = 0xDF8119;
    public static final int DEF_UDOL = 0xDF811F;
    public static final int SEC = 0xDF811A;
    public static final int MAG_CVM_REQ = 0xDF811E;
    public static final int MAG_CVM_NO = 0xDF812C;

    public static final int AMOUNT = 0x9F02;
    public static final int AMOUNT_OTHER = 0x9F03;

    public static final int TRANS_TYPE = 0x9C;
    public static final int TRANS_DATE = 0x9A;
    public static final int TRANS_TIME = 0x9F21;

    //TAC Online
    public static final int TERM_DEFAULT = 0xDF8120;
    public static final int TERM_DENIAL = 0xDF8121;
    public static final int TERM_ONLINE = 0xDF8122;

    //limit  set for AID
    public static final int FLOOR_LIMIT = 0xDF8123;
    public static final int TRANS_LIMIT = 0xDF8124;
    public static final int TRANS_CVM_LIMIT = 0xDF8125;
    public static final int CVM_LIMIT = 0xDF8126;

    public static final int MAX_TORN = 0xDF811D;

    public static final int COUNTRY_CODE = 0x9F1A;
    public static final int CURRENCY_CODE = 0x5F2A;

    public static final int KERNEL_CFG = 0xDF811B;

    public static final int CAPK_ID = 0x8F;
    public static final int CAPK_RID = 0x4F;

    public static final int PRO_ID = 0x9F5A;

    public static final int TRACK1 = 0x56;
    public static final int TRACK2 = 0x57;
    public static final int TRACK2_1 = 0x9F6B;

    public static final int PAN_SEQ_NO = 0x5F34;
    public static final int APP_LABEL = 0x50;
    public static final int TVR = 0x95;
    public static final int TSI = 0x9B;
    public static final int ATC = 0x9F36;
    public static final int APP_CRYPTO = 0x9F26;
    public static final int APP_NAME = 0x9F12;

    public static final int LIST = 0xDF8129;

    public static final int CRYPTO = 0x9F27;

    public static final int ACCOUNT_TYPE = 0x5F57;
    public static final int ACQUIRER_ID = 0x9F01;
    public static final int INTER_DEV_NUM = 0x9F1E;
    public static final int MERCHANT_CATEGORY_CODE = 0x9F15;
    public static final int MERCHANT_ID = 0x9F16;
    public static final int MERCHANT_NAME_LOCATION = 0x9F4E;
    public static final int TERMINAL_CAPABILITY = 0x9F33;
    public static final int TERMINAL_ID = 0x9F1C;
    public static final int MOB_SUP = 0x9F7E;

    public static final int BALANCE_BEFORE_GAC = 0xDF8104;
    public static final int BALANCE_AFTER_GAC = 0xDF8105;
    public static final int MESS_HOLD_TIME = 0xDF812D;

    public static final int DS_AC_TYPE = 0xDF8108;
    public static final int DS_INPUT_CARD = 0xDF60;
    public static final int DS_INPUT_TERMINAL = 0xDF8109;
    public static final int DS_ODS_INFO = 0xDF62;
    public static final int DS_ODS_READER = 0xDF810A;
    public static final int DS_ODS_TERMINAL = 0xDF63;

    public static final int FST_WRITE = 0xDF8110;
    public static final int READ = 0xDF8112;
    public static final int WIRTE_BEFORE_AC = 0xFF8102;
    public static final int WIRTE_AFTER_AC = 0xFF8103;
    public static final int TIMEOUT = 0xDF8127;

    public static final int ADDITIONAL_CAPABILITY = 0x9F40;
    public static final int DS_OPERATOR_ID = 0x9F5C;

    private TagsTable() {

    }
}
