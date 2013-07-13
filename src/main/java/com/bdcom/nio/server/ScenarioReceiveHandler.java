package com.bdcom.nio.server;

import com.bdcom.nio.BDPacket;
import com.bdcom.nio.BDPacketUtil;
import com.bdcom.nio.ServerContent;
import com.bdcom.pojo.Scenario;
import com.bdcom.service.scenario.ScenarioMgr;
import com.bdcom.util.SerializeUtil;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-11    <br/>
 * Time: 15:34  <br/>
 */
public class ScenarioReceiveHandler extends CommonHandler {

    private static byte[] lock = new byte[0];

    private static ScenarioMgr scenarioMgr;

    @Override
    protected BDPacket doHandle(BDPacket bdPacket) {
        Scenario sce = null;
        boolean deserialSuccess = true;
        try {
            sce = (Scenario) SerializeUtil.deserializeFromByteArray(bdPacket.getData());
        } catch (IOException e) {
            deserialSuccess = false;
            System.err.println( e.getMessage() );
        } catch (ClassNotFoundException e) {
            deserialSuccess = false;
            System.err.println( e.getMessage() );
        } finally {
            if ( !deserialSuccess ) {
                return BDPacketUtil.responseToInvalidData(bdPacket.getRequestID()) ;
            }
        }

        save(sce);
        return BDPacketUtil.emptyResponse( bdPacket.getRequestID() );
    }

    private void save(Scenario scenario) {
        synchronized ( lock ) {
            if ( null == scenarioMgr ) {
                scenarioMgr = ServerContent.getScenarioMgr();
                scenarioMgr.reloadScenarios();
            }
            scenarioMgr.addScenario(scenario);
        }
    }

}
