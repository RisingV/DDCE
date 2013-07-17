package com.bdcom.nio.server;

import com.bdcom.nio.ServerContent;
import com.bdcom.sys.config.PathConfig;
import com.bdcom.biz.script.ScriptMgr;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-15    <br/>
 * Time: 17:17  <br/>
 */
public abstract class ScriptHandler extends CommonHandler {

    protected static boolean needsRebuild = true;

    private String name = "scriptMgr";

    private ScriptMgr scriptMgr;

    protected ScriptMgr getScriptMgr() {
        if ( null == scriptMgr ) {
            synchronized (ServerContent.GLOBAL_LOCK1) {
                if ( null == scriptMgr ) {
                    scriptMgr = (ScriptMgr) ServerContent.getContent(name);
                    if ( null == scriptMgr ) {
                        PathConfig pathConfig = ServerContent.getPathConfig();
                        scriptMgr = new ScriptMgr( pathConfig );
                        ServerContent.addContent(name, scriptMgr);
                    }
                }
            }
        }
        return scriptMgr;
    }

}
