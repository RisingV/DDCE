package com.bdcom.dce.nio;

import com.bdcom.dce.nio.exception.GlobalException;
import com.bdcom.dce.nio.exception.ResponseException;
import com.bdcom.dce.nio.server.Message;
import com.bdcom.dce.biz.pojo.BaseTestRecord;
import com.bdcom.dce.biz.pojo.ITesterRecord;
import com.bdcom.dce.biz.pojo.LoginAuth;
import com.bdcom.dce.util.SerializeUtil;
import com.bdcom.dce.util.logger.ErrorLogger;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: francis
 * Date: 13-7-4
 * Time: 15:21
 */
public abstract class BDPacketUtil {

    public static BDPacket emptyResponse(int requestID) {
        BDPacket pack = BDPacket.newPacket(requestID);
        pack.setDataType( DataType.INTEGER );
        pack.setData( new byte[1] );

        return pack;
    }

    public static BDPacket responseToUnknown(int requestID) {
        byte[] data = intToByteArray(Message.INT.UNKNOWN_REQ);

        BDPacket pack = BDPacket.newPacket(requestID);
        pack.setDataType( DataType.INTEGER  );
        pack.setData(data);

        return pack;
    }

    public static BDPacket responseToNull() {
        byte[] data = Message.STRING.NULL_REQ.getBytes();

        BDPacket pack = BDPacket.newPacket( RequestID.NULL_REQ );
        pack.setDataType( DataType.STRING );
        pack.setData( data );

        return pack;
    }

    public static BDPacket responseToInvalidVer(int requestID) {
        byte[] data = intToByteArray(Message.INT.INVALID_VERSION);

        BDPacket pack = BDPacket.newPacket(requestID);
        pack.setDataType( DataType.INTEGER  );
        pack.setData(data);

        return pack;
    }

    public static BDPacket responseToInvalidData(int requestID) {
        byte[] data = Message.STRING.CORRUPTED_DATA.getBytes();

        BDPacket pack = BDPacket.newPacket( requestID );
        pack.setDataType( DataType.STRING );
        pack.setData( data );

        return pack;
    }

    public static BDPacket responseToTerminal() {
        byte[] data = Message.STRING.TERMINAL_CONFIRM.getBytes();

        BDPacket pack = BDPacket.newPacket( RequestID.TERMINAL );
        pack.setDataType( DataType.STRING );
        pack.setData(data);

        return pack;
    }

    public static BDPacket terminalRequest() {
        BDPacket pack = BDPacket.newPacket( RequestID.TERMINAL );
        pack.setDataType( DataType.INTEGER );
        pack.setData( new byte[0] );

        return pack;
    }

    public static BDPacket encapsulateToPacket(LoginAuth auth) throws IOException {
        if ( null == auth ) {
            return null;
        }

        byte[] data = SerializeUtil.serializeToByteArray( auth );

        BDPacket pack = BDPacket.newPacket( RequestID.LOGIN );
        pack.setDataType( DataType.LOGIN_AUTH );
        pack.setData( data );

        return pack;
    }

    public static BDPacket encapsulateToPacket(BaseTestRecord record) throws IOException {
        if ( null == record ) {
            return null;
        }

        byte[] data = SerializeUtil.serializeToByteArray( record );

        BDPacket pack = BDPacket.newPacket( RequestID.SEND_BASE_TEST_REC );
        pack.setDataType( DataType.BASE_TEST_RECORD );
        pack.setData( data );

        return pack;
    }

    public static BDPacket encapsulateToPacket(ITesterRecord record) throws IOException {
        if ( null == record ) {
            return null;
        }

        byte[] data = SerializeUtil.serializeToByteArray(record);

        BDPacket pack = BDPacket.newPacket( RequestID.SEND_I_TESTER_REC );
        pack.setDataType( DataType.I_TESTER_RECORD );
        pack.setData( data );

        return pack;
    }

    public static BDPacket writeStringMsg(BDPacket packet, String msg) {
        if ( null == packet ) {
            return null;
        }
        packet.setDataType( DataType.STRING );
        packet.setData( null != msg ? msg.getBytes() : "".getBytes());

        return packet;
    }

    public static int parseIntResponse(BDPacket packet, int requestID)
            throws ResponseException, GlobalException {
        if ( requestID != packet.getRequestID() ) {
            throw new ResponseException("Invalid Response!");
        }
        globalExceptionCheck(packet);

        int status = 0;
        if ( DataType.INTEGER == packet.getDataType() ) {
            status = BDPacketUtil.byteArrayToInt( packet.getData() );
        } else if ( DataType.STRING == packet.getDataType() ) {
            throw new ResponseException( new String( packet.getData() ) );
        } else {
            throw new ResponseException("Unknown Data Type in Response!");
        }

        return status;
    }

    public static String[] parseStringArrayResponse(BDPacket packet, int requestID)
            throws ResponseException, GlobalException {
        if ( null == packet ) {
            return null;
        }
        if ( requestID != packet.getRequestID() ) {
            throw new ResponseException("Invalid Response!");
        }
        globalExceptionCheck(packet);

        String[] strArray = null;
        if ( DataType.STRING_ARRAY == packet.getDataType() ) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream( packet.getData() ));
            try {
                int len = in.readInt();
                strArray = new String[len];
                for ( int i = 0; i < len; i++ ) {
                    int bytesLen = in.readInt();
                    byte[] bytes = new byte[bytesLen];
                    in.read(bytes);
                    strArray[i] = new String(bytes);
                }
            } catch (IOException e) {
                throw new ResponseException("parse response fail!: "+e.getMessage());
            }
        }

        return strArray;
    }

    public static void globalExceptionCheck(BDPacket pack) throws GlobalException {
        if ( null == pack ||
                DataType.GLOBAL_EXCEPTION != pack.getDataType() ) {
            return;
        }

        Exception e = null;
        try {
            e = (Exception) SerializeUtil
                    .deserializeFromByteArray(pack.getData());
        } catch (IOException e1) {
            ErrorLogger.log( e1.getMessage());
        } catch (ClassNotFoundException e1) {
            ErrorLogger.log( e1.getMessage());
        }

        throw new GlobalException(e, e.getMessage());
    }

    /**
     * only convert first 4 byte. if byte array is null, return 0.
     * */
    public static int byteArrayToInt(byte[] bs) {
        if ( null ==  bs ) {
            return 0;
        }
        int len = 4;
        if ( bs.length < 4 ) {
            len = bs.length;
        }
        int x = 0;
        for ( int i = len-1; i >= 0 ; i-- ) {
            x <<= 8;
            x = (x & 0xffffff00) | (bs[i] & 0xff);
        }
        return x;
    }

    public static byte[] intToByteArray(int raw) {
        byte[] data = new byte[4];
        for( int i = 0; i < 4; i++ ) {
            data[i] = (byte) (raw & 0xff);
            if ( i != 3 ) {
                raw >>= 8;
            } else {
                raw >>>= 8;
            }
        }

        return data;
    }

}
