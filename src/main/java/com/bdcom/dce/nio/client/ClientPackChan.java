package com.bdcom.dce.nio.client;

import com.bdcom.dce.nio.BDPacket;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-16    <br/>
 * Time: 12:05  <br/>
 */
public interface ClientPackChan {

    public void sendPacket(BDPacket packet);

    public BDPacket receivePacket() throws InterruptedException;

}
