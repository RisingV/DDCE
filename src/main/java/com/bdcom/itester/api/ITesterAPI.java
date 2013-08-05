package com.bdcom.itester.api;

import com.bdcom.itester.lib.*;

/**
 * Created with IntelliJ IDEA.
 * User: francis
 * Date: 13-7-29
 * Time: 2:23
 */
public interface ITesterAPI {

    public CommuStatus connectToServer(String ipAddr);

    public int disconnectToServer(int socketId);

    public ChassisInfo getChassisInfo(int socketId);

    public CardInfo getCardInfo(int socketId, int cardId);

    public EthPhyProper getEthernetPhysical(int socketId, int cardId, int portId);

    public int clearStatReliably(int socketId, int cardId, int portId);

    public int setHeader(int socketId, int cardId, int portId, int validStreamCount, int length, byte[] strHead);

    public int setPayload(int socketId, int cardId, int portId, int length, byte[] data, int payloadType);

    public int setDelayCount(int socketId, int cardId, int portId, int delayCount);

    public int setTxMode(int socketId, int cardId, int portId, int mode, int burstNum);

    public int startPort(int socketId, int cardId, int portId);

    public int stopPort(int socketId, int cardId, int portId);

    public PortStats getPortAllStats(int socketId, int cardId, int portId, int length);

    public LinkStatus getLinkStatus(int socketId, int cardId, int portId);

    public WorkInfo getWorkInfo(int socketId, int cardId, int portId);

    public int setUsedState(int socketId, int cardId, int portId, int usedState);

    public UsedState getUsedState(int socketId, int cardId, int portId);

    public int setStreamId(int socketId, int cardId, int portId, int iStartId, int iIdNum);

    public int setEthernetPhysicalForATT(int socketId, int cardId, int portId,
                           int nego, int ethPhySpeed, int fullDuplex, int loopback);

    public int loadFPGA(int socketId, int cardId, int ethPhySpeed);

    public int resetFPGA(int socketId, int cardId);

    public StreamInfo getStreamSendInfo(int socketId, int cardId, int portId, int streamId);

    public StreamInfo getStreamRecInfo(int socketId, int cardId, int portId, int streamId);

    public int startCapture(int socketId, int cardId, int portId);

    public CaptureResult stopCapture(int socketId, int cardId, int portId);

    public int setStreamLength(int socketId, int cardId, int portId, int streamId, int length);

}
