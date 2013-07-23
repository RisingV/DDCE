package com.bdcom.nio;

/**
 * Created with IntelliJ IDEA.
 * User: francis
 * Date: 13-7-4
 * Time: 下午3:27
 */
public interface DataType {

    public static final int INTEGER = 0;

    public static final int STRING = 1;

    public static final int STRING_ARRAY = 2;

    public static final int FILE = 3;

    public static final int MAP = 4;

    public static final int LOGIN_AUTH = 10;

    public static final int BASE_TEST_RECORD = 11;

    public static final int I_TESTER_RECORD = 12;

    public static final int SCENARIO = 13;

    //System remained
    public static final int LOCAL_EXCEPTION = Byte.MAX_VALUE - 1;

    public static final int GLOBAL_EXCEPTION = Byte.MAX_VALUE;

}
