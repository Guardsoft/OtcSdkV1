package com.otc.sdk.pos.flows.domain.usecase.pax.jemv.clsspaypass.trans;

import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.device.model.ApduRespL2;
import com.pax.jemv.device.model.ApduSendL2;
import com.pax.jemv.paypass.api.ClssPassApi;
import com.pax.jemv.paypass.listener.IClssPassCBFun;

/**
 * Created by a on 2017-04-10.
 */

public class Test_ClssPassCBFunApi implements IClssPassCBFun {
    ByteArray outcomeParamSet = new ByteArray(8);
    ByteArray userInterReqData = new ByteArray(24);
    ByteArray errIndication = new ByteArray(6);

    @Override
    public int sendDEKData(byte[] bytes, int i) {
        return 0;
    }// 回调函数在这实现 - by wfh

    @Override
    public int receiveDETData(ByteArray byteArray, byte[] bytes) {
        return 0;
    }

    @Override
    public int addAPDUToTransLog(ApduSendL2 apduSendL2, ApduRespL2 apduRespL2) {
        return 0;
    }

    @Override
    public int sendTransDataOutput(byte b) {
        if ((b & 0x01) != 0) {
            ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0xDF, (byte) 0x81, 0x29}, (byte) 3, 8, outcomeParamSet);
            //getTlv(TagsTable.LIST, outcomeParamSet);
        }

        if ((b & 0x04) != 0) {
            ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0xDF, (byte) 0x81, 0x16}, (byte) 3, 24, userInterReqData);
        }

        if ((b & 0x02) != 0) {
            ClssPassApi.Clss_GetTLVDataList_MC(new byte[]{(byte) 0xDF, (byte) 0x81, 0x15}, (byte) 3, 6, errIndication);
        }
        return 0;
    }
    /**自动生成代码**/

}
