package com.bdcom.nio.server;

import com.bdcom.biz.pojo.ITesterRecord;
import com.bdcom.nio.BDPacket;
import com.bdcom.nio.BDPacketUtil;
import com.bdcom.nio.ServerContent;
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

        ITesterRecord checked = ServerContent.SaveITesterRecord(record);
        BDPacket pack = null;
        try {
            pack = BDPacketUtil.encapsulateToPacket( checked );
        } catch (IOException e) {
            System.err.println( "IOException when encapsulate ITesterRecord: " + e.getMessage() );
        }

        return pack;
    }
}
