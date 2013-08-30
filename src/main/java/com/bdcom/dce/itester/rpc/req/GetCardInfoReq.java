package com.bdcom.dce.itester.rpc.req;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-29    <br/>
 * Time: 17:58  <br/>
 */
public class GetCardInfoReq implements Serializable {

    private static final long serialVersionUID = 2988361423405023691L;

    private int socketId;

    private int cardId;

    public int getSocketId() {
        return socketId;
    }

    public void setSocketId(int socketId) {
        this.socketId = socketId;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

}
