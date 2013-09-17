package com.bdcom.dce.view.message;

import com.bdcom.dce.biz.pojo.BaseTestRecord;
import com.bdcom.dce.biz.pojo.ITesterRecord;
import com.bdcom.dce.util.LocaleUtil;
import com.bdcom.dce.util.logger.MsgLogger;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-12    <br/>
 * Time: 15:24  <br/>
 */
public class MessageRecorderImpl implements MessageRecorder {

    private final MessageTable messageTable;
    private final SubmitHistoryTable submitHistoryTable;

    public MessageRecorderImpl(MessageTable messageTable,
                               SubmitHistoryTable submitHistoryTable) {
        this.messageTable = messageTable;
        this.submitHistoryTable = submitHistoryTable;
    }

    @Override
    public void addMessage(String type, String msg) {
        messageTable.addMessage( type, msg );
        StringBuilder sb = new StringBuilder();
        sb.append(type)
                .append(" ")
                .append( msg );

        MsgLogger.log( sb.toString() );
    }

    @Override
    public void addBaseTestRecord(BaseTestRecord record, int sendStatus, String extraMsg) {
        String serial = record.getSerialNumber();
        String sendResult = getMessage( sendStatus );
        String msg = serial + " "+ sendResult + " " + extraMsg;
        messageTable.addMessage( "", msg );
        submitHistoryTable.addBaseTestRecord( record );
        MsgLogger.log( msg );
        MsgLogger.log( record.toLogString() );
    }

    @Override
    public void addITesterRecord(ITesterRecord record, String extraMsg) {
        //if needed. implement this
    }

    private String getMessage( int status ) {
        String msg = null;
        switch ( status ) {
            case 0: msg = ADD_FAIL; break;
            case 1: msg = INCOMPLETE_ADD; break;
            case 2: msg = ADDED_AND_PASS; break;
            case 3: msg = ADDED_BUT_NOT_PASS; break;
            case 4: msg = INVALID_WORK_ORDER; break;
        }

        if ( null != msg ) {
            msg = LocaleUtil.getLocalName( msg );
        }
        return msg;
    }

    private static final String ADD_FAIL = "fail to add";
    private static final String INCOMPLETE_ADD = "incomplete add";
    private static final String ADDED_AND_PASS = "added and pass";
    private static final String ADDED_BUT_NOT_PASS = "added but not pass";
    private static final String INVALID_WORK_ORDER = "work order is invalid";

}
