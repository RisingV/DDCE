package com.bdcom.dce.nio.server;

import com.bdcom.dce.biz.pojo.LoginAuth;
import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.BDPacketUtil;
import com.bdcom.dce.nio.DataType;
import com.bdcom.dce.nio.bdpm.PMInterface;
import com.bdcom.dce.util.SerializeUtil;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-5    <br/>
 * Time: 14:38  <br/>
 */
public class LoginHandler extends CommonHandler {

    private final PMInterface pmInterface;

    public LoginHandler(PMInterface pmInterface) {
        super(pmInterface);
        this.pmInterface = pmInterface;
    }

    @Override
    protected BDPacket doHandle(BDPacket bdPacket) {
        LoginAuth auth = null;
        boolean deserialSuccess = true;
        try {
            auth = (LoginAuth) SerializeUtil.deserializeFromByteArray(bdPacket.getData());
        } catch (IOException e) {
            deserialSuccess = false;
            System.err.println( "IO Exception occurs when deserialize LoginAuth: "
            + e.getMessage() );
        } catch (ClassNotFoundException e) {
            deserialSuccess = false;
            System.err.println("ClassNotFoundException occurs when deserialize LoginAuth"
                    + e.getMessage());
        } finally {
            if ( !deserialSuccess ) {
                return BDPacketUtil.responseToInvalidData( bdPacket.getRequestID() ) ;
            }
        }
        int loginStatus = pmInterface.login(auth);
        byte[] data = BDPacketUtil.intToByteArray(loginStatus);

        BDPacket pack = BDPacket.newPacket( bdPacket.getRequestID() );
        pack.setDataType(DataType.INTEGER );
        pack.setData( data );

        return pack;
    }

}
