package com.bdcom.dce.nio.server;

import com.bdcom.dce.biz.script.ScriptMgr;
import com.bdcom.dce.nio.bdpm.PMInterface;
import com.bdcom.dce.sys.configure.PathConfig;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-15    <br/>
 * Time: 17:17  <br/>
 */
public abstract class ScriptHandler extends CommonHandler {

    protected static final byte[] SCRIPT_STUFF_LOCK = new byte[0];

    protected static boolean needsRebuild = true;

    private  final PMInterface pmInterface;

    protected ScriptHandler(PMInterface pmInterface) {
        super(pmInterface);
        this.pmInterface = pmInterface;
    }

    private String name = "scriptMgr";

    private ScriptMgr scriptMgr;

    protected ScriptMgr getScriptMgr() {
        if ( null == scriptMgr ) {
            synchronized ( SCRIPT_STUFF_LOCK ) {
                if ( null == scriptMgr ) {
                    scriptMgr = (ScriptMgr) pmInterface.getContent(name);
                    if ( null == scriptMgr ) {
                        PathConfig pathConfig = pmInterface.getPathConfig();
                        scriptMgr = new ScriptMgr( pathConfig );
                        pmInterface.addContent(name, scriptMgr);
                    }
                }
            }
        }
        return scriptMgr;
    }

}
