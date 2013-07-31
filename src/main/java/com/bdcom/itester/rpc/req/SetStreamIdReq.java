package com.bdcom.itester.rpc.req;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-30    <br/>
 * Time: 14:16  <br/>
 */
public class SetStreamIdReq implements Serializable {

    private static final long serialVersionUID = -3538673818874689878L;

    private int socketId;

    private int cardId;

    private int portId;

    private int iStartId;

    private int iIdNum;

    public int getSocketId() {
        return socketId;
    }

    public int getCardId() {
        return cardId;
    }

    public int getPortId() {
        return portId;
    }

    public int getiStartId() {
        return iStartId;
    }

    public int getiIdNum() {
        return iIdNum;
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

    public void setiStartId(int iStartId) {
        this.iStartId = iStartId;
    }

    public void setiIdNum(int iIdNum) {
        this.iIdNum = iIdNum;
    }

}
