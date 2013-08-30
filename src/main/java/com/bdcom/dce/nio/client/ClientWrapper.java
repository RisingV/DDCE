package com.bdcom.dce.nio.client;

import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.BDPacketUtil;
import com.bdcom.dce.nio.DataType;
import com.bdcom.dce.nio.RequestID;
import com.bdcom.dce.nio.exception.GlobalException;
import com.bdcom.dce.sys.config.ServerConfig;
import com.bdcom.dce.util.log.ErrorLogger;

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

    private SortingThread responseSortingThread;

    private Hook restartSortingThreadHook;

    private boolean isRunning = false;

    private boolean sortThreadRunning = false;

    public ClientWrapper(ServerConfig serverConfig) {
        client = new NIOClient(serverConfig);
        responseContainer = new ConcurrentHashMap<Integer, BlockingQueue<BDPacket>>();
        restartSortingThreadHook = new Hook() {
            @Override
            public void call() {
                responseSortingThread = newSortingThread();
                responseSortingThread.setRestartHook(restartSortingThreadHook);
                responseSortingThread.start();
            }
        };
    }

    public ServerConfig getServerConfig() {
        return client.getServerConfig();
    }

    public BDPacket send(BDPacket packet) throws IOException, GlobalException {
        start();
        int requestID = packet.getRequestID();
        client.sendPacket( packet );

        BDPacket response = null;
        boolean interrupted = false;
        try {
            while( true ) {
                response = getResponseByRequestID( requestID );
                if ( DataType.GLOBAL_EXCEPTION != response.getDataType() ) {
                    break;
                } else {
                    if ( responseSortingThread.hasUnhandledException() ) {
                        responseSortingThread.handledRecentException();
                        BDPacketUtil.globalExceptionCheck( packet );
                        break;
                    } else {
                        continue;
                    }
                }
            }
        } catch (InterruptedException e) { // this damn exception almost never happen, so ignore it
            interrupted = true;
            ErrorLogger.log( "fetch Response interrupted! request:" + requestID  );
        } finally {
            if ( interrupted ) {
                return null;
            }
        }

        return response;
    }

    public  UniChannel<BDPacket> asyncSend(BDPacket packet) throws IOException {
        start();
        int requestID = packet.getRequestID();
        client.sendPacket( packet );

        BlockingQueue<BDPacket> responseQueue = getResponseQueue( requestID );

        return new FilterQueue<BDPacket>( responseQueue );
    }

    public void shutdown() {
        if ( isRunning ) {
            stopResponseSortingThread();
            client.sendPacket( BDPacketUtil.terminalRequest() );
            client.shutdown();
            clearBlockingQueue( responseContainer );
            isRunning = false;
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
        if ( !sortThreadRunning ) {
            responseSortingThread = newSortingThread();
            responseSortingThread.setRestartHook(restartSortingThreadHook);
            responseSortingThread.start();
            sortThreadRunning = true;
        }
    }

    private void stopResponseSortingThread() {
        if ( sortThreadRunning ) {
            responseSortingThread.terminal();
            sortThreadRunning = false;
        }
    }

    private SortingThread newSortingThread() {
        return new SortingThread(client, responseContainer);
    }

    private synchronized static BlockingQueue allocateBlockingQueue(Integer ID,
                      Map<Integer, BlockingQueue<BDPacket>> queueContainer) {

        BlockingQueue<BDPacket> queue = queueContainer.get(ID);
        if ( null == queue ) { //double checking!!
            queue = new LinkedBlockingDeque<BDPacket>();
            queueContainer.put( ID, queue );
        }

        return queue;
    }

    private synchronized static void clearBlockingQueue(
            Map<Integer,BlockingQueue<BDPacket>> queueContainer) {
        if ( null != queueContainer ) {
            queueContainer.clear();
        }
    }

    static class SortingThread extends Thread {
        private static final int MAX_INTERRUPTED_TIMES = 3;
        private static int interruptedCount = 0;
        private Hook restartHook;
        private boolean keepRunning;

        private static byte[] sharedLock = new byte[0];
        private static int exceptionOccurs = 0;

        private final NIOClient client;
        private final Map<Integer, BlockingQueue<BDPacket>> packetContainer;

        SortingThread(NIOClient client,
          Map<Integer, BlockingQueue<BDPacket>> packetContainer) {
            this.client = client;
            this.packetContainer = packetContainer;
            setDaemon( true );
            keepRunning = true;
        }

        void setRestartHook(Hook restartHook) {
            this.restartHook = restartHook;
        }

        void terminal() {
            this.keepRunning = false;
        }

        @Override
        public void run() {
            while( keepRunning ) {
                try {
                    BDPacket packet = client.receivePacket();

                    if ( null == packet ) {
                        continue;
                    }
                    if  (RequestID.BROADCAST == packet.getRequestID() ) {
                        if (DataType.GLOBAL_EXCEPTION == packet.getDataType() ) {
                            broadcastException( packet );
                        } else {
                            broadcast( packet );
                        }
                    } else {
                        dispatch(packet);
                    }

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

        public boolean hasUnhandledException() {
            synchronized ( sharedLock ) {
                return exceptionOccurs > 0;
            }
        }

        private void addExceptionCount() {
            synchronized ( sharedLock ) {
                exceptionOccurs++;
            }
        }

        public void handledRecentException() {
            synchronized ( sharedLock ) {
                exceptionOccurs--;
            }
        }

        private void dispatch(BDPacket packet) {
            int requestID = packet.getRequestID();
            BlockingQueue packQueue = packetContainer.get( requestID );
            if ( null == packQueue ) {
                packQueue = allocateBlockingQueue( requestID, packetContainer );
            }
            packQueue.add( packet );
        }

        private void broadcastException(BDPacket packet) {
            addExceptionCount();
            broadcast(packet);
        }

        private void broadcast(BDPacket packet) {
            for ( Map.Entry<Integer, BlockingQueue<BDPacket>> e :
                    packetContainer.entrySet() ) {

                BlockingQueue<BDPacket> queue = e.getValue();
                Integer id = e.getKey();
                if ( null != queue )  {
                    BDPacket clonedPack = BDPacket.clone( packet );
                    clonedPack.setRequestID( id.intValue() );

                    queue.add( clonedPack );
                }

            }
        }

        private synchronized int plusInterruptedCount() {
            return ++interruptedCount;
        }

    }

    private final class FilterQueue<T> implements UniChannel<T> {

        private final BlockingQueue<T> queue;

        public FilterQueue(BlockingQueue<T> queue) {
            this.queue = queue;
        }

        @Override
        public T take() throws InterruptedException {
            BDPacket response = null;
            while( true ) {
                response = (BDPacket) queue.take();
                if ( DataType.GLOBAL_EXCEPTION != response.getDataType() ) {
                    break;
                } else {
                    if ( responseSortingThread.hasUnhandledException() ) {
                        responseSortingThread.handledRecentException();
                        break;
                    } else {
                        continue;
                    }
                }
            }

            return (T) response;
        }

    }

    interface Hook {
        public void call();
    }

}

