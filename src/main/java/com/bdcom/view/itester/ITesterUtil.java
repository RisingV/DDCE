package com.bdcom.view.itester;

import com.bdcom.itester.api.wrapper.ITesterException;
import com.bdcom.util.LocaleUtil;
import com.bdcom.view.util.MsgDialogUtil;

import java.awt.*;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-8-21    <br/>
 * Time: 16:34  <br/>
 */
public abstract class ITesterUtil {

    private static final String CARD_ID = "Card ID:";
    private static final String PORT_ID = "Port ID:";
    private static final String IS_IN_USE = "is in use!";
    private static final String IS_NOT_LINKED = "is not linked!";
    private static final String INVALID_IP = "invalid ip:";
    private static final String CANT_CONNECT = "can't connect to server:";

    public static String reportTestException(ITesterException e, Component source) {
        int type = e.getErrType();
        StringBuilder sb = new StringBuilder();
        switch ( type ) {
            case ITesterException.INVALID_IP: {
                String invalidIpMsg = LocaleUtil.getLocalName(INVALID_IP);
                sb.append( invalidIpMsg )
                        .append( e.getServerIP() );
                break;
            }
            case ITesterException.CONNECT_FAIL: {
                String connectFailMsg = LocaleUtil.getLocalName( CANT_CONNECT );
                sb.append( connectFailMsg )
                        .append( e.getServerIP() );
                break;
            }
            case ITesterException.PORT_IN_USE: {
                String cardId = LocaleUtil.getLocalName( CARD_ID );
                String portId = LocaleUtil.getLocalName( PORT_ID );
                String inUse = LocaleUtil.getLocalName( IS_IN_USE );

                sb.append( cardId ).append( e.getCardId() )
                        .append( " " )
                        .append( portId ).append( e.getPortId() )
                        .append( " " ).append( inUse );
                break;
            }
            case ITesterException.PORT_NOT_LINKED: {
                String cardId = LocaleUtil.getLocalName( CARD_ID );
                String portId = LocaleUtil.getLocalName( PORT_ID );
                String notLinked = LocaleUtil.getLocalName( IS_NOT_LINKED );

                sb.append( cardId ).append( e.getCardId() )
                        .append( " " )
                        .append( portId ).append( e.getPortId() )
                        .append( " " ).append( notLinked );
                break;
            }
        }
        String msg = sb.toString();
        MsgDialogUtil.showErrorDialog( source, msg );
        return msg;
    }

}
