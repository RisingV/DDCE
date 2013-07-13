package just.foor;

import com.bdcom.nio.BDPacket;
import com.bdcom.nio.BDPacketUtil;
import com.bdcom.nio.client.ClientProxy;
import com.bdcom.pojo.BaseTestRecord;
import com.bdcom.pojo.ITesterRecord;
import com.bdcom.pojo.LoginAuth;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-8    <br/>
 * Time: 09:58  <br/>
 */
public class ClientTester0 {
    public static void main(String[] s) {
//        ClientProxy proxy = new ClientProxy();
        for ( int i = 0; i < 100; i++ ) {
        ClientProxy proxy = new ClientProxy("127.0.0.1", 9999);
            testing(proxy);
        }
    }
    public static void testing(ClientProxy proxy) {

        LoginAuth auth = new LoginAuth();
        auth.setUserName("Francis");
        auth.setUserPasswd("Fangyy");

        BaseTestRecord rec0 = new BaseTestRecord();
        ITesterRecord rec1 = new ITesterRecord();

        try {
            //BDPacket pack0 = proxy.sendLoginAuth(auth);
            //printPacket( pack0 );

            BDPacket pack1 = proxy.sendBaseTestRecord( rec0 );
            printPacket( pack1 );

            BDPacket pack2 = proxy.sendITesterRecord( rec1 );
            printPacket( pack2 );

            BDPacket pack3 = proxy.sendRawPacket(BDPacketUtil.terminalRequest() );
            printPacket( pack3 );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void printPacket(BDPacket pack) {
        System.out.println( "version: "+ pack.getVersion() );
        System.out.println( "requestID: " + pack.getRequestID() );
        System.out.println( "DataType: " + pack.getDataType() );
        System.out.println( "Data: " + pack.getData() );
    }
}

