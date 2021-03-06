package com.bdcom.dce.nio.server;

import com.bdcom.dce.biz.script.ScriptMgr;
import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.DataType;
import com.bdcom.dce.nio.bdpm.PMInterface;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-15    <br/>
 * Time: 17:41  <br/>
 */
public class ScriptFileListHandler extends ScriptHandler {

    public ScriptFileListHandler(PMInterface pmInterface) {
        super(pmInterface);
    }

    @Override
    protected BDPacket doHandle(BDPacket bdPacket) {
        BDPacket response = BDPacket.newPacket( bdPacket.getRequestID() );
        response.setDataType(DataType.STRING_ARRAY );

        String[] nameList = getFileList();

        boolean writeSuccess = true;
        try {
            response.writeStringArray(nameList);
        } catch (IOException e) {
            writeSuccess = false;
            System.err.println(e.getMessage());
        } finally {
            if ( !writeSuccess ) {
                response.setDataType( DataType.STRING );
                response.setData("get script file list fail!".getBytes());
                return response;
            }
        }

        return response;
    }

    private String[] getFileList() {
        String[] fileNameList = null;
        synchronized ( SCRIPT_STUFF_LOCK ) {
            ScriptMgr scriptMgr = getScriptMgr();
            scriptMgr.reloadScripts();
            File[] fileList = scriptMgr.getScriptConfFiles();
            int len = fileList.length;
            fileNameList = new String[len];
            for (int i = 0; i < len; i++ ) {
                fileNameList[i] = fileList[i].getName();
            }
        }
        return fileNameList;
    }
}
