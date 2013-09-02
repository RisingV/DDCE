package com.bdcom.dce.nio.bdpm;

import com.bdcom.dce.biz.pojo.BaseTestRecord;
import com.bdcom.dce.biz.pojo.ITesterRecord;
import com.bdcom.dce.biz.pojo.LoginAuth;
import com.bdcom.dce.sys.configure.PathConfig;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-8-30    <br/>
 * Time: 11:07  <br/>
 *
 * this class is just a compatible stub
 */
public class FakeTestInterface implements PMInterface {

    public static final String BASE_TEST = System.getProperty( "user.dir" )
            + File.separator + "Base_Test_Storage";

    private Map<String, Object> content = new ConcurrentHashMap<String, Object>();

    private PathConfig pathConfig;

    @Override
    public PathConfig getPathConfig() {
        synchronized ( this ) {
            if ( null == pathConfig ) {
                pathConfig = new PathConfig(BASE_TEST);
            }
            return pathConfig;
        }
    }

    @Override
    public void addContent(String name, Object obj) {
        content.put(name, obj);
    }

    @Override
    public Object getContent(String name) {
        return content.get( name );
    }

    @Override
    public int getNIOServerPort() {
        return 9999;
    }

    @Override
    public void initApplicationVersion() {
        //version of BDPacket set here
    }

    @Override
    public int getRunningApplicationVersion() {
        return 40;
    }

    @Override
    public int login(LoginAuth auth) {
        return 2;
    }

    @Override
    public int saveBaseTestRecord(BaseTestRecord record) {
        return 1;
    }

    @Override
    public ITesterRecord handlerITesterRecord(ITesterRecord record) {
        if ( ITesterRecord.CHECK_WORK_ORDER == record.getType() ) {
            record.setEverTested( false );
            record.setWorkOrderValid( true );
            //check if work order is valid or has tested
            //and set record status;
        } else if ( ITesterRecord.COMMIT_TEST_RESULT == record.getType() ) {
            //save record
        }
        return record;
    }

}
