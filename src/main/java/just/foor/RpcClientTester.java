package just.foor;

import com.bdcom.itester.api.ITesterAPI;
import com.bdcom.itester.lib.CardInfo;
import com.bdcom.itester.lib.CommuStatus;
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
        CommuStatus cs = it.connectToServer( "172.16.22.202" );
        CardInfo cardInfo = it.getCardInfo(cs.getSocketId(), 1);
        int status0 = it.disconnectToServer( cs.getSocketId() );
        CommuStatus cs2 = it.connectToServer( "172.16.22.202" );
        int status1 = it.disconnectToServer( cs2.getSocketId() );

        System.out.println( "isConnected: " + cs.isConnected() );
        System.out.println( "socketID: " + cs.getSocketId() );

        System.out.println( "2 isConnected: " + cs2.isConnected() );
        System.out.println( "2 socketID: " + cs2.getSocketId() );

        System.out.println( "status0: " + status0 );
        System.out.println( "status1: " + status1 );

        System.out.println( "cardID:" + cardInfo.getCardId() );
        System.out.println( "cardType:" + cardInfo.getCardType() );
        System.out.println( "portNumber:" + cardInfo.getPortNumber() );
        System.out.println( "description:" + cardInfo.getDescription() );

    }

}
