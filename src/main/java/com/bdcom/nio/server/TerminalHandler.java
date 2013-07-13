package com.bdcom.nio.server;

import com.bdcom.nio.BDPacket;
import com.bdcom.nio.BDPacketUtil;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-8    <br/>
 * Time: 15:53  <br/>
 */
public class TerminalHandler implements IHandler {
    @Override
    public BDPacket handle(BDPacket bdPacket) {
        return BDPacketUtil.responseToTerminal();
    }
}
