package com.bdcom.dce.sys;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-17    <br/>
 * Time: 11:15  <br/>
 */
public interface AppContent {
    public void addAttribute(String name, Object attr);
    public Object getAttribute(String name);
    public String getStringAttr(String name);
    public boolean getBoolAttr(String name);
}
