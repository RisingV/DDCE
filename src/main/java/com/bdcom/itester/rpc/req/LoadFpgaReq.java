package com.bdcom.itester.rpc.req;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-30    <br/>
 * Time: 14:22  <br/>
 */
public class LoadFpgaReq implements Serializable {

    private static final long serialVersionUID = -6363159477477695030L;

    private int socketId;

    private int cardId;

    private int ethPhySpeed;

    public int getSocketId() {
        return socketId;
    }

    public int getCardId() {
        return cardId;
    }

    public int getEthPhySpeed() {
        return ethPhySpeed;
    }

    public void setSocketId(int socketId) {
        this.socketId = socketId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public void setEthPhySpeed(int ethPhySpeed) {
        this.ethPhySpeed = ethPhySpeed;
    }
}
