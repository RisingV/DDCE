package com.bdcom.dce.sys.configure;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-16    <br/>
 * Time: 11:07  <br/>
 */
public class NetConfig implements Serializable {

    private static final long serialVersionUID = 8271466162923560026L;
    private static final String FILE_NAME = "connection.conf";

    private String ipAddress;
    private int port;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
