package com.bdcom.nio.server;

import com.bdcom.nio.ServerContent;
import com.bdcom.nio.BDPacket;
import com.bdcom.nio.BDPacketUtil;
import com.bdcom.nio.DataType;
import com.bdcom.biz.pojo.ITesterRecord;
import com.bdcom.util.SerializeUtil;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-5    <br/>
 * Time: 15:29  <br/>
 */
public class ITesterHandler extends CommonHandler {
    @Override
    protected BDPacket doHandle(BDPacket bdPacket) {
        ITesterRecord record = null;
        boolean deserialSuccess = true;
        try {
            record = (ITesterRecord) SerializeUtil.deserializeFromByteArray(bdPacket.getData());
        } catch (IOException e) {
            deserialSuccess = false;
            System.err.println( "IOException occurs when deserialize ITesterRecord: "
                    + e.getMessage() );
        } catch (ClassNotFoundException e) {
            deserialSuccess = false;
            System.err.println( "ClassNotFoundException occurs when deserialize ITesterRecord: "
                    + e.getMessage() );
        } finally {
            if ( !deserialSuccess ) {
                return BDPacketUtil.responseToInvalidData( bdPacket.getRequestID() ) ;
            }
        }

        int saveStatus = ServerContent.SaveITesterRecord(record);
        byte[] data = BDPacketUtil.intToByteArray( saveStatus );

        BDPacket pack = BDPacket.newPacket( bdPacket.getRequestID() );
        pack.setDataType( DataType.INTEGER );
        pack.setData( data );

        return pack;
    }
}
