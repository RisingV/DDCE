package com.bdcom.dce.nio.server;

import com.bdcom.dce.biz.pojo.BaseTestRecord;
import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.BDPacketUtil;
import com.bdcom.dce.nio.DataType;
import com.bdcom.dce.nio.bdpm.PMInterface;
import com.bdcom.dce.util.SerializeUtil;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-5    <br/>
 * Time: 15:09  <br/>
 */
public class BaseTestHandler extends CommonHandler {

    private final PMInterface pmInterface;

    public BaseTestHandler(PMInterface pmInterface) {
        super( pmInterface );
        this.pmInterface = pmInterface;
    }

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

        int saveStatus = pmInterface.saveBaseTestRecord(record);
        byte[] data = BDPacketUtil.intToByteArray( saveStatus );

        BDPacket pack = BDPacket.newPacket( bdPacket.getRequestID() );
        pack.setDataType( DataType.INTEGER );
        pack.setData( data );

        return pack;
    }

}
