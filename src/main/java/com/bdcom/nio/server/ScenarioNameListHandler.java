package com.bdcom.nio.server;

import com.bdcom.nio.BDPacket;
import com.bdcom.nio.DataType;
import com.bdcom.nio.ServerContent;
import com.bdcom.service.scenario.ScenarioMgr;

import java.io.IOException;
import java.util.Set;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-11    <br/>
 * Time: 16:47  <br/>
 */
public class ScenarioNameListHandler extends CommonHandler {
    @Override
    protected BDPacket doHandle(BDPacket bdPacket) {

        ScenarioMgr scenarioMgr = ServerContent.getScenarioMgr();
        Set<String> nameLst = scenarioMgr.getScenarioNameList();
        String[] strArray = new String[nameLst.size()];
        strArray = nameLst.toArray(strArray);

        BDPacket response = BDPacket.newPacket( bdPacket.getRequestID() );
        response.setDataType(DataType.STRING_ARRAY );

        boolean writeSuccess = true;
        try {
            response.writeStringArray(strArray);
        } catch (IOException e) {
            writeSuccess = false;
            System.err.println(e.getMessage());
        } finally {
            if ( !writeSuccess ) {
                response.setDataType( DataType.STRING );
                response.setData("get scenario name list fail!".getBytes());
                return response;
            }
        }

        return response;
    }
}
