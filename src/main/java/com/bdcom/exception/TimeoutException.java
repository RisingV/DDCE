package com.bdcom.exception;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-11    <br/>
 * Time: 15:07  <br/>
 */
public class TimeoutException extends RuntimeException {
    public TimeoutException() {
    }
    public TimeoutException(String message) {
        super(message);
    }
}
