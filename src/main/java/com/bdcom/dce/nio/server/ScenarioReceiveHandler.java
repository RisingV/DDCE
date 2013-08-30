package com.bdcom.dce.nio.server;

import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.BDPacketUtil;
import com.bdcom.dce.nio.ServerContent;
import com.bdcom.dce.biz.pojo.Scenario;
import com.bdcom.dce.biz.scenario.ScenarioMgr;
import com.bdcom.dce.util.SerializeUtil;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-11    <br/>
 * Time: 15:34  <br/>
 */
public class ScenarioReceiveHandler extends ScenarioHandler {

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
        synchronized ( ServerContent.GLOBAL_LOCK0 ) {
            getScenarioMgr().addScenario(scenario);
        }
    }

}
