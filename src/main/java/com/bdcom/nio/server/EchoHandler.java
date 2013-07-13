package com.bdcom.nio.server;

import com.bdcom.nio.BDPacket;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-9    <br/>
 * Time: 10:14  <br/>
 */
public class EchoHandler extends CommonHandler {
    @Override
    protected BDPacket doHandle(BDPacket bdPacket) {
        //System.out.println("receive echo msg: " + new String(bdPacket.getData()));
        return bdPacket;
    }
}
