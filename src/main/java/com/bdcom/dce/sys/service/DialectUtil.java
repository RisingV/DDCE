package com.bdcom.dce.sys.service;

import com.bdcom.dce.biz.pojo.BaseTestRecord;
import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.DataType;
import com.bdcom.dce.nio.RequestID;
import com.bdcom.dce.sys.ApplicationConstants;
import com.bdcom.dce.util.SerializeUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-12    <br/>
 * Time: 17:44  <br/>
 */
public abstract class DialectUtil implements ApplicationConstants {

    public static BDPacket generateResponseToUIClient(BaseTestRecord record , int sendStatus) {
        Map<String, Object> infoMap = new HashMap<String, Object>();
        infoMap.put( DIALECT.BASE_RECORD, record );
        infoMap.put( DIALECT.SEND_STATUS, new Integer(sendStatus) );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( infoMap );
        } catch (IOException e) {
            //TODO
        }
        BDPacket response = BDPacket.newPacket( RequestID.LOCAL.REPORT_SENDING_RESULT );
        response.setDataType(DataType.MAP);
        response.setData( data );

        return response;
    }

    public static Map<String, Object> parseResponseFromCLI(BDPacket response) {
        byte[] data = response.getData();
        Object deserialized = null;
        try {
            deserialized = SerializeUtil.deserializeFromByteArray(data);
        } catch (IOException e) {
            //TODO
        } catch (ClassNotFoundException e) {
            //TODO
        }

        Map<String, Object> infoMap = null;
        if (  deserialized instanceof Map ) {
            infoMap = (Map<String, Object>) deserialized;
        }

        return infoMap;

    }

}
