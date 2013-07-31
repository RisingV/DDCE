package com.bdcom.itester.rpc.req;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-30    <br/>
 * Time: 14:08  <br/>
 */
public class GetPortAllStatsReq implements Serializable {

    private static final long serialVersionUID = -4078752590250209286L;

    private int socketId;

    private int cardId;

    private int portId;

    private int length;

    public int getSocketId() {
        return socketId;
    }

    public int getCardId() {
        return cardId;
    }

    public int getPortId() {
        return portId;
    }

    public int getLength() {
        return length;
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

    public void setLength(int length) {
        this.length = length;
    }

}
