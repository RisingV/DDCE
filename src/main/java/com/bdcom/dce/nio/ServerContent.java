package com.bdcom.dce.nio;

import com.bdcom.dce.biz.pojo.BaseTestRecord;
import com.bdcom.dce.biz.pojo.ITesterRecord;
import com.bdcom.dce.biz.pojo.LoginAuth;
import com.bdcom.dce.sys.config.PathConfig;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: francis
 * Date: 13-7-4
 * Time: 下午2:58
 */
public abstract class ServerContent {

    public static final byte[] GLOBAL_LOCK0 = new byte[0];
    public static final byte[] GLOBAL_LOCK1 = new byte[0];

    public static final String BASE_TEST = System.getProperty( "user.dir" )
            + File.separator + "Base_Test_Storage";

    private static Map<String, Object> content = new ConcurrentHashMap<String, Object>();

    private static PathConfig pathConfig;

    public synchronized static PathConfig getPathConfig() {
       if ( null == pathConfig ) {
           pathConfig = new PathConfig(BASE_TEST);
       }
       return pathConfig;
    }

    public static void addContent(String name, Object obj) {
        content.put(name, obj);
    }

    public static Object getContent(String name) {
        return content.get( name );
    }

    public static int getNIOServerPort() {
        //TODO
        return 9999;
    }

    public static int getRunningApplicationVersion() {
        //TODO
        return 1;
    }

    public static int login(LoginAuth auth) {
        //TODO
        return 2;
    }

    public static int SaveBaseTestRecord(BaseTestRecord record) {
        //TODO
        return 1;
    }

    public static ITesterRecord SaveITesterRecord(ITesterRecord record) {
        if ( ITesterRecord.CHECK_WORK_ORDER == record.getType() ) {
            record.setEverTested( false );
            record.setWorkOrderValid( true );
            //TODO
            //check if work order is valid or has tested
            //and set record status;
        } else if ( ITesterRecord.COMMIT_TEST_RESULT == record.getType() ) {
            //TODO
            //save record
        }
        return record;
    }

}