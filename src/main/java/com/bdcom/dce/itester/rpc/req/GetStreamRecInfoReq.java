package com.bdcom.dce.itester.rpc.req;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-30    <br/>
 * Time: 14:26  <br/>
 */
public class GetStreamRecInfoReq implements Serializable {

    private static final long serialVersionUID = -6485782483567487009L;

    private int socketId;

    private int cardId;

    private int portId;

    private int streamId;

    public int getSocketId() {
        return socketId;
    }

    public int getCardId() {
        return cardId;
    }

    public int getPortId() {
        return portId;
    }

    public int getStreamId() {
        return streamId;
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

    public void setStreamId(int streamId) {
        this.streamId = streamId;
    }

}
