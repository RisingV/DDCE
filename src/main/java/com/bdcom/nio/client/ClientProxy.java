package com.bdcom.nio.client;

import com.bdcom.exception.LoginException;
import com.bdcom.exception.ResponseException;
import com.bdcom.nio.BDPacket;
import com.bdcom.nio.BDPacketUtil;
import com.bdcom.nio.RequestID;
import com.bdcom.pojo.BaseTestRecord;
import com.bdcom.pojo.ITesterRecord;
import com.bdcom.pojo.LoginAuth;
import com.bdcom.service.scenario.ScenarioMgr;
import com.bdcom.util.log.ErrorLogger;
import com.bdcom.service.script.ScriptMgr;

import java.io.IOException;

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

    public ClientProxy(String ip, int port) {
        client = new ClientWrapper(ip, port);
        scenarioTransfer = new ScenarioTransfer(client);
        scriptTransfer = new ScriptTransfer(client);
    }

    public BDPacket sendRawPacket(BDPacket packet) throws IOException, InterruptedException {
        return client.send( packet );
    }

    public int sendLoginAuth(LoginAuth auth) throws IOException, LoginException {
        int status = -1;
        try {
            BDPacket packet = BDPacketUtil.encapsulateToPacket(auth);
            BDPacket response = client.send(packet);

            status = BDPacketUtil.parseIntResponse(response, RequestID.LOGIN);
        } catch (InterruptedException e) {
            ErrorLogger.log(e.getMessage());
        } catch (ResponseException e) {
            throw new LoginException( e.getMessage() );
        }

        return status;
    }

    public int sendBaseTestRecord(BaseTestRecord record) throws IOException, ResponseException {
        int status = -1;
        try {
            BDPacket packet = BDPacketUtil.encapsulateToPacket(record);
            BDPacket response = client.send(packet);
            status = BDPacketUtil.parseIntResponse( response, RequestID.SEND_BASE_TEST_REC );
        } catch (InterruptedException e) {
            ErrorLogger.log(e.getMessage());
        }

        return status;
    }

    public BDPacket sendITesterRecord(ITesterRecord record) throws IOException, InterruptedException {
        BDPacket packet = BDPacketUtil.encapsulateToPacket(record);
        return client.send(packet);
    }

    public void uploadScenarios(ScenarioMgr scenarioMgr) throws IOException {
        scenarioTransfer.upload(scenarioMgr);
    }

    public void downloadScenarios(ScenarioMgr scenarioMgr) throws IOException, ResponseException {
        scenarioTransfer.download(scenarioMgr);
    }

    public void uploadScriptConfig(ScriptMgr scriptMgr) throws IOException {
        scriptTransfer.upload(scriptMgr);
    }

    public void downloadScriptConfig(ScriptMgr scriptMgr) throws IOException, ResponseException {
        scriptTransfer.download(scriptMgr);
    }

    public void shutdown() {
        client.shutdown();
    }

}
