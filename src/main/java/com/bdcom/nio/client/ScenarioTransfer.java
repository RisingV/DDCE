package com.bdcom.nio.client;

import com.bdcom.exception.ResponseException;
import com.bdcom.nio.BDPacket;
import com.bdcom.nio.BDPacketUtil;
import com.bdcom.nio.DataType;
import com.bdcom.nio.RequestID;
import com.bdcom.biz.pojo.Scenario;
import com.bdcom.biz.scenario.ScenarioMgr;
import com.bdcom.util.log.ErrorLogger;
import com.bdcom.util.SerializeUtil;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-11    <br/>
 * Time: 15:00  <br/>
 */
public class ScenarioTransfer {

    private final ClientWrapper client;

    public ScenarioTransfer(ClientWrapper client) {
        this.client = client;
    }

    public void upload(ScenarioMgr scenarioMgr) throws IOException {
        Set<String> nameSet = scenarioMgr.getScenarioNameList();
        BlockingQueue<BDPacket> responseQueue = null;
        for(String name : nameSet ) {
            Scenario sce = scenarioMgr.getScenarioByName(name);
            BDPacket pack = encapsulateUploadReq(sce);
            responseQueue = client.asyncSend(pack);
        }

        int responseNum = nameSet.size();
        try {
            for ( int i = 0; responseNum > i; i++ ) {
                responseQueue.take();
            }
        } catch (InterruptedException e) {
            ErrorLogger.log(e.getMessage());
        }
    }

    public void download(ScenarioMgr scenarioMgr) throws IOException, ResponseException {
        String[] names = null;
        BlockingQueue<BDPacket> responseQueue = null;
        int num = 0;

        try {
            names = getScenarioNameList();
        } catch (InterruptedException e) {
            //almost not happen!
            ErrorLogger.log(e.getMessage());
        }
        num = names.length;
        for ( int i = 0; i < num; i++ ) {
            BDPacket request = encapsulateDownloadReq(names[i]);
            responseQueue = client.asyncSend(request);
        }

        Scenario[] scenarios = new Scenario[num];
        try {
            for (int i = 0; i < num; i++ ) {
                BDPacket pack = responseQueue.take();
                scenarios[i] = unpack( pack );
            }
        } catch (InterruptedException e) {
            //almost not happen!
            ErrorLogger.log(e.getMessage());
        }

        scenarioMgr.removeAll();
        for ( Scenario sce : scenarios ) {
            scenarioMgr.addScenario( sce );
        }
    }

    public String[] getScenarioNameList() throws IOException, InterruptedException, ResponseException {
        BDPacket request = BDPacket.newPacket( RequestID.GET_SCENARIO_NAME_LIST );
        BDPacket response = client.send( request );

        String[] names = BDPacketUtil.parseStringArrayResponse(response, request.getRequestID());
        return names;
    }

    private BDPacket encapsulateUploadReq(Scenario scenario) throws IOException {
        if ( null == scenario ) {
            return null;
        }

        byte[] data = SerializeUtil.serializeToByteArray(scenario);

        BDPacket pack = BDPacket.newPacket( RequestID.UPLOAD_SCENARIO );
        pack.setDataType( DataType.SCENARIO );
        pack.setData( data );

        return pack;
    }

    private Scenario unpack(BDPacket packet) throws ResponseException, IOException {
        if ( null == packet ) {
            return null;
        }

        if ( RequestID.UPLOAD_SCENARIO != packet.getRequestID() ) {
            throw new ResponseException("Response with wrong requestID!");
        }

        Scenario scenario = null;
        if ( DataType.SCENARIO == packet.getDataType() ) {
            try {
                scenario = (Scenario) SerializeUtil
                        .deserializeFromByteArray(packet.getData());
            } catch (ClassNotFoundException e) {
                //mostly not happen
                ErrorLogger.log(e.getMessage());
            }
        } else if ( DataType.STRING == packet.getDataType() ) {
            throw new ResponseException( new String( packet.getData() ) );
        } else {
            throw new ResponseException( "Invalid Data Type!" );
        }

        return scenario;
    }

    private BDPacket encapsulateDownloadReq(String name) {
        if ( null == name ) {
            return null;
        }

        BDPacket pack = BDPacket.newPacket( RequestID.DOWNLOAD_SCENARIO );
        pack.setDataType( DataType.STRING );
        pack.setData( name.getBytes() );

        return pack;
    }

}
