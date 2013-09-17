package com.bdcom.dce.nio.server;

import com.bdcom.dce.biz.storage.StorableMgr;
import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.BDPacketUtil;
import com.bdcom.dce.nio.DataType;
import com.bdcom.dce.nio.bdpm.PMInterface;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-16    <br/>
 * Time: 16:15  <br/>
 */
public class StorageMD5CheckHandler extends ResourceHandler  {

    public StorageMD5CheckHandler(PMInterface pmInterface) {
        super(pmInterface);
    }

    @Override
    protected BDPacket doHandle(BDPacket bdPacket) {
        StorableMgr mgr = getStorableMgr();
        String serverMD5 = mgr.getMD5String();
        String clientMD5 = new String( bdPacket.getData() );

        byte[] data = null;
        if ( serverMD5.equals( clientMD5 ) ) {
            data = BDPacketUtil.intToByteArray( 0 );
        } else {
            data = BDPacketUtil.intToByteArray( 1 );
        }

        BDPacket response = BDPacket.newPacket( bdPacket.getRequestID() );
        response.setDataType( DataType.INTEGER );
        response.setData( data );

        return response;
    }

}
