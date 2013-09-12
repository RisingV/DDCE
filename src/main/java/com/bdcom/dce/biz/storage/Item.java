package com.bdcom.dce.biz.storage;

import java.util.Date;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-9    <br/>
 * Time: 10:11  <br/>
 */
public interface Item {

    public String getRemarkName();
    public void setRemarkName(String remark);
    public String getSerial();
    public void setSerial(String serial);
    public int getBeginIndex();
    public void setBeginIndex(int beginIndex);
    public Date getDateCreate();
    public Date getDateModify();
    public String getFormattedCreateDate();
    public String getFormattedModifyDate();

}
