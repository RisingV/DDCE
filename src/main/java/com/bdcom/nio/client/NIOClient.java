package com.bdcom.nio.client;

import com.bdcom.util.log.ErrorLogger;
import com.bdcom.nio.BDPacket;
import naga.NIOService;
import naga.NIOSocket;
import naga.SocketObserver;
import naga.packetreader.RegularPacketReader;
import naga.packetwriter.RegularPacketWriter;

import java.io.EOFException;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created with IntelliJ IDEA.
 * User: francis
 * Date: 13-7-4
 * Time: 14:04
 */
public class NIOClient {

    private final String host;
    private final int port;

    private NIOService nioService;
    private NIOSocket nioSocket;

    private BlockingQueue<BDPacket> inChan;
    private BlockingQueue<BDPacket> outChan;

    private RollingDaemon rollingDaemon;
    private boolean started = false;

    public NIOClient(String host, int port) {
        this.host = host;
        this.port = port;
        inChan = new LinkedBlockingDeque<BDPacket>();
        outChan = new LinkedBlockingDeque<BDPacket>();
    }

    public void sendPacket(BDPacket packet) {
        inChan.add(packet);
    }

    public BDPacket receivePacket() throws InterruptedException {
        return outChan.take();
    }

    private void init() throws IOException {

        nioService = new NIOService();
        nioSocket = nioService.openSocket( host, port );

        nioSocket.setPacketReader(new RegularPacketReader(4, true));
        nioSocket.setPacketWriter(new RegularPacketWriter(4, true));

        nioSocket.listen( new ConnectionHandler(inChan, outChan) );
    }

    public void start() throws IOException {
        if ( !started ) {
            init();
            startClientDaemon(nioService);
            startWritingDaemon(nioSocket, inChan);
            started = true;
        }
    }

    public void shutdown() {
        if ( started ) {
             rollingDaemon.stopNIOService();
            //nioSocket.close();
        }
    }

    private void startClientDaemon(final NIOService nioService) {
        rollingDaemon = new RollingDaemon( nioService );
        rollingDaemon.start();
    }

    private void startWritingDaemon(final NIOSocket socket, final BlockingQueue<BDPacket> inChan) {
        Thread writingThread = new Thread() {
            {
                setDaemon( true );
            }
            @Override
            public void run() {
                BDPacket pack0 = null;
                try {
                    while ( true ) {
                        pack0 = inChan.take();
                        socket.write( pack0.toBytes() );
                    }
                } catch (InterruptedException e) {
                    ErrorLogger.log("InChan interrupted: " + e.getMessage());
                } catch (IOException e) {
                    ErrorLogger.log("NIOSocket write fail: " + e.getMessage());
                }
            }
        };
        writingThread.start();
    }

}


class ConnectionHandler implements SocketObserver {

    private final BlockingQueue<BDPacket> inChan;

    private final BlockingQueue<BDPacket> outChan;

    ConnectionHandler(BlockingQueue<BDPacket> inChan, BlockingQueue<BDPacket> outChan) {
        this.inChan = inChan;
        this.outChan = outChan;
    }

    @Override
    public void connectionOpened(NIOSocket nioSocket) {
        System.out.println("Connection opened: " + nioSocket );
    }

    @Override
    public void connectionBroken(NIOSocket nioSocket, Exception e) {
        StringBuilder sb = new StringBuilder();
        String proID =System.getProperty("nio.process");
        sb.append("ProcessID: ").append(proID).append(" ");
        if ( null == e ) {
            sb.append( "Client: " )
              .append(nioSocket)
              .append(" exit normally!");
            System.out.println( sb.toString() );
        } else if ( e instanceof EOFException ) {
            sb.append( "Client: ")
                    .append( nioSocket )
                    .append("reach EOF!");
            System.out.println(sb.toString());
        } else {
            sb.append( "Connection [ " )
              .append( nioSocket )
              .append( " ] has broken! due to: ")
              .append( e.getMessage() );
            ErrorLogger.log(sb.toString());
        }
    }

    @Override
    public void packetReceived(NIOSocket socket, byte[] packet) {
        try {
            BDPacket pack = BDPacket.parse( packet);
            outChan.add( pack );
        } catch (IOException e) {
            ErrorLogger.log("BDPacket parse fail! :" + e.getMessage());
        }
    }

    @Override
    public void packetSent(NIOSocket socket, Object tag) {
        String proID =System.getProperty("nio.process");
        System.out.println("ProcessID: " +proID + " packet sent from client: " + socket );
    }
}

class RollingDaemon extends Thread {

    private final NIOService nioService;
    private boolean keepON = true;

    public RollingDaemon(NIOService nioService) {
        this.nioService = nioService;
        setDaemon( true );
    }

    @Override
    public void run() {
        while( keepON ) {
            try {
                nioService.selectBlocking();
            } catch (IOException e) {
                ErrorLogger.log("from NioService selectBlocking: " + e.getMessage());
            }
        }
    }

    public void stopNIOService() {
        keepON = false;
    }

}
