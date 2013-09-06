package com.bdcom.dce.sys;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-9    <br/>
 * Time: 16:47  <br/>
 */
public interface ApplicationConstants {

    abstract class RUN_TIME {

        public static final String CURRENT_DIR = System.getProperty("user.dir");

        public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH#mm#ss");
    }

    public static final String TEST_NUM = "testerNum";

    public static final String BEGIN_TIME = "beginTime";

    public static final String END_TIME = "endTime";

    public static final String _STATUS = "status";

    public static final String _OK = "OK";

    public static final String _NOT_OK = "NOT_OK";

    public static final String _STATUS_SETTING = "status setting";

    public static final String _PASS = "pass";

    public static final String _UNPASS = "unpass";

    public static final String CREATE_SCES = "define scenarios";

    public static final String SELECT_ATTRS = "Select PreDefind Attrs";

    public static final String SETTING_ATTRS = "Setting PreDefind Attrs";

    public static final String DEFINED_SCENARIOS = "Defined Scenarios";

    public static final String SCENARIO_NAME = "Scenario Name";

    public static final String REFRESH_BT = "refresh";

    public static final String CREATE_BT = "create";

    public static final String SYNC_BT = "sync";

    public static final String SAVE_BT = "save";

    public static final String CANCEL_BT =  "cancel";

    public static final String EDIT_BT =  "edit";

    public static final String DEL_BT =  "delete";

    public static final String EXE_BT = "execute";

    public static final String INDEX_BT = "search";

    public static final String SUBMIT_BT = "submit";

    public static final String SERIAL_NUM = "serial number";

    public static final String MATCHING_SERIAL = "matching serial";

    public static final String MODE_SELECTION = "commit mode";

    public static final String SCE_EDIT_MODE = "scenario edit mode";

    public static final String AUTO_MODE = "auto mode";

    public static final String HAND_MODE = "hand mode";

    public static final String SCE_EDITABLE = "sce editable";

    public static final String SCE_UNEDITABLE = "sce uneditable";

    public static final String IF_USE_SCENARIO = "if use scenario";

    public static final String SERACH_AND_SUBMIT = "search and submit";

    public static final String SCE_NAME_REDUPLICATED = "scenario name reduplicated!";

    public static final String MODE_SETTING = "mode setting";

    public static final String TEST_DATA = "test data";

    public static final String SCE_SELECT_LIST = "scenario list";

    public static final String SUBMIT_REC = "submit record";

    public static final String SCE_MANAGEMENT = "scenario management";

    public static final String USER_NAME = "username";

    public static final String PASS_WORD = "password";

    public static final String LOG_IN = "login";

    public static final String _EXIT = "exit";

    public static final String _ERROR = "ERROR";

    public static final String _MESSAGE = "MESSAGE";

    public static final String _CONFIRM_EXIT = "exit?";

    public static final String _TIME = "time";

    public static final String _MSG = "msg";

    public static final String _MSG_TYPE = "msg type";

    public static final String EMPTY_RECORD = "can't submit empty recourd";

    public static final String IS_BLANK = "is blank!";

    public static final String NULL_SERIAL = "null serial";

    public static final String SERIAL_USED = "serial used";

    public static final String NULL_CRT = "null crt";

    public static final String NULL_PATH = "null path";

    public static final String SCRIPT_PATH = "script path";

    public static final String RAW_DATA_PATH = "raw data path";

    public static final String IF_USE_DEFAULT_DIR = "if use default directory";

    public static final String SET_RAW_DATA_PATH = "set raw data path";

    public static final String ADD_OR_MODIFY_SCRIPT = "add and modify scripts";

    public static final String SEARCH_SCRIPT = "search script";

    public static final String SCRIPT_LIST = "script list";

    public static final String RESOURCE_LIST = "resource list";

    public static final String RECENT_ADD2DB = "recent added dr table(debug)";

    public static final String DEBUG_DR_LIST = "recent added datarecord(debug only)";

    public static final String NO_DBG_DATA = "this login session no test record!";

    public static final String SELECT_ROW_TO_DEL = "please select rows to delete!";

    public static final String DELETE_FAIL = "delete fail!";

    public static final String DELETE_DONE = "deletion done!";

    public static final String SENDING_WARN_FAIL="sending warnning fail!";

