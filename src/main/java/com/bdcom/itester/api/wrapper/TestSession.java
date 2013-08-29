package com.bdcom.itester.api.wrapper;

import com.bdcom.itester.api.ITesterAPI;
import com.bdcom.itester.lib.PortStats;
import com.bdcom.itester.lib.StreamInfo;

/**
* Created with IntelliJ IDEA. <br/>
* User: francis    <br/>
* Date: 13-8-19    <br/>
* Time: 11:29  <br/>
*/
public class TestSession {
    private ITesterAPI api;
    private int pktNum;
    private int socketId;
    private int[] idList; //{ srcCardId, srcPortId, dstCardId, dstPortId }
    private int[] streamIDs;
    private boolean finished = false;
    private Boolean passed = null;
    private PortStats ps;

    TestSession(ITesterAPI api, int pktNum, int socketId,
                int[] idList, int[] streamIDs) {
        this.api = api;
        this.pktNum = pktNum;
        this.socketId = socketId;
        this.idList = idList;
        this.streamIDs = streamIDs;
    }

    public PortStats getPortStats() {
        return ps;
    }

    public boolean isTestDone() throws ITesterException {
        if ( !finished ) {
            return 100 == getProgressPercent();
        }
        return finished;
    }

    public int getProgressPercent() throws ITesterException {
        int percent = 100;
        if ( !finished ) {
            int pktSentNum = 0;
            for ( int i=0; i < streamIDs.length; i++ ) {
                int sid = streamIDs[i];
                StreamInfo si = api.getStreamSendInfo( socketId, idList[0], idList[1], sid);
                if ( !si.isConnected() ) {
                    throw new ITesterException( ITesterException.CONNECT_FAIL );
                }
                pktSentNum += si.getPacketCount();
            }
            //System.out.println( pktSentNum +"*100/"+pktNum );
            percent = pktSentNum*100 / pktNum;
            finished = pktSentNum >= pktNum;
        }
        return percent;
    }

    public boolean isTestPass() {
        if ( !finished ) {
            return false;
        }
        getTestResultAndStop();
        return passed.booleanValue();
    }

    public void forceClose() {
        if ( !finished ) {
            getTestResultAndStop();
            finished = true;
        }
    }

    private void getTestResultAndStop() {
        if ( null == passed ) {
            ps = api.getPortAllStats( socketId, idList[0], idList[1], 8 );
            long[] stats = ps.getStats();
            if ( stats[0] == stats[3] ) {
                passed = Boolean.TRUE;
            } else {
                passed = Boolean.FALSE;
            }
            api.stopPort( socketId, idList[0], idList[1] );
            api.stopPort( socketId, idList[2], idList[3] );

            api.setUsedState( socketId, idList[0], idList[1], ITesterAPIWrapper.NOT_USE );
            api.setUsedState( socketId, idList[2], idList[3], ITesterAPIWrapper.NOT_USE );
        }
    }

}
