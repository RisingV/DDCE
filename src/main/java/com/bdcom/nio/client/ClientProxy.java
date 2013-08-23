package com.bdcom.nio.client;

import com.bdcom.biz.pojo.BaseTestRecord;
import com.bdcom.biz.pojo.ITesterRecord;
import com.bdcom.biz.pojo.LoginAuth;
import com.bdcom.biz.scenario.ScenarioMgr;
import com.bdcom.biz.script.ScriptMgr;
import com.bdcom.nio.BDPacket;
import com.bdcom.nio.BDPacketUtil;
import com.bdcom.nio.DataType;
import com.bdcom.nio.RequestID;
import com.bdcom.nio.exception.GlobalException;
import com.bdcom.nio.exception.ResponseException;
import com.bdcom.sys.config.ServerConfig;
import com.bdcom.util.SerializeUtil;
import com.bdcom.util.log.ErrorLogger;

import java.io.IOException;
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

    public ClientProxy(ServerConfig serverConfig) {
        client = new ClientWrapper(serverConfig);
        scenarioTransfer = new ScenarioTransfer(client);
        scriptTransfer = new ScriptTransfer(client);
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
        TimeoutWrapper timeoutWrapper = new TimeoutWrapper(client);
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
