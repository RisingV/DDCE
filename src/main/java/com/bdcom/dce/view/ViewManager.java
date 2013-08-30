package com.bdcom.dce.view;

import com.bdcom.dce.view.itester.ITesterFrame;
import com.bdcom.dce.view.util.ViewUtil;
import com.bdcom.dce.sys.Applicable;
import com.bdcom.dce.sys.ApplicationConstants;
import com.bdcom.dce.util.LocaleUtil;
import com.bdcom.dce.util.logger.ErrorLogger;

import javax.swing.*;
import java.awt.*;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2012-11-23 <br>
 * Auto-Generated by eclipse Juno <br>
 */

public class ViewManager implements ApplicationConstants {
	
	private static final String IMAGE_PATH = "/images/logo.png";

	private LoginFrame loginFrame;
	
	private MainFrame mainFrame;
	
	private ScenarioMgrFrame smTab;
	
	private SubmitFrame submitTab;
	
	private ScriptMgrFrame scmTab;
	
	private ScriptList scriptListTab;

    private ITesterFrame itesterTab;

    private final Applicable app;

	public ViewManager( Applicable app ) {
        this.app = app;
		setLookAndFeel();
		init();
	}
	
	public void display() {
		loginFrame.display();
	}
	
	public void reinit() {
		init();
	}
	
	private void init() {
		Image im = ViewUtil.getImage(IMAGE_PATH);
		
		mainFrame = (MainFrame) getCompo(COMPONENT.MAIN_FRAME);
		loginFrame = (LoginFrame) getCompo(COMPONENT.LOGIN_FRAME);
		smTab = (ScenarioMgrFrame) getCompo(COMPONENT.SCENARIO_MGR_FRAME);
		submitTab = (SubmitFrame) getCompo(COMPONENT.SUBMIT_FRAME);
		scmTab = (ScriptMgrFrame) getCompo(COMPONENT.SCRIPT_MGR_FRAME );
		scriptListTab = (ScriptList) getCompo(COMPONENT.SCRIPT_LIST);
        itesterTab = (ITesterFrame) getCompo(COMPONENT.ITESTER_FRAME);

		smTab.setScenarioListRefreshHook(
	    		submitTab.getScenarioListRefreshHook() 
	    		);
		smTab.setTabTitle(
				getLocalName( SCE_MANAGEMENT )
				);
		smTab.setTabTip(
				getLocalName( SCE_MANAGEMENT )
				);
		
		submitTab.setTabTitle(
				getLocalName( SUBMIT_REC )
				);
		submitTab.setTabTip( 
				getLocalName( SUBMIT_REC )
				);
		
		scmTab.setTabTitle(
				getLocalName( SCRIPT_MANGEMENT )
				);
		scmTab.setTabTip( 
				getLocalName(SCRIPT_MANGEMENT )
				);
		
		scriptListTab.setTabTitle(
				getLocalName( SCRIPT_LIST )
				);
		
		mainFrame.addViewTab(submitTab);
        mainFrame.addViewTab(itesterTab);
		mainFrame.addRootViewTab(smTab);
		mainFrame.addRootViewTab(scmTab);
		mainFrame.addRootViewTab(scriptListTab);
		mainFrame.addRefresher(smTab.getScenarioListRefreshHook());
		mainFrame.addRefresher(submitTab.getFcAddHook());
		mainFrame.addRefresher(scmTab.getScriptListRefreshHook());
		
		loginFrame.setImage(im);
		mainFrame.setImage(im);
		
		submitTab.setMsgTable(mainFrame.getMsgTable());
//		mainFrame.init(); //can't add viewTab after int() called
		ViewUtil.centerWindow(loginFrame);		
		
	}
	
	private void setLookAndFeel() {
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		try {
			UIManager.setLookAndFeel(UIManager
					.getSystemLookAndFeelClassName() );
		} catch (ClassNotFoundException e) {
			ErrorLogger.log(e.getMessage());
		} catch (InstantiationException e) {
			ErrorLogger.log(e.getMessage());
		} catch (IllegalAccessException e) {
			ErrorLogger.log(e.getMessage());
		} catch (UnsupportedLookAndFeelException e) {
			ErrorLogger.log(e.getMessage());
		}
	}
	
	private String getLocalName(String name) {
		return LocaleUtil.getLocalName(name);
	}

    private Object getCompo(String name) {
        return app.getAttribute( name );
    }
	
}
