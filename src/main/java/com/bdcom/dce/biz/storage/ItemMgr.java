package com.bdcom.dce.biz.storage;

import java.util.Set;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-9    <br/>
 * Time: 10:09  <br/>
 */
public interface ItemMgr {

    public Set<Item> getByRemarkName(String remark);
    public boolean isRemarkNameUsed(String remark);
    public Item getByFullSerial(String serial);
    public boolean isSerialUsed(String serial);
    public Set<Item> getBySerialMatching(String serial);
    public void addItem(Item i);
    public void removeItem(Item i);
    public Item[] getAll();

}
