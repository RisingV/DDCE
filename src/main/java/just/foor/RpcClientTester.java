package just.foor;

import com.bdcom.itester.api.ITesterAPI;
import com.bdcom.itester.lib.*;
import com.bdcom.itester.rpc.RpcClient;
import com.bdcom.sys.ApplicationConstants;
import com.bdcom.sys.config.PathConfig;
import com.bdcom.sys.config.ServerConfig;

import java.io.File;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-31    <br/>
 * Time: 16:16  <br/>
 */
public class RpcClientTester implements ApplicationConstants {

    public static void main(String... s) {
        PathConfig pathConfig = new PathConfig(
                RUN_TIME.CURRENT_DIR + File.separator + "RPC-config" );

        ServerConfig serverConfig = new ServerConfig( pathConfig );
        serverConfig.setDefaultIP("172.16.22.222");
        serverConfig.setDefaultPort( 7777 );
        serverConfig.writeToConfigFile("172.16.22.222", "7777");

        ITesterAPI it = new RpcClient( serverConfig );
        CommuStatus cs = it.connectToServer("172.16.22.202");
        ChassisInfo ci = it.getChassisInfo( cs.getSocketId() );

        System.out.println( " isConnected: " + cs.isConnected() );
        System.out.println( " socketID: " + cs.getSocketId() );
        System.out.println( " CardNum: " + ci.getCardNum() );
        System.out.println( " ChassisType: " + ci.getChassisType() );
        System.out.println(" description: " + ci.getDescription());

        for ( int num = 0 ; num < ci.getCardNum(); num++ ) {
            CardInfo cardInfo = it.getCardInfo(cs.getSocketId(), num);
            System.out.println( "connected(" + num+ "): " + cardInfo.getCardId() );
            System.out.println( "cardID(" + num+ "): " + cardInfo.getCardId() );
            System.out.println( "cardType(" + num+ "): " +  cardInfo.getCardType() );
            System.out.println( "portNumber(" + num+ "): " + cardInfo.getPortNumber() );
            System.out.println( "description(" + num+ "): " + cardInfo.getDescription() );
            for ( int pid = 0; pid < cardInfo.getPortNumber(); pid++ ) {
                EthPhyProper epp = it.getEthernetPhysical( cs.getSocketId(), num, pid );
                UsedState us = it.getUsedState(cs.getSocketId(), num, pid);
                LinkStatus ls = it.getLinkStatus(cs.getSocketId(), num, pid);
                PortStats ps = it.getPortAllStats(cs.getSocketId(), num, pid, 8);
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
                System.out.println( "   LinkStatus: " );
                System.out.println( "       connected("+num+","+pid+"): "+ ls.isConnected() );
                System.out.println( "       linkup("+num+","+pid+"): "+ ls.isLinked() );
                System.out.println( "   PortStats: " );
                System.out.println( "       connected("+num+","+pid+"): "+ ps.isConnected() );
                System.out.println( "       stats("+num+","+pid+"): "+ ps.getStats() );
            }
        }



        int status0 = it.disconnectToServer( cs.getSocketId() );
        System.out.println( "status0: " + status0 );
    }

}
