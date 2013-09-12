package com.bdcom.dce.nio.server;

import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.DataType;
import com.bdcom.dce.nio.bdpm.PMInterface;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-12    <br/>
 * Time: 16:53  <br/>
 */
public class GetCompleteSerialHandler extends CommonHandler {

    PMInterface pmi;
    public GetCompleteSerialHandler(PMInterface pmInterface) {
        super(pmInterface);
        this.pmi = pmInterface;
    }

    @Override
    protected BDPacket doHandle(BDPacket bdPacket) {
        byte[] data = bdPacket.getData();
        String serial = new String( data );
        serial = pmi.getCompleteSerial(serial);

        BDPacket response = BDPacket.newPacket( bdPacket.getRequestID() );
        response.setDataType(DataType.STRING );
        response.setData( serial.getBytes() );

        return response;
    }
}
