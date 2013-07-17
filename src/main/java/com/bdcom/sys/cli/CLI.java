package com.bdcom.sys.cli;

import com.bdcom.biz.pojo.BaseTestRecord;
import com.bdcom.biz.pojo.UserInfo;
import com.bdcom.nio.BDPacket;
import com.bdcom.nio.DataType;
import com.bdcom.nio.RequestID;
import com.bdcom.nio.client.ClientPackChan;
import com.bdcom.nio.client.NIOClient;
import com.bdcom.sys.AppContentAdaptor;
import com.bdcom.sys.Applicable;
import com.bdcom.sys.ApplicationConstants;
import com.bdcom.sys.config.PathConfig;
import com.bdcom.sys.config.ServerConfig;
import com.bdcom.sys.service.Dialect;
import com.bdcom.util.SerializeUtil;
import com.bdcom.util.log.ErrorLogger;

import java.io.IOException;
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
        BaseTestRecord record = parseCMD(args);
        ClientPackChan chan = startDialect();

        Map<String, Object> infomap = readExtraInfo(chan);
        Boolean isFC = (Boolean) infomap.get(BASE_TEST.IS_FC);
        record.setFC( null!=isFC && isFC.booleanValue() );

        BDPacket sendStatus = sendBaseReord(record);
        chan.sendPacket( sendStatus );

        startExitTimer();
        try {
            chan.receivePacket();  // make sure GUI Client receive send status report
        } catch (InterruptedException e) {
            String msg = "From CLI Application: report sending status interrupted: "
                    + e.getMessage();
            ErrorLogger.log( msg );
        }

        terminal();
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
            infomap = (Map<String, Object>)
                    SerializeUtil.deserializeFromByteArray(data);
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

    @Override
    public UserInfo getUserInfo() {
        //do nothing
        return null;
    }

}

