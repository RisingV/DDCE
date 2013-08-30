package com.bdcom.devtest;

import com.bdcom.itester.api.ITesterAPI;
import com.bdcom.itester.api.wrapper.TestSession;
import com.bdcom.itester.lib.*;
import com.bdcom.itester.rpc.RpcClient;
import com.bdcom.itester.api.wrapper.EthFrameUtil;
import com.bdcom.itester.api.wrapper.ITesterAPIWrapper;
import com.bdcom.itester.api.wrapper.ITesterException;
import com.bdcom.sys.ApplicationConstants;
import com.bdcom.sys.config.PathConfig;
import com.bdcom.sys.config.ServerConfig;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-31    <br/>
 * Time: 16:16  <br/>
 */
public class RpcClientTester implements ApplicationConstants {

    private static final String SERV_IP = "172.16.22.202";

    private static final int STREAM_BASE = 4660;

    public static void main(String... s) {
        PathConfig pathConfig = new PathConfig(
                RUN_TIME.CURRENT_DIR + File.separator + "RPC-config" );

        ServerConfig serverConfig = new ServerConfig( pathConfig );
        serverConfig.setDefaultIP("172.16.22.222");
        serverConfig.setDefaultPort( 7777 );
        serverConfig.writeToConfigFile("172.16.22.222", "7777");

        ITesterAPI it = new RpcClient( serverConfig );
        iTestAPIWrapperTest( it, 3, 1, 3, 2 );
        //doubleStreamTest( it, 3, 1, 3, 2 );

        //setUnused( it , 3, 1);
        //setUnused( it , 3, 2);
        //sendTest( it );

        //captureTest( it, 3, 0 );
        //tryCapture( it );
        //cp( it , 4, 2);



        //stopPort( it );
       // sendTest( it );
        //headerTest();
  //      captureTest( it );
       // sendTesty( it );
    }

