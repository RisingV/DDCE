package com.bdcom.dce.biz.pojo;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-6    <br/>
 * Time: 14:20  <br/>
 */
public abstract class TestTypeRecord implements Serializable {

    private static final long serialVersionUID = -8668388559624179526L;

    public static final int BASE_TEST = 1;
    public static final int RE_TEST = 2;
    public static final int OTHER_TEST = 3;

    private static int currentTestType = BASE_TEST;

    public static void setCurrentTestType(int type) {
        currentTestType = type;
    }

    private final int testType;

    public TestTypeRecord() {
        this.testType = currentTestType;
    }

    public int getTestType() {
        return testType;
    }

}
