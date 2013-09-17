package com.bdcom.dce.sys.gui;

import com.bdcom.dce.biz.scenario.ScenarioMgr;
import com.bdcom.dce.biz.script.ScriptExecutor0;
import com.bdcom.dce.biz.script.ScriptMgr;
import com.bdcom.dce.biz.storage.StorableMgr;
import com.bdcom.dce.biz.storage.StoreMgr;
import com.bdcom.dce.itester.api.ITesterAPI;
import com.bdcom.dce.itester.api.JniAPIImpl;
import com.bdcom.dce.nio.client.ClientProxy;
import com.bdcom.dce.sys.AppContentAdaptor;
import com.bdcom.dce.sys.ApplicationConstants;
import com.bdcom.dce.sys.configure.PathConfig;
import com.bdcom.dce.sys.configure.ScriptEnvConfig;
import com.bdcom.dce.sys.configure.ServerConfig;
import com.bdcom.dce.sys.service.AutoDownloadService;
import com.bdcom.dce.sys.service.Dialect;
import com.bdcom.dce.util.logger.ErrorLogger;
import com.bdcom.dce.view.AbstractFrame;
import com.bdcom.dce.view.LoginFrame;
import com.bdcom.dce.view.MainFrame;
import com.bdcom.dce.view.ViewManager;
import com.bdcom.dce.view.itester.ITesterFrame;
import com.bdcom.dce.view.message.MessageRecorder;
import com.bdcom.dce.view.message.MessageRecorderImpl;
import com.bdcom.dce.view.message.MessageTable;
import com.bdcom.dce.view.message.SubmitHistoryTable;
import com.bdcom.dce.view.resource.ResourceMgrFrame;
import com.bdcom.dce.view.scripttest.ScriptEnvConfigDialog;
import com.bdcom.dce.view.scripttest.ScriptTestFrame;

import javax.swing.*;
import java.text.DateFormat;
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
        ScriptEnvConfig scriptEnvConfig = ScriptEnvConfig.getInstance(pathConfig);

        addAttribute( CONFIG.PATH_CONFIG, pathConfig );
        addAttribute( CONFIG.SERVER_CONFIG, serverConfig );
        addAttribute( CONFIG.SCRIPT_ENV_CONFIG, scriptEnvConfig );

        //int logical compo
        StorableMgr storableMgr = new StoreMgr( pathConfig );
        ScriptMgr scriptMgr = new ScriptMgr( pathConfig );
        ScenarioMgr scenarioMgr = new ScenarioMgr((pathConfig));
        ClientProxy clientProxy = new ClientProxy( serverConfig );
        AutoDownloadService autoDownloadService = new AutoDownloadService( clientProxy, storableMgr );

        //ScriptExecutor scriptExecutor = new ScriptExecutor(this);
        ScriptExecutor0 scriptExecutor = new ScriptExecutor0( this );
        Dialect dialect = new Dialect(this);

        scriptMgr.reloadScripts();

        addAttribute( COMPONENT.STORABLE_MGR, storableMgr );
        addAttribute( COMPONENT.SCRIPT_EXECUTOR, scriptExecutor );
        addAttribute( COMPONENT.SCRIPT_MGR, scriptMgr );
        addAttribute( COMPONENT.SCENARIO_MGR, scenarioMgr );
        addAttribute( COMPONENT.NIO_CLIENT, clientProxy );
        addAttribute( COMPONENT.DIALECT, dialect );
        addAttribute( COMPONENT.AUTO_DOWNLOAD_SERVICE, autoDownloadService );

        //init util compo
        ViewManager.initLookAndFeel(); // set global lookAndFeel first!

        //SubmitFrame submitFrame = new SubmitFrame(clientProxy, this);
        ///ScenarioMgrFrame scenarioMgrFrame = new ScenarioMgrFrame( clientProxy, this );
        //ScriptMgrFrame scriptMgrFrame = new ScriptMgrFrame( clientProxy, this );
        //ScriptList scriptList = new ScriptList(this);
        ScriptTestFrame scriptTestFrame = new ScriptTestFrame(this);
        ResourceMgrFrame resourceMgrFrame = new ResourceMgrFrame(this);
        //MsgTable msgTable = new MsgTable(this);

        MessageTable messageTable = new MessageTable();
        SubmitHistoryTable submitHistoryTable = new SubmitHistoryTable(this);
        MessageRecorder messageRecorder = new MessageRecorderImpl( messageTable, submitHistoryTable );
        ScriptEnvConfigDialog scriptEnvConfigDialog = new ScriptEnvConfigDialog( this, null );

        addAttribute( COMPONENT.MESSAGE_TABLE, messageTable );
        addAttribute( COMPONENT.SUBMIT_HISTORY_TABLE, submitHistoryTable );
        addAttribute( COMPONENT.MESSAGE_RECORDER, messageRecorder );
        addAttribute( COMPONENT.SCRIPT_ENV_CONFIG_DIALOG, scriptEnvConfigDialog );

        ITesterAPI api = JniAPIImpl.getInstance();
        ITesterFrame iTesterFrame = new ITesterFrame( api, clientProxy, this );
        addAttribute( COMPONENT.ITESTER_API, api );
        addAttribute( COMPONENT.ITESTER_FRAME, iTesterFrame );

        //addAttribute( COMPONENT.MSG_TABLE, msgTable );
        //addAttribute( COMPONENT.SCRIPT_LIST, scriptList );
        addAttribute( COMPONENT.SCRIPT_TEST_FRAME, scriptTestFrame );
        addAttribute( COMPONENT.RESOURCE_LIST, resourceMgrFrame );
        //addAttribute( COMPONENT.SCENARIO_MGR_FRAME, scenarioMgrFrame );
        //addAttribute( COMPONENT.SUBMIT_FRAME, submitFrame );
        //addAttribute( COMPONENT.SCRIPT_MGR_FRAME, scriptMgrFrame );

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

        AutoDownloadService autoDownloadService =
                (AutoDownloadService) getAttribute( COMPONENT.AUTO_DOWNLOAD_SERVICE );
        autoDownloadService.start();
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
            displayingFrame.close();
        }

        disconnectAndKillScriptExecutor();
        saveResources();
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
        mainFrame.close();

        init();

        AbstractFrame loginFrame = (AbstractFrame) getAttribute( COMPONENT.LOGIN_FRAME );
        loginFrame.display();
        System.gc();
    }

    private void saveResources() {
        StorableMgr mgr = (StorableMgr) getAttribute( COMPONENT.STORABLE_MGR );
        if ( null != mgr && mgr.isStorageLoaded() ) {
            mgr.saveToLocalStorage();
        }
    }

    private void disconnectAndKillScriptExecutor() {
        ClientProxy clientProxy = (ClientProxy)
                getAttribute( COMPONENT.NIO_CLIENT );
        if ( null != clientProxy ) {
            clientProxy.shutdown();
        }

        ScriptExecutor0 scriptExecutor = (ScriptExecutor0)
                getAttribute( COMPONENT.SCRIPT_EXECUTOR );
        if ( null != scriptExecutor ) {
            scriptExecutor.killAllRunningScript();
        }
    }

    public AbstractFrame getFrame(String name) {
        Object frame = getAttribute( name );
        return (AbstractFrame) frame;
    }

}
