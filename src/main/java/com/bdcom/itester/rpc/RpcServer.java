package com.bdcom.itester.rpc;

import com.bdcom.nio.BDPacket;
import com.bdcom.nio.server.IHandler;
import com.bdcom.nio.server.NIOServer;
import com.bdcom.util.log.ErrorLogger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: francis
 * Date: 13-7-29
 * Time: 17:01
 */
public class RpcServer extends Thread {

    public static void main(String... s) {
        RpcServer rpcServer = new RpcServer( 7777 );
        rpcServer.start();
    }

    private int port;

    private NIOServer nioServer;

    private Map<Integer, IHandler> servMap;

    public RpcServer(int port) {
        this.port = port;
        final LocalCaller local = LocalCaller.getInstance();
        servMap = new HashMap<Integer, IHandler>() {
            {
                put( RpcID.CONNECT_TO_SERVER , new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket packet) {
                        return local.connectToServer(packet);
                    }
                });

                put( RpcID.DISCONNECT_TO_SERVER, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return  local.disconnectToServer(bdPacket);
                    }
                }
                );

                put( RpcID.GET_CHASSIS_INFO, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket packet) {
                        return local.getChassisInfo(packet);
                    }
                });

                put( RpcID.GET_CARD_INFO, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.getCardInfo( bdPacket );
                    }
                });

                put( RpcID.GET_ETHER_PHYSICAL, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.getEthernetPhysical(bdPacket);
                    }
                });

                put( RpcID.CLEAR_STAT_RELIABLY, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.clearEthernetPhysical(bdPacket);
                    }
                });

                put( RpcID.SET_HEADER, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.setHeader(bdPacket);
                    }
                });

                put( RpcID.SET_PAYLOAD, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.setPaylaod(bdPacket);
                    }
                });

                put( RpcID.SET_DELAY_COUNT , new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.setDelayCount(bdPacket);
                    }
                });

                put( RpcID.SET_TX_MODE, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.setTxMode(bdPacket);
                    }
                });

                put( RpcID.START_PORT, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.startPort(bdPacket);
                    }
                });

                put( RpcID.STOP_PORT, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.stopPort(bdPacket);
                    }
                });

                put( RpcID.GET_PORT_ALL_STATS, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.getPortAllStats(bdPacket);
                    }
                });

                put( RpcID.GET_LINK_STATUS, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.getLinkStatus(bdPacket);
                    }
                });

                put( RpcID.GET_WORK_INFO, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.getWorkInfo(bdPacket);
                    }
                });

                put( RpcID.GET_USED_STATE, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.getUsedState(bdPacket);
                    }
                });

                put( RpcID.SET_USED_STATE, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.setUsedState(bdPacket);
                    }
                });

                put( RpcID.SET_STREAM_ID, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.setStreamId( bdPacket );
                    }
                });

                put( RpcID.SET_ETHERNET_PHY_4_ATT, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.setEthernetPhysicalForATT(bdPacket);
                    }
                });

                put( RpcID.SET_FRAM_LENGTH_CHANGE, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.setFramLenghtChange(bdPacket);
                    }
                });

                put( RpcID.LOAD_FPGA, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.loadFPGA(bdPacket);
                    }
                });

                put( RpcID.RESET_FPGA, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.resetFPGA( bdPacket );
                    }
                });

                put( RpcID.GET_STREAM_SEND_INFO, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.getStreamSendInfo( bdPacket );
                    }
                });

                put( RpcID.GET_STREAM_REC_INFO, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.getStreamRecInfo( bdPacket );
                    }
                });

                put( RpcID.START_CAPTURE, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.startCapture( bdPacket );
                    }
                });

                put( RpcID.STOP_CAPTURE, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.stopCapture(bdPacket);
                    }
                });

                put( RpcID.SET_STREAM_LENGTH, new IHandler() {
                    @Override
                    public BDPacket handle(BDPacket bdPacket) {
                        return local.setStreamLength(bdPacket);
                    }
                });

            }
        };
    }

    @Override
    public void run() {
        nioServer = new NIOServer( port );
        nioServer.setHandlerMap( servMap );
        try {
            nioServer.start();
        } catch (IOException e) {
            ErrorLogger.log("RPC Server start up fail: " + e.getMessage() );
        }
    }

}
