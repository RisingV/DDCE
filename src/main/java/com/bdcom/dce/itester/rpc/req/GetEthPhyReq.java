package com.bdcom.dce.itester.rpc.req;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-29    <br/>
 * Time: 18:00  <br/>
 */
public class GetEthPhyReq implements Serializable {

    private int socketId;

    private int cardId;

    private int portId;

    public int getSocketId() {
        return socketId;
    }

    public int getCardId() {
        return cardId;
    }

    public int getPortId() {
        return portId;
    }

    public void setSocketId(int socketId) {
        this.socketId = socketId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public void setPortId(int portId) {
        this.portId = portId;
    }

}
