package com.bdcom.dce.nio.server;

import com.bdcom.dce.nio.BDPacket;

/**
 * Created with IntelliJ IDEA.
 * User: francis
 * Date: 13-7-4
 * Time: 上午11:06
 */
public interface IHandler {
    public BDPacket handle(BDPacket bdPacket);
}
