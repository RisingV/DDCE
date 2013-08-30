package com.bdcom.dce.itester.rpc;

import com.bdcom.dce.itester.lib.*;
import com.bdcom.dce.itester.rpc.req.*;
import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.BDPacketUtil;
import com.bdcom.dce.sys.ApplicationConstants;
import com.bdcom.dce.sys.config.PathConfig;
import com.bdcom.dce.util.SerializeUtil;
import com.bdcom.dce.util.log.ErrorLogger;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: francis
 * Date: 13-7-29
 * Time: 17:14
 */
public class LocalCaller implements ApplicationConstants {

    private static final int RPC_RESPONSE = Byte.MAX_VALUE / 2;

    private static LocalCaller instance;

    public static LocalCaller getInstance() {
        if ( null == instance ) {
            synchronized ( LocalCaller.class ) {
                if ( null == instance ) {
                    instance = new LocalCaller();
                }
            }
        }

        return instance;
    }

    private ITesterLibLoader iTesterLibLoader;

    private LocalCaller() {
        String currDir = RUN_TIME.CURRENT_DIR;
        PathConfig pathConfig = new PathConfig(currDir);
        ITesterLibLoader.registerNatives(pathConfig);
        iTesterLibLoader = new ITesterLibLoader();
    }

    public BDPacket connectToServer(BDPacket packet) {
        String addr = new String( packet.getData() );
        CommuStatus commuStatus = iTesterLibLoader.connectToServer(addr);

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray(commuStatus);
        } catch (IOException e) {
            logExeception(e);
        }

        BDPacket response = BDPacket.newPacket(packet.getRequestID());
        response.setDataType( RPC_RESPONSE );
        response.setData( data );

