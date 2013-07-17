package com.bdcom.nio;

/**
 * Created with IntelliJ IDEA.
 * User: francis
 * Date: 13-7-4
 * Time: 下午3:04
 */
public interface RequestID {

    public static final int NULL_REQ = 0;

    public static final int LOGIN = 1;

    public static final int SEND_BASE_TEST_REC = 2;

    public static final int SEND_I_TESTER_REC = 3;

    public static final int UPLOAD_SCENARIO = 4;

    public static final int GET_SCENARIO_NAME_LIST = 5;

    public static final int DOWNLOAD_SCENARIO = 6;

    public static final int UPLOAD_SCRIPT = 7;

    public static final int GET_SCRIPT_FILE_LIST = 8;

    public static final int DOWNLOAD_SCRIPT = 9;


    //For debug!
    public static final int ECHO = Byte.MAX_VALUE - 1;

    //This is the biggest RequestID! Because of the fact that RequestID stores in 1 byte!
    public static final int TERMINAL = Byte.MAX_VALUE;

    abstract class LOCAL {

        public static final int READ_EXTRA_INFO = 101;

        public static final int REPORT_SENDING_RESULT = 102;

    }

}
