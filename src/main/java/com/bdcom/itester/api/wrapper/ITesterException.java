package com.bdcom.itester.api.wrapper;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-8-14    <br/>
 * Time: 17:35  <br/>
 */
public class ITesterException extends Exception {

    public static final int INVALID_IP = 1;
    public static final int CONNECT_FAIL = 2;
    public static final int PORT_NOT_LINKED = 3;
    public static final int PORT_IN_USE = 4;

    private int cardId;
    private int portId;
    private int errType;

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public int getPortId() {
        return portId;
    }

    public int getErrType() {
        return errType;
    }

    public void setPortId(int portId) {
        this.portId = portId;
    }

    public ITesterException(String msg) {
        super(msg);
    }

    public ITesterException(int errType) {
        this.errType = errType;
    }

    public ITesterException(String msg, int errType) {
        super(msg);
        this.errType = errType;
    }

    public ITesterException(int errType, int cardId, int portId) {
        this.errType = errType;
        this.cardId = cardId;
        this.portId = portId;
    }

}
