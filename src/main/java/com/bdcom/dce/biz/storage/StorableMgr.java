package com.bdcom.dce.biz.storage;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-9    <br/>
 * Time: 10:59  <br/>
 */
public interface StorableMgr extends ItemMgr {

    public byte[] getMD5Bytes();
    public String getMD5String();
    public boolean isStorageLoaded();
    public void loadStorage();
    public void saveToLocalStorage();

}
