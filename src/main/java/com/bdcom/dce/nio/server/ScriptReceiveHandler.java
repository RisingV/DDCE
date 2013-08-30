package com.bdcom.dce.nio.server;

import com.bdcom.dce.nio.exception.ResponseException;
import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.BDPacketUtil;
import com.bdcom.dce.nio.DataType;
import com.bdcom.dce.nio.ServerContent;
import com.bdcom.dce.biz.script.ScriptMgr;
import com.bdcom.dce.util.SerializeUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-15    <br/>
 * Time: 15:37  <br/>
 */
public class ScriptReceiveHandler extends ScriptHandler {

    @Override
    protected BDPacket doHandle(BDPacket bdPacket) {
        boolean saveSuccess = true;
        String msg = null;
        try {
            parseAndSave(bdPacket);
        } catch (IOException e) {
            saveSuccess = false;
            System.err.println(e.getMessage());
            msg = "Write to server fail!";
        } catch (ResponseException e) {
            saveSuccess = false;
            msg = e.getMessage();
        } finally {
            if ( !saveSuccess ) {
                BDPacket response = BDPacket.newPacket(bdPacket.getRequestID());
                response.setDataType( DataType.STRING );
                response.setData(msg.getBytes());
                return  response;
            }
        }
        return BDPacketUtil.emptyResponse(bdPacket.getRequestID());
    }

    private void parseAndSave(BDPacket pack) throws IOException, ResponseException {

        if ( DataType.FILE == pack.getDataType() ) {
            synchronized (ServerContent.GLOBAL_LOCK1 ) {
                long ms = System.currentTimeMillis();
                ScriptMgr scriptMgr = getScriptMgr();
                File fileToSave = scriptMgr.newEmptyScriptConfFile(String.valueOf(ms));
                SerializeUtil.byteArrayToFile(pack.getData(), fileToSave );
                needsRebuild = true;
            }
        } else {
            throw new ResponseException("Invalid Data Type");
        }

    }
}
