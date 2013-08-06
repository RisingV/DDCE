package com.bdcom.itester.rpc;

import com.bdcom.itester.api.ITesterAPI;
import com.bdcom.itester.lib.*;
import com.bdcom.itester.rpc.req.*;
import com.bdcom.nio.BDPacket;
import com.bdcom.nio.BDPacketUtil;
import com.bdcom.nio.client.ClientWrapper;
import com.bdcom.sys.config.ServerConfig;
import com.bdcom.util.SerializeUtil;
import com.bdcom.util.StringUtil;
import com.bdcom.util.log.ErrorLogger;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-30    <br/>
 * Time: 17:36  <br/>
 */
public class RpcClient implements ITesterAPI {

    private static final int RPC_REQUEST = Byte.MAX_VALUE / 2;

    private ClientWrapper client;

    public RpcClient(ServerConfig serverConfig) {
        this.client = new ClientWrapper(serverConfig);
    }

    @Override
    public CommuStatus connectToServer(String ipAddr) {
        if ( null == ipAddr || !StringUtil.isVaildIp(ipAddr) ) {
            return null;
        }

        BDPacket request = BDPacket.newPacket( RpcID.CONNECT_TO_SERVER );
        request.setDataType( RPC_REQUEST );
        request.setData( ipAddr.getBytes() );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException(e);
        } catch (IOException e) {
            logException(e);
        }

        CommuStatus cs = null;
        try {
            cs = (CommuStatus) SerializeUtil
                    .deserializeFromByteArray(response.getData());
        } catch (IOException e) {
            logException(e);
        } catch (ClassNotFoundException e) {
            logException(e);
        }

