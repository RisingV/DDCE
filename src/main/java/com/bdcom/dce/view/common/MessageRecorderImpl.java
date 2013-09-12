package com.bdcom.dce.view.common;

import com.bdcom.dce.biz.pojo.BaseTestRecord;
import com.bdcom.dce.biz.pojo.ITesterRecord;
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
    public void addBaseTestRecord(BaseTestRecord record, String extraMsg) {
        String serial = record.getSerialNumber();
        String msg = serial + " " + extraMsg;
        messageTable.addMessage( "", msg );
        submitHistoryTable.addBaseTestRecord( record );
        MsgLogger.log( msg );
    }

    @Override
    public void addITesterRecord(ITesterRecord record, String extraMsg) {
        //if needed. implement this
    }

}
