package com.bdcom.dce.nio.bdpm;

import com.bdcom.dce.biz.pojo.BaseTestRecord;
import com.bdcom.dce.biz.pojo.ITesterRecord;
import com.bdcom.dce.biz.pojo.LoginAuth;
import com.bdcom.dce.sys.configure.PathConfig;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-8-30    <br/>
 * Time: 10:59  <br/>
 */
public interface PMInterface {

    public PathConfig getPathConfig();

    public void addContent(String name, Object obj);

    public Object getContent(String name);

    public int getNIOServerPort();

    public void initApplicationVersion();

    public int getRunningApplicationVersion();

    public int login(LoginAuth auth);

    public int saveBaseTestRecord(BaseTestRecord record);

    public ITesterRecord handlerITesterRecord(ITesterRecord record);

    public String getCompleteSerial(String serial);

}