    private static void iTestAPIWrapperTest(ITesterAPI it, int c0, int p0, int c1, int p1) {
        ITesterAPIWrapper itw = new ITesterAPIWrapper(it);

        try {
            final TestSession ts = itw.startTest( "172.16.22.202",c0,p0,c1,p1,15);
            Thread reportProgress = new Thread() {
                @Override
                public void run() {
                    while ( true ) {
                        int percent = 0;
                        try {
                            percent = ts.getProgressPercent();
                        } catch (ITesterException e) {
                            e.printStackTrace();
                            break;
                        }
                        System.out.println( "testProgress: " + percent +"%");
                        try {
                            TimeUnit.SECONDS.sleep( 1 );
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if ( 100 == percent ) {
                            break;
                        }
                    }
                }
            };
            reportProgress.start();

            int c = 0;
            while ( true ) {
                try {
                    TimeUnit.SECONDS.sleep( 12 );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                c++;

                if ( ts.isTestDone() ) {
                    System.out.println( "Test result : " + ts.isTestPass() );
                    break;
                }
                if  ( c >= 4 ) {
                    System.out.println( "Force to stop test");
                    ts.forceClose();
                    System.out.println( "Test result : " + ts.isTestPass() );
                    break;
                }
            }
        } catch (ITesterException e) {
            e.printStackTrace();
        }

        itw.closeCurrConnection();
    }

    private static void cp(ITesterAPI it, int cd, int pd) {
        CommuStatus cs = it.connectToServer( SERV_IP );
        int socketId = cs.getSocketId();

        //it.setUsedState( socketId, 4,3,1);

        //it.startPort( socketId, 4, 3);
        it.startCapture( socketId, cd, pd);
        try {
            TimeUnit.SECONDS.sleep( 5 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CaptureResult cr = it.stopCapture( socketId, cd, pd);
        System.out.println( cr.getFrames() );
        //it.setUsedState( socketId, 4,3,0);
        //it.clearStatReliably( socketId, 4, 3 );
        it.disconnectToServer( socketId );
    }

    private static void stopPort(ITesterAPI it) {
        CommuStatus cs = it.connectToServer( SERV_IP );
        int socketId = cs.getSocketId();
        WorkInfo wi = it.getWorkInfo( socketId, 3, 2);
        System.out.println("workNow? " + wi.isWorkNow() );
        it.disconnectToServer( socketId );
    }

    private static void tryCapture(ITesterAPI it) {
        CommuStatus cs = it.connectToServer( SERV_IP );
        int socketId = cs.getSocketId();

        int[] head = EthFrameUtil.getHeader( 3, 2, 3, 0 );

        int[] h = new int[ head.length * 2];
        System.arraycopy( head, 0, h, 0, head.length );
        System.arraycopy( head, 0, h, head.length, head.length );

        int payload = 0x5A;
        it.setTxMode( socketId, 3, 2, 0, 0);
        it.setPayload(socketId, 3, 2, 0, payload, 0);
        it.setHeader(socketId, 3, 2, 2, 2 * head.length, h);

        it.setStreamId( socketId, 3, 2, 0, 1);
        it.setStreamLength(socketId, 3, 2, 0, 100 - head.length);
        it.setStreamLength(socketId, 3, 2, 1, 200 - head.length);
        it.setFramLengthChange(socketId, 3, 2, 0);

        it.setUsedState( socketId, 3, 2, 1 );
        it.startPort( socketId, 3, 2);
        try {
            TimeUnit.SECONDS.sleep( 10 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        it.stopPort(socketId, 3, 2);
        it.setUsedState(socketId, 3, 2, 0);
        it.disconnectToServer( socketId );
    }

    private static void captureTest(final ITesterAPI it, final int cd, final int pd) {
        CommuStatus cs = null;
        synchronized ( SERV_IP ) {
            cs = it.connectToServer( SERV_IP );
        }

        if ( !cs.isConnected() ) {
            System.out.println( "connect to " + SERV_IP + "fail!" );
            return;
        }

        int socketId = cs.getSocketId();
        it.setUsedState( socketId, cd ,pd, 1);
        it.startPort( socketId, cd , pd );
        it.startCapture( socketId, cd, pd );

        try {
            TimeUnit.SECONDS.sleep( 5 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CaptureResult cr = it.stopCapture( socketId, cd, pd );
        it.stopPort( socketId, cd , pd);
        it.setUsedState( socketId, cd ,pd, 0);
        it.clearStatReliably( socketId, cd, pd);
        it.disconnectToServer( socketId );

        System.out.println("capture result: " + cr.getFrames() );
    }

    private static void headerTest() {
        String hstr = EthFrameUtil.getHeaderRawStr( 3, 0, 3, 1);
        int[] ba = EthFrameUtil.getHeader( 3, 0, 3, 1);

        System.out.println( "length: " + hstr.length() );
        System.out.println( "String Header: " + hstr );
        System.out.println( "Byte Header: " + toHexString(ba) );
    }


    private static void doubleStreamTest(ITesterAPI it, int cd0, int pd0, int cd1, int pd1) {
        CommuStatus cs = null;
        synchronized ( SERV_IP ) {
            cs = it.connectToServer( SERV_IP );
        }

        if ( !cs.isConnected() ) {
            System.out.println( "connect to " + SERV_IP + "fail!" );
            return;
        }

        int socketId = cs.getSocketId();
        EthPhyProper epp = it.getEthernetPhysical( socketId, cd0, pd0 );
        int speedCount = 1;
        switch ( epp.getSpeed() ) {
            case 0: {
                speedCount = 10;
                System.out.println( "speed: 10M" );
                break;
            }
            case 1: {
                speedCount = 100;
                System.out.println( "speed: 100M" );
                break;
            }
            case 2: {
                speedCount = 1000;
                System.out.println( "speed: 1000M" );
                break;
            }
        }


        int packetNum = STREAM_BASE * speedCount;
        int streamId0 = 0;
        int byteData = 0x5A;
        int ePayloadType = 0;

        int[] head0to1 = EthFrameUtil.getHeader(cd0, pd0, cd1, pd1);
        int[] head1to0 = EthFrameUtil.getHeader(cd1, pd1, cd0, pd0);

        //it.startCapture( socketId, cd0, pd0 );

        int l0 = head0to1.length;
        int l1 = head1to0.length;

        int len = l0 + l1;
        int[] h = new int[len];
        System.arraycopy( head0to1, 0, h, 0, l0 );
        System.arraycopy( head1to0, 0, h, l0, l1);

        it.setHeader( socketId, cd0, pd0, 2, len, h );
        it.setHeader( socketId, cd1, pd1, 2, len, h );

        it.setPayload( socketId, cd0, pd0, 0, byteData, ePayloadType);
        it.setPayload( socketId, cd1, pd1, 0, byteData, ePayloadType);

        it.setDelayCount( socketId, cd0, pd0, 12 );
        it.setDelayCount( socketId, cd1, pd1, 12 );

        it.setTxMode( socketId, cd0, pd0, 1, packetNum);
        it.setTxMode( socketId, cd1, pd1, 1, packetNum);

        it.setStreamId( socketId, cd0, pd0, streamId0, 1);
        it.setStreamId( socketId, cd1, pd1, streamId0, 1);

        it.setFramLengthChange( socketId, cd0, pd0, 0);
        it.setFramLengthChange( socketId, cd1, pd1, 0);
        it.setStreamLength( socketId, cd0, pd0, streamId0, 68-head0to1.length );
        it.setStreamLength( socketId, cd1, pd1, streamId0, 68-head1to0.length );
        it.setStreamLength( socketId, cd0, pd0, streamId0 + 1, 1518-head0to1.length );
        it.setStreamLength( socketId, cd1, pd1, streamId0 + 1, 1518-head1to0.length );

        it.setUsedState( socketId, cd0, pd0, 1 );
        it.setUsedState( socketId, cd1, pd1, 1 );

        long start = System.currentTimeMillis();
        it.startPort( socketId , cd0, pd0 );
        it.startPort( socketId , cd1, pd1 );

        long point0 = System.currentTimeMillis();
        LinkStatus ls0 = it.getLinkStatus( socketId, cd0, pd0 );
        LinkStatus ls1 = it.getLinkStatus( socketId, cd1, pd1 );
        UsedState us0 = it.getUsedState( socketId, cd0, pd0 );
        UsedState us1 = it.getUsedState( socketId, cd1, pd1 );
        WorkInfo wi0 = it.getWorkInfo(socketId, cd0, pd0 );
        WorkInfo wi1 = it.getWorkInfo( socketId, cd1, pd1 );

        System.out.println( "   LinkStatus: " );
        System.out.println("       connected("+cd0+", "+pd0+"): " + ls0.isConnected());
        System.out.println( "       linkup("+cd0+", "+pd0+"): "+ ls0.isLinked() );
        System.out.println( "   LinkStatus: " );
        System.out.println("       connected("+cd1+", "+pd1+"): " + ls1.isConnected());
        System.out.println( "       linkup("+cd1+", "+pd1+"): "+ ls1.isLinked() );
        System.out.println( "   UsedState: " );
        System.out.println( "       connected("+cd0+", "+pd0+"): "+ us0.isConnected() );
        System.out.println( "       used("+cd0+", "+pd0+"): "+ us0.isUsed() );
        System.out.println( "   UsedState: " );
        System.out.println( "       connected("+cd1+", "+pd1+"): "+ us1.isConnected() );
        System.out.println("       used("+cd1+", "+pd1+"): " + us1.isUsed());
        System.out.println( "   WorkInfo: ");
        System.out.println( "       connected("+cd0+", "+pd0+"): "+ wi0.isConnected() );
        System.out.println( "       workNow("+cd0+", "+pd0+"): "+ wi0.isWorkNow() );
        System.out.println( "   WorkInfo: ");
        System.out.println( "       connected("+cd1+", "+pd1+"): "+ wi1.isConnected() );
        System.out.println( "       workNow("+cd1+", "+pd1+"): "+ wi1.isWorkNow() );

        long extra0 = System.currentTimeMillis() - point0;
        long extra1 = 0;
        long end = 0;
        int c = 0;
        while ( true ) {
            try {
                TimeUnit.SECONDS.sleep( 10 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            c++;

            long tmpPoint0 = System.currentTimeMillis();
            StreamInfo si0 = it.getStreamSendInfo( socketId, cd0, pd0, streamId0 );
            StreamInfo si1 = it.getStreamSendInfo( socketId, cd1, pd0, streamId0 + 1);
            long tmpPoint1 = System.currentTimeMillis();

            extra1 += tmpPoint1 - tmpPoint0;

            long sentCount = si0.getPacketCount() + si1.getPacketCount();
            if ( sentCount >= packetNum || c >= 4) {
                end = System.currentTimeMillis();
                it.stopPort( socketId, cd0, pd0);
                it.stopPort( socketId, cd1, pd1);
                break;
            }
        }
        long realTimeSpent = end - start;
        long sentTimeSpent = realTimeSpent - extra0 - extra1;

        System.out.println( "extra0: " + extra0 );
        System.out.println( "extra1: " + extra1 );
        System.out.println( "sentTimeSpent: " + sentTimeSpent );
        System.out.println( "realTimeSpent: " + realTimeSpent );



        /*
        int count = 0;
        while( true ) {
            count++;
            try {
                TimeUnit.SECONDS.sleep( 1 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if ( count > 5 ) {
                CaptureResult cr = it.stopCapture( socketId, cd0, pd0);
                System.out.println("Capture Result("+ cd0 + ", "+ pd0 + "): " + cr.getFrames() );
            }
            if ( count >= 10) {
                it.stopPort( socketId, cd0, pd0);
                it.stopPort( socketId, cd1, pd1);
                break;
            }
        }
        */

        it.setUsedState( socketId, cd0, pd0, 0 );
        it.setUsedState( socketId, cd1, pd1, 0 );

        System.out.println( "   After stop UsedState: " );
        System.out.println( "       connected("+cd0+", "+pd0+"): "+ us0.isConnected() );
        System.out.println( "       used("+cd0+", "+pd0+"): "+ us0.isUsed() );
        System.out.println( "   After stop UsedState: " );
        System.out.println( "       connected("+cd1+", "+pd1+"): "+ us1.isConnected() );
        System.out.println("       used("+cd1+", "+pd1+"): " + us1.isUsed());
        System.out.println( "   After stop WorkInfo: ");
        System.out.println( "       connected("+cd0+", "+pd0+"): "+ wi0.isConnected() );
        System.out.println( "       workNow("+cd0+", "+pd0+"): "+ wi0.isWorkNow() );
        System.out.println( "   After stop WorkInfo: ");
        System.out.println( "       connected("+cd1+", "+pd1+"): "+ wi1.isConnected() );
        System.out.println( "       workNow("+cd1+", "+pd1+"): "+ wi1.isWorkNow() );

        PortStats ps0 = it.getPortAllStats( socketId, cd0, pd0, 8);
        PortStats ps1 = it.getPortAllStats( socketId, cd1, pd1, 8);

        System.out.println( "   PortStats: " );
        System.out.println( "       connected("+cd0+", "+pd0+"): "+ ps0.isConnected() );
        System.out.println( "       stats("+cd0+", "+pd0+"): "+ printIntArray(ps0.getStats()) );
        System.out.println( "   PortStats: " );
        System.out.println( "       connected("+cd1+", "+pd1+"): "+ ps1.isConnected() );
        System.out.println( "       stats("+cd1+", "+pd1+"): "+ printIntArray(ps1.getStats()) );

        it.clearStatReliably( socketId, cd0, pd0 );
        it.clearStatReliably( socketId, cd1, pd1 );

        it.disconnectToServer( socketId );

    }

    private static void setUnused(ITesterAPI it, int cardId, int portId  ) {
        CommuStatus cs = it.connectToServer(SERV_IP);

        if ( !cs.isConnected() ) {
            System.out.println( "connect to " + SERV_IP + "fail!" );
            return;
        }

        it.setUsedState( cs.getSocketId(), cardId, portId, 0 );

    }

    private static void sendTest(ITesterAPI it) {
        CommuStatus cs = it.connectToServer(SERV_IP);

        if ( !cs.isConnected() ) {
            System.out.println( "connect to " + SERV_IP + "fail!" );
            return;
        }

        int[] head0to1 = EthFrameUtil.getHeader(3, 0, 3, 1);
        int[] head1to0 = EthFrameUtil.getHeader(3, 1, 3, 0);
        int socketId = cs.getSocketId();

        it.setEthernetPhysicalForATT( socketId, 3, 0, 1, 0, 1, 0 );
        it.setEthernetPhysicalForATT( socketId, 3, 1, 1, 0, 1, 0 );

        it.setUsedState( socketId, 3, 0, 1 );
        it.setUsedState( socketId, 3, 1, 1 );

        it.setTxMode( socketId, 3, 0, 0, 0);
        it.setTxMode( socketId, 3, 1, 0, 0);

        it.setHeader( socketId, 3, 0, 0, head0to1.length, head0to1 );
        it.setHeader( socketId, 3, 1, 0, head1to0.length, head1to0 );

        it.startPort( socketId , 3, 0 );
        it.startPort( socketId , 3, 1 );

        LinkStatus ls0 = it.getLinkStatus( socketId, 3, 0);
        LinkStatus ls1 = it.getLinkStatus( socketId, 3, 1);
        UsedState us0 = it.getUsedState( socketId, 3, 0 );
        UsedState us1 = it.getUsedState( socketId, 3, 1 );
        WorkInfo wi0 = it.getWorkInfo(socketId, 3, 0);
        WorkInfo wi1 = it.getWorkInfo( socketId, 3, 1 );

        System.out.println( "   LinkStatus: " );
        System.out.println("       connected(3, 0): " + ls0.isConnected());
        System.out.println( "       linkup(3, 0): "+ ls0.isLinked() );
        System.out.println( "   LinkStatus: " );
        System.out.println("       connected(3, 1): " + ls1.isConnected());
        System.out.println( "       linkup(3, 1): "+ ls1.isLinked() );
        System.out.println( "   UsedState: " );
        System.out.println( "       connected(3, 0): "+ us0.isConnected() );
        System.out.println( "       used(3, 0): "+ us0.isUsed() );
        System.out.println( "   UsedState: " );
        System.out.println( "       connected(3, 1): "+ us1.isConnected() );
        System.out.println("       used(3, 1): " + us1.isUsed());
        System.out.println( "   WorkInfo: ");
        System.out.println( "       connected(3, 0): "+ wi0.isConnected() );
        System.out.println( "       workNow(3, 0): "+ wi0.isWorkNow() );
        System.out.println( "   WorkInfo: ");
        System.out.println( "       connected(3, 1): "+ wi1.isConnected() );
        System.out.println( "       workNow(3, 1): "+ wi1.isWorkNow() );

        try {
            TimeUnit.SECONDS.sleep( 3 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        it.stopPort( socketId, 3, 0 );
        it.stopPort( socketId, 3, 1 );

        it.setUsedState( socketId, 3, 0, 0 );
        it.setUsedState(socketId, 3, 1, 0);

        System.out.println( "   After stop UsedState: " );
        System.out.println( "       connected(3, 0): "+ us0.isConnected() );
        System.out.println( "       used(3, 0): "+ us0.isUsed() );
        System.out.println( "   After stop UsedState: " );
        System.out.println( "       connected(3, 1): "+ us1.isConnected() );
        System.out.println("       used(3, 1): " + us1.isUsed());
        System.out.println( "   After stop WorkInfo: ");
        System.out.println( "       connected(3, 0): "+ wi0.isConnected() );
        System.out.println( "       workNow(3, 0): "+ wi0.isWorkNow() );
        System.out.println( "   After stop WorkInfo: ");
        System.out.println( "       connected(3, 1): "+ wi1.isConnected() );
        System.out.println( "       workNow(3, 1): "+ wi1.isWorkNow() );

        PortStats ps0 = it.getPortAllStats( socketId, 3, 0, 8);
        PortStats ps1 = it.getPortAllStats( socketId, 3, 1, 8);

        System.out.println( "   PortStats: " );
        System.out.println( "       connected(3, 0): "+ ps0.isConnected() );
        System.out.println( "       stats(3, 0): "+ printIntArray(ps0.getStats()) );
        System.out.println( "   PortStats: " );
        System.out.println( "       connected(3, 1): "+ ps1.isConnected() );
        System.out.println( "       stats(3, 1): "+ printIntArray(ps1.getStats()) );

        it.clearStatReliably( socketId, 3, 0 );
        it.clearStatReliably( socketId, 3, 1 );

        it.disconnectToServer( socketId );
    }

    private static void printAll(ITesterAPI it) {
        CommuStatus cs = it.connectToServer(SERV_IP);
        ChassisInfo ci = it.getChassisInfo( cs.getSocketId() );

        int socketId = cs.getSocketId();

        System.out.println( " isConnected: " + cs.isConnected() );
        System.out.println(" socketID: " + cs.getSocketId());
        System.out.println( " CardNum: " + ci.getCardNum() );
        System.out.println( " ChassisType: " + ci.getChassisType() );
        System.out.println( " description: " + ci.getDescription() );

        for ( int num = 0 ; num < ci.getCardNum(); num++ ) {
            CardInfo cardInfo = it.getCardInfo( socketId, num);
            System.out.println( "connected(" + num+ "): " + cardInfo.getCardId() );
            System.out.println( "cardID(" + num+ "): " + cardInfo.getCardId() );
            System.out.println( "cardType(" + num+ "): " +  cardInfo.getCardType() );
            System.out.println( "portNumber(" + num+ "): " + cardInfo.getPortNumber() );
            System.out.println( "description(" + num+ "): " + cardInfo.getDescription() );
            for ( int pid = 0; pid < cardInfo.getPortNumber(); pid++ ) {
                EthPhyProper epp = it.getEthernetPhysical( socketId, num, pid );
                UsedState us = it.getUsedState( socketId, num, pid);
                LinkStatus ls = it.getLinkStatus( socketId, num, pid);
                WorkInfo wi = it.getWorkInfo( socketId, num, pid );
                PortStats ps = it.getPortAllStats( socketId, num, pid, 8 );
                //StreamInfo ssi = it.getStreamSendInfo()
                System.out.println( "   EthPhyProper: " );
                System.out.println( "       connected("+num+","+pid+"): "+ epp.isConnected() );
                System.out.println( "       link("+num+","+pid+"): "+ epp.isLinked() );
                System.out.println( "       nego("+num+","+pid+"): "+ epp.getNego() );
                System.out.println( "       speed("+num+","+pid+"): "+ epp.getSpeed() );
                System.out.println( "       fullDuplex(" +num+","+pid+"): "+ epp.getFullDuplex() );
                System.out.println( "       loopback(" +num+","+pid+"): "+ epp.getLoopback() );
                System.out.println( "   UsedState: " );
                System.out.println( "       connected("+num+","+pid+"): "+ us.isConnected() );
                System.out.println( "       used("+num+","+pid+"): "+ us.isUsed() );
                System.out.println( "   WorkInfo: ");
                System.out.println( "       connected("+num+","+pid+"): "+ wi.isConnected() );
                System.out.println( "       workNow("+num+","+pid+"): "+ wi.isWorkNow() );
                System.out.println( "   LinkStatus: " );
                System.out.println("       connected(" + num + "," + pid + "): " + ls.isConnected());
                System.out.println( "       linkup("+num+","+pid+"): "+ ls.isLinked() );
                System.out.println( "   PortStats: " );
                System.out.println( "       connected("+num+","+pid+"): "+ ps.isConnected() );
                System.out.println( "       stats("+num+","+pid+"): "+ printIntArray(ps.getStats()) );
            }
        }

//        it.setUsedState( socketId, 3, 0, 0 );
//        it.setUsedState( socketId, 3, 1, 0 );

        int status0 = it.disconnectToServer( cs.getSocketId() );
        System.out.println( "status0: " + status0 );
    }

    private static String printIntArray(long[] a) {
        if ( null == a ) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for ( int i = 0; i < a.length; i++ ) {
            if ( i != a.length - 1 ) {
                sb.append(a[i]).append(", ");
            } else {
                sb.append(a[i]);
            }
        }
        sb.append(" ]");

        return sb.toString();
    }

    private static String toHexString(int[] ia) {
        StringBuilder sb = new StringBuilder();
        for ( int i : ia ) {
            int high = 0x0F & i>>4;
            int low = 0x0F & i;
            sb.append( getChar( high ) );
            sb.append( getChar( low ) );
        }
        return sb.toString();
    }

    private static char getChar(int i) {
        char x = '0';
        switch ( i ) {
            case 0: x = '0';break;
            case 1: x = '1';break;
            case 2: x = '2';break;
            case 3: x = '3';break;
            case 4: x = '4';break;
            case 5: x = '5';break;
            case 6: x = '6';break;
            case 7: x = '7';break;
            case 8: x = '8';break;
            case 9: x = '9';break;
            case 10: x = 'A';break;
            case 11: x = 'B';break;
            case 12: x = 'C';break;
            case 13: x = 'D';break;
            case 14: x = 'E';break;
            case 15: x = 'F';break;
        }
        return x;
    }

}
