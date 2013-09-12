package com.bdcom.dce.nio.client;

import com.bdcom.dce.biz.storage.Item;
import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.DataType;
import com.bdcom.dce.nio.RequestID;
import com.bdcom.dce.nio.exception.GlobalException;
import com.bdcom.dce.util.SerializeUtil;

import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-12    <br/>
 * Time: 16:11  <br/>
 */
public class StorageTransfer {

    private final ClientWrapper client;

    public StorageTransfer(ClientWrapper client) {
        this.client = client;
    }

    public void uploadStorage(Map<String, Item> storage)
            throws IOException, GlobalException {
        if ( null == storage ) {
            return;
        }

        byte[] data = SerializeUtil.serializeToByteArray( storage );
        BDPacket packet = BDPacket.newPacket( RequestID.UPLOAD_LOCAL_STORAGE );
        packet.setDataType( DataType.MAP );
        packet.setData( data );

        client.send( packet );
    }

    public Map<String, Item> downloadStorage() throws IOException, GlobalException {
        BDPacket request = BDPacket.newPacket( RequestID.DOWNLOAD_LOCAL_STORAGE );
        request.setData( new byte[0] );

        BDPacket response = client.send( request );
        Object deserialized = null;
        try {
            deserialized = SerializeUtil.deserializeFromByteArray( response.getData() );
        } catch (ClassNotFoundException e) {
        }
        return (Map<String, Item>) deserialized;
    }

}
