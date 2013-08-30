package com.bdcom.nio.server;

import com.bdcom.nio.RequestID;
import com.bdcom.nio.naga.*;
import com.bdcom.nio.BDPacket;
import com.bdcom.nio.BDPacketUtil;
import com.bdcom.nio.naga.packetreader.RegularPacketReader;
import com.bdcom.nio.naga.packetwriter.RegularPacketWriter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: francis
 * Date: 13-7-4
 * Time: 10:29
 */
public class NIOServer {
    private final int port;

    private NIOService nioService;
    private NIOServerSocket nioServerSocket;
    private Map<Integer, IHandler> handlerMap;

    public NIOServer(int port) {
        this.port = port;
    }

    public void setHandlerMap(Map<Integer, IHandler> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public void addHandler(Integer id, IHandler handler) {
        if ( null == handlerMap ) {
            handlerMap = new HashMap<Integer, IHandler>();
        }

        handlerMap.put(id, handler);
    }

    private void init() throws IOException {
        nioService = new NIOService();
        nioServerSocket = nioService.openServerSocket(port);
        nioServerSocket.listen( new ServerSocketObserverAdapter() {
            @Override
            public void newConnection(NIOSocket nioSocket) {
//                System.out.println("Received connection: " + nioSocket);

                nioSocket.setPacketReader(new RegularPacketReader(4, true));
                nioSocket.setPacketWriter(new RegularPacketWriter(4, true));

                nioSocket.listen( new ConnectionHandler(handlerMap) );
            }
        });

        nioServerSocket.setConnectionAcceptor( ConnectionAcceptor.ALLOW );
    }

    public void start() throws IOException {
        init();
        while( true ) {
            nioService.selectBlocking();
        }
    }


}

class ConnectionHandler extends SocketObserverAdapter {

    Map<Integer, IHandler> handlerMap;

    ConnectionHandler(Map<Integer, IHandler> handlerMap) {
        this.handlerMap = handlerMap;
    }

    private BDPacket handle(BDPacket req) {
        IHandler handler = handlerMap.get( req.getRequestID() );
        if ( null != handler ) {
            return handler.handle( req );
        } else {
            return BDPacketUtil.responseToUnknown( req.getRequestID() );
        }
    }

    @Override
    public void packetReceived(NIOSocket socket, byte[] packet) {

        try {
            BDPacket request = BDPacket.parse( packet );
            BDPacket response = handle( request );

            socket.write( response.toBytes() );
            if ( RequestID.TERMINAL == response.getRequestID() ) {
                socket.closeAfterWrite();
            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }

    @Override
    public void connectionBroken(NIOSocket nioSocket, Exception e) {
        StringBuilder sb = new StringBuilder();
        if ( null == e ) {
            sb.append( "Client: " )
              .append( nioSocket )
              .append(" exit normally!" );
            System.out.println( sb.toString() );
        } else if ( e instanceof EOFException) {
            sb.append( "Client: ")
              .append( nioSocket )
              .append("reach EOF!");
            System.out.println(sb.toString());
        } else {
            sb.append( "Connection [ " )
                    .append(nioSocket)
                    .append( " ] has broken! due to: ")
                    .append( e.getMessage() );
            System.err.println( sb.toString() );
        }
    }
}

