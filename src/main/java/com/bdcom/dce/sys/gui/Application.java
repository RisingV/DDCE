package com.bdcom.dce.sys.gui;

import com.bdcom.dce.biz.scenario.ScenarioMgr;
import com.bdcom.dce.biz.script.ScriptExecutor;
import com.bdcom.dce.biz.script.ScriptMgr;
import com.bdcom.dce.itester.api.ITesterAPI;
import com.bdcom.dce.itester.api.JniAPIImpl;
import com.bdcom.dce.nio.client.ClientProxy;
import com.bdcom.dce.sys.AppContentAdaptor;
import com.bdcom.dce.sys.ApplicationConstants;
import com.bdcom.dce.sys.configure.PathConfig;
import com.bdcom.dce.sys.configure.ServerConfig;
import com.bdcom.dce.sys.service.Dialect;
import com.bdcom.dce.util.logger.ErrorLogger;
import com.bdcom.dce.view.*;
import com.bdcom.dce.view.itester.ITesterFrame;

import javax.swing.*;
import java.text.DateFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-9    <br/>
 * Time: 15:45  <br/>
 */
public class Application extends AppContentAdaptor implements GuiInterface, ApplicationConstants {

    public static final String CURRENT_DIR = RUN_TIME.CURRENT_DIR;

    public static final DateFormat DATE_FORMAT = RUN_TIME.DATE_FORMAT;

    public static Application instance;

    public static void main(String[] args) {
        Application.start();
    }

    public static void start() {
        instance = new Application();
        instance.run();
    }

    private Map<String, Object> attributes;

    private Application() {}

    private void run() {
        init();
        startSysService();
        startViewMgr();
    }

    private void init() {

        //int configure
        PathConfig pathConfig = new PathConfig( CURRENT_DIR );
        ServerConfig serverConfig = new ServerConfig( pathConfig );

        addAttribute( CONFIG.PATH_CONFIG, pathConfig );
        addAttribute( CONFIG.SERVER_CONFIG, serverConfig );

        //int logical compo
        ScriptMgr scriptMgr = new ScriptMgr( pathConfig );
        ScenarioMgr scenarioMgr = new ScenarioMgr((pathConfig));
        ClientProxy clientProxy = new ClientProxy( serverConfig );

        ScriptExecutor scriptExecutor = new ScriptExecutor(this);
        Dialect dialect = new Dialect(this);

        scriptMgr.reloadScripts();

        addAttribute( COMPONENT.SCRIPT_EXECUTOR, scriptExecutor );
        addAttribute( COMPONENT.SCRIPT_MGR, scriptMgr );
        addAttribute( COMPONENT.SCENARIO_MGR, scenarioMgr );
        addAttribute( COMPONENT.NIO_CLIENT, clientProxy );
        addAttribute( COMPONENT.DIALECT, dialect );

        //init util compo
        SubmitFrame submitFrame = new SubmitFrame(clientProxy, this);
        ScenarioMgrFrame scenarioMgrFrame = new ScenarioMgrFrame( clientProxy, this );
        ScriptMgrFrame scriptMgrFrame = new ScriptMgrFrame( clientProxy, this );
        ScriptList scriptList = new ScriptList(this);
        MsgTable msgTable = new MsgTable(this);

        // test code start
        /*
        PathConfig xpathConfig = new PathConfig(
                RUN_TIME.CURRENT_DIR + File.separator + "RPC-configure" );

        ServerConfig xserverConfig = new ServerConfig( xpathConfig );
        xserverConfig.setDefaultIP("172.16.22.222");
        xserverConfig.setDefaultPort( 7777 );
        xserverConfig.writeToConfigFile("172.16.22.222", "7777");
        ITesterAPI api = new RpcClient(xserverConfig);
        */
        ITesterAPI api = JniAPIImpl.getInstance();
        ITesterFrame iTesterFrame = new ITesterFrame( api, clientProxy, this );
        addAttribute( COMPONENT.ITESTER_API, api );
        addAttribute( COMPONENT.ITESTER_FRAME, iTesterFrame );
        // test code end

        addAttribute( COMPONENT.MSG_TABLE, msgTable );
        addAttribute( COMPONENT.SCRIPT_LIST, scriptList );
        addAttribute( COMPONENT.SCENARIO_MGR_FRAME, scenarioMgrFrame );
        addAttribute( COMPONENT.SUBMIT_FRAME, submitFrame );
        addAttribute( COMPONENT.SCRIPT_MGR_FRAME, scriptMgrFrame );

        //init toplevel frame
        LoginFrame loginFrame = new LoginFrame( clientProxy, this );
        MainFrame mainFrame = new MainFrame( this );
        loginFrame.setFrameAfterLogin( mainFrame );
        addAttribute( COMPONENT.LOGIN_FRAME, loginFrame );
        addAttribute( COMPONENT.MAIN_FRAME, mainFrame );

        //last
        ViewManager viewManager = new ViewManager(this);
        addAttribute( COMPONENT.VIEW_MGR, viewManager );
    }

    private void startSysService() {
        Dialect dialect = (Dialect) getAttribute(COMPONENT.DIALECT);

        dialect.startServer();
    }

    private void startViewMgr() {
        ViewManager viewManager = (ViewManager) getAttribute( COMPONENT.VIEW_MGR );
        viewManager.display();
    }

    public void registerCurrentDisplay(AbstractFrame frame) {
        addAttribute( COMPONENT.DISPLAYING_FRAME, frame );
    }

    public AbstractFrame getCurrentDisplay() {
        return (AbstractFrame) getAttribute( COMPONENT.DISPLAYING_FRAME );
    }


    public void terminal() {
        AbstractFrame displayingFrame = this.getCurrentDisplay();
        if ( null != displayingFrame ) {
            displayingFrame.hideFrame();
        }

        disconnectAndKillScriptExecutor();
        try {
            TimeUnit.MILLISECONDS.sleep(450);
        } catch (InterruptedException e) {
            ErrorLogger.log(e.getMessage());
        }
        System.exit(JFrame.NORMAL);
    }

    public void logout() {
        disconnectAndKillScriptExecutor();

        AbstractFrame mainFrame = (AbstractFrame) getAttribute( COMPONENT.MAIN_FRAME );
        mainFrame.hideFrame();

        init();

        AbstractFrame loginFrame = (AbstractFrame) getAttribute( COMPONENT.LOGIN_FRAME );
        loginFrame.display();
        System.gc();
    }

    private void disconnectAndKillScriptExecutor() {
        ClientProxy clientProxy = (ClientProxy)
                getAttribute( COMPONENT.NIO_CLIENT );
        if ( null != clientProxy ) {
            clientProxy.shutdown();
        }

        ScriptExecutor scriptExecutor = (ScriptExecutor)
                getAttribute( COMPONENT.SCRIPT_EXECUTOR );
        if ( null != scriptExecutor ) {
            scriptExecutor.killAllRunningScript();
        }
    }

//    public UserInfo getUserInfo() {
//        UserInfo userInfo = (UserInfo) getAttribute( USER.USER_INFO );
//        if ( null == userInfo ) {
//            String userNum = getStringAttr( USER.USER_NUM );
//            String userRank = getStringAttr( USER.USER_RANK );
//
//            userInfo = new UserInfo();
//            userInfo.setUserNum( userNum );
//            if ( USER.ROOT.equals( userRank ) ) {
//                userInfo.setSupervisor( true );
//            } else {
//                userInfo.setSupervisor( false );
//            }
//        }
//
//        return userInfo;
//    }

    public AbstractFrame getFrame(String name) {
        Object frame = getAttribute( name );
        return (AbstractFrame) frame;
    }

}
