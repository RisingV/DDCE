package com.bdcom.nio.client;

import com.bdcom.exception.TimeoutException;
import com.bdcom.nio.BDPacket;
import com.bdcom.util.log.ErrorLogger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-11    <br/>
 * Time: 15:15  <br/>
 */
public class TimeoutWrapper {

    private final ClientWrapper client;

    public TimeoutWrapper(ClientWrapper client) {
        this.client = client;
    }

    public BDPacket send(BDPacket request, long timeout)
            throws TimeoutException, IOException, InterruptedException {
        BDPacket response = null;
        if ( timeout > 0 ) {
            TimeoutThread counting = new TimeoutThread( timeout );
            counting.start();
            response = client.send( request );
            counting.cancel();
        } else {
            response = client.send( request );
        }

        return response;
    }

}

class TimeoutThread extends Thread {

    private boolean cancel = false;

    private final long timeout;

    TimeoutThread(long timeout) {
        this.timeout = timeout;
    }

    public synchronized void cancel() {
        cancel = true;
    }

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep( timeout );
        } catch (InterruptedException e) {
            ErrorLogger.log("time counting fail!: " + e.getMessage());
        }
        if ( !cancel ) {
            throw new TimeoutException();
        }
    }
}

