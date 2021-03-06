package com.bdcom.devtest;

import com.bdcom.dce.itester.api.ITesterAPI;
import com.bdcom.dce.itester.api.JniAPIImpl;
import com.bdcom.dce.itester.api.wrapper.DeviceStatus;
import com.bdcom.dce.itester.api.wrapper.ITesterAPIWrapper;
import com.bdcom.dce.itester.api.wrapper.ITesterException;
import com.bdcom.dce.view.itester.tree.DeviceInfoTreeBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-8-19    <br/>
 * Time: 16:40  <br/>
 */
public class DeviceTreeTester extends JFrame {

    public static void main(String... s) {
        new DeviceTreeTester();
    }

    public DeviceTreeTester() {
//        PathConfig pathConfig = new PathConfig(
//                ApplicationConstants.RUN_TIME.CURRENT_DIR + File.separator + "RPC-configure" );

//        ServerConfig serverConfig = new ServerConfig( pathConfig );
//        serverConfig.setDefaultIP("172.16.22.222");
//        serverConfig.setDefaultPort( 7777 );
//        serverConfig.writeToConfigFile("172.16.22.222", "7777");
//
//        ITesterAPI it = new RpcClient( serverConfig );
        ITesterAPI it = JniAPIImpl.getInstance();
        ITesterAPIWrapper itw = new ITesterAPIWrapper( it );

        try {
            final DeviceStatus deviceStatus = itw.getDeviceStatus("172.16.22.202");
            Thread fetcher = new Thread() {
                @Override
                public void run() {
                    while ( true ) {
                        DeviceStatus.PortLocation[] portLocations = deviceStatus.getLinkedAndNotUsedPorts();
                        StringBuilder sb = new StringBuilder();
                        sb.append( "[");
                        for ( int i = 0; i < portLocations.length; i++ ) {
                            sb.append("(").append( portLocations[i].getCardId() )
                                    .append(", ").append( portLocations[i].getPortId() )
                                    .append( ") ");
                        }
                        sb.append( "]");
                        System.out.println( sb.toString());
                        try {
                            TimeUnit.SECONDS.sleep( 3 );
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            //fetcher.start();
            JTree tree = DeviceInfoTreeBuilder.buildTree(deviceStatus);

            Container content = getContentPane();
            content.add( tree, BorderLayout.CENTER );
            pack();
            this.setVisible( true );
        } catch (ITesterException e) {
            e.printStackTrace();
        } finally {

        }
    }

}
