package just.foor;

import com.bdcom.itester.api.ITesterAPI;
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
        serverConfig.setDefaultPort( 7777 );

        ITesterAPI it = new RpcClient( serverConfig );
        CommuStatus commuStatus = it.connectToServer( "127.0.0.1" );

        System.out.println( "isConnected: " + commuStatus.isConnected() );
        System.out.println( "socketID: " + commuStatus.getSocketId() );
    }

}
