package com.bdcom.nio.client;

import com.bdcom.sys.config.ServerConfig;
import com.bdcom.nio.BDPacket;
import com.bdcom.nio.BDPacketUtil;
import com.bdcom.util.log.ErrorLogger;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-5    <br/>
 * Time: 16:48  <br/>
 */
public class ClientWrapper {

    private NIOClient client;

    private Map<Integer, BlockingQueue<BDPacket>> responseContainer;

    private Thread responseSortingThread;

    private Hook restartSortingThreadHook;

    private boolean isRunning = false;

    public ClientWrapper(ServerConfig serverConfig) {
        client = new NIOClient(serverConfig);
        responseContainer = new ConcurrentHashMap<Integer, BlockingQueue<BDPacket>>();
        restartSortingThreadHook = new Hook() {
            @Override
            public void call() {
                SortingThread st = newSortingThread();
                st.setRestartHook( restartSortingThreadHook );
                st.start();
            }
        };
    }

    public ServerConfig getServerConfig() {
        return client.getServerConfig();
    }

    public BDPacket send(BDPacket packet) throws InterruptedException, IOException {
        start();
        int requestID = packet.getRequestID();
        client.sendPacket( packet );

        return  getResponseByRequestID( requestID );
    }

    public BlockingQueue<BDPacket> asyncSend(BDPacket packet) throws IOException {
        start();
        int requestID = packet.getRequestID();
        client.sendPacket( packet );

        return getResponseQueue( requestID );
    }

    public void shutdown() {
        if ( isRunning ) {
            client.sendPacket( BDPacketUtil.terminalRequest() );
            client.shutdown();
        }
    }

    private BlockingQueue<BDPacket> getResponseQueue(int requestID) {
        BlockingQueue<BDPacket> packQueue = responseContainer.get(requestID);
        if ( null == packQueue ) {
            packQueue = allocateBlockingQueue( requestID, responseContainer );
        }
        return packQueue;
    }

    private BDPacket getResponseByRequestID(int requestID) throws InterruptedException {
        BlockingQueue<BDPacket> packQueue = getResponseQueue(requestID);
        return packQueue.take();
    }

    private void start() throws IOException {
        if ( !isRunning ) {
            client.start();
            startResponseSortingThread();
            isRunning = true;
        }
    }

    private void startResponseSortingThread() {
        SortingThread st = newSortingThread();
        st.setRestartHook( restartSortingThreadHook );
        st.start();
    }

    private SortingThread newSortingThread() {
        return new SortingThread(client, responseContainer);
    }

    public synchronized static BlockingQueue allocateBlockingQueue(Integer ID,
                      Map<Integer, BlockingQueue<BDPacket>> queueContainer) {
        BlockingQueue<BDPacket> queue = new LinkedBlockingDeque<BDPacket>();

        queueContainer.put( ID, queue );

        return queue;
    }

    static class SortingThread extends Thread {
        private static final int MAX_INTERRUPTED_TIMES = 3;
        private static int interruptedCount = 0;
        private Hook restartHook;

        private final NIOClient client;
        private final Map<Integer, BlockingQueue<BDPacket>> packetContainer;

        SortingThread(NIOClient client,
          Map<Integer, BlockingQueue<BDPacket>> packetContainer) {
            this.client = client;
            this.packetContainer = packetContainer;
            setDaemon( true );
        }

        void setRestartHook(Hook restartHook) {
            this.restartHook = restartHook;
        }

        @Override
        public void run() {
            while( true ) {
                try {
                    BDPacket packet = client.receivePacket();
                    int requestID = packet.getRequestID();
                    BlockingQueue packQueue = packetContainer.get( requestID );
                    if ( null == packQueue ) {
                        packQueue = allocateBlockingQueue( requestID, packetContainer );
                    }
                    packQueue.add( packet );
                } catch (InterruptedException e) {
                    int count = plusInterruptedCount();
                    ErrorLogger.log("SortingThread interrupted: "
                            + count + " times : " + e.getMessage());
                    if ( count < MAX_INTERRUPTED_TIMES) {
                        if ( null != restartHook ) {
                            restartHook.call();
                        }
                    } else {
                        break;
                        //TODO report Exception!
                    }
                }
            }
        }

        private synchronized int plusInterruptedCount() {
            return ++interruptedCount;
        }
    }

    interface Hook {
        public void call();
    }

}