    public static final String SCRIPT_MANGEMENT = "script management";

    public static final String IP_ADDR = "ip address";

    public static final String _PORT = "port";

    public static final String NULL_IP = "null ip";

    public static final String NULL_PORT = "null port";

    public static final String INVAILD_IP = "invaild ip";

    public static final String INVAILD_PORT = "invaild port";

    public static final String DATE_FORMAT_EG = "date format e.g.";

    public static final String ONE_DATE_FORMAT_EG ="2012-07-02-10-29-28(y-m-d-h-m-s)";

    public static final String BEGIN_INDEX = "begin index";

    public static final String END_INDEX = "end index";

    public static final String INVAILD_BEGIN_INDEX = "beginIndex must be no less than 0";

    public static final String SCRIPT_INTERPRETER = "script interpreter";

    public static final String CRT_SESSIONS = "crt sessions";

    public static final String SCRIPT_IPT_PATH = "script interpreter path";

    public static final String SET_SCRIPT_IPT_PATH = "set script interpreter path";

    public static final String SYS_NAME = "system name";

    public static final String EXECUTING = "executing...";

    public static final String SENDING = "sending...";

    public static final String MSG_LIST = "message list";

    public static final String MATCH_SERIAL = "matching serial";

    public static final String _TAB = "	";

    public static final String _SYS = "System";

    public static final String _OTHER = "other";

    public static final String _LOG_OUT = "logout";

    public static final String LOGIN_MSG = "has login";

    public static final String LOGOUT_MSG = "has logout";

    public static final String NO_MATCHING_SCE = "no matching scenario";

    public static final String NO_MATCHING_SCE_IF_SEND_MAIL = "no matching scenario, if send mail";

    public static final String IS_FC = "is first component";

    public static final String _YES = "yes";

    public static final String _UPLOAD = "upload";

    public static final String _UPLOADING = "uploading";

    public static final String _DOWNLD = "download";

    public static final String _DOWNLDING = "downloading";

    public static final String _UPLOAD_DONE_SCRIPT = "script upload done";

    public static final String _DOWNLD_DONE_SCRIPT = "script download done";

    public static final String _UPLOAD_DONE_SCE = "scenario upload done";

    public static final String _DOWNLD_DONE_SCE = "scenario download done";

    abstract class LOGIN {
        public static final int COMMON = 1;
        public static final int ROOT = 2;
    }

    abstract class USER {

        public static final String USER_INFO = "User Info";

        public static final String USER_NUM = "user num";

        public static final String USER_RANK = "user rank";

        public static final String ROOT = "root";

        public static final String COMMON_USER = "common user";

        public static final String SUPERVISOR = "supervisor";

    }

    abstract class COMPONENT {

        public static final String DIALECT = "Dialect";

        public static final String NIO_CLIENT = "NIO Client";

        public static final String DISPLAYING_FRAME = "Displaying Frame";

        public static final String VIEW_MGR = "View Mgr";

        public static final String MSG_TABLE = "Message Table";

        public static final String MAIN_FRAME = "Main Frame";

        public static final String LOGIN_FRAME = "Login Frame";

        public static final String SCRIPT_EXECUTOR = "Script Executor";

        public static final String SCENARIO_MGR_FRAME = "Scenario Mgr Frame";

        public static final String SUBMIT_FRAME = "Submit Frame";

        public static final String SCRIPT_MGR_FRAME = "Script Mgr Frame";

        public static final String ITESTER_API = "ITester API";

        public static final String ITESTER_FRAME = "ITester Frame";

        public static final String SCRIPT_LIST = "Script List Frame";

        public static final String RESOURCE_LIST = "Resource List Frame";

        public static final String SCENARIO_MGR = "Scenario Mgr";

        public static final String SCRIPT_MGR = "Script Mgr";

    }

    abstract class CONFIG {

        public static final String PATH_CONFIG = "Path Config";

        public static final String SERVER_CONFIG = "Server Config";

        public static final String LOCAL_SERVER_CONFIG = "Local Server Config";

    }

    abstract class CONTENT {

        public static final String SERIAL_NUM_SET = "Serial Number Set";

    }

    abstract class TEST_ATTR {

        public static final String IS_FC = "is fc";
        public static final String TEST_TYPE = "Test Type";

    }

}
