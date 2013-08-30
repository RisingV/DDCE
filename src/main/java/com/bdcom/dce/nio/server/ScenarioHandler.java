package com.bdcom.dce.nio.server;

import com.bdcom.dce.biz.scenario.ScenarioMgr;
import com.bdcom.dce.nio.bdpm.PMInterface;
import com.bdcom.dce.sys.configure.PathConfig;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-15    <br/>
 * Time: 16:55  <br/>
 */
public abstract class ScenarioHandler extends CommonHandler {

    protected static final byte[] SCE_STUFF_LOCK = new byte[0];

    protected ScenarioHandler(PMInterface pmInterface) {
        super(pmInterface);
        this.pmInterface = pmInterface;
    }

    private final PMInterface pmInterface;

    private ScenarioMgr scenarioMgr;

    private String name = "ScenarioMgr";

    protected ScenarioMgr getScenarioMgr() {
        if ( null == scenarioMgr ) {
            synchronized ( SCE_STUFF_LOCK ) {
                if ( null == scenarioMgr ) {
                    scenarioMgr = (ScenarioMgr) pmInterface.getContent(name);
                    if ( null == scenarioMgr ) {
                        PathConfig pathConfig = pmInterface.getPathConfig();
                        scenarioMgr = new ScenarioMgr(pathConfig);
                        pmInterface.addContent(name, scenarioMgr);
                    }
                }
            }
        }
        return scenarioMgr;
    }

}
