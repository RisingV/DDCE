package com.bdcom.dce.itester.rpc.req;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-30    <br/>
 * Time: 14:03  <br/>
 */
public class SetTxModeReq implements Serializable {

    private static final long serialVersionUID = -2699940658813127504L;

    private int socketId;

    private int cardId;

    private int portId;

    private int mode;

    private int burstNum;

    public int getSocketId() {
        return socketId;
    }

    public int getCardId() {
        return cardId;
    }

    public int getMode() {
        return mode;
    }

    public int getBurstNum() {
        return burstNum;
    }

    public void setSocketId(int socketId) {
        this.socketId = socketId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setBurstNum(int burstNum) {
        this.burstNum = burstNum;
    }

    public int getPortId() {
        return portId;
    }

    public void setPortId(int portId) {
        this.portId = portId;
    }
}
