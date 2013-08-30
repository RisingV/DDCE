package com.bdcom.dce.sys;

import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.util.StringUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-17    <br/>
 * Time: 11:37  <br/>
 */
public abstract class AppContentAdaptor implements AppContent {

    private static final int VER_SET_BY_HAND = 400;

    static {
        initAppVersion();
    }

    private static void initAppVersion() {
        int ver = 0;
        String verStr = System.getProperty("dce.version");
        if ( StringUtil.isNotBlank( verStr) &&
                StringUtil.isValidNumber( verStr ) ) {
            ver = Integer.parseInt( verStr );
        } else {
            ver = VER_SET_BY_HAND;
        }
        BDPacket.setCurrentVersion( ver );
    }

    private Map<String, Object> attributes;

    public void addAttribute(String name, Object attr) {
        if ( null == attributes ) { //double checking
            synchronized ( this ) {
                if ( null == attributes ) {
                    attributes = new ConcurrentHashMap<String, Object>();
                }
            }
        }
        attributes.put(name, attr);
    }

    public Object getAttribute(String name) {
        if ( null == attributes ) {
            return null;
        }
        return attributes.get( name );
    }

    public String getStringAttr(String name) {
        return (String) getAttribute( name );
    }

    public boolean getBoolAttr(String name) {
        Object bool = getAttribute(name);
        if ( null == bool ) {
            return false;
        }
        return ((Boolean) bool).booleanValue();
    }

}
