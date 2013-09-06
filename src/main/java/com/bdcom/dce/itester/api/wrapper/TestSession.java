package com.bdcom.dce.itester.api.wrapper;

import com.bdcom.dce.itester.api.ITesterAPI;
import com.bdcom.dce.itester.lib.PortStats;
import com.bdcom.dce.itester.lib.StreamInfo;

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
    private String ip;
    private boolean finished = false;
    private Boolean passed = null;
    private PortStats ps0;
    private PortStats ps1;

    TestSession(ITesterAPI api, int pktNum, int socketId,
                int[] idList, int[] streamIDs, String ip) {
        this.api = api;
        this.pktNum = pktNum;
        this.socketId = socketId;
        this.idList = idList;
        this.streamIDs = streamIDs;
        this.ip = ip;
    }

    public PortStats getSrcPortStats() {
        return ps0;
    }

    public PortStats getDstPortStats() {
        return ps1;
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
                    throw new ITesterException( ITesterException.CONNECT_FAIL, ip );
                }
                pktSentNum += si.getPacketCount();
            }
            //System.out.println( pktSentNum +"*100/"+pktNum );
            percent = pktSentNum*100 / pktNum;
            finished = pktSentNum >= pktNum;
        }
        return percent;
    }

    public boolean isTestPass() throws ITesterException {
        return isTestPass( 100 );
    }

    public boolean isTestPass(int percent) throws ITesterException {
        if ( !finished ) {
            return false;
        }
        getTestResultAndStop( percent );
        return passed.booleanValue();
    }

    public void forceClose() throws ITesterException {
        forceClose( 100 );
    }

    public void forceClose( int percent ) throws ITesterException {
        if ( !finished ) {
            getTestResultAndStop( percent );
            finished = true;
        }
    }

    private void getTestResultAndStop(int percent) throws ITesterException {
        if ( percent < 1 || 100 < percent ) {
            percent = 100;
        }
        if ( null == passed ) {
            ps0 = api.getPortAllStats( socketId, idList[0], idList[1], 8 );
            connectionCheck( ps0 );
            ps1 = api.getPortAllStats( socketId, idList[2], idList[3], 8 );
            connectionCheck( ps1 );

            long[] stats0 = ps0.getStats();
            long[] stats1 = ps1.getStats();
            if ( ( stats0[0]*percent/100 ) <= stats0[3] &&
                    ( stats1[0]*percent/100 ) <= stats1[3] ) {
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

    private void connectionCheck(PortStats ps) throws ITesterException {
        if ( !ps.isConnected() ) {
            throw  new ITesterException( ITesterException.CONNECT_FAIL, ip );
        }
    }

}
