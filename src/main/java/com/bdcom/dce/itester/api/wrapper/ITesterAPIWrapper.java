package com.bdcom.dce.itester.api.wrapper;

import com.bdcom.dce.itester.api.ITesterAPI;
import com.bdcom.dce.itester.lib.*;
import com.bdcom.dce.util.StringUtil;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-8-14    <br/>
 * Time: 15:58  <br/>
 */
public class ITesterAPIWrapper {

    private static final int BURST_MODE = 1;
    private static final int PAY_LOAD_CONTENT = 0xA5;
    private static final int PAY_LOAD_TYPE = 0;
    private static final int PKT_NUM_PER_SEC_BASE = 155;
    private static final int DEFAULT_DELAY_COUNT = 12;
    private static final int START_STREAM_ID = 0;
    private static final int STEP_RANGE = 1;

    static final int IN_USE = 1;
    static final int NOT_USE = 0;

    private ITesterAPI api;
    private String IP = null;
    private CommuStatus commuStatus;

    private int[] streams = { 68, 1518 };

    public ITesterAPIWrapper(ITesterAPI api) {
        this.api = api;
    }

    public ITesterAPI getApi() {
        return api;
    }

    public void setApi(ITesterAPI api) {
        this.api = api;
    }

    public void resetStreams(int[] streams) {
        this.streams = streams;
    }

    public DeviceStatus getDeviceStatus(String ip) throws ITesterException {
        if ( !StringUtil.isVaildIp(ip) ) {
            throw new ITesterException( ITesterException.INVALID_IP, ip );
        }
        if ( null == commuStatus || !commuStatus.isConnected() || !ip.equals( IP ) ) {
            commuStatus = api.connectToServer(ip);
            IP = ip;
        }
        if ( !commuStatus.isConnected() ) {
            throw new ITesterException( ITesterException.CONNECT_FAIL, ip );
        }
        return new DeviceStatus(api, commuStatus.getSocketId(), IP );
    }

    public TestSession startTest( String ip, int portIndex0, int portIndex1, int seconds )
            throws ITesterException {
        int cardId0 = portIndex0 / 4;
        int portId0 = portIndex0 % 4;
        int cardId1 = portIndex1 / 4;
        int portId1 = portIndex1 % 4;

        return startTest( ip, cardId0, portId0, cardId1, portId1, seconds );
    }

    public TestSession startTest( String ip, int cd0, int pd0, int cd1, int pd1, int seconds )
            throws ITesterException {
        if ( !StringUtil.isVaildIp(ip) ) {
            throw new ITesterException( ITesterException.INVALID_IP, ip );
        }
        if ( null == commuStatus || !commuStatus.isConnected() || !ip.equals( IP ) ) {
            commuStatus = api.connectToServer(ip);
            IP = ip;
        }
        if ( !commuStatus.isConnected() ) {
            throw new ITesterException( ITesterException.CONNECT_FAIL, ip );
        }
        int socketId = commuStatus.getSocketId();

        LinkStatus ls0 = api.getLinkStatus( socketId, cd0, pd0 );
        if ( !ls0.isLinked() ) {
            throw new ITesterException( ITesterException.PORT_NOT_LINKED, cd0, pd0 );
        }
        LinkStatus ls1 = api.getLinkStatus( socketId, cd1, pd1 );
        if ( !ls1.isLinked() ) {
            throw new ITesterException( ITesterException.PORT_NOT_LINKED, cd1, pd1 );
        }
        UsedState us0 = api.getUsedState( socketId, cd0, pd0 );
        if ( us0.isUsed() ) {
            throw new ITesterException( ITesterException.PORT_IN_USE, cd0, pd0 );
        }
        UsedState us1 = api.getUsedState( socketId, cd1, pd1 );
        if ( us1.isUsed() ) {
            throw new ITesterException( ITesterException.PORT_IN_USE, cd1, pd1 );
        }

        int pktNum = seconds * PKT_NUM_PER_SEC_BASE * getBandwidth( socketId, cd0, pd0 );

        int[] head0to1 = EthFrameUtil.getHeader(cd0, pd0, cd1, pd1);
        int[] head1to0 = EthFrameUtil.getHeader(cd1, pd1, cd0, pd0);

        int streamCount = streams.length;
        int l0 = head0to1.length;
        int l1 = head1to0.length;
        int totalLen = (l0 + l1) * streamCount / 2;
        int[] totalHead = new int[totalLen];

        int pos = 0;
        int subLen = 0;
        int[] subHead = null;
        for ( int i=0; i < streamCount; i++ ) {
            if ( 0 == i % 2 ) {
                subHead = head0to1;
                subLen = l0;
            } else {
                subHead = head1to0;
                subLen = l1;
            }
            System.arraycopy( subHead, 0, totalHead, pos, subLen );
            pos += subLen;
        }

        api.clearStatReliably( socketId, cd0, pd0 );
        api.clearStatReliably( socketId, cd1, pd1 );

        api.setHeader( socketId, cd0, pd0, streamCount, totalLen, totalHead );
        api.setHeader( socketId, cd1, pd1, streamCount, totalLen, totalHead );

        api.setPayload( socketId, cd0, pd0, 0, PAY_LOAD_CONTENT, PAY_LOAD_TYPE);
        api.setPayload( socketId, cd1, pd1, 0, PAY_LOAD_CONTENT, PAY_LOAD_TYPE);

        api.setDelayCount( socketId, cd0, pd0, DEFAULT_DELAY_COUNT );
        api.setDelayCount( socketId, cd1, pd1, DEFAULT_DELAY_COUNT );

        api.setTxMode( socketId, cd0, pd0, BURST_MODE, pktNum);
        api.setTxMode( socketId, cd1, pd1, BURST_MODE, pktNum);

        api.setStreamId( socketId, cd0, pd0, START_STREAM_ID, STEP_RANGE );
        api.setStreamId( socketId, cd1, pd1, START_STREAM_ID, STEP_RANGE );

        api.setFramLengthChange( socketId, cd0, pd0, 0);
        api.setFramLengthChange( socketId, cd1, pd1, 0);

        int streamId = START_STREAM_ID;
        int[] streamIDs = new int[streamCount];
        for ( int j = 0; j < streamCount; j++ ) {
            api.setStreamLength( socketId, cd0, pd0, streamId, streams[j]-l0 );
            api.setStreamLength( socketId, cd1, pd1, streamId, streams[j]-l1 );
            streamIDs[j] = streamId;
            streamId += STEP_RANGE;
        }

        api.setUsedState( socketId, cd0, pd0, IN_USE );
        api.setUsedState( socketId, cd1, pd1, IN_USE );

        api.startPort( socketId, cd0, pd0 );
        api.startPort( socketId, cd1, pd1 );

        return new TestSession( api, pktNum, socketId,
                new int[] { cd0, pd0, cd1, pd1 }, streamIDs );
    }

    public void closeCurrConnection() {
        if ( null != commuStatus && commuStatus.isConnected() ) {
            int socketId = commuStatus.getSocketId();
            api.disconnectToServer( socketId );
            commuStatus = null;
        }
    }

    private int getBandwidth(int socketId, int cardId, int portId) {
        EthPhyProper epp = api.getEthernetPhysical( socketId, cardId, portId );
        int bandwidth = 1;
        switch ( epp.getSpeed() ) {
            case 0: {
                bandwidth = 10;
                break;
            }
            case 1: {
                bandwidth = 100;
                break;
            }
            case 2: {
                bandwidth = 1000;
                break;
            }
        }
        return bandwidth;
    }

}
