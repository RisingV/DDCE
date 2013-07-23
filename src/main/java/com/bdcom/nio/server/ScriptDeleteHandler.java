package com.bdcom.nio.server;

import com.bdcom.biz.script.ScriptMgr;
import com.bdcom.nio.BDPacket;
import com.bdcom.nio.BDPacketUtil;
import com.bdcom.nio.ServerContent;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-18    <br/>
 * Time: 14:41  <br/>
 */
public class ScriptDeleteHandler extends ScriptHandler {

    @Override
    protected BDPacket doHandle(BDPacket bdPacket) {
        deleteAllScripts();
        return BDPacketUtil.emptyResponse( bdPacket.getRequestID() );
    }

    private void deleteAllScripts() {
        ScriptMgr scriptMgr = getScriptMgr();
        synchronized (ServerContent.GLOBAL_LOCK1 ) {
            scriptMgr.reloadScripts();
            scriptMgr.removeAll();
        }
    }

}
