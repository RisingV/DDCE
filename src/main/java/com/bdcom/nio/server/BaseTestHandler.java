package com.bdcom.nio.server;

import com.bdcom.nio.ServerContent;
import com.bdcom.nio.BDPacket;
import com.bdcom.nio.BDPacketUtil;
import com.bdcom.nio.DataType;
import com.bdcom.pojo.BaseTestRecord;
import com.bdcom.util.SerializeUtil;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-5    <br/>
 * Time: 15:09  <br/>
 */
public class BaseTestHandler extends CommonHandler {

    @Override
    protected BDPacket doHandle(BDPacket bdPacket) {
        BaseTestRecord record = null;
        boolean deserialSuccess = true;
        try {
            record = (BaseTestRecord) SerializeUtil.deserializeFromByteArray(bdPacket.getData());
        } catch (IOException e) {
            deserialSuccess = false;
            System.err.println( "IO Exception occurs when deserialize BaseTestRecord: "
                    + e.getMessage() );
        } catch (ClassNotFoundException e) {
            deserialSuccess = false;
            System.err.println( "ClassNotFoundException occurs when deserialize BaseTestRecord: "
                    + e.getMessage() );
        } finally {
            if ( !deserialSuccess ) {
                return BDPacketUtil.responseToInvalidData( bdPacket.getRequestID() ) ;
            }
        }

        int saveStatus = ServerContent.SaveBaseTestRecord(record);
        byte[] data = BDPacketUtil.intToByteArray( saveStatus );

        BDPacket pack = BDPacket.newPacket( bdPacket.getRequestID() );
        pack.setDataType( DataType.INTEGER );
        pack.setData( data );

        return pack;
    }

}
