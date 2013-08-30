package com.bdcom.dce.itester.rpc.req;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-30    <br/>
 * Time: 14:20  <br/>
 */
public class SetFramLengthChangeReq implements Serializable {

    private static final long serialVersionUID = 319267330762094531L;

    private int socketId;

    private int cardId;

    private int portId;

    private int isChange;

    public int getSocketId() {
        return socketId;
    }

    public int getCardId() {
        return cardId;
    }

    public int getPortId() {
        return portId;
    }

    public int getChange() {
        return isChange;
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

    public void setChange(int change) {
        isChange = change;
    }

}
