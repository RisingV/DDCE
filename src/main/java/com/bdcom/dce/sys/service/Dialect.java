package com.bdcom.dce.sys.service;

import com.bdcom.dce.biz.pojo.BaseTestRecord;
import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.BDPacketUtil;
import com.bdcom.dce.nio.DataType;
import com.bdcom.dce.nio.RequestID;
import com.bdcom.dce.nio.client.ClientPackChan;
import com.bdcom.dce.nio.client.NIOClient;
import com.bdcom.dce.nio.server.IHandler;
import com.bdcom.dce.nio.server.NIOServer;
import com.bdcom.dce.sys.Applicable;
import com.bdcom.dce.sys.ApplicationConstants;
import com.bdcom.dce.sys.configure.PathConfig;
import com.bdcom.dce.sys.configure.ServerConfig;
import com.bdcom.dce.util.SerializeUtil;
import com.bdcom.dce.util.logger.ErrorLogger;
import com.bdcom.dce.view.message.MessageRecorder;
import com.bdcom.dce.view.util.MsgDialogUtil;

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
        if ( null == client ) {
            return;
        }
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
        localServerConfig.setPort( PORT );
        localServerConfig.writeToConfigFile( HOST, String.valueOf( PORT ) );

        app.addAttribute( CONFIG.LOCAL_SERVER_CONFIG, localServerConfig );
    }

    class SendingResultHandler implements IHandler {
        @Override
        public BDPacket handle(BDPacket bdPacket) {
            boolean isSuccess = true;
//            String msg = null;
//            int status = -1;
//            try {
//                status = BDPacketUtil.parseIntResponse(bdPacket,
//                        bdPacket.getRequestID());
//            } catch (ResponseException e) {
//                isSuccess = false;
//                msg = e.getMessage();
//                ErrorLogger.log(msg);
//            } catch (GlobalException e) {
//                //TODO needs to be properly handled
//                e.printStackTrace();
//            } finally {
//                if ( !isSuccess ) {
//                    MsgDialogUtil.showErrorDialog( msg );
//                }
//            }

            //temporary solution!!!!
//            ScriptExecutor scriptExecutor =  (ScriptExecutor)
//                   app.getAttribute(COMPONENT.SCRIPT_EXECUTOR);
//            scriptExecutor.setSendResult( true, String.valueOf( status) );

            Map<String, Object> infoMap = DialectUtil.parseResponseFromCLI( bdPacket );
            BaseTestRecord record = (BaseTestRecord) infoMap.get( DIALECT.BASE_RECORD );
            Integer status = (Integer) infoMap.get( DIALECT.SEND_STATUS );
            MessageRecorder recorder = (MessageRecorder)
                    app.getAttribute( COMPONENT.MESSAGE_RECORDER );
            if ( null != recorder ) {
                recorder.addBaseTestRecord( record, status, "" );
            }

            return BDPacketUtil.emptyResponse( bdPacket.getRequestID() );
        }
    }

    class ReadExtraInfoHandler implements IHandler {

        @Override
        public BDPacket handle(BDPacket bdPacket) {

            Map<String, Object> infomap = new HashMap<String, Object>();

            boolean isFC = app.getBoolAttr( TEST_ATTR.IS_FC );
            Integer testType = (Integer) app.getAttribute( TEST_ATTR.TEST_TYPE );
            String testerNum = app.getStringAttr( USER.USER_NUM );

            infomap.put( TEST_ATTR.IS_FC, Boolean.valueOf( isFC ) );
            infomap.put( TEST_ATTR.TEST_TYPE, testType );
            infomap.put( TEST_ATTR.TESTER_NUM, testerNum );

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
