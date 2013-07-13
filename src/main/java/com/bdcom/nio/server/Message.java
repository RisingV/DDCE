package com.bdcom.nio.server;

/**
 * Created with IntelliJ IDEA.
 * User: francis
 * Date: 13-7-4
 * Time: 下午3:30
 */
public interface Message {

    abstract class INT {

        public static final int UNKNOWN_REQ = 0;

        public static final int INVALID_VERSION = 1;

        public static final int LOGIN_FAIL = 2;

        public static final int LOGIN_SUCCESS = 3;

    }

    abstract class STRING {

        public static final String NULL_REQ = "Invalid Null Request";

        public static final String CORRUPTED_DATA = "Corrupted Request Data";

        public static final String INVALID_LOGIN_AUTH = "Invalid LoginAuth";

        public static final String TERMINAL_CONFIRM = "Terminal Request Confirmed";

        /** for future use   **/
    }

}
