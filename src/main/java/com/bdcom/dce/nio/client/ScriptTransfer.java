package com.bdcom.dce.nio.client;

import com.bdcom.dce.biz.script.ScriptMgr;
import com.bdcom.dce.nio.exception.GlobalException;
import com.bdcom.dce.nio.exception.ResponseException;
import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.BDPacketUtil;
import com.bdcom.dce.nio.DataType;
import com.bdcom.dce.nio.RequestID;
import com.bdcom.dce.util.SerializeUtil;
import com.bdcom.dce.util.logger.ErrorLogger;

import java.io.File;
import java.io.IOException;

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

    public void upload(ScriptMgr scriptMgr) throws IOException, GlobalException {
        File[] files = scriptMgr.getScriptConfFiles();
        if ( null == files || files.length == 0 ) {
            return;
        }

        try {
            deleteBackupOnServer();
        } catch (InterruptedException e) {
            // mostly not happen
            ErrorLogger.log(e.getMessage());
        }

        UniChannel<BDPacket> responseChan = null;
        int count = 0;
        for ( File file : files ) {
            if ( null == file ) {
                continue;
            }
            BDPacket request = encapsulateUploadReq(file);
            responseChan = client.asyncSend( request );
            count++;
        }

        try {
            for ( int i=0; i < count; i++ ) {
                BDPacket response = responseChan.take();
                BDPacketUtil.globalExceptionCheck(response);
            }
        } catch (InterruptedException e) {
            ErrorLogger.log(e.getMessage());
        }

    }

    public void download(ScriptMgr scriptMgr)
            throws IOException, ResponseException, GlobalException {
        String[] names = null;
        try {
            names = getScriptFileNameList();
        } catch (InterruptedException e) {
            ErrorLogger.log(e.getMessage());
        }

        UniChannel<BDPacket> responseChan = null;
        int count = 0;
        for(String name: names ) {
            if ( null == name ) {
                continue;
            }
            BDPacket req = encapsulateDownloadReq( name );
            responseChan = client.asyncSend( req );
            count++;
        }

        scriptMgr.removeAll();
        try {
            for ( int i = 0; i < count; i++ ) {
                BDPacket response = responseChan.take();
                BDPacketUtil.globalExceptionCheck( response );
                File newFile = scriptMgr.newEmptyScriptConfFile("-"+i+"-");
                writeToFile(response,newFile);
            }
        } catch (InterruptedException e) {
            ErrorLogger.log(e.getMessage());
        }

        scriptMgr.reloadScripts();
    }

    public String[] getScriptFileNameList() throws
            IOException, InterruptedException, ResponseException, GlobalException {

        BDPacket request = BDPacket.newPacket( RequestID.GET_SCRIPT_FILE_LIST );
        BDPacket response = client.send( request );

        String[] names = BDPacketUtil.parseStringArrayResponse(response, request.getRequestID());
        return names;
    }

    public void deleteBackupOnServer()
            throws IOException, InterruptedException, GlobalException {
        BDPacket request = BDPacket.newPacket( RequestID.DELETE_BACKUP_SCRIPTS );
        BDPacket response = client.send( request );
        BDPacketUtil.globalExceptionCheck( response );
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
