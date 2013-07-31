package com.bdcom.itester.rpc.req;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-30    <br/>
 * Time: 14:01  <br/>
 */
public class SetDelayCountReq implements Serializable {

    private static final long serialVersionUID = 8463239773100174588L;

    private int socketId;

    private int cardId;

    private int portId;

    private int delayCount;

    public int getSocketId() {
        return socketId;
    }

    public int getCardId() {
        return cardId;
    }

    public int getPortId() {
        return portId;
    }

    public int getDelayCount() {
        return delayCount;
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

    public void setDelayCount(int delayCount) {
        this.delayCount = delayCount;
    }

}
