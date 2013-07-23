package com.bdcom.nio.exception;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-10    <br/>
 * Time: 17:05  <br/>
 */
public class ResponseException extends Exception {
    public ResponseException() {
    }

    public ResponseException(String message) {
        super(message);
    }
}
