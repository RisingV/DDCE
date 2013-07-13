package com.bdcom.nio.server;

import com.bdcom.nio.BDPacket;
import com.bdcom.nio.BDPacketUtil;
import com.bdcom.nio.DataType;
import com.bdcom.nio.ServerContent;
import com.bdcom.pojo.Scenario;
import com.bdcom.service.scenario.ScenarioMgr;
import com.bdcom.util.SerializeUtil;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-11    <br/>
 * Time: 17:31  <br/>
 */
public class ScenarioDownloadHandler extends CommonHandler {
    @Override
    protected BDPacket doHandle(BDPacket bdPacket) {
        int requestID = bdPacket.getRequestID();
        ScenarioMgr scenarioMgr = ServerContent.getScenarioMgr();
        BDPacket response = null;
        if (DataType.STRING == bdPacket.getDataType() ) {
            String name = new String(bdPacket.getData());
            Scenario scenario = scenarioMgr.getScenarioByName( name );
            response = encapsulateToPacket( scenario, requestID, name);
        } else {
            response = BDPacket.newPacket( requestID );
            response = BDPacketUtil.writeStringMsg(response, "Invalid Request!" );
        }

        return response;
    }

    private BDPacket encapsulateToPacket( Scenario scenario , int requestID, String name) {
        BDPacket response = BDPacket.newPacket( requestID );
        if ( null == scenario ) {
            StringBuilder sb = new StringBuilder();
            sb.append("can't find scenario named: ").append( name );

            response = BDPacketUtil.writeStringMsg( response, sb.toString() );
        } else {
            boolean serialSuccess = true;
            try {
                SerializeUtil.serializeToByteArray( scenario );
            } catch (IOException e) {
                serialSuccess = false;
                System.err.println( e.getMessage() );
            } finally {
                if ( !serialSuccess ) {
                    response = BDPacketUtil.writeStringMsg( response, "Inner Server Error!" );
                    return response;
                }
            }
        }

        return response;
    }
}
