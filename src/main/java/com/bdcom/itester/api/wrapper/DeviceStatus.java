package com.bdcom.itester.api.wrapper;

import com.bdcom.itester.api.ITesterAPI;
import com.bdcom.itester.lib.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Created with IntelliJ IDEA. <br/>
* User: francis    <br/>
* Date: 13-8-19    <br/>
* Time: 11:29  <br/>
*/
public class DeviceStatus {

    private Map<Integer, Boolean> localUseStatus;
    private ITesterAPI api;
    private int socketId;

    private ChassisInfo chassisInfo; // static info, once fetched, needn't update
    private List<CardInfo> cardInfoList; // static info, once fetched, needn't update

    private String chassisDescription;
    private String chassisDescPre;
    private List<String> cardDescList;

    DeviceStatus(ITesterAPI api, int socketId, String pre) {
        this.api = api;
        this.socketId = socketId;
        this.chassisDescPre = pre;
        localUseStatus = new HashMap<Integer, Boolean>();
        fetchDeviceStatus();
    }

    void fetchDeviceStatus() {
        chassisInfo = api.getChassisInfo( socketId );

        StringBuilder sb = new StringBuilder();
        sb.append( chassisDescPre )
           .append( ": ")
           .append( chassisInfo.getDescription() );
        chassisDescription = sb.toString();

        int num = chassisInfo.getCardNum();
        if ( null == cardInfoList ) {
            cardInfoList = new ArrayList<CardInfo>(20);
        }
        for ( int i = 0; i < num; i++ ) {
            CardInfo cardInfo = api.getCardInfo( socketId, i );
            cardInfoList.add( i, cardInfo );
            addCardDesc( i, cardInfo );
        }
    }

    private void addCardDesc(int cardId, CardInfo cardInfo ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "Card " )
          .append(cardId)
          .append( ", " )
          .append( cardInfo.getDescription() );
        if  ( null == cardDescList ) {
            cardDescList = new ArrayList<String>();
        }
        cardDescList.add(cardId, sb.toString() );
    }

    public int getCardCount() {
        return chassisInfo.getCardNum();
    }

    public int getPortCount(int cardId) {
        if ( cardId < 0 || getCardCount() <= cardId) {
            return 0;
        }
        CardInfo cardInfo = cardInfoList.get( cardId );
        return cardInfo.getPortNumber();
    }

    public String getChassisDescription() {
        return chassisDescription;
    }

    public String getCardDesc(int cardId) {
        if ( cardId >= chassisInfo.getCardNum()  || 0 > cardId  ) {
            return null;
        }
        return cardDescList.get( cardId );
    }

    public String getPortDesc(int cardId, int portId) {
        if ( cardId >= chassisInfo.getCardNum()  || 0 > cardId  ) {
            return null;
        }
        CardInfo cardInfo = cardInfoList.get( cardId );
        if ( portId >= cardInfo.getPortNumber() || 0 > portId ) {
            return null;
        }
        EthPhyProper epp = api.getEthernetPhysical( socketId, cardId, portId );
        int speedRank = epp.getSpeed();
        String speed = null;
        if ( 0 == speedRank ) {
            speed = "10M";
        } else if ( 1 == speedRank ) {
            speed = "100M";
        } else if ( 2 == speedRank ) {
            speed = "1000M";
        } else {
            speed = "unknown";
        }
        StringBuilder sb = new StringBuilder();
        sb.append( "Port(" )
          .append( cardId )
          .append( "," )
          .append( portId )
          .append( "): ")
          .append( speed )
          .append( " | " )
          .append( 1 == epp.getFullDuplex() ? "Full" : "Half" );

        return sb.toString();
    }

    public void setLocalUsed( int cardId, int portId, boolean used ) {
        localUseStatus.put( 10*cardId + portId, used );
    }

    public boolean isLocalUsed( int cardId, int portId ) {
        Boolean used = localUseStatus.get( 10*cardId + portId );
        if ( null == used ) {
            return false;
        }
        return used.booleanValue();
    }

    public boolean isLinked( int cardId, int portId ) {
        LinkStatus ls = api.getLinkStatus(socketId, cardId, portId);
        return ls.isLinked();
    }

    public boolean isUsed( int cardId, int portId ) {
        UsedState us = api.getUsedState( socketId, cardId, portId );
        return us.isUsed();
    }

    public PortLocation[] getLinkedAndNotUsedPorts() {
        List<PortLocation> portLocationList = new ArrayList<PortLocation>(20);
        int cardNum = chassisInfo.getCardNum();
        for ( int cardId = 0; cardId < cardNum; cardId++ ) {
            CardInfo cardInfo = cardInfoList.get( cardId );
            int portNum = cardInfo.getPortNumber();
            for ( int portId = 0; portId < portNum; portId++ ) {
                if ( isLinked( cardId, portId )
                        && !isUsed( cardId, portId ) ) {
                    portLocationList.add( new PortLocation( cardId, portId ) );
                }
            }
        }

        PortLocation[] portLocations = new PortLocation[portLocationList.size()];
        portLocations = portLocationList.toArray( portLocations );
        return portLocations;
    }

    public class PortLocation {

        private final int cardId;
        private final int portId;

        public PortLocation( int cardId, int portId ) {
            this.cardId = cardId;
            this.portId = portId;
        }

        public int getCardId() {
            return cardId;
        }

        public int getPortId() {
            return portId;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append( "(" )
              .append( cardId )
              .append( "," )
              .append( portId )
              .append( ")" );
            return sb.toString();
        }

        @Override
        public boolean equals(Object obj) {
            Class<?> clazz = obj.getClass();
            if ( clazz != PortLocation.class ) {
                return false;
            }
            PortLocation that = (PortLocation) obj;
            if ( this == obj ) {
                return true;
            }
            if ( this.cardId == that.portId &&
                    this.portId == that.portId ) {
                return true;
            }
            return false;
        }
    }

}
