package com.bdcom.itester.rpc.req;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-30    <br/>
 * Time: 13:59  <br/>
 */
public class SetPayloadReq implements Serializable {

    private static final long serialVersionUID = 4622394343962083755L;

    private int socketId;

    private int cardId;

    private int portId;

    private int length;

    private byte[] data;

    private int payloadType;

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

    public byte[] getData() {
        return data;
    }

    public int getPayloadType() {
        return payloadType;
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

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setPayloadType(int payloadType) {
        this.payloadType = payloadType;
    }

}
