package com.bdcom.dce.nio.server;

import com.bdcom.dce.biz.storage.Item;
import com.bdcom.dce.biz.storage.StorableMgr;
import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.DataType;
import com.bdcom.dce.nio.bdpm.PMInterface;
import com.bdcom.dce.util.SerializeUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-12    <br/>
 * Time: 16:44  <br/>
 */
public class DownloadResourceHandler extends ResourceHandler {

    public DownloadResourceHandler(PMInterface pmInterface) {
        super(pmInterface);
    }

    @Override
    protected BDPacket doHandle(BDPacket bdPacket) {
        StorableMgr mgr = getStorableMgr();
        if ( !mgr.isStorageLoaded() ) {
            mgr.loadStorage();
        }

        Item[] itemSet = mgr.getAll();
        Map<String, Item> itemMap = new HashMap<String, Item>(itemSet.length);
        for ( Item i : itemSet ) {
            if ( null != i ) {
                itemMap.put( i.getSerial(), i );
            }
        }

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray(itemMap);
        } catch (IOException e) {
        }
        BDPacket packet = BDPacket.newPacket( bdPacket.getRequestID() );
        packet.setDataType( DataType.MAP );
        packet.setData( data );

        return packet;
    }

}
