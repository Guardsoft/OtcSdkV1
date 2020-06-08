package com.otc.sdk.pos.flows.domain.usecase.pax.tradepaypw.abl.core.utils;

public class TrackUtils {

    /**
     * 从磁道2数据中获取主帐号
     *
     * @param track
     * @return
     * @date 2015年5月22日下午3:28:14
     * @example
     */
    public static String getPan(String track) {
        if (track == null)
            return null;

        int len = track.indexOf('=');
        if (len < 0) {
            len = track.indexOf('D');
            if (len < 0)
                return null;
        }

        if ((len < 13) || (len > 19))
            return null;
        return track.substring(0, len);
    }

    public static String getServiceCode(String track) {
        // todo
        return null;
    }

    /**
     * 判定是否为IC卡
     *
     * @param track
     * @return
     */
    public static boolean isIcCard(String track) {
        if (track == null)
            return false;

        int index = track.indexOf('=');
        if (index < 0) {
            index = track.indexOf('D');
            if (index < 0)
                return false;
        }

        if (index + 6 > track.length())
            return false;

        if ("2".equals(track.substring(index + 5, index + 6)) || "6".equals(track.substring(index + 5, index + 6))) {
            return true;
        }
        return false;
    }

    /**
     * 获取有效期
     *
     * @param track
     * @return
     */
    public static String getExpDate(String track) {
        if (track == null)
            return null;

        int index = track.indexOf('=');
        if (index < 0) {
            index = track.indexOf('D');
            if (index < 0)
                return null;
        }

        if (index + 5 > track.length())
            return null;
        return track.substring(index + 1, index + 5);
    }
}
