package com.bdcom.dce.view.itester.tree;

import com.bdcom.dce.itester.api.wrapper.DeviceStatus;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-8-19    <br/>
 * Time: 15:09  <br/>
 */
public class ServerNode extends DefaultMutableTreeNode implements CustomizedNode {

    private final DeviceStatus deviceStatus;
    private Icon serverIcon;

    public ServerNode( DeviceStatus deviceStatus, Icon serverIcon ) {
        this.deviceStatus = deviceStatus;
        this.serverIcon = serverIcon;
    }

    @Override
    public String getProperDescription() {
        return deviceStatus.getChassisDescription();
    }

    @Override
    public Icon getCustomizedIcon() {
        return serverIcon;
    }
}
