package com.bdcom.devtest;

import com.bdcom.dce.nio.exception.GlobalException;
import com.bdcom.dce.sys.config.ServerConfig;
import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.DataType;
import com.bdcom.dce.nio.RequestID;
import com.bdcom.dce.nio.client.ClientProxy;
import com.bdcom.dce.sys.gui.Application;
import com.bdcom.dce.sys.config.PathConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-8    <br/>
 * Time: 09:58  <br/>
 */
public class ClientTester {
    public static void main(String[] s) {
        String proID = System.getProperty("nio.process");
        ServerConfig serverConfig = new ServerConfig( new PathConfig(Application.CURRENT_DIR) );

        ClientProxy proxy = new ClientProxy(serverConfig);
        try {
            for ( int i = 0; i < 100; i++ ) {
                sendingEcho( proxy, i, proID );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (GlobalException e) {
            e.printStackTrace();
        }

        proxy.shutdown();

        try {
            TimeUnit.SECONDS.sleep( 2 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void sendingEcho(ClientProxy proxy, int id, String proID) throws IOException, InterruptedException, GlobalException {
        StringBuilder sb = new StringBuilder();
        sb.append( "request num: " ).append( id );
        sb.append(" Process ID: ").append( proID );


        BDPacket pack = BDPacket.newPacket( RequestID.ECHO );
        pack.setDataType( DataType.STRING );
        pack.setData( sb.toString().getBytes() );

        BDPacket response = proxy.sendRawPacket( pack );
        System.out.println("response received: " + new String( response.getData() ));
    }

    public static void printPacket(BDPacket pack) {
        System.out.println( "version: "+ pack.getVersion() );
        System.out.println( "requestID: " + pack.getRequestID() );
        System.out.println( "DataType: " + pack.getDataType() );
        System.out.println( "Data: " + pack.getData() );
    }
}

