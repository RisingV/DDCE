package com.bdcom.dce.sys.service;

import com.bdcom.dce.biz.storage.StorableMgr;
import com.bdcom.dce.nio.client.ClientProxy;
import com.bdcom.dce.nio.exception.GlobalException;
import com.bdcom.dce.nio.exception.ResponseException;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-16    <br/>
 * Time: 17:35  <br/>
 */
public class AutoDownloadService {

    private final ClientProxy client;
    private final StorableMgr mgr;

    public AutoDownloadService(ClientProxy client, StorableMgr mgr) {
        this.client = client;
        this.mgr = mgr;
    }

    public void start() {
        try {
            client.checkAndDownloadResource( mgr );
        } catch (GlobalException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ResponseException e) {
            e.printStackTrace();
        }
    }

}
