package com.bdcom.dce.nio.client;

import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.exception.GlobalException;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-11    <br/>
 * Time: 15:15  <br/>
 */
public class TimeoutWrapper {

    private ClientWrapper client;
    private final ExecutorService exec = Executors.newFixedThreadPool(1);

    public TimeoutWrapper(ClientWrapper client) {
        this.client = client;
    }

    public BDPacket send(BDPacket request, long timeout)
            throws IOException, InterruptedException, TimeoutException, GlobalException {
        BDPacket response = null;
        if ( timeout > 0 ) {
            TimerTask<BDPacket> task = new TimerTask<BDPacket>(client,request);
            Future<BDPacket> future = exec.submit( task );
            try {
                response = future.get( timeout, TimeUnit.SECONDS );
            } catch (ExecutionException e) {
                throw new TimeoutException( e.getMessage() );
            }
        } else {
            response = client.send( request );
        }

        return response;
    }

}

class TimerTask<T> implements Callable<T> {

    private ClientWrapper client;
    private BDPacket request;

    TimerTask(ClientWrapper client, BDPacket request) {
        this.client = client;
        this.request = request;
    }

    @Override
    public T call() throws Exception {
        return (T) client.send(request);
    }

}

