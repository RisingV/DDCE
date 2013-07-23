package com.bdcom.view.util;

import com.bdcom.nio.exception.GlobalException;
import com.bdcom.sys.gui.Application;
import com.bdcom.sys.ApplicationConstants;
import com.bdcom.util.LocaleUtil;
import com.bdcom.util.log.ErrorLogger;

import javax.swing.*;
import java.awt.*;
import java.io.EOFException;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2012-11-28 <br>
 * Auto-Generated by eclipse Juno <br>
 */

public abstract class MsgDialogUtil implements ApplicationConstants {

    private static final String CANT_CONNECT = "Connection refused";

    public static void reportGlobalException(GlobalException e) {
        Exception origin = e.getOriginException();
        String msg = null;

        if ( origin instanceof EOFException ) {
            msg = "Server is closed!";
        } else {
            String msg0 = e.getMessage();
            if ( msg0.indexOf(CANT_CONNECT) > 0 ) {
                msg = "Can't connect to server!";
            } else {
                msg = msg0;
            }
        }

        showErrorDialog( msg );
        ErrorLogger.log( msg );
    }
	
	public static void showErrorDialog(String msg) {
        JFrame current = (JFrame) Application.instance.getCurrentDisplay();
		showDialog( current,
				msg, _ERROR, JOptionPane.ERROR_MESSAGE);
	}
	
	public static void showErrorDialog(Component compo, String msg) {
		showDialog(compo, msg, _ERROR, JOptionPane.ERROR_MESSAGE);
	}
	
	public static void showErrorDialogLocalised(String msg) {
		showErrorDialog(
				LocaleUtil.getLocalName(msg)
				);
	}
	
	public static void showErrorDialogLocalised(Component compo, String msg) {
		showErrorDialog(
				compo,
				LocaleUtil.getLocalName(msg)
				);
	}
	
	public static void showMsgDialog(String msg) {
        JFrame current = (JFrame) Application.instance.getCurrentDisplay();
		showDialog( current,
				msg, _MESSAGE, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void showMsgDialog(Component compo, String msg) {
		showDialog(compo, 
				msg, _MESSAGE, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void showMsgDialogLocalised(String msg) {
		showMsgDialog(
				LocaleUtil.getLocalName(msg)
				);
	}
	
	public static void showMsgDialogLocalised(Component compo,String msg) {
		showMsgDialog(
				compo,
				LocaleUtil.getLocalName(msg)
				);
	}
	
	private static void showDialog(Component compo, String msg, String msgType, int dialogType) {
		JOptionPane.showMessageDialog(
                compo,
				msg,
				LocaleUtil.getLocalName(msgType),
				dialogType);
	}

}
