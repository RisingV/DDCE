package com.bdcom.nio.client;

import com.bdcom.exception.ResponseException;
import com.bdcom.nio.BDPacket;
import com.bdcom.nio.BDPacketUtil;
import com.bdcom.nio.DataType;
import com.bdcom.nio.RequestID;
import com.bdcom.util.log.ErrorLogger;
import com.bdcom.service.script.ScriptMgr;
import com.bdcom.util.SerializeUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-12    <br/>
 * Time: 13:51  <br/>
 */
public class ScriptTransfer {

    private final ClientWrapper client;

    public ScriptTransfer(ClientWrapper client) {
        this.client = client;
    }

    public void upload(ScriptMgr scriptMgr) throws IOException {
        File[] files = scriptMgr.getScriptConfFiles();
        if ( null == files || files.length == 0 ) {
            return;
        }

        BlockingQueue<BDPacket> responseQueue = null;
        int count = 0;
        for ( File file : files ) {
            if ( null == file ) {
                continue;
            }
            BDPacket request = encapsulateUploadReq(file);
            responseQueue = client.asyncSend( request );
            count++;
        }

        try {
            for ( int i=0; i < count; i++ ) {
                responseQueue.take();
            }
        } catch (InterruptedException e) {
            ErrorLogger.log(e.getMessage());
        }

    }

    public void download(ScriptMgr scriptMgr) throws IOException, ResponseException {
        String[] names = null;
        try {
            names = getScriptFileNameList();
        } catch (InterruptedException e) {
            ErrorLogger.log(e.getMessage());
        }

        BlockingQueue<BDPacket> responseQueue = null;
        int count = 0;
        for(String name: names ) {
            if ( null == name ) {
                continue;
            }
            BDPacket req = encapsulateDownloadReq( name );
            responseQueue = client.asyncSend( req );
            count++;
        }

        try {
            for ( int i = 0; i < count; i++ ) {
                BDPacket response = responseQueue.take();
                File newFile = scriptMgr.newEmptyScriptConfFile("-"+i+"-");
                writeToFile(response,newFile);
            }
        } catch (InterruptedException e) {
            ErrorLogger.log(e.getMessage());
        }

        scriptMgr.reloadScripts();
    }

    public String[] getScriptFileNameList() throws IOException, InterruptedException, ResponseException {
        BDPacket request = BDPacket.newPacket( RequestID.GET_SCRIPT_FILE_LIST );
        BDPacket response = client.send( request );

        String[] names = BDPacketUtil.parseStringArrayResponse(response, request.getRequestID());
        return names;
    }


    private BDPacket encapsulateUploadReq(File file) throws IOException {
        byte[] data = SerializeUtil.fileToByteArray(file);

        BDPacket pack = BDPacket.newPacket( RequestID.UPLOAD_SCRIPT );
        pack.setDataType(DataType.FILE );
        pack.setData(data);

        return pack;
    }

    private BDPacket encapsulateDownloadReq(String name) {
        byte[] data = name.getBytes();

        BDPacket pack = BDPacket.newPacket( RequestID.DOWNLOAD_SCRIPT );
        pack.setDataType(DataType.STRING);
        pack.setData( data );

        return pack;
    }

    private void writeToFile(BDPacket pack, File file) throws IOException, ResponseException {
        if ( pack.getDataType() == DataType.FILE ) {
            SerializeUtil.byteArrayToFile(pack.getData(), file);
        } else if ( DataType.STRING == pack.getDataType() ) {
            throw new ResponseException( new String(pack.getData()) );
        } else {
            throw new ResponseException( "Invalid Data Type!" );
        }
    }

}
