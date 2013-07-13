package just.foor;

import com.bdcom.nio.server.ServerStarter;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-8    <br/>
 * Time: 09:57  <br/>
 */
public class ServerTester {
    public static void main(String...s) {
        new Thread( new ServerStarter() ).start();
    }
}
