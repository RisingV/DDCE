package com.bdcom.dce.nio.server;

import com.bdcom.dce.nio.RequestID;
import com.bdcom.dce.nio.bdpm.PMInterface;

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

    private NIOServer nioServer;

    private Map<Integer, IHandler> servMap;

    private final PMInterface pmInterface;

    public ServerStarter(PMInterface pmInterface) {
        this.pmInterface = pmInterface;
        initServMap( this.pmInterface );
    }

    private void initServMap(final PMInterface pm ) {
        servMap = new HashMap<Integer, IHandler>() {
            {
                put( RequestID.LOGIN, new LoginHandler(pm) );
                put( RequestID.SEND_BASE_TEST_REC, new BaseTestHandler(pm) );
                put( RequestID.SEND_I_TESTER_REC, new ITesterHandler(pm) );
                put( RequestID.UPLOAD_SCENARIO, new ScenarioReceiveHandler(pm) );
                put( RequestID.GET_SCENARIO_NAME_LIST, new ScenarioNameListHandler(pm) );
                put( RequestID.DOWNLOAD_SCENARIO, new ScenarioDownloadHandler(pm) );
                put( RequestID.DELETE_BACKUP_SCENARIOS, new ScenarioDeleteHandler(pm) );
                put( RequestID.UPLOAD_SCRIPT, new ScriptReceiveHandler(pm) );
                put( RequestID.GET_SCRIPT_FILE_LIST, new ScriptFileListHandler(pm) );
                put( RequestID.DOWNLOAD_SCRIPT, new ScriptDownloadHandler(pm) );
                put( RequestID.DELETE_BACKUP_SCRIPTS, new ScriptDeleteHandler(pm) );
                put( RequestID.UPLOAD_LOCAL_STORAGE, new UploadResourceHandler(pm) );
                put( RequestID.DOWNLOAD_LOCAL_STORAGE, new DownloadResourceHandler(pm) );
                put( RequestID.GET_COMPLETE_SERIAL, new GetCompleteSerialHandler(pm) );
                put( RequestID.ECHO, new EchoHandler(pm) );
                put( RequestID.TERMINAL, new TerminalHandler() );
            }
        };
    }

    private void start() throws IOException {
        int port = pmInterface.getNIOServerPort();
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
