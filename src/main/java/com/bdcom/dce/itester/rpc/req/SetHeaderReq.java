package com.bdcom.dce.itester.rpc.req;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-30    <br/>
 * Time: 13:55  <br/>
 */
public class SetHeaderReq implements Serializable {

    private static final long serialVersionUID = -3172677241268606963L;

    private int socketId;

    private int cardId;

    private int portId;

    private int validStreamCount;

    private int length;

    private int[] strHead;

    public int getSocketId() {
        return socketId;
    }

    public int getCardId() {
        return cardId;
    }

    public int getPortId() {
        return portId;
    }

    public int getValidStreamCount() {
        return validStreamCount;
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

    public void setValidStreamCount(int validStreamCount) {
        this.validStreamCount = validStreamCount;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int[] getStrHead() {
        return strHead;
    }

    public void setStrHead(int[] strHead) {
        this.strHead = strHead;
    }

}
