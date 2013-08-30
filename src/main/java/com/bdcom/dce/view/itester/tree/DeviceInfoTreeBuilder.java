package com.bdcom.dce.view.itester.tree;

import com.bdcom.dce.itester.api.wrapper.DeviceStatus;
import com.bdcom.dce.view.util.ViewUtil;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-8-19    <br/>
 * Time: 15:25  <br/>
 */

public abstract class DeviceInfoTreeBuilder {

    private static Icon serverIcon;
    private static Icon cardIcon;
    private static Icon portUnlinkedIcon;
    private static Icon portLinkedIcon;
    private static Icon portInUseIcon;
    private static Icon portInTestIcon;
    private static boolean iconLoaded = false;

    private static void loadIcons() {
        serverIcon = new ImageIcon( ViewUtil.getImage("/images/dev-tree/server.png") );
        cardIcon = new ImageIcon( ViewUtil.getImage("/images/dev-tree/card.png") );
        portUnlinkedIcon = new ImageIcon( ViewUtil.getImage("/images/dev-tree/port-unlinked.png") );
        portLinkedIcon = new ImageIcon( ViewUtil.getImage("/images/dev-tree/port-linked.png") );
        portInUseIcon = new ImageIcon( ViewUtil.getImage("/images/dev-tree/port-in-use.png") );
        portInTestIcon = new ImageIcon( ViewUtil.getImage("/images/dev-tree/port-in-test.png") );
        iconLoaded = true;
    }

    public static JTree buildTree(DeviceStatus deviceStatus) {
        return buildTree( deviceStatus, 1.8f );
    }

    public static JTree buildTree(DeviceStatus deviceStatus, float updateFrequency) {
        if ( !iconLoaded ) {
            loadIcons();
        }
        ServerNode serverNode = new ServerNode( deviceStatus, serverIcon );
                int cardNum = deviceStatus.getCardCount();
                for ( int cardId = 0; cardId < cardNum; cardId++ ) {

                    CardNode cardNode = new CardNode( deviceStatus, cardId, cardIcon );
                    int portNum = deviceStatus.getPortCount( cardId );
                    for ( int portId = 0; portId < portNum; portId++ ) {

                PortNode portNode = new PortNode.Builder()
                        .portInfo( deviceStatus , cardId, portId )
                        .portUnlinkedIcon( portUnlinkedIcon )
                        .portLinkedIcon( portLinkedIcon )
                        .portInUseIcon( portInUseIcon )
                        .portInTestIcon( portInTestIcon )
                        .build();
                cardNode.add( portNode );
            }
            serverNode.add( cardNode );
        }

        JTree deviceInfoTree = new JTree( serverNode );
        deviceInfoTree.setCellRenderer( new DeviceTreeCellRenderer() );
        new UpdateTimer( deviceInfoTree, updateFrequency ).start();

        for (int i = deviceInfoTree.getRowCount() - 1; i >= 0; i--) {
            deviceInfoTree.expandRow( i );
        }
        return deviceInfoTree;
    }

    private static class UpdateTimer extends Thread {
        private static final long ONE_SEC = 1000;

        private final JTree tree;
        private final long interval;

        UpdateTimer(JTree tree, float updateFrequency) {
            this.tree = tree;
            this.interval = (long) ((float)ONE_SEC * updateFrequency);
        }
        @Override
        public void run() {
            while( true ) {
                try {
                    TimeUnit.MILLISECONDS.sleep( interval );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tree.update( tree.getGraphics() );
            }
        }
    }

}
