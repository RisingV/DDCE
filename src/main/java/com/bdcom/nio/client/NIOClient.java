package com.bdcom.nio.client;

import com.bdcom.nio.BDPacket;
import com.bdcom.nio.DataType;
import com.bdcom.nio.RequestID;
import com.bdcom.sys.config.ServerConfig;
import com.bdcom.util.SerializeUtil;
import com.bdcom.util.log.ErrorLogger;
import com.bdcom.util.log.MsgLogger;
import naga.NIOService;
import naga.NIOSocket;
import naga.SocketObserver;
import naga.packetreader.RegularPacketReader;
import naga.packetwriter.RegularPacketWriter;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created with IntelliJ IDEA.
 * User: francis
 * Date: 13-7-4
 * Time: 14:04
 */
public class NIOClient implements ClientPackChan {

    private final ServerConfig serverConfig;

    private NIOService nioService;
    private NIOSocket nioSocket;

    private BlockingQueue<BDPacket> inChan;
    private BlockingQueue<BDPacket> outChan;
    private ConnectionHandler connectionHandler;

    private RollingDaemon rollingDaemon;
    private WritingDaemon writingDaemon;

    private boolean started = false;
    private boolean rollingStarted = false;
    private boolean writingStarted = false;


    public NIOClient(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        inChan = new LinkedBlockingDeque<BDPacket>();
        outChan = new LinkedBlockingDeque<BDPacket>();
        connectionHandler = new ConnectionHandler(inChan, outChan);
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void sendPacket(BDPacket packet) {
        inChan.add(packet);
    }

    public BDPacket receivePacket() throws InterruptedException {
        return outChan.take();
    }

    private void init() throws IOException {
        String host = serverConfig.getIpAddrStr();
        int port = serverConfig.getPort();

        System.out.println("host:" +host);
        System.out.println("port:" + port);

        nioService = new NIOService();
        nioSocket = nioService.openSocket( host, port );

        nioSocket.setPacketReader( new RegularPacketReader(4, true) );
        nioSocket.setPacketWriter( new RegularPacketWriter(4, true)) ;

        nioSocket.listen( connectionHandler );
    }

    public void start() throws IOException {
        if ( !started ) {
            init();
            startRollingDaemon(nioService);
            startWritingDaemon(nioSocket, inChan);
            started = true;
        }
    }

    public void shutdown() {
        if ( started ) {
            stopRollingDaemon();
            stopWritingDaemon();
            started = false;
        }
    }

    private void startRollingDaemon(NIOService nioService) {
        if ( !rollingStarted ) {
            rollingDaemon = new RollingDaemon( nioService );
            rollingDaemon.start();
            rollingStarted = true;
        }
    }

    private void startWritingDaemon(NIOSocket socket, BlockingQueue<BDPacket> inChan) {
        if ( !writingStarted ) {
            writingDaemon = new WritingDaemon(socket, inChan);
            writingDaemon.start();
            writingStarted = true;
        }
    }


    private void stopRollingDaemon() {
        if ( rollingStarted ) {
            rollingDaemon.terminal();
            rollingStarted = false;
        }
    }

    private void stopWritingDaemon() {
        if ( writingStarted ) {
            writingDaemon.terminal();
            writingStarted = false;
        }
    }

}


class ConnectionHandler implements SocketObserver {

    private BlockingQueue<BDPacket> inChan;

    private BlockingQueue<BDPacket> outChan;

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
        //if server is closed. e will be EOFException!
        if ( null == e ) {
            StringBuilder sb = new StringBuilder();
            sb.append( "Client: " )
              .append(nioSocket)
              .append(" exit normally!");
            MsgLogger.log(sb.toString());
        } else {
            byte[] data = null;
            try {
                data = SerializeUtil.serializeToByteArray( e );
            } catch (IOException e1 ) {
                ErrorLogger.log(
                        "IOException when broadcast connection broken exception:"
                                + e.getMessage() );
            }
            BDPacket pack = BDPacket.newPacket( RequestID.BROADCAST );
            pack.setDataType( DataType.GLOBAL_EXCEPTION );
            pack.setData( data );

            outChan.add( pack );
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
       // System.out.println(" packet sent from client: " + socket );
    }
}

class WritingDaemon extends Thread {
    private NIOSocket socket;
    private BlockingQueue<BDPacket> inChan;
    private boolean keepOn = true;

    WritingDaemon(NIOSocket socket, BlockingQueue<BDPacket> inChan ) {
        this.socket = socket;
        this.inChan = inChan;
        setDaemon( true );
    }

    @Override
    public void run() {
        BDPacket pack0 = null;
        try {
            while ( keepOn ) {
                pack0 = inChan.take();
                socket.write( pack0.toBytes() );
            }
        } catch (InterruptedException e) {
            ErrorLogger.log("InChan interrupted: " + e.getMessage());
        } catch (IOException e) {
            ErrorLogger.log("NIOSocket write fail: " + e.getMessage());
        }
    }

    public void terminal() {
        keepOn = false;
    }
}

class RollingDaemon extends Thread {

    private final NIOService nioService;
    private boolean keepOn = true;

    public RollingDaemon(NIOService nioService) {
        this.nioService = nioService;
        setDaemon( true );
    }

    @Override
    public void run() {
        while( keepOn ) {
            try {
                nioService.selectBlocking();
            } catch (IOException e) {
                ErrorLogger.log("from NioService selectBlocking: " + e.getMessage());
            }
        }
    }

    public void terminal() {
        keepOn = false;
    }

}

