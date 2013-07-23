package com.bdcom.nio.server;

import com.bdcom.nio.ServerContent;
import com.bdcom.nio.RequestID;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-5    <br/>
 * Time: 15:41  <br/>
 */
public class ServerStarter implements Runnable {

    private int port;

    private NIOServer nioServer;

    private Map<Integer, IHandler> servMap;

    public ServerStarter() {
        port = ServerContent.getNIOServerPort();
        servMap = new HashMap<Integer, IHandler>() {
            {
                put( RequestID.LOGIN, new LoginHandler() );
                put( RequestID.SEND_BASE_TEST_REC, new BaseTestHandler() );
                put( RequestID.SEND_I_TESTER_REC, new ITesterHandler() );
                put( RequestID.UPLOAD_SCENARIO, new ScenarioReceiveHandler() );
                put( RequestID.GET_SCENARIO_NAME_LIST, new ScenarioNameListHandler() );
                put( RequestID.DOWNLOAD_SCENARIO, new ScenarioDownloadHandler() );
                put( RequestID.DELETE_BACKUP_SCENARIOS, new ScenarioDeleteHandler() );
                put( RequestID.UPLOAD_SCRIPT, new ScriptReceiveHandler() );
                put( RequestID.GET_SCRIPT_FILE_LIST, new ScriptFileListHandler() );
                put( RequestID.DOWNLOAD_SCRIPT, new ScriptDownloadHandler() );
                put( RequestID.DELETE_BACKUP_SCRIPTS, new ScriptDeleteHandler() );
                put( RequestID.ECHO, new EchoHandler() );
                put( RequestID.TERMINAL, new TerminalHandler() );
            }
        };
    }

    private void start() throws IOException {
        nioServer = new NIOServer( port );
        nioServer.setHandlerMap( servMap );
        nioServer.start();
    }

    @Override
    public void run() {
        try {
            start();
        } catch (IOException e) {
            System.err.println( "NIOServer start failed due to: " + e.getMessage() );
        }
    }
}
