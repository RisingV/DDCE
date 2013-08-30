package com.bdcom.dce.nio.exception;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-19    <br/>
 * Time: 09:59  <br/>
 */
public class GlobalException extends Exception {

    private Exception origin;

    public GlobalException() {}

    public GlobalException(String msg) {
        super(msg);
    }

    public GlobalException(Exception origin) {
        this.origin = origin;
    }

    public GlobalException(Exception origin, String msg) {
        super(msg);
        this.origin = origin;
    }

    public  void setOriginException(Exception origin) {
        this.origin = origin;
    }

    public Exception getOriginException() {
        return origin;
    }

}
