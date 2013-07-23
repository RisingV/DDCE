package com.bdcom.sys.service;

import com.bdcom.biz.script.ScriptExecutor;
import com.bdcom.nio.exception.GlobalException;
import com.bdcom.view.util.MsgDialogUtil;
import com.bdcom.nio.exception.ResponseException;
import com.bdcom.nio.BDPacket;
import com.bdcom.nio.BDPacketUtil;
import com.bdcom.nio.DataType;
import com.bdcom.nio.RequestID;
import com.bdcom.nio.client.ClientPackChan;
import com.bdcom.nio.client.NIOClient;
import com.bdcom.nio.server.IHandler;
import com.bdcom.nio.server.NIOServer;
import com.bdcom.sys.Applicable;
import com.bdcom.sys.ApplicationConstants;
import com.bdcom.sys.config.PathConfig;
import com.bdcom.sys.config.ServerConfig;
import com.bdcom.util.SerializeUtil;
import com.bdcom.util.log.ErrorLogger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-16    <br/>
 * Time: 11:50  <br/>
 */
public class Dialect implements ApplicationConstants {

    private static final String HOST = "127.0.0.1";

    private static final int PORT = 9998;

    private NIOServer server;

    private NIOClient client;

    private final Applicable app;

    public Dialect(Applicable app) {
        this.app = app;
        generateLocalServerConfig();
    }

    private Map<Integer, IHandler> servMap = new HashMap<Integer, IHandler>() {
        {
            put( RequestID.LOCAL.REPORT_SENDING_RESULT, new SendingResultHandler() );
            put( RequestID.LOCAL.READ_EXTRA_INFO, new ReadExtraInfoHandler() );
        }
    };

    public void startServer() {
        server = new NIOServer(PORT);
        server.setHandlerMap(servMap);
        Thread servThread = new Thread() {
            { setDaemon( true );}

            @Override
            public void run() {
                try {
                    server.start();
                } catch (IOException e) {
                    MsgDialogUtil.showErrorDialog("Dialect start fail!");
                    ErrorLogger.log("Dialect start fail!: " + e.getMessage() );
                    app.terminal();
                }
            }
        };

        servThread.start();
    }

    public ClientPackChan startClient() throws IOException {
        ServerConfig serverConfig = (ServerConfig)
                app.getAttribute( CONFIG.LOCAL_SERVER_CONFIG );
        client = new NIOClient(serverConfig);
        client.start();

        return client;
    }

    public void CloseClient() {
        BDPacket closeReq = BDPacketUtil.terminalRequest();
        client.sendPacket( closeReq );

        try {
            TimeUnit.MILLISECONDS.sleep( 333 );
        } catch (InterruptedException e) {
        } finally {
            client.shutdown();
        }

    }

    private void generateLocalServerConfig() {
        String tmpDir = RUN_TIME.CURRENT_DIR + File.separator + "tmp";
        PathConfig pathConfig = new PathConfig( tmpDir );
        ServerConfig localServerConfig = new ServerConfig(pathConfig);

        localServerConfig.setDefaultIP( HOST );
        localServerConfig.setDefaultPort( PORT );

        app.addAttribute( CONFIG.LOCAL_SERVER_CONFIG, localServerConfig );
    }

    class SendingResultHandler implements IHandler {
        @Override
        public BDPacket handle(BDPacket bdPacket) {
            boolean isSuccess = true;
            String msg = null;
            int status = -1;
            try {
                status = BDPacketUtil.parseIntResponse(bdPacket,
                        bdPacket.getRequestID());
            } catch (ResponseException e) {
                isSuccess = false;
                msg = e.getMessage();
                ErrorLogger.log(msg);
            } catch (GlobalException e) {
                //TODO needs to be properly handled
                e.printStackTrace();
            } finally {
                if ( !isSuccess ) {
                    MsgDialogUtil.showErrorDialog( msg );
                }
            }

            //temporary solution!!!!
            ScriptExecutor scriptExecutor =  (ScriptExecutor)
                   app.getAttribute(COMPONENT.SCRIPT_EXECUTOR);
            scriptExecutor.setSendResult( true, String.valueOf( status) );

            return BDPacketUtil.emptyResponse( bdPacket.getRequestID() );
        }
    }

    class ReadExtraInfoHandler implements IHandler {

        @Override
        public BDPacket handle(BDPacket bdPacket) {

            Map<String, Object> infomap = new HashMap<String, Object>();
            boolean isFC = app.getBoolAttr( BASE_TEST.IS_FC );
            infomap.put(BASE_TEST.IS_FC, Boolean.valueOf( isFC ));

            byte[] data = null;
            try {
                data = SerializeUtil.serializeToByteArray(infomap);
            } catch (IOException e) {
                //Mostly not happen!
                ErrorLogger.log(e.getMessage());
            }

            BDPacket response = BDPacket.newPacket(bdPacket.getRequestID());
            response.setDataType(DataType.MAP);
            response.setData(data);

            return response;
        }
    }

}
