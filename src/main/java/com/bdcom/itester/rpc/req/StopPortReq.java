package com.bdcom.itester.rpc.req;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-30    <br/>
 * Time: 14:07  <br/>
 */
public class StopPortReq implements Serializable {

    private static final long serialVersionUID = 678980714721415374L;

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

    public void setPortId(int portId) {
        this.portId = portId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public void setSocketId(int socketId) {
        this.socketId = socketId;
    }

}
