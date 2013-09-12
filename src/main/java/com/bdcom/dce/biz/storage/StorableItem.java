package com.bdcom.dce.biz.storage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-9    <br/>
 * Time: 09:45  <br/>
 */
public abstract class StorableItem implements Storable {

    private static final long serialVersionUID = -5950051133662820156L;
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String remarkName;
    private String serial;
    private int beginIndex;
    protected final Date dateCreate;
    protected Date dateModify;

    public StorableItem( String serial, int beginIndex ) {
        this(serial, beginIndex, new Date());
    }

    private StorableItem(String serial, int beginIndex, Date dateCreate) {
        this.serial = serial;
        this.beginIndex = beginIndex;
        this.dateCreate = dateCreate;
        updateDateModify();
    }

    @Override
    public String getRemarkName() {
        return remarkName;
    }

    @Override
    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    @Override
    public String getSerial() {
        return serial;
    }

    @Override
    public void setSerial(String serial) {
        this.serial = serial;
        updateDateModify();
    }

    @Override
    public int getBeginIndex() {
        return beginIndex;
    }

    @Override
    public void setBeginIndex(int beginIndex) {
        this.beginIndex = beginIndex;
        updateDateModify();
    }

    @Override
    public Date getDateCreate() {
        return dateCreate;
    }

    @Override
    public Date getDateModify() {
        return dateModify;
    }

    public String getFormattedCreateDate() {
        return DATE_FORMAT.format( dateCreate );
    }

    public String getFormattedModifyDate() {
        return DATE_FORMAT.format( dateModify );
    }

    protected void updateDateModify() {
        dateModify = new Date();
    }

}
