package com.bdcom.sys;

import com.bdcom.biz.pojo.UserInfo;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-15    <br/>
 * Time: 11:43  <br/>
 */
public interface Applicable extends AppContent {

    public void terminal();
    public void logout();
    public UserInfo getUserInfo();

}
