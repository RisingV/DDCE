package com.bdcom.dce.itester.rpc.req;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-30    <br/>
 * Time: 14:23  <br/>
 */
public class ResetFpgaReq implements Serializable {

    private static final long serialVersionUID = -1294843182997779724L;

    private int socketId;

    private int cardId;

    public int getSocketId() {
        return socketId;
    }

    public int getCardId() {
        return cardId;
    }

    public void setSocketId(int socketId) {
        this.socketId = socketId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

}
