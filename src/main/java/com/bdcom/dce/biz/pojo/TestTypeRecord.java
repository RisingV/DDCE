package com.bdcom.dce.biz.pojo;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-6    <br/>
 * Time: 14:20  <br/>
 */
public abstract class TestTypeRecord {

    public static final int BASE_TEST = 1;
    public static final int RE_TEST = 2;

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
