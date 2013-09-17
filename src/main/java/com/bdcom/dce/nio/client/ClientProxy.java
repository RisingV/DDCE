package com.bdcom.dce.nio.client;

import com.bdcom.dce.biz.pojo.BaseTestRecord;
import com.bdcom.dce.biz.pojo.ITesterRecord;
import com.bdcom.dce.biz.pojo.LoginAuth;
import com.bdcom.dce.biz.scenario.ScenarioMgr;
import com.bdcom.dce.biz.script.ScriptMgr;
import com.bdcom.dce.biz.storage.Item;
import com.bdcom.dce.biz.storage.StorableMgr;
import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.BDPacketUtil;
import com.bdcom.dce.nio.DataType;
import com.bdcom.dce.nio.RequestID;
import com.bdcom.dce.nio.exception.GlobalException;
import com.bdcom.dce.nio.exception.ResponseException;
import com.bdcom.dce.sys.configure.ServerConfig;
import com.bdcom.dce.util.SerializeUtil;
import com.bdcom.dce.util.logger.ErrorLogger;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-8    <br/>
 * Time: 09:32  <br/>
 */
public class ClientProxy {

    ClientWrapper client;
    ScenarioTransfer scenarioTransfer;
    ScriptTransfer scriptTransfer;
    StorageTransfer storageTransfer;
    TimeoutWrapper timeoutWrapper;

    public ClientProxy(ServerConfig serverConfig) {
        client = new ClientWrapper(serverConfig);
        scenarioTransfer = new ScenarioTransfer(client);
        scriptTransfer = new ScriptTransfer(client);
        timeoutWrapper = new TimeoutWrapper(client);
        storageTransfer = new StorageTransfer( client );
    }

    public ServerConfig getServerConfig() {
        return client.getServerConfig();
    }

    public BDPacket sendRawPacket(BDPacket packet) throws IOException, GlobalException {
        return client.send( packet );
    }

    public int sendLoginAuth(LoginAuth auth)
            throws IOException, ResponseException, GlobalException, TimeoutException {
        int status = -1;
        try {
            BDPacket packet = BDPacketUtil.encapsulateToPacket(auth);
            BDPacket response = timeoutWrapper.send(packet, 2);

            status = BDPacketUtil.parseIntResponse(response, RequestID.LOGIN);
        } catch (InterruptedException e) {
            ErrorLogger.log(e.getMessage());
        } catch (ResponseException e) {
            throw new ResponseException( e.getMessage() );
        }

        return status;
    }

    public String getCompleteSerial(String serial) throws IOException, GlobalException {
        BDPacket request = BDPacket.newPacket( RequestID.GET_COMPLETE_SERIAL );
        request.setDataType( DataType.STRING );
        request.setData( serial.getBytes() );

        BDPacket response = client.send( request );
        serial = new String( response.getData() );

        return serial;
    }

    public void uploadResource(StorableMgr mgr) throws IOException, GlobalException {
        Item[] itemSet = mgr.getAll();
        Map<String, Item> itemMap = new HashMap<String, Item>(itemSet.length);
        for ( Item i : itemSet ) {
            if ( null != i ) {
                itemMap.put( i.getSerial(), i );
            }
        }

        storageTransfer.uploadStorage( itemMap );
    }

    public void downloadResource(StorableMgr mgr) throws IOException, GlobalException {
        Map<String, Item> itemMap = storageTransfer.downloadStorage();
        Collection<Item> c = itemMap.values();
        for ( Item i : c ) {
            mgr.addItem( i );
        }
    }

    public boolean resourceMD5Check(StorableMgr mgr) throws IOException,
            GlobalException, ResponseException {
        String localMd5 = mgr.getMD5String();
        byte[] data = localMd5.getBytes();

        BDPacket request = BDPacket.newPacket( RequestID.STORAGE_MD5_CHECK );
        request.setDataType( DataType.STRING );
        request.setData( data );

        BDPacket response = client.send( request );
        int isSame = BDPacketUtil.parseIntResponse( response, request.getRequestID() );
        return isSame == 0;
    }

    public void checkAndDownloadResource(StorableMgr mgr) throws GlobalException,
            IOException, ResponseException {
        if ( !resourceMD5Check( mgr ) ) {
            downloadResource( mgr );
        }
    }

    public int sendBaseTestRecord(BaseTestRecord record)
            throws IOException, ResponseException, GlobalException {

        BDPacket packet = BDPacketUtil.encapsulateToPacket(record);
        BDPacket response = client.send(packet);
        return BDPacketUtil.parseIntResponse( response, RequestID.SEND_BASE_TEST_REC );
    }

    public ITesterRecord sendITesterRecord(ITesterRecord record) throws IOException,
            ResponseException, GlobalException {
        BDPacket packet = BDPacketUtil.encapsulateToPacket(record);
        BDPacket response = client.send( packet );
        if ( DataType.I_TESTER_RECORD !=  response.getDataType() ) {
            throw new ResponseException("Invalid Response!");
        }
        ITesterRecord resObj = null;
        try {
            resObj = (ITesterRecord) SerializeUtil.deserializeFromByteArray(response.getData());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return resObj;
    }

    public void uploadScenarios(ScenarioMgr scenarioMgr) throws IOException, GlobalException {
        scenarioTransfer.upload(scenarioMgr);
    }

    public void downloadScenarios(ScenarioMgr scenarioMgr)
            throws IOException, ResponseException, GlobalException {
        scenarioTransfer.download(scenarioMgr);
    }

    public void uploadScriptConfig(ScriptMgr scriptMgr) throws IOException, GlobalException {
        scriptTransfer.upload(scriptMgr);
    }

    public void downloadScriptConfig(ScriptMgr scriptMgr)
            throws IOException, ResponseException, GlobalException {
        scriptTransfer.download(scriptMgr);
    }

    public void shutdown() {
        client.shutdown();
    }

}
