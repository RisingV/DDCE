package com.bdcom.nio.server;

import com.bdcom.nio.exception.ResponseException;
import com.bdcom.nio.BDPacket;
import com.bdcom.nio.DataType;
import com.bdcom.nio.ServerContent;
import com.bdcom.biz.script.ScriptMgr;
import com.bdcom.util.SerializeUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-16    <br/>
 * Time: 09:42  <br/>
 */
public class ScriptDownloadHandler extends ScriptHandler {

    private Map<String, File> nfMap = new HashMap<String, File>();

    @Override
    protected BDPacket doHandle(BDPacket bdPacket) {
        rebuildNameFileMapping();

        int reqID = bdPacket.getRequestID();
        BDPacket response = null;
        boolean handleSuccess = true;
        String msg = null;

        try {
            response = handleReq( bdPacket );
        } catch (ResponseException e) {
            handleSuccess = false;
            msg = e.getMessage();
            System.err.println("Request ID: "+ reqID + " ResponseException: " + msg );
        } catch (IOException e) {
            handleSuccess = false;
            msg = "Inner Server Error";
            System.err.println(e.getMessage());
        } finally {
            if ( !handleSuccess ) {
                response = BDPacket.newPacket(reqID);
                response.setDataType( DataType.STRING );
                response.setData( msg.getBytes() );
                return response;
            }
        }

        return response;
    }

    private BDPacket handleReq(BDPacket pack) throws ResponseException, IOException {
        BDPacket response = null;
        if (DataType.STRING == pack.getDataType() ) {
            String name = new String( pack.getData() );
            File f = nfMap.get(name);
            if ( null != f) {
                byte[] data = SerializeUtil.fileToByteArray(f);

                response = BDPacket.newPacket( pack.getRequestID() );
                response.setDataType( DataType.FILE );
                response.setData(data);
            } else {
                throw new ResponseException("Can't find File with name: " + name);
            }
        } else {
            throw new ResponseException("Invalid Data Type!");
        }

        return response;
    }

    private void rebuildNameFileMapping() {
        if ( needsRebuild ) {
            synchronized ( ServerContent.GLOBAL_LOCK1 ) {
                ScriptMgr scriptMgr = getScriptMgr();
                File[] files = scriptMgr.getScriptConfFiles();

                nfMap.clear();
                for ( File f : files ) {
                    if ( null != f ) {
                        nfMap.put( f.getName(), f );
                    }
                }
            }
        }
    }
}
