package com.bdcom.service;

import com.bdcom.clientview.*;
import com.bdcom.nio.client.ClientProxy;
import com.bdcom.pojo.UserInfo;
import com.bdcom.service.scenario.ScenarioMgr;
import com.bdcom.util.log.ErrorLogger;
import com.bdcom.service.script.ScriptExecutor;
import com.bdcom.service.script.ScriptMgr;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-9    <br/>
 * Time: 15:45  <br/>
 */
public abstract class Application implements ApplicationConstants {

    public static final String CURRENT_DIR = System.getProperty("user.dir");

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH#mm#ss");

    private static Map<String, Object> attributes;

    private static ClientProxy nioClientProxy;

    public static void main(String[] args) {
        Application.start();
    }

    public static void start() {
        init();
        ViewManager viewManager = (ViewManager) getAttribute( COMPONENT.VIEW_MGR );
        viewManager.display();
    }

    public static void init() {
        addAttribute( COMPONENT.VIEW_MGR, new ViewManager() );
        addAttribute( COMPONENT.MAIN_FRAME, new Frame() );
        addAttribute( COMPONENT.LOGIN_FRAME, new LoginFrame() );
        addAttribute( COMPONENT.SCENARIO_MGR_FRAME, new ScenarioMgrFrame() );
        addAttribute( COMPONENT.SUBMIT_FRAME, new SubmitFrame() );
        addAttribute( COMPONENT.SCRIPT_MGR_FRAME, new ScriptMgrFrame() );
        addAttribute( COMPONENT.SCRIPT_LIST, new ScriptList() );
        addAttribute( COMPONENT.SCRIPT_EXECUTOR, new ScriptExecutor() );
        addAttribute( COMPONENT.SCENARIO_MGR, new ScenarioMgr() );
        addAttribute( COMPONENT.SCRIPT_MGR, new ScriptMgr());
    }

    public static void registerCurrentDisplay(AbstractFrame frame) {
        addAttribute( COMPONENT.DISPLAYING_FRAME, frame );
    }

    public static AbstractFrame getCurrentDisplay() {
        return (AbstractFrame) getAttribute( COMPONENT.DISPLAYING_FRAME );
    }


    public static void terminal() {
        disconnectionAndKillScriptExecutor();
        try {
            TimeUnit.MILLISECONDS.sleep(450);
        } catch (InterruptedException e) {
            ErrorLogger.log(e.getMessage());
        }
        System.exit(JFrame.NORMAL);
    }

    public static void logOut() {
        disconnectionAndKillScriptExecutor();

        AbstractFrame mainFrame = (AbstractFrame) getAttribute( COMPONENT.MAIN_FRAME );
        mainFrame.hideFrame();

        init();

        AbstractFrame loginFrame = (AbstractFrame) getAttribute( COMPONENT.LOGIN_FRAME );
        loginFrame.display();
        System.gc();
    }

    private static void disconnectionAndKillScriptExecutor() {
        if ( null != nioClientProxy ) {
            nioClientProxy.shutdown();
        }

        ScriptExecutor scriptExecutor = (ScriptExecutor) getAttribute( COMPONENT.SCRIPT_EXECUTOR );
        scriptExecutor.killAllRunningScript();
    }

    public static void setNioClientProxy(String ip, int port) {
        if ( null != nioClientProxy ) {
            nioClientProxy.shutdown();
        }
        nioClientProxy = new ClientProxy(ip, port);
    }

    public static void setNioClientProxy(String ip, String port) {
        int portInt = Integer.parseInt( port );
        setNioClientProxy( ip, portInt );
    }

    public static ClientProxy getNioClientProxy() {
        return nioClientProxy;
    }

    public static UserInfo getUserInfo() {
        UserInfo userInfo = (UserInfo) getAttribute( USER.USER_INFO );
        if ( null == userInfo ) {
            String userNum = getStringAttr( USER.USER_NUM );
            String userRank = getStringAttr( USER.USER_RANK );

            userInfo = new UserInfo();
            userInfo.setUserNum( userNum );
            if ( USER.ROOT.equals( userRank ) ) {
                userInfo.setSupervisor( true );
            } else {
                userInfo.setSupervisor( false );
            }
        }

        return userInfo;
    }

    public static void addAttribute(String name, Object attr) {
        if ( null == attributes ) {
            synchronized ( Application.class ) {
                if ( null == attributes ) {
                    attributes = new ConcurrentHashMap<String, Object>();
                }
            }
        }
        attributes.put(name, attr);
    }

    public static Object getAttribute(String name) {
        if ( null == attributes ) {
            return null;
        }
        return attributes.get( name );
    }

    public static String getStringAttr(String name) {
        return (String) getAttribute( name );
    }

    public static boolean getBoolAttr(String name) {
        Object bool = getAttribute(name);
        if ( null == bool ) {
            return false;
        }
        return ((Boolean) bool).booleanValue();
    }

}
