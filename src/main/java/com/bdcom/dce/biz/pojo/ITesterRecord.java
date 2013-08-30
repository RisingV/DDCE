package com.bdcom.dce.biz.pojo;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-5    <br/>
 * Time: 15:30  <br/>
 */
public class ITesterRecord implements Serializable {

    private static final long serialVersionUID = 8721480492204072805L;

    public static final int CHECK_WORK_ORDER = 0xA;

    public static final int COMMIT_TEST_RESULT = 0xB;

    public static ITesterRecord checkWorkOrderInstance(String workOrder, String barCode) {
        return new ITesterRecord( CHECK_WORK_ORDER, workOrder, barCode );
    }

    public static ITesterRecord checkWorkOrderInstance(ITesterRecord itr) {
        return new ITesterRecord( CHECK_WORK_ORDER, itr);
    }

    public static ITesterRecord commitTestResultInstance(String workOrder, String barCode) {
        return new ITesterRecord( COMMIT_TEST_RESULT, workOrder, barCode );
    }

    public static ITesterRecord commitTestResultInstance(ITesterRecord itr) {
        return new ITesterRecord( COMMIT_TEST_RESULT, itr);
    }

    private final int type;

    private final String workOrder;

    private final String barCode;

    private boolean workOrderValid;

    private boolean everTested;

    private boolean testPass;

    private ITesterRecord(int type, String workOrder, String barCode) {
        this.type = type;
        this.workOrder = workOrder;
        this.barCode = barCode;
    }

    private ITesterRecord(int type, ITesterRecord itr) {
        this( type, itr.workOrder, itr.barCode );
    }

    public int getType() {
        return type;
    }

    public String getWorkOrder() {
        return workOrder;
    }

    public String getBarCode() {
        return barCode;
    }

    public boolean isWorkOrderValid() {
        return workOrderValid;
    }

    public void setWorkOrderValid(boolean workOrderValid) {
        this.workOrderValid = workOrderValid;
    }

    public boolean isEverTested() {
        return everTested;
    }

    public void setEverTested(boolean everTested) {
        this.everTested = everTested;
    }

    public boolean isTestPass() {
        return testPass;
    }

    public void setTestPass(boolean testPass) {
        this.testPass = testPass;
    }

}
