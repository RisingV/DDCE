package just.foor;

import com.bdcom.itester.api.ITesterAPI;
import com.bdcom.itester.lib.*;
import com.bdcom.itester.rpc.RpcClient;
import com.bdcom.itester.util.EthFrameUtil;
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

    public static void main(String... s) {
        PathConfig pathConfig = new PathConfig(
                RUN_TIME.CURRENT_DIR + File.separator + "RPC-config" );

        ServerConfig serverConfig = new ServerConfig( pathConfig );
        serverConfig.setDefaultIP("172.16.22.222");
        serverConfig.setDefaultPort( 7777 );
        serverConfig.writeToConfigFile("172.16.22.222", "7777");

        ITesterAPI it = new RpcClient( serverConfig );

        sendTest( it );
        //printAll( it );
    }

    private static void sendTest(ITesterAPI it) {
        CommuStatus cs = it.connectToServer(SERV_IP);

        if ( !cs.isConnected() ) {
            System.out.println( "connect to " + SERV_IP + "fail!" );
            return;
        }

        byte[] head0to1 = EthFrameUtil.getHeader(3, 0, 3, 1);
        byte[] head1to0 = EthFrameUtil.getHeader(3, 1, 3, 0);
        int socketId = cs.getSocketId();

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
            TimeUnit.SECONDS.sleep( 5 );
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
        System.out.println( " socketID: " + cs.getSocketId() );
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

}
