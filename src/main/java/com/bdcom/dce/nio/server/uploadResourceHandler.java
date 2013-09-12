package com.bdcom.dce.nio.server;

import com.bdcom.dce.biz.storage.Item;
import com.bdcom.dce.biz.storage.StorableMgr;
import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.BDPacketUtil;
import com.bdcom.dce.nio.bdpm.PMInterface;
import com.bdcom.dce.util.SerializeUtil;

import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-12    <br/>
 * Time: 16:31  <br/>
 */
public class UploadResourceHandler extends ResourceHandler {

    public UploadResourceHandler(PMInterface pmInterface) {
        super(pmInterface);
    }

    @Override
    protected BDPacket doHandle(BDPacket bdPacket) {
        byte[] data = bdPacket.getData();
        Object deserialized = null;
        try {
            deserialized = SerializeUtil.deserializeFromByteArray( data );
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
        }

        if ( deserialized instanceof Map) {
            StorableMgr mgr =  getStorableMgr();
            Item[] items = mgr.getAll();
            for ( Item i : items ) {
                mgr.removeItem( i );
            }
            Map<String, Item> itemMap = (Map<String, Item>) deserialized;
            for( Map.Entry<String, Item> e : itemMap.entrySet() ) {
                mgr.addItem( e.getValue() );
            }
            mgr.saveToLocalStorage();
        }
        return BDPacketUtil.emptyResponse( bdPacket.getRequestID() );
    }

}
