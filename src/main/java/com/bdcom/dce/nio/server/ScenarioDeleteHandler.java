package com.bdcom.dce.nio.server;

import com.bdcom.dce.biz.scenario.ScenarioMgr;
import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.BDPacketUtil;
import com.bdcom.dce.nio.bdpm.PMInterface;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-18    <br/>
 * Time: 14:33  <br/>
 */
public class ScenarioDeleteHandler extends ScenarioHandler {

    public ScenarioDeleteHandler(PMInterface pmInterface) {
        super(pmInterface);
    }

    @Override
    protected BDPacket doHandle(BDPacket bdPacket) {
        deleteAllScenarios();
        return BDPacketUtil.emptyResponse( bdPacket.getRequestID() );
    }

    private void deleteAllScenarios() {
        ScenarioMgr scenarioMgr = getScenarioMgr();
        synchronized ( SCE_STUFF_LOCK ) {
            scenarioMgr.reloadScenarios();
            scenarioMgr.removeAll();
        }
    }

}