        return cs;
    }

    @Override
    public int disconnectToServer(int socketId) {
        byte[] data = BDPacketUtil.intToByteArray( socketId );

        BDPacket request = BDPacket.newPacket( RpcID.DISCONNECT_TO_SERVER );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException(e);
        } catch (IOException e) {
            logException(e);
        }

        int status = BDPacketUtil.byteArrayToInt( response.getData() );
        return status;
    }

    @Override
    public ChassisInfo getChassisInfo(int socketId) {
        byte[] data = BDPacketUtil.intToByteArray( socketId );

        BDPacket request = BDPacket.newPacket( RpcID.GET_CHASSIS_INFO );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send(request);
        } catch (InterruptedException e) {
            logException(e);
        } catch (IOException e) {
            logException(e);
        }

        ChassisInfo chassisInfo = null;
        try {
            chassisInfo = (ChassisInfo) SerializeUtil
                    .deserializeFromByteArray(response.getData());
        } catch (IOException e) {
            logException(e);
        } catch (ClassNotFoundException e) {
            logException(e);
        }

        return chassisInfo;
    }

    @Override
    public CardInfo getCardInfo(int socketId, int cardId) {
        GetCardInfoReq cardInfoReq = new GetCardInfoReq();
        cardInfoReq.setSocketId( socketId );
        cardInfoReq.setCardId( cardId );

        byte[] rdata  = null;
        try {
            rdata = SerializeUtil.serializeToByteArray(cardInfoReq);
        } catch (IOException e) {
            logException(e);
        }

        BDPacket request = BDPacket.newPacket( RpcID.GET_CARD_INFO );
        request.setDataType(RPC_REQUEST);
        request.setData(rdata);

        BDPacket response = null;
        try {
            response = client.send(request);
        } catch (InterruptedException e) {
            logException(e);
        } catch (IOException e) {
            logException(e);
        }

        CardInfo cardInfo = null;
        try {
            cardInfo = (CardInfo) SerializeUtil
                    .deserializeFromByteArray( response.getData() );
        } catch (IOException e) {
            logException(e);
        } catch (ClassNotFoundException e) {
            logException(e);
        }

        return cardInfo;
    }

    @Override
    public EthPhyProper getEthernetPhysical(int socketId, int cardId, int portId) {
        GetEthPhyReq getEthPhyReq = new GetEthPhyReq();
        getEthPhyReq.setSocketId( socketId );
        getEthPhyReq.setCardId( cardId );
        getEthPhyReq.setPortId( portId );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( getEthPhyReq );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket( RpcID.GET_ETHER_PHYSICAL );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send(request);
        } catch (InterruptedException e) {
            logException( e );
        } catch (IOException e) {
            logException( e );
        }

        EthPhyProper ethPhyProper = null;
        try {
            ethPhyProper = (EthPhyProper) SerializeUtil
                    .deserializeFromByteArray(response.getData());
        } catch (IOException e) {
            logException( e );
        } catch (ClassNotFoundException e) {
            logException( e );
        }

        return ethPhyProper;
    }

    @Override
    public int clearStatReliably(int socketId, int cardId, int portId) {
        ClearStatReliablyReq clearStatReliablyReq = new ClearStatReliablyReq();
        clearStatReliablyReq.setSocketId( socketId );
        clearStatReliablyReq.setCardId( cardId );
        clearStatReliablyReq.setPortId( portId );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( clearStatReliablyReq );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket( RpcID.CLEAR_STAT_RELIABLY );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException( e );
        } catch (IOException e) {
            logException( e );
        }

        int status = BDPacketUtil.byteArrayToInt( response.getData() );
        return status;
    }

    @Override
    public int setHeader(int socketId, int cardId, int portId, int validStreamCount,
                         int length, byte[] strHead) {
        SetHeaderReq setHeaderReq = new SetHeaderReq();
        setHeaderReq.setSocketId( socketId );
        setHeaderReq.setCardId( cardId );
        setHeaderReq.setValidStreamCount( validStreamCount );
        setHeaderReq.setLength( length );
        setHeaderReq.setStrHead( strHead );

        byte[] data = null;
        try {
            data =SerializeUtil.serializeToByteArray( setHeaderReq );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket( RpcID.SET_HEADER );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException( e );
        } catch (IOException e) {
            logException( e );
        }

        int status = BDPacketUtil.byteArrayToInt( response.getData() );
        return status;
    }

    @Override
    public int setPayload(int socketId, int cardId, int portId, int length,
                          byte[] data, int payloadType) {
        SetPayloadReq setPayloadReq = new SetPayloadReq();
        setPayloadReq.setSocketId( socketId );
        setPayloadReq.setCardId( cardId );
        setPayloadReq.setLength( length );
        setPayloadReq.setData( data );
        setPayloadReq.setPayloadType( payloadType );

        byte[] content = null;
        try {
            content = SerializeUtil.serializeToByteArray( setPayloadReq );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket( RpcID.SET_PAYLOAD );
        request.setDataType( RPC_REQUEST );
        request.setData( content );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException( e );
        } catch (IOException e) {
            logException( e );
        }

        int status = BDPacketUtil.byteArrayToInt( response.getData() );
        return status;
    }

    @Override
    public int setDelayCount(int socketId, int cardId, int portId, int delayCount) {
        SetDelayCountReq setDelayCountReq = new SetDelayCountReq();
        setDelayCountReq.setSocketId(socketId);
        setDelayCountReq.setCardId(cardId);
        setDelayCountReq.setDelayCount( delayCount );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( setDelayCountReq );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket( RpcID.SET_DELAY_COUNT );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException( e );
        } catch (IOException e) {
            logException( e );
        }

        int status = BDPacketUtil.byteArrayToInt( response.getData() );
        return status;
    }

    @Override
    public int setTxMode(int socketId, int cardId, int portId, int mode, int burstNum) {
        SetTxModeReq setTxModeReq = new SetTxModeReq();
        setTxModeReq.setSocketId( socketId );
        setTxModeReq.setCardId( cardId );
        setTxModeReq.setMode( mode );
        setTxModeReq.setBurstNum( burstNum );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( setTxModeReq );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket( RpcID.SET_TX_MODE );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException( e );
        } catch (IOException e) {
            logException( e );
        }

        int status = BDPacketUtil.byteArrayToInt( response.getData() );
        return status;
    }

    @Override
    public int startPort(int socketId, int cardId, int portId) {
        StartPortReq startPortReq = new StartPortReq();
        startPortReq.setSocketId( socketId );
        startPortReq.setCardId( cardId );
        startPortReq.setPortId( portId );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( startPortReq );
        } catch (IOException e) {
            logException(e);
        }

        BDPacket request = BDPacket.newPacket( RpcID.START_PORT );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException(e);
        } catch (IOException e) {
            logException(e);
        }

        int status = BDPacketUtil.byteArrayToInt( response.getData() );
        return status;
    }

    @Override
    public int stopPort(int socketId, int cardId, int portId) {
        StopPortReq stopPortReq = new StopPortReq();
        stopPortReq.setSocketId( socketId );
        stopPortReq.setCardId( cardId );
        stopPortReq.setPortId( portId );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( stopPortReq );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket( RpcID.STOP_PORT );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException( e );
        } catch (IOException e) {
            logException( e );
        }

        int status = BDPacketUtil.byteArrayToInt( response.getData() );
        return status;
    }

    @Override
    public PortStats getPortAllStats(int socketId, int cardId, int portId, int length) {
        GetPortAllStatsReq getPortAllStatsReq = new GetPortAllStatsReq();
        getPortAllStatsReq.setSocketId( socketId );
        getPortAllStatsReq.setCardId( cardId );
        getPortAllStatsReq.setPortId( portId );
        getPortAllStatsReq.setLength( length );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( getPortAllStatsReq );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket( RpcID.GET_PORT_ALL_STATS );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException( e );
        } catch (IOException e) {
            logException( e );
        }

        PortStats portStats = null;
        try {
            portStats = (PortStats) SerializeUtil
                    .deserializeFromByteArray(response.getData());
        } catch (IOException e) {
            logException( e );
        } catch (ClassNotFoundException e) {
            logException( e );
        }

        return portStats;
    }

    @Override
    public LinkStatus getLinkStatus(int socketId, int cardId, int portId) {
        GetLinkStatusReq getLinkStatusReq = new GetLinkStatusReq();
        getLinkStatusReq.setSocketId( socketId );
        getLinkStatusReq.setCardId( cardId );
        getLinkStatusReq.setPortId( portId );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( getLinkStatusReq );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket( RpcID.GET_LINK_STATUS );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException( e );
        } catch (IOException e) {
            logException( e );
        }

        LinkStatus linkStatus = null;
        try {
            linkStatus = (LinkStatus) SerializeUtil
                    .deserializeFromByteArray(response.getData());
        } catch (IOException e) {
            logException( e );
        } catch (ClassNotFoundException e) {
            logException( e );
        }

        return linkStatus;
    }

    @Override
    public WorkInfo getWorkInfo(int socketId, int cardId, int portId) {
        GetWorkInfoReq getWorkInfoReq = null;
        getWorkInfoReq.setSocketId( socketId );
        getWorkInfoReq.setCardId( cardId );
        getWorkInfoReq.setPortId( portId );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( getWorkInfoReq );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket(RpcID.GET_WORK_INFO );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException( e );
        } catch (IOException e) {
            logException( e );
        }

        WorkInfo workInfo = null;
        try {
            workInfo = (WorkInfo) SerializeUtil
                    .deserializeFromByteArray(response.getData());
        } catch (IOException e) {
            logException( e );
        } catch (ClassNotFoundException e) {
            logException( e );
        }

        return workInfo;
    }

    @Override
    public int setUsedState(int socketId, int cardId, int portId, int usedState) {
        SetUsedStateReq setUsedStateReq = new SetUsedStateReq();
        setUsedStateReq.setSocketId( socketId );
        setUsedStateReq.setCardId( cardId );
        setUsedStateReq.setPortId( portId );
        setUsedStateReq.setUsedState( usedState );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( setUsedStateReq );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket( RpcID.SET_USED_STATE );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException( e );
        } catch (IOException e) {
            logException( e );
        }

        int status = BDPacketUtil.byteArrayToInt( response.getData() );
        return status;
    }

    @Override
    public UsedState getUsedState(int socketId, int cardId, int portId) {
        GetUsedStateReq getUsedStateReq = new GetUsedStateReq();
        getUsedStateReq.setSocketId( socketId );
        getUsedStateReq.setCardId( cardId );
        getUsedStateReq.setPortId( portId );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( getUsedStateReq );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket( RpcID.GET_USED_STATE );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException( e );
        } catch (IOException e) {
            logException( e );
        }

        UsedState usedState = null;
        try {
            usedState = (UsedState) SerializeUtil
                    .deserializeFromByteArray(response.getData());
        } catch (IOException e) {
            logException( e );
        } catch (ClassNotFoundException e) {
            logException( e );
        }

        return usedState;
    }

    @Override
    public int setStreamId(int socketId, int cardId, int portId, int iStartId, int iIdNum) {
        SetStreamIdReq setStreamIdReq = new SetStreamIdReq();
        setStreamIdReq.setSocketId( socketId );
        setStreamIdReq.setCardId(cardId);
        setStreamIdReq.setPortId( portId );
        setStreamIdReq.setiStartId( iStartId );
        setStreamIdReq.setiIdNum( iIdNum );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( setStreamIdReq );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket( RpcID.SET_STREAM_ID );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException( e );
        } catch (IOException e) {
            logException( e );
        }

        int status = BDPacketUtil.byteArrayToInt( response.getData() );
        return status;
    }

    @Override
    public int setEthernetPhysicalForATT(int socketId, int cardId, int portId,
                             int nego, int ethPhySpeed, int fullDuplex, int loopback) {
        SetEthPhy4AttReq setEthPhy4AttReq = new SetEthPhy4AttReq();
        setEthPhy4AttReq.setSocketId( socketId );
        setEthPhy4AttReq.setCardId( cardId );
        setEthPhy4AttReq.setPortId( portId );
        setEthPhy4AttReq.setNego( nego );
        setEthPhy4AttReq.setEthPhySpeed( ethPhySpeed );
        setEthPhy4AttReq.setFullDuplex( fullDuplex );
        setEthPhy4AttReq.setLoopback( loopback );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( setEthPhy4AttReq );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket( RpcID.SET_ETHERNET_PHY_4_ATT );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException(e);
        } catch (IOException e) {
            logException(e);
        }

        int status = BDPacketUtil.byteArrayToInt( response.getData() );
        return status;
    }

    @Override
    public int loadFPGA(int socketId, int cardId, int ethPhySpeed) {
        LoadFpgaReq loadFpgaReq = new LoadFpgaReq();
        loadFpgaReq.setSocketId(socketId);
        loadFpgaReq.setCardId(cardId);
        loadFpgaReq.setEthPhySpeed( ethPhySpeed );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( loadFpgaReq );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket( RpcID.LOAD_FPGA );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException( e );
        } catch (IOException e) {
            logException( e );
        }

        int status = BDPacketUtil.byteArrayToInt( response.getData() );
        return status;
    }

    @Override
    public int resetFPGA(int socketId, int cardId) {
        ResetFpgaReq resetFpgaReq = new ResetFpgaReq();
        resetFpgaReq.setSocketId( socketId );
        resetFpgaReq.setCardId( cardId );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( resetFpgaReq );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket( RpcID.RESET_FPGA );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException( e );
        } catch (IOException e) {
            logException( e );
        }

        int status = BDPacketUtil.byteArrayToInt( response.getData() );
        return status;
    }

    @Override
    public StreamInfo getStreamSendInfo(int socketId, int cardId, int portId, int streamId) {
        GetStreamSendInfoReq getStreamSendInfoReq = new GetStreamSendInfoReq();
        getStreamSendInfoReq.setSocketId( socketId );
        getStreamSendInfoReq.setCardId( cardId );
        getStreamSendInfoReq.setPortId( portId );
        getStreamSendInfoReq.setStreamId( streamId );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( data );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket( RpcID.GET_STREAM_SEND_INFO );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException( e );
        } catch (IOException e) {
            logException( e );
        }

        StreamInfo streamInfo = null;
        try {
            streamInfo = (StreamInfo) SerializeUtil
                    .deserializeFromByteArray(response.getData());
        } catch (IOException e) {
            logException( e );
        } catch (ClassNotFoundException e) {
            logException( e );
        }

        return streamInfo;
    }

    @Override
    public StreamInfo getStreamRecInfo(int socketId, int cardId, int portId, int streamId) {
        GetStreamRecInfoReq getStreamRecInfoReq = new GetStreamRecInfoReq();
        getStreamRecInfoReq.setSocketId( socketId );
        getStreamRecInfoReq.setCardId( cardId );
        getStreamRecInfoReq.setPortId( portId );
        getStreamRecInfoReq.setStreamId( streamId );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( getStreamRecInfoReq );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket( RpcID.GET_STREAM_REC_INFO );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException( e );
        } catch (IOException e) {
            logException( e );
        }

        StreamInfo streamInfo = null;
        try {
            streamInfo = (StreamInfo) SerializeUtil
                    .deserializeFromByteArray(response.getData());
        } catch (IOException e) {
            logException( e );
        } catch (ClassNotFoundException e) {
            logException( e );
        }

        return streamInfo;
    }

    @Override
    public int startCapture(int socketId, int cardId, int portId) {
        StartCaptureReq startCaptureReq = new StartCaptureReq();
        startCaptureReq.setSocketId( socketId );
        startCaptureReq.setCardId( cardId );
        startCaptureReq.setPortId( portId );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( startCaptureReq );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket( RpcID.START_CAPTURE );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException( e );
        } catch (IOException e) {
            logException( e );
        }

        int status = BDPacketUtil.byteArrayToInt( response.getData() );
        return status;
    }

    @Override
    public CaptureResult stopCapture(int socketId, int cardId, int portId) {
        StopCaptureReq stopCaptureReq = new StopCaptureReq();
        stopCaptureReq.setSocketId( socketId );
        stopCaptureReq.setCardId( cardId );
        stopCaptureReq.setPortId( portId );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( stopCaptureReq );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket( RpcID.STOP_CAPTURE );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException( e );
        } catch (IOException e) {
            logException( e );
        }

        CaptureResult captureResult = null;
        try {
            captureResult = (CaptureResult) SerializeUtil
                    .deserializeFromByteArray(response.getData());
        } catch (IOException e) {
            logException( e );
        } catch (ClassNotFoundException e) {
            logException( e );
        }

        return captureResult;
    }

    @Override
    public int setStreamLength(int socketId, int cardId, int portId, int streamId, int length) {
        SetStreamLengthReq setStreamLengthReq = new SetStreamLengthReq();
        setStreamLengthReq.setSocketId( socketId );
        setStreamLengthReq.setCardId(cardId);
        setStreamLengthReq.setPortId( portId );
        setStreamLengthReq.setStreamId( streamId );
        setStreamLengthReq.setLength( length );

        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray( setStreamLengthReq );
        } catch (IOException e) {
            logException( e );
        }

        BDPacket request = BDPacket.newPacket( RpcID.SET_STREAM_LENGTH );
        request.setDataType( RPC_REQUEST );
        request.setData( data );

        BDPacket response = null;
        try {
            response = client.send( request );
        } catch (InterruptedException e) {
            logException( e );
        } catch (IOException e) {
            logException( e );
        }

        int status = BDPacketUtil.byteArrayToInt( response.getData() );
        return status;
    }

    private void logException(Exception e) {
        ErrorLogger.log( "RPC Client" + e.getMessage() );
    }

}
