package com.bdcom.view.itester.tree;

import com.bdcom.itester.api.wrapper.DeviceStatus;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-8-19    <br/>
 * Time: 10:45  <br/>
 */
public class PortNode extends DefaultMutableTreeNode implements CustomizedNode {

    private final DeviceStatus deviceStatus;
    private final int cardId;
    private final int portId;

    private Icon portUnlinkedIcon;
    private Icon portLinkedIcon;
    private Icon portInUseIcon;
    private Icon portInTestIcon;

    public PortNode(DeviceStatus deviceStatus, int cardId, int portId) {
        this.deviceStatus = deviceStatus;
        this.cardId = cardId;
        this.portId = portId;
    }

    public PortNode(DeviceStatus deviceStatus, int cardId, int portId,
                    Icon portUnlinkedIcon,
                    Icon portLinkedIcon,
                    Icon portInUseIcon,
                    Icon portInTestIcon) {
        this( deviceStatus, cardId, portId );
        this.portUnlinkedIcon = portUnlinkedIcon;
        this.portLinkedIcon = portLinkedIcon;
        this.portInUseIcon = portInUseIcon;
        this.portInTestIcon = portInTestIcon;
    }

    @Override
    public String getProperDescription() {
        return deviceStatus.getPortDesc( cardId, portId );
    }

    private static int counter = 0;

    @Override
    public Icon getCustomizedIcon() {
        boolean linked = deviceStatus.isLinked( cardId, portId );
        boolean used = deviceStatus.isUsed( cardId, portId );
        boolean locallyUsed = deviceStatus.isLocalUsed( cardId, portId );
        if ( !linked ) {
            return portUnlinkedIcon;
        } else {
            if ( !used ) {
                return portLinkedIcon;
            } else {
                if ( !locallyUsed ) {
                    return portInUseIcon;
                } else {
                    return portInTestIcon;
                }
            }
        }
    }

    public static class Builder {
        private DeviceStatus deviceStatus;
        private int cardId;
        private int portId;
        private Icon portUnlinkedIcon;
        private Icon portLinkedIcon;
        private Icon portInUseIcon;
        private Icon portInTestIcon;

        public Builder portInfo(DeviceStatus deviceStatus, int cardId, int portId) {
            this.deviceStatus = deviceStatus;
            this.cardId = cardId;
            this.portId = portId;

            return this;
        }

        public Builder portUnlinkedIcon(Icon portUnlinkedIcon) {
            this.portUnlinkedIcon = portUnlinkedIcon;

            return this;
        }

        public Builder portLinkedIcon(Icon portLinkedIcon) {
            this.portLinkedIcon = portLinkedIcon;

            return this;
        }

        public Builder portInUseIcon(Icon portInUseIcon) {
            this.portInUseIcon = portInUseIcon;

            return this;
        }

        public Builder portInTestIcon(Icon portInTestIcon) {
            this.portInTestIcon = portInTestIcon;

            return this;
        }

        public PortNode build() {
            return new PortNode( deviceStatus, cardId, portId,
                        portUnlinkedIcon,
                        portLinkedIcon,
                        portInUseIcon,
                        portInTestIcon
                    );
        }

    }

}
