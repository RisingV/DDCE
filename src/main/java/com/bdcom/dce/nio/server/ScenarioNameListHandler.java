package com.bdcom.dce.nio.server;

import com.bdcom.dce.biz.scenario.ScenarioMgr;
import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.DataType;
import com.bdcom.dce.nio.bdpm.PMInterface;

import java.io.IOException;
import java.util.Set;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-11    <br/>
 * Time: 16:47  <br/>
 */
public class ScenarioNameListHandler extends ScenarioHandler {

    public ScenarioNameListHandler(PMInterface pmInterface) {
        super(pmInterface);
    }

    @Override
    protected BDPacket doHandle(BDPacket bdPacket) {
        BDPacket response = BDPacket.newPacket( bdPacket.getRequestID() );
        response.setDataType(DataType.STRING_ARRAY );

        String[] nameList = getNameList();

        boolean writeSuccess = true;
        try {
            response.writeStringArray(nameList);
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

    private String[] getNameList() {

        synchronized ( SCE_STUFF_LOCK ) {
            ScenarioMgr scenarioMgr = getScenarioMgr();
            scenarioMgr.reloadScenarios();
            Set<String> nameList = scenarioMgr.getScenarioNameList();
            String[] strArray = new String[nameList.size()];
            strArray = nameList.toArray(strArray);
            return strArray;
        }

    }

}
