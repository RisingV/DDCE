package com.bdcom.nio.server;

import com.bdcom.nio.ServerContent;
import com.bdcom.sys.config.PathConfig;
import com.bdcom.biz.scenario.ScenarioMgr;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-15    <br/>
 * Time: 16:55  <br/>
 */
public abstract class ScenarioHandler extends CommonHandler {

    private ScenarioMgr scenarioMgr;

    private String name = "ScenarioMgr";

    protected ScenarioMgr getScenarioMgr() {
        if ( null == scenarioMgr ) {
            synchronized (ServerContent.GLOBAL_LOCK0) {
                if ( null == scenarioMgr ) {
                    scenarioMgr = (ScenarioMgr) ServerContent.getContent(name);
                    if ( null == scenarioMgr ) {
                        PathConfig pathConfig = ServerContent.getPathConfig();
                        scenarioMgr = new ScenarioMgr(pathConfig);
                        ServerContent.addContent(name, scenarioMgr);
                    }
                }
            }
        }
        return scenarioMgr;
    }

}
