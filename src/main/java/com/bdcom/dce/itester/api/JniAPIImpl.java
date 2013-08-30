package com.bdcom.dce.itester.api;

import com.bdcom.dce.itester.lib.*;
import com.bdcom.dce.sys.ApplicationConstants;
import com.bdcom.dce.sys.configure.PathConfig;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-8-20    <br/>
 * Time: 10:56  <br/>
 */
public class JniAPIImpl implements ITesterAPI {

    private static JniAPIImpl instance;

    public static JniAPIImpl getInstance() {
        if ( null == instance ) {
            synchronized ( JniAPIImpl.class ) {
                if ( null == instance ) {
                    instance = new JniAPIImpl();
                }
            }
        }

        return instance;
    }

    private ITesterLibLoader jni;

    private JniAPIImpl() {
        String currDir = ApplicationConstants.RUN_TIME.CURRENT_DIR;
        PathConfig pathConfig = new PathConfig(currDir);
        ITesterLibLoader.registerNatives(pathConfig);
        jni = new ITesterLibLoader();
    }

    @Override
    public CommuStatus connectToServer(String ipAddr) {
        return jni.connectToServer( ipAddr );
    }

    @Override
    public int disconnectToServer(int socketId) {
        return jni.disconnectToServer( socketId );
    }

    @Override
    public ChassisInfo getChassisInfo(int socketId) {
        return jni.getChassisInfo( socketId );
    }

    @Override
    public CardInfo getCardInfo(int socketId, int cardId) {
        return jni.getCardInfo( socketId, cardId );
    }

    @Override
    public EthPhyProper getEthernetPhysical(int socketId, int cardId, int portId) {
        return jni.getEthernetPhysical( socketId, cardId, portId );
    }

    @Override
    public int clearStatReliably(int socketId, int cardId, int portId) {
        return jni.clearStatReliably( socketId, cardId, portId );
    }

    @Override
    public int setHeader(int socketId, int cardId, int portId, int validStreamCount, int length, int[] strHead) {
        return jni.setHeader( socketId, cardId, portId, validStreamCount, length, strHead );
    }

    @Override
    public int setPayload(int socketId, int cardId, int portId, int length, int data, int payloadType) {
        return jni.setPayload( socketId, cardId, portId, length, data, payloadType );
    }

    @Override
    public int setDelayCount(int socketId, int cardId, int portId, int delayCount) {
        return jni.setDelayCount( socketId, cardId, portId, delayCount );
    }

    @Override
    public int setTxMode(int socketId, int cardId, int portId, int mode, int burstNum) {
        return jni.setTxMode( socketId, cardId, portId, mode, burstNum );
    }

    @Override
    public int startPort(int socketId, int cardId, int portId) {
        return jni.startPort( socketId, cardId, portId );
    }

    @Override
    public int stopPort(int socketId, int cardId, int portId) {
        return jni.stopPort( socketId, cardId, portId );
    }

    @Override
    public PortStats getPortAllStats(int socketId, int cardId, int portId, int length) {
        return jni.getPortAllStats( socketId, cardId, portId, length );
    }

    @Override
    public LinkStatus getLinkStatus(int socketId, int cardId, int portId) {
        return jni.getLinkStatus( socketId, cardId, portId );
    }

    @Override
    public WorkInfo getWorkInfo(int socketId, int cardId, int portId) {
        return jni.getWorkInfo( socketId, cardId, portId );
    }

    @Override
    public int setUsedState(int socketId, int cardId, int portId, int usedState) {
        return jni.setUsedState( socketId, cardId, portId, usedState );
    }

    @Override
    public UsedState getUsedState(int socketId, int cardId, int portId) {
        return jni.getUsedState( socketId, cardId, portId );
    }

    @Override
    public int setStreamId(int socketId, int cardId, int portId, int iStartId, int iIdNum) {
        return jni.setStreamId( socketId, cardId, portId, iStartId, iIdNum );
    }

    @Override
    public int setEthernetPhysicalForATT(int socketId, int cardId, int portId,
                                 int nego, int ethPhySpeed, int fullDuplex, int loopback) {
        return jni.setEthernetPhysicalForATT( socketId, cardId, portId,
                nego, ethPhySpeed, fullDuplex, loopback);
    }

    @Override
    public int setFramLengthChange(int socketId, int cardId, int portId, int isChange) {
        return jni.setFramLengthChange( socketId, cardId, portId, isChange );
    }

    @Override
    public int loadFPGA(int socketId, int cardId, int ethPhySpeed) {
        return jni.loadFPGA( socketId, cardId, ethPhySpeed );
    }

    @Override
    public int resetFPGA(int socketId, int cardId) {
        return jni.resetFPGA( socketId, cardId );
    }

    @Override
    public StreamInfo getStreamSendInfo(int socketId, int cardId, int portId, int streamId) {
        return jni.getStreamSendInfo( socketId, cardId, portId, streamId );
    }

    @Override
    public StreamInfo getStreamRecInfo(int socketId, int cardId, int portId, int streamId) {
        return jni.getStreamRecInfo( socketId, cardId, portId, streamId );
    }

    @Override
    public int startCapture(int socketId, int cardId, int portId) {
        return jni.startCapture( socketId, cardId, portId );
    }

    @Override
    public CaptureResult stopCapture(int socketId, int cardId, int portId) {
        return jni.stopCapture( socketId, cardId, portId );
    }

    @Override
    public int setStreamLength(int socketId, int cardId, int portId, int streamId, int length) {
        return jni.setStreamLength( socketId, cardId, portId, streamId, length );
    }

}
