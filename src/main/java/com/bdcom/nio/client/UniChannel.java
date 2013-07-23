package com.bdcom.nio.client;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-18    <br/>
 * Time: 17:11  <br/>
 */
public interface UniChannel<T> {

    public T take() throws InterruptedException;

}
