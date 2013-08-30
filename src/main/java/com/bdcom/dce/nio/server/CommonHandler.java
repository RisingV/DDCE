package com.bdcom.dce.nio.server;

import com.bdcom.dce.nio.ServerContent;
import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.BDPacketUtil;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-5    <br/>
 * Time: 14:16  <br/>
 */
abstract public class CommonHandler implements IHandler {

    @Override
    public BDPacket handle(BDPacket bdPacket) {
        if ( null == bdPacket ) {
            return BDPacketUtil.responseToNull();
        }
        if (ServerContent.getRunningApplicationVersion() !=
                bdPacket.getVersion() ) {
            return BDPacketUtil.responseToInvalidVer( bdPacket.getRequestID() );
        }

        return doHandle( bdPacket );
    }

    abstract protected BDPacket doHandle(BDPacket bdPacket);

}