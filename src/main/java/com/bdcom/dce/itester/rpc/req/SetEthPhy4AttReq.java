package com.bdcom.dce.itester.rpc.req;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-30    <br/>
 * Time: 14:17  <br/>
 */
public class SetEthPhy4AttReq implements Serializable {

    private static final long serialVersionUID = 4749480324483372777L;

    private int socketId;

    private int cardId;

    private int portId;

    private int nego;

    private int ethPhySpeed;

    private int fullDuplex;

    private int loopback;

    public int getSocketId() {
        return socketId;
    }

    public int getCardId() {
        return cardId;
    }

    public int getPortId() {
        return portId;
    }

    public int getNego() {
        return nego;
    }

    public int getEthPhySpeed() {
        return ethPhySpeed;
    }

    public int getFullDuplex() {
        return fullDuplex;
    }

    public int getLoopback() {
        return loopback;
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

    public void setNego(int nego) {
        this.nego = nego;
    }

    public void setEthPhySpeed(int ethPhySpeed) {
        this.ethPhySpeed = ethPhySpeed;
    }

    public void setFullDuplex(int fullDuplex) {
        this.fullDuplex = fullDuplex;
    }

    public void setLoopback(int loopback) {
        this.loopback = loopback;
    }

}
