package com.bdcom.dce.nio.server;

import com.bdcom.dce.biz.storage.StorableMgr;
import com.bdcom.dce.biz.storage.StoreMgr;
import com.bdcom.dce.nio.bdpm.PMInterface;
import com.bdcom.dce.sys.configure.PathConfig;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-12    <br/>
 * Time: 16:33  <br/>
 */
public abstract class ResourceHandler extends CommonHandler {

    private static final byte[] lock = new byte[0];
    private static StorableMgr mgr;

    private PMInterface pmi;

    public ResourceHandler(PMInterface pmInterface) {
        super(pmInterface);
        pmi = pmInterface;
    }

    protected StorableMgr getStorableMgr() {
        synchronized ( lock ) {
            if ( null == mgr ) {
                PathConfig config = pmi.getPathConfig();
                mgr = new StoreMgr(config);
            }
        }
        return mgr;
    }

}
