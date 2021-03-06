package com.bdcom.dce.sys.cli;

import com.bdcom.dce.biz.pojo.BaseTestRecord;
import com.bdcom.dce.biz.pojo.TestTypeRecord;
import com.bdcom.dce.nio.BDPacket;
import com.bdcom.dce.nio.BDPacketUtil;
import com.bdcom.dce.nio.DataType;
import com.bdcom.dce.nio.RequestID;
import com.bdcom.dce.nio.client.ClientPackChan;
import com.bdcom.dce.nio.client.NIOClient;
import com.bdcom.dce.nio.exception.GlobalException;
import com.bdcom.dce.nio.exception.ResponseException;
import com.bdcom.dce.sys.AppContentAdaptor;
import com.bdcom.dce.sys.Applicable;
import com.bdcom.dce.sys.ApplicationConstants;
import com.bdcom.dce.sys.configure.PathConfig;
import com.bdcom.dce.sys.configure.ServerConfig;
import com.bdcom.dce.sys.service.Dialect;
import com.bdcom.dce.sys.service.DialectUtil;
import com.bdcom.dce.util.SerializeUtil;
import com.bdcom.dce.util.logger.ErrorLogger;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-17    <br/>
 * Time: 10:56  <br/>
 */
public class CLI extends AppContentAdaptor implements Applicable, ApplicationConstants {

    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.start(args);
    }

    private CliParser cliParser;

    private Dialect dialect;

    private NIOClient client;

    public CLI() {
        initContent();
        initCompos();
    }

    public void start(String[] args) {
        ClientPackChan chan = startDialect();

        Map<String, Object> infomap = readExtraInfo(chan);
        if ( null != infomap ) {
            if ( infoMapValidation( infomap ) ) {
                Integer testType = (Integer) infomap.get( TEST_ATTR.TEST_TYPE );
                TestTypeRecord.setCurrentTestType( testType );

                BaseTestRecord record = parseCMD(args);
                Boolean isFC = (Boolean) infomap.get(TEST_ATTR.IS_FC);
                String userNum = (String) infomap.get( TEST_ATTR.TESTER_NUM );
                record.setFC( null!=isFC && isFC.booleanValue() );
                record.setTesterNum( userNum );

                BDPacket sendStatus = sendBaseReord(record);

                int status = 0;
                try {
                    status = BDPacketUtil.parseIntResponse(sendStatus,
                            sendStatus.getRequestID());
                } catch (ResponseException e) {
                    //TODO
                } catch (GlobalException e) {
                    //TODO
                }

                BDPacket response = DialectUtil.generateResponseToUIClient( record, status );
                chan.sendPacket( response );

                startExitTimer();
                try {
                    chan.receivePacket();  //making sure GUI Client receive send status report
                } catch (InterruptedException e) {
                    String msg = "From CLI Application: report sending status interrupted: "
                            + e.getMessage();
                    ErrorLogger.log( msg );
                }
            } else {
                System.out.println( "can't get enough information from GUI-client!" );
            }
        } else {
            System.out.println( "There is no running script on gui-client" );
        }

        terminal();
    }

    private boolean infoMapValidation(Map<String, Object> infomap) {
        return ( null != infomap ) &&
               ( infomap.containsKey( TEST_ATTR.IS_FC ) ) &&
               ( infomap.containsKey( TEST_ATTR.TEST_TYPE ) ) &&
               ( infomap.containsKey( TEST_ATTR.TESTER_NUM) );
    }

    private void initContent() {
        PathConfig pathConfig = new PathConfig( RUN_TIME.CURRENT_DIR );
        ServerConfig serverConfig = new ServerConfig(pathConfig);

        addAttribute( CONFIG.PATH_CONFIG, pathConfig );
        addAttribute( CONFIG.SERVER_CONFIG, serverConfig );
    }

    private void initCompos() {
        cliParser = new CliParser();
        dialect = new Dialect(this);

        ServerConfig serverConfig = (ServerConfig)
                getAttribute( CONFIG.SERVER_CONFIG );

        client = new NIOClient(serverConfig);
        try {
            client.start();
        } catch (IOException e) {
            //TODO
        }
    }

    private BaseTestRecord parseCMD(String[] args) {
        BaseTestRecord record = cliParser.parse(args);
        if ( record.isHelpFlag() ) {
            terminal();
        }
        return record;
    }

    private ClientPackChan startDialect() {
        ClientPackChan chan = null;
        try {
            chan = dialect.startClient();
        } catch (IOException e) {
            String msg = "From CLI Application: Dialect Client start fail! duo to: " +
                    e.getMessage();
            ErrorLogger.log(msg);
            terminal();
        }

        return chan;
    }

    private Map<String, Object> readExtraInfo(ClientPackChan chan) {
        chan.sendPacket(
                BDPacket.newPacket(
                        RequestID.LOCAL.READ_EXTRA_INFO
                )
        );

        BDPacket extraInfo = null;
        try {
            extraInfo = chan.receivePacket();
        } catch (InterruptedException e) {
            String msg = "From CLI Application: read extra info interrupted: "
                    + e.getMessage();
            ErrorLogger.log(msg);
            terminal();
        }

        byte[] data = extraInfo.getData();
        Map<String, Object> infomap = null;
        try {
            infomap = deserialize( data );
        } catch (IOException e) {
            String msg = "From CLI Application: deserialize infomap fail! :"
                    + e.getMessage();
            ErrorLogger.log( msg );
        } catch (ClassNotFoundException e) {
            //not happen
            ErrorLogger.log( e.getMessage() );
        }

        return infomap;
    }

    private Map<String, Object> deserialize(byte[] data) throws IOException, ClassNotFoundException {
        Object obj = SerializeUtil.deserializeFromByteArray(data);
        Map<String, Object> map = null;
        if ( obj instanceof ConnectException ) {
            System.out.println( "Can't connect to dialect service of gui client!" );
            terminal(); //exit point
        } else if ( obj instanceof Map) {
            map = (Map<String, Object>) obj;
        } else {
            terminal(); //exit point
        }

        return map;
    }

    private BDPacket sendBaseReord(BaseTestRecord record)  {
        byte[] data = null;
        try {
            data = SerializeUtil.serializeToByteArray(record);
        } catch (IOException e) {
            String msg = "From CLI Application: serialize BaseTestRecord fail! :"
                    + e.getMessage();
            ErrorLogger.log( msg );
            terminal();
        }

        BDPacket sendReq = BDPacket.newPacket( RequestID.SEND_BASE_TEST_REC );
        sendReq.setDataType( DataType.BASE_TEST_RECORD );
        sendReq.setData( data );

        client.sendPacket( sendReq );

        BDPacket sendStatus = null;
        try {
            sendStatus = client.receivePacket();
        } catch (InterruptedException e) {
            String msg = "From CLI Application: receive send status interrupted: "
                    + e.getMessage();
            ErrorLogger.log( msg );
        }

        return sendStatus;
    }

    private void startExitTimer() {
        Thread timerThread = new Thread() {
            {
                setDaemon( true );
            }
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep( 2 );
                } catch (InterruptedException e) {
                }
                terminal();
            }
        };
        timerThread.start();
    }


    @Override
    public void terminal() {
        if ( null != dialect ) {
            dialect.CloseClient();
        }
        if ( null != client ) {
            client.shutdown();
        }
        System.exit(0);
    }

    @Override
    public void logout() {
        //do nothing
    }

}