        return response;
    }

    public BDPacket disconnectToServer(BDPacket packet) {
        int socketId = BDPacketUtil.byteArrayToInt( packet.getData() );
        int status = iTesterLibLoader.disconnectToServer( socketId );

        byte[] data = BDPacketUtil.intToByteArray( status );
        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData( data );

        return response;
    }

    public BDPacket getChassisInfo(BDPacket packet) {
        int socketId = -1;
        socketId = BDPacketUtil.byteArrayToInt( packet.getData() );
        ChassisInfo chassisInfo = iTesterLibLoader.getChassisInfo( socketId );
        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray(chassisInfo);
        } catch (IOException e) {
            logExeception(e);
        }

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType(RPC_RESPONSE);
        response.setData(data);

        return response;
    }

    public BDPacket getCardInfo(BDPacket packet) {
        GetCardInfoReq getCardInfoReq = null;

        try {
            getCardInfoReq = (GetCardInfoReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        CardInfo cardInfo = iTesterLibLoader.getCardInfo(
                getCardInfoReq.getSocketId(),
                getCardInfoReq.getCardId()
        );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( cardInfo );
        } catch (IOException e) {
            logExeception(e);
        }

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket getEthernetPhysical(BDPacket packet) {
        GetEthPhyReq getEthPhyReq = null;
        try {
            getEthPhyReq = (GetEthPhyReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        EthPhyProper ethPhyProper = iTesterLibLoader.getEthernetPhysical(
                getEthPhyReq.getSocketId(),
                getEthPhyReq.getCardId(),
                getEthPhyReq.getPortId()
        );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray(ethPhyProper);
        } catch (IOException e) {
            logExeception(e);
        }

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket clearEthernetPhysical(BDPacket packet){
        ClearStatReliablyReq clearStatReliablyReq = null;
        try {
            clearStatReliablyReq = (ClearStatReliablyReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        int status = iTesterLibLoader.clearStatReliably(
                clearStatReliablyReq.getSocketId(),
                clearStatReliablyReq.getCardId(),
                clearStatReliablyReq.getPortId()
        );

        byte[] data = null;
        data = BDPacketUtil.intToByteArray( status );

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket setHeader(BDPacket packet) {
        SetHeaderReq setHeaderReq = null;
        try {
            setHeaderReq = (SetHeaderReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        int status = iTesterLibLoader.setHeader(
                setHeaderReq.getSocketId(),
                setHeaderReq.getCardId(),
                setHeaderReq.getPortId(),
                setHeaderReq.getValidStreamCount(),
                setHeaderReq.getLength(),
                setHeaderReq.getStrHead()
        );

        byte[] data = BDPacketUtil.intToByteArray( status );

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket setPaylaod(BDPacket packet) {
        SetPayloadReq setPayloadReq = null;
        try {
            setPayloadReq = (SetPayloadReq) SerializeUtil
                    .deserializeFromByteArray( packet.getData() );
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        int status = iTesterLibLoader.setPayload(
                setPayloadReq.getSocketId(),
                setPayloadReq.getCardId(),
                setPayloadReq.getPortId(),
                setPayloadReq.getLength(),
                setPayloadReq.getData(),
                setPayloadReq.getPayloadType()
        );

        byte[] data = BDPacketUtil.intToByteArray(status);

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket setDelayCount(BDPacket packet) {
        SetDelayCountReq setDelayCountReq = null;
        try {
            setDelayCountReq = (SetDelayCountReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        int status = iTesterLibLoader.setDelayCount(
                setDelayCountReq.getSocketId(),
                setDelayCountReq.getCardId(),
                setDelayCountReq.getPortId(),
                setDelayCountReq.getDelayCount()
        );

        byte[] data = BDPacketUtil.intToByteArray( status );

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket setTxMode(BDPacket packet) {
        SetTxModeReq setTxModeReq = null;
        try {
            setTxModeReq = (SetTxModeReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        int status = iTesterLibLoader.setTxMode(
                setTxModeReq.getSocketId(),
                setTxModeReq.getCardId(),
                setTxModeReq.getPortId(),
                setTxModeReq.getMode(),
                setTxModeReq.getBurstNum()
        );

        byte[] data = BDPacketUtil.intToByteArray( status );

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket startPort(BDPacket packet) {
        StartPortReq startPortReq = null;
        try {
            startPortReq = (StartPortReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        int status = iTesterLibLoader.startPort(
                startPortReq.getSocketId(),
                startPortReq.getCardId(),
                startPortReq.getPortId()
        );

        byte[] data = BDPacketUtil.intToByteArray( status );

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket stopPort(BDPacket packet) {
        StopPortReq stopPortReq = null;
        try {
            stopPortReq = (StopPortReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        int status = iTesterLibLoader.stopPort(
                stopPortReq.getSocketId(),
                stopPortReq.getCardId(),
                stopPortReq.getPortId()
        );

        byte[] data = BDPacketUtil.intToByteArray( status );

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket getPortAllStats(BDPacket packet) {
        GetPortAllStatsReq getPortAllStatsReq = null;
        try {
            getPortAllStatsReq = (GetPortAllStatsReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        PortStats portStats = iTesterLibLoader.getPortAllStats(
                getPortAllStatsReq.getSocketId(),
                getPortAllStatsReq.getCardId(),
                getPortAllStatsReq.getPortId(),
                getPortAllStatsReq.getLength()
        );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( portStats );
        } catch (IOException e) {
            logExeception(e);
        }

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket getLinkStatus(BDPacket packet) {
        GetLinkStatusReq getLinkStatusReq = null;
        try {
            getLinkStatusReq = (GetLinkStatusReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        LinkStatus linkStatus = iTesterLibLoader.getLinkStatus(
                getLinkStatusReq.getSocketId(),
                getLinkStatusReq.getCardId(),
                getLinkStatusReq.getPortId()
        );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( linkStatus );
        } catch (IOException e) {
            logExeception(e);
        }

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket getWorkInfo(BDPacket packet) {
        GetWorkInfoReq getWorkInfoReq = null;
        try {
            getWorkInfoReq = (GetWorkInfoReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        WorkInfo workInfo = iTesterLibLoader.getWorkInfo(
                getWorkInfoReq.getSocketId(),
                getWorkInfoReq.getCardId(),
                getWorkInfoReq.getPortId()
        );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( workInfo );
        } catch (IOException e) {
            logExeception(e);
        }

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket setUsedState(BDPacket packet) {
        SetUsedStateReq setUsedStateReq = null;
        try {
            setUsedStateReq = (SetUsedStateReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        int status = iTesterLibLoader.setUsedState(
                setUsedStateReq.getSocketId(),
                setUsedStateReq.getCardId(),
                setUsedStateReq.getPortId(),
                setUsedStateReq.getUsedState()
        );

        byte[] data = BDPacketUtil.intToByteArray( status );

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket getUsedState(BDPacket packet) {
        GetUsedStateReq getUsedStateReq = null;
        try {
            getUsedStateReq = (GetUsedStateReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        UsedState usedState = iTesterLibLoader.getUsedState(
                getUsedStateReq.getSocketId(),
                getUsedStateReq.getCardId(),
                getUsedStateReq.getPortId()
        );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( usedState );
        } catch (IOException e) {
            logExeception(e);
        }

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket setStreamId(BDPacket packet) {
        SetStreamIdReq setStreamIdReq = null;
        try {
            setStreamIdReq = (SetStreamIdReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }


        int status = iTesterLibLoader.setStreamId(
                setStreamIdReq.getSocketId(),
                setStreamIdReq.getCardId(),
                setStreamIdReq.getPortId(),
                setStreamIdReq.getiStartId(),
                setStreamIdReq.getiIdNum()
        );

        byte[] data = BDPacketUtil.intToByteArray( status );

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket setEthernetPhysicalForATT(BDPacket packet) {
        SetEthPhy4AttReq setEthPhy4AttReq = null;
        try {
            setEthPhy4AttReq = (SetEthPhy4AttReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        int status = iTesterLibLoader.setEthernetPhysicalForATT(
                setEthPhy4AttReq.getSocketId(),
                setEthPhy4AttReq.getCardId(),
                setEthPhy4AttReq.getPortId(),
                setEthPhy4AttReq.getNego(),
                setEthPhy4AttReq.getEthPhySpeed(),
                setEthPhy4AttReq.getFullDuplex(),
                setEthPhy4AttReq.getLoopback()
        );

        byte[] data = BDPacketUtil.intToByteArray( status );

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket setFramLenghtChange(BDPacket packet) {
        SetFramLengthChangeReq setFramLengthChangeReq = null;
        try {
            setFramLengthChangeReq = (SetFramLengthChangeReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        int status = iTesterLibLoader.setFramLengthChange(
                setFramLengthChangeReq.getSocketId(),
                setFramLengthChangeReq.getCardId(),
                setFramLengthChangeReq.getPortId(),
                setFramLengthChangeReq.getChange()
        );

        byte[] data = BDPacketUtil.intToByteArray( status );

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket loadFPGA(BDPacket packet) {
        LoadFpgaReq loadFpgaReq = null;
        try {
            loadFpgaReq = (LoadFpgaReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        int status = iTesterLibLoader.loadFPGA(
                loadFpgaReq.getSocketId(),
                loadFpgaReq.getCardId(),
                loadFpgaReq.getEthPhySpeed()
        );

        byte[] data = BDPacketUtil.intToByteArray( status );

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket resetFPGA(BDPacket packet) {
        ResetFpgaReq resetFpgaReq = null;
        try {
            resetFpgaReq = (ResetFpgaReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        int status = iTesterLibLoader.resetFPGA(
                resetFpgaReq.getSocketId(),
                resetFpgaReq.getCardId()
        );

        byte[] data = BDPacketUtil.intToByteArray( status );

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket getStreamSendInfo(BDPacket packet) {
        GetStreamSendInfoReq getStreamSendInfoReq = null;
        try {
            getStreamSendInfoReq = (GetStreamSendInfoReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        StreamInfo streamInfo = iTesterLibLoader.getStreamSendInfo(
                getStreamSendInfoReq.getSocketId(),
                getStreamSendInfoReq.getCardId(),
                getStreamSendInfoReq.getPortId(),
                getStreamSendInfoReq.getStreamId()
        );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( streamInfo );
        } catch (IOException e) {
            logExeception(e);
        }

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket getStreamRecInfo(BDPacket packet) {
        GetStreamRecInfoReq getStreamRecInfoReq = null;
        try {
            getStreamRecInfoReq = (GetStreamRecInfoReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        StreamInfo streamInfo = iTesterLibLoader.getStreamRecInfo(
                getStreamRecInfoReq.getSocketId(),
                getStreamRecInfoReq.getCardId(),
                getStreamRecInfoReq.getPortId(),
                getStreamRecInfoReq.getStreamId()
        );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( streamInfo );
        } catch (IOException e) {
            logExeception(e);
        }

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket startCapture(BDPacket packet) {
        StartCaptureReq startCaptureReq = null;
        try {
            startCaptureReq = (StartCaptureReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        int status = iTesterLibLoader.startCapture(
                startCaptureReq.getSocketId(),
                startCaptureReq.getCardId(),
                startCaptureReq.getPortId()
        );

        byte[] data = BDPacketUtil.intToByteArray( status );

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket stopCapture(BDPacket packet) {
        StopCaptureReq stopCaptureReq = null;
        try {
            stopCaptureReq = (StopCaptureReq) SerializeUtil
                    .deserializeFromByteArray(packet.getData());
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        CaptureResult captureResult  = iTesterLibLoader.stopCapture(
                stopCaptureReq.getSocketId(),
                stopCaptureReq.getCardId(),
                stopCaptureReq.getPortId()
        );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray(captureResult);
        } catch (IOException e) {
            logExeception(e);
        }

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    public BDPacket setStreamLength(BDPacket packet) {
        SetStreamLengthReq setStreamLengthReq = null;
        try {
            setStreamLengthReq = (SetStreamLengthReq ) SerializeUtil
                    .deserializeFromByteArray( packet.getData() );
        } catch (IOException e) {
            logExeception(e);
        } catch (ClassNotFoundException e) {
            logExeception(e);
        }

        int status = iTesterLibLoader.setStreamLength(
                setStreamLengthReq.getSocketId(),
                setStreamLengthReq.getCardId(),
                setStreamLengthReq.getPortId(),
                setStreamLengthReq.getStreamId(),
                setStreamLengthReq.getLength()
        );

        byte[] data = BDPacketUtil.intToByteArray( status );

        BDPacket response = BDPacket.newPacket( packet.getRequestID() );
        response.setDataType( RPC_RESPONSE );
        response.setData(data);

        return response;
    }

    private void logExeception(Exception e) {
        ErrorLogger.log( "RPC LocalCaller: " + e.getMessage() );
    }

}
