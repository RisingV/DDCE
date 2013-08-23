package com.bdcom.view.itester.tree;

import com.bdcom.itester.api.wrapper.DeviceStatus;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-8-19    <br/>
 * Time: 15:02  <br/>
 */
public class CardNode extends DefaultMutableTreeNode implements CustomizedNode {

    private final DeviceStatus deviceStatus;
    private final int cardId;
    private Icon cardIcon;

    public CardNode( DeviceStatus deviceStatus, int cardId, Icon cardIcon ) {
        this.deviceStatus = deviceStatus;
        this.cardId = cardId;
        this.cardIcon = cardIcon;
    }

    public int getCardId() {
        return cardId;
    }

    @Override
    public String getProperDescription() {
        return deviceStatus.getCardDesc( cardId );
    }

    @Override
    public Icon getCustomizedIcon() {
        return cardIcon;
    }
}
