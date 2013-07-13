package com.bdcom.nio;

import com.bdcom.exception.LoginException;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-9    <br/>
 * Time: 16:55  <br/>
 */
public abstract class BDPacketParser {

    public static int parseLoginAuthPkt(BDPacket packet) throws LoginException {
        int status = 0;
        if ( RequestID.LOGIN != packet.getRequestID() ) {
            throw new LoginException("Invalid Login Response!");
        }
        return parseIntResponse( packet );
    }

    public static int parseBaseTestRecPkt(BDPacket packet) throws LoginException {
        return responseValidation(packet, RequestID.SEND_BASE_TEST_REC);
    }

    public static int responseValidation(BDPacket packet, int requestID) throws LoginException {
        if ( requestID != packet.getRequestID() ) {
            throw new LoginException("Invalid Response!");
        }

        return parseIntResponse(packet);
    }

    private static int parseIntResponse(BDPacket packet) throws LoginException {
        int status = 0;
        if ( DataType.INTEGER == packet.getDataType() ) {
            status = BDPacketUtil.byteArrayToInt( packet.getData() );
        } else if ( DataType.STRING == packet.getDataType() ) {
            throw new LoginException( new String( packet.getData() ) );
        } else {
            throw new LoginException("Unknown Data Type in Response!");
        }

        return status;
    }
}
