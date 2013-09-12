package com.bdcom.dce.view.common;

import com.bdcom.dce.biz.pojo.BaseTestRecord;
import com.bdcom.dce.biz.pojo.ITesterRecord;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-12    <br/>
 * Time: 15:21  <br/>
 */
public interface MessageRecorder {

    public void addMessage(String type, String msg);

    public void addBaseTestRecord(BaseTestRecord record, String extraMsg);

    public void addITesterRecord(ITesterRecord record, String extraMsg);

}
