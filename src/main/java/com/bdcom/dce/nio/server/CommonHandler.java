package com.bdcom.dce.nio.server;

import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.BDPacketUtil;
import com.bdcom.dce.nio.bdpm.PMInterface;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-5    <br/>
 * Time: 14:16  <br/>
 */
abstract public class CommonHandler implements IHandler {

    private final PMInterface pmInterface;

    protected CommonHandler(PMInterface pmInterface) {
        this.pmInterface = pmInterface;
    }

    @Override
    public BDPacket handle(BDPacket bdPacket) {
        if ( null == bdPacket ) {
            return BDPacketUtil.responseToNull();
        }
        if ( pmInterface.getRunningApplicationVersion() !=
                bdPacket.getVersion() ) {
            return BDPacketUtil.responseToInvalidVer( bdPacket.getRequestID() );
        }

        return doHandle( bdPacket );
    }

    abstract protected BDPacket doHandle(BDPacket bdPacket);

}
