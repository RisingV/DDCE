package com.bdcom.nio;

import com.bdcom.pojo.BaseTestRecord;
import com.bdcom.pojo.ITesterRecord;
import com.bdcom.pojo.LoginAuth;
import com.bdcom.service.scenario.ScenarioMgr;

/**
 * Created with IntelliJ IDEA.
 * User: francis
 * Date: 13-7-4
 * Time: 下午2:58
 */
public abstract class ServerContent {

    private static ScenarioMgr scenarioMgr;

    public static int getNIOServerPort() {
        //TODO
        return 9999;
    }

    public static int getRunningApplicationVersion() {
        //TODO
        return 1;
    }

    public static int login(LoginAuth auth) {
        //TODO
        return 1;
    }

    public static int SaveBaseTestRecord(BaseTestRecord record) {
        //TODO
        return 1;
    }

    public static int SaveITesterRecord(ITesterRecord record) {
        //TODO
        return 1;
    }

    public static ScenarioMgr getScenarioMgr() {
        if ( null == scenarioMgr ) {
            synchronized ( ServerContent.class ) {
                if ( null == scenarioMgr ) {
                    scenarioMgr = new ScenarioMgr();
                    scenarioMgr.reloadScenarios();
                }
            }
        }
        return scenarioMgr;
    }

}
