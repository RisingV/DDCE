package com.bdcom.itester.rpc.req;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-30    <br/>
 * Time: 14:12  <br/>
 */
public class SetUsedStateReq implements Serializable {

    private static final long serialVersionUID = 924442186284651915L;

    private int socketId;

    private int cardId;

    private int portId;

    private int usedState;

    public int getSocketId() {
        return socketId;
    }

    public int getCardId() {
        return cardId;
    }

    public int getPortId() {
        return portId;
    }

    public int getUsedState() {
        return usedState;
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

    public void setUsedState(int usedState) {
        this.usedState = usedState;
    }

}
