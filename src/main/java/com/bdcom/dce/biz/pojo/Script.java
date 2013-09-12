package com.bdcom.dce.biz.pojo;

import com.bdcom.dce.biz.storage.StorableItem;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-6    <br/>
 * Time: 17:49  <br/>
 */
public class Script extends StorableItem implements Serializable {

    private static final long serialVersionUID = -3444900807126217662L;
    private String firstPath;
    private String secondPath;

    public Script() {
        this( "", 0 );
    }

    public Script(String serial, int beginIndex) {
        super(serial, beginIndex);
    }

    public String getPath() {
        return firstPath;
    }

    public void setPath(String path) {
        this.firstPath = path;
        updateDateModify();
    }

    public String getSecondPath() {
        return secondPath;
    }

    public void setSecondPath(String secondPath) {
        this.secondPath = secondPath;
    }

}
