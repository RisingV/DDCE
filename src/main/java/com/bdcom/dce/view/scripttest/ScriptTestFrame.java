package com.bdcom.dce.view.scripttest;

import com.bdcom.dce.biz.pojo.BaseTestRecord;
import com.bdcom.dce.biz.pojo.Scenario;
import com.bdcom.dce.biz.pojo.Script;
import com.bdcom.dce.biz.pojo.TestTypeRecord;
import com.bdcom.dce.biz.scenario.ScenarioUtil;
import com.bdcom.dce.biz.script.ScriptExecutor0;
import com.bdcom.dce.biz.storage.Item;
import com.bdcom.dce.biz.storage.StorableMgr;
import com.bdcom.dce.nio.client.ClientProxy;
import com.bdcom.dce.nio.exception.GlobalException;
import com.bdcom.dce.nio.exception.ResponseException;
import com.bdcom.dce.sys.AppContent;
import com.bdcom.dce.sys.ApplicationConstants;
import com.bdcom.dce.sys.configure.ScriptEnvConfig;
import com.bdcom.dce.util.LocaleUtil;
import com.bdcom.dce.util.StringUtil;
import com.bdcom.dce.util.logger.ErrorLogger;
import com.bdcom.dce.view.ViewTab;
import com.bdcom.dce.view.util.GBC;
import com.bdcom.dce.view.util.MsgDialogUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-11    <br/>
 * Time: 14:23  <br/>
 */
public class ScriptTestFrame extends JPanel
        implements ViewTab, ApplicationConstants  {

    private static final long serialVersionUID = 7509732886880433565L;

    private static final String SERIAL_NUM = "serial number";
    private static final String EXECUTE = "execute";
    private static final String EXECUTING = "executing...";
    private static final String CANT_FIND_FILE = "can't find file:";
    private static final String EXECUTOR_INTERRUPTED = "Script executor interrupted!";
    private static final String SCRIPT_EXECUTE_FINISHED = "Script executing finished!";
    private static final String SERIAL_MATCHING = "Serial Matching";
    private static final String EXECUTE_SESSIONS = "Execute Sessions";
    private static final String BLANK_SERIAL = "serial can't be blank!";
    private static final String NO_SESSION_SELECTED = "please select execute sessions!";

    private final AppContent app;

    private JPanel frame = this;
    private JLabel serialLabel;
    private JTextField serialField;
    private JCheckBox[] sessionCBoxes;

    private JButton executeBt;

    private JPanel serialPane;
    private JPanel crtSessionPane;
    private JPanel extraPane;

    private ScenarioDisplayDialog scenarioDialog;

    public ScriptTestFrame(AppContent app) {
        this.app = app;
        initUI();
    }

    private void initUI() {
        initButtons();
        initDialogs();
        initSerialPane();
        initCrtSessionPane();
    }

    private void initSerialPane() {
        String serialNum = LocaleUtil.getLocalName( SERIAL_NUM );
        String serialMatching = LocaleUtil.getLocalName( SERIAL_MATCHING );
        serialLabel = new JLabel( serialNum );
        serialField.setPreferredSize( new Dimension( 350, 20 ));

        Border titledBorder = BorderFactory.createTitledBorder( serialMatching );
        serialPane = new JPanel();
        serialPane.setBorder( titledBorder );
        serialPane.setLayout( new GridBagLayout() );
        serialPane.add( serialLabel, new GBC(0, 0).setInsets(5, 10, 5, 10) );
        serialPane.add( serialField, new GBC(1, 0).setInsets(5, 10, 5, 10) );
        serialPane.add( executeBt, new GBC(2, 0).setInsets( 5, 10, 5, 10 ) );

    }

    private void initCrtSessionPane() {
        String executeSessions = LocaleUtil.getLocalName( EXECUTE_SESSIONS );
        ScriptEnvConfig config = getScriptEnvConfig( app );
        String[] crtSessions = config.getCrtSessions();

        Border titledBorder = BorderFactory.createTitledBorder( executeSessions );
        crtSessionPane = new JPanel();
        crtSessionPane.setBorder( titledBorder );
        crtSessionPane.setLayout( new GridBagLayout() );
        for (int i = 0; i < crtSessions.length; i++ ) {
            String session = crtSessions[i];
            JCheckBox jcb = new JCheckBox( session );
            jcb.setName( session );
            sessionCBoxes[i] = jcb;
            crtSessionPane.add( jcb, new GBC(i,0).setInsets( 5, 10, 5, 10 ) );
        }
    }

    private void initDialogs() {
        scenarioDialog = new ScenarioDisplayDialog( frame );
    }

    private void initButtons() {
        String execute = LocaleUtil.getLocalName( EXECUTE );
        executeBt = new JButton( execute );
        executeBt.setPreferredSize( new Dimension( 70, 20 ) );
        executeBt.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doExecute();
            }
        });
    }

    private void doExecute() {
        new Thread() {
            @Override
            public void run() {
                if ( inputValidation() ) {
                    try {
                        String executing = LocaleUtil.getLocalName( EXECUTING );
                        executeBt.setEnabled( false );
                        executeBt.setText( executing );

                        doExecute0();
                    } finally {
                        String execute = LocaleUtil.getLocalName( EXECUTE );
                        executeBt.setEnabled( true );
                        executeBt.setText( execute );
                    }
                }
            }
        }.start();
    }

    private void doExecute0() {
        String serialToMatch = serialField.getText();
        Item i = matchingResource( serialToMatch );
        if ( i instanceof Script ) {
            String[] sessions = getSelectedSessions();
            executeScript( (Script) i, sessions );
        } else if ( i instanceof Scenario ) {
            showScenarioDialog( (Scenario) i);
        }
    }

    private boolean inputValidation() {
        String serialToMatch = serialField.getText();
        if ( !StringUtil.isNotBlank( serialToMatch ) ) {
            String msg = LocaleUtil.getLocalName( BLANK_SERIAL );
            MsgDialogUtil.showMsgDialog( msg );
            return false;
        }
        boolean selectedAny = false;
        for ( int i = 0; i < sessionCBoxes.length; i++ ) {
            selectedAny |= sessionCBoxes[i].isSelected();
            if ( selectedAny ) {
                break;
            }
        }

        if ( !selectedAny ) {
            String msg = LocaleUtil.getLocalName( NO_SESSION_SELECTED );
            MsgDialogUtil.showMsgDialog( msg );
            return false;
        }

        return true;
    }

    private String[] getSelectedSessions() {
        List<String> sessionList = new ArrayList<String>();
        for ( int i = 0; i < sessionCBoxes.length; i++ ) {
            JCheckBox jcb = sessionCBoxes[i];
            sessionList.add( jcb.getName() );
        }

        String[] sessions = new String[sessionList.size()];
        sessions = sessionList.toArray( sessions );
        return sessions;
    }

    private Item matchingResource(String serial) {
        StorableMgr mgr = getStorableMgr(app);
        Set<Item> itemSet = mgr.getBySerialMatching( serial );
        if ( null == itemSet || itemSet.isEmpty() ) {
            ClientProxy client = getClientProxy( app );
            try {
                serial = client.getCompleteSerial( serial );
            } catch (IOException e) {
                e.printStackTrace(); //TODO
            } catch (GlobalException e) {
                MsgDialogUtil.reportGlobalException( e );
            }
            itemSet = mgr.getBySerialMatching( serial );
        }

        return ( null == itemSet) ? null : itemSet.iterator().next();
    }

    private void executeScript(Script script, String[] sessions) {
        ScriptExecutor0 executor = getScriptExecutor( app );
        String path = getScriptPath( script, app );

        File scriptFile = new File( path );
        if ( !scriptFile.exists() ) {
            String msg = LocaleUtil.getLocalName( CANT_FIND_FILE );
            MsgDialogUtil.showMsgDialog( msg + path );
            return;
        }
        executor.execute( path, sessions );
        boolean interrupted = false;
        try {
            executor.waitFor();
        } catch (InterruptedException e) {
            interrupted = true;
            String msg = LocaleUtil.getLocalName( EXECUTOR_INTERRUPTED );
            MsgDialogUtil.showErrorDialog(msg);
        } finally {
            if ( !interrupted ) {
                String msg = LocaleUtil.getLocalName( SCRIPT_EXECUTE_FINISHED );
                MsgDialogUtil.showMsgDialog( msg );
            }
        }

    }

    private void showScenarioDialog(Scenario scenario) {
        scenarioDialog.display(scenario);
        try {
            synchronized ( scenarioDialog ) {
                while( scenarioDialog.isDisplaying() ) {
                        scenarioDialog.wait();
                }
            }
        } catch (InterruptedException e) {
            ErrorLogger.log( "the thread waiting scenarioDialog" +
                    " to exit has been interrupted: " + e.getMessage() );
        }
    }

    private boolean submitRecord(BaseTestRecord record) {
        ClientProxy client = getClientProxy( app );
        int status = -1;
        try {
             status = client.sendBaseTestRecord( record );
        } catch (IOException e) {
            ErrorLogger.log( "IOException when submit BaseTestRecord:"
                    + e.getMessage() );
        } catch (ResponseException e) {
            ErrorLogger.log( "Invalid response when submit BaseTestRecord:"
                    + e.getMessage() );
        } catch (GlobalException e) {
            MsgDialogUtil.reportGlobalException( e );
        }

        return status > 0;
    }

    private String getScriptPath(Script script, AppContent app) {
        Integer loginMode = (Integer) app.getAttribute( TEST_ATTR.TEST_TYPE );
        if ( loginMode == TestTypeRecord.OTHER_TEST ) {
            return script.getSecondPath();
        } else {
            return script.getPath();
        }
    }

    private ScriptExecutor0 getScriptExecutor(AppContent app) {
        return (ScriptExecutor0) app.getAttribute( COMPONENT.SCRIPT_EXECUTOR );
    }

    private ScriptEnvConfig getScriptEnvConfig(AppContent app) {
        return (ScriptEnvConfig) app.getAttribute( CONFIG.SCRIPT_ENV_CONFIG );
    }

    private StorableMgr getStorableMgr(AppContent app) {
        return (StorableMgr) app.getAttribute( COMPONENT.STORABLE_MGR );
    }

    private ClientProxy getClientProxy(AppContent app) {
        return (ClientProxy) app.getAttribute(  COMPONENT.NIO_CLIENT );
    }

    @Override
    public void setTabTitle(String tabTitle) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getTabTitle() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setTabIcon(Icon tabIcon) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Icon getTabIcon() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Component getTabComponent() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setTabTip(String tabTip) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getTabTip() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    class ScenarioDisplayDialog extends JDialog {

        private static final long serialVersionUID = -8191433373460432589L;
        private static final String DATE_CREATE = "Create Date";
        private static final String DATE_MODIFY = "Modify Date";
        private static final String BASIC_INFO = "Basic Info";
        private static final String CONTENT = "Content";

        private static final String PASS = "Pass!";
        private static final String NOT_PASS = "Not Pass!";

        private final JComponent fatherFrame;

        private JDialog dialog = this;

        private Map<String, JLabel> labelCache = new HashMap<String, JLabel>();
        private Map<String, JTextField> fieldCache = new HashMap<String, JTextField>();

        private JLabel nameLabel;
        private JLabel serialLabel;
        private JLabel beginIndexLabel;
        private JLabel createDateLabel;
        private JLabel modifyDateLabel;

        private JTextField nameField;
        private JTextField serialField;
        private JTextField beginIndexField;
        private JTextField createDateField;
        private JTextField modifyDateField;

        private JButton passBt;
        private JButton notPassBt;

        private JPanel basicPane;
        private JPanel contentPane;
        private JPanel buttonPane;

        private Scenario matchedScenario;
        private boolean displaying = false;

        public ScenarioDisplayDialog(JComponent fatherFrame) {
            this.fatherFrame = fatherFrame;
            initUI();
        }

        private void initUI() {
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    if (null != fatherFrame) {
                        fatherFrame.setEnabled(true);
                    }
                    setDisplaying( false );
                }
            });

            initBasicPane();
            initContentPane();
            initButtonPane();

            setLayout(new GridBagLayout());
            add( basicPane, new GBC(0, 0) );
            add( contentPane, new GBC(0, 1) );
            add( buttonPane, new GBC(0, 2) );
        }

        private void initBasicPane() {
            String name = LocaleUtil.getLocalName( SCENARIO_NAME );
            String serial = LocaleUtil.getLocalName( SERIAL_NUM );
            String beginIndex = LocaleUtil.getLocalName( BEGIN_INDEX );
            String createDate = LocaleUtil.getLocalName( DATE_CREATE );
            String modifyDate = LocaleUtil.getLocalName( DATE_MODIFY );
            String basicInfo = LocaleUtil.getLocalName( BASIC_INFO );

            nameLabel = newLabel( name );
            serialLabel = newLabel( serial );
            beginIndexLabel = newLabel( beginIndex );
            createDateLabel = newLabel( createDate );
            modifyDateLabel = newLabel( modifyDate );

            nameField = newField();
            serialField = newField();
            beginIndexField = newField();
            createDateField = newField();
            modifyDateField = newField();

            Border titledBorder = BorderFactory.createTitledBorder( basicInfo );
            basicPane = new JPanel();
            basicPane.setBorder( titledBorder );
            basicPane.setLayout( new GridBagLayout() );
            basicPane.add( nameLabel, new GBC(0, 0).setAnchor( GBC.WEST )
                    .setInsets( 4, 8, 4, 8)
                    .setFill( GBC.HORIZONTAL ) );
            basicPane.add( nameField, new GBC(1, 0).setAnchor( GBC.WEST )
                    .setInsets( 4, 8, 4, 8) );
            basicPane.add( serialLabel, new GBC(2, 0).setAnchor( GBC.WEST )
                    .setInsets( 4, 8, 4, 8)
                    .setFill( GBC.HORIZONTAL ) );
            basicPane.add(serialField, new GBC(3, 0).setAnchor(GBC.WEST)
                    .setInsets(4, 8, 4, 8));

            basicPane.add(beginIndexLabel, new GBC(0, 1).setAnchor(GBC.WEST)
                    .setInsets(4, 8, 4, 8)
                    .setFill(GBC.HORIZONTAL));
            basicPane.add(beginIndexField, new GBC(1, 1).setAnchor(GBC.WEST)
                    .setInsets(4, 8, 4, 8));
            basicPane.add( createDateLabel, new GBC(2, 1).setAnchor( GBC.WEST )
                    .setInsets(4, 8, 4, 8)
                    .setFill(GBC.HORIZONTAL) );
            basicPane.add( createDateField, new GBC(3, 1).setAnchor( GBC.WEST )
                    .setInsets(4, 8, 4, 8) );

            basicPane.add( modifyDateLabel, new GBC(0, 2).setAnchor( GBC.WEST )
                    .setInsets(4, 8, 4, 8)
                    .setFill(GBC.HORIZONTAL) );
            basicPane.add( modifyDateField, new GBC(1, 2).setAnchor( GBC.WEST )
                    .setInsets(4, 8, 4, 8) );
        }

        private void initContentPane() {
            contentPane = new JPanel();
            contentPane.setLayout( new GridBagLayout() );

            String content = LocaleUtil.getLocalName( CONTENT );
            Border titledBorder = BorderFactory.createTitledBorder( content );
            contentPane.setBorder( titledBorder );
        }

        private void initButtonPane() {
            String pass = LocaleUtil.getLocalName( PASS );
            String notPass = LocaleUtil.getLocalName( NOT_PASS );

            passBt = new JButton( pass );
            notPassBt = new JButton( notPass );

            passBt.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    submit( true );
                }
            });
            notPassBt.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    submit( false );
                }
            });

            buttonPane = new JPanel();
            buttonPane.setLayout( new GridBagLayout() );
            buttonPane.add(passBt, new GBC(0, 0).setInsets(10));
            buttonPane.add( notPassBt, new GBC(1, 0).setInsets( 10 ) );

        }

        private void submit(boolean isPass) {
            BaseTestRecord record = ScenarioUtil.mergePreProvidedScenario(
                    new BaseTestRecord(), matchedScenario );
            record.setStatus(String.valueOf( isPass )); //TODO maybe needs fixing
            if ( submitRecord( record ) ) {
                //TODO report
            } else {
                //TODO report
            }
        }

        public boolean isDisplaying() {
            return displaying;
        }

        private void setDisplaying(boolean displaying) {
            synchronized ( this ) {
                this.displaying = displaying;
                this.notifyAll();
            }
        }

        public void display(Scenario scenario) {
            matchedScenario = scenario;
            update( scenario );
            if ( null != fatherFrame ) {
                fatherFrame.setEnabled( false );
            }
            dialog.setAlwaysOnTop(true);
            dialog.pack();
            dialog.setResizable(false);
            dialog.setLocationRelativeTo( null );
            setDisplaying( true );
            dialog.setVisible(true);
        }

        public void close() {
            if ( null != fatherFrame ) {
                fatherFrame.setEnabled( true );
            }
            dialog.setVisible(false);
            setDisplaying( false );
        }

        public void update(Scenario scenario) {
            matchedScenario = scenario;
            updateBasicPane(scenario);
            updateContentPane( scenario );
        }

        private void updateBasicPane(Scenario scenario) {
            if ( null == scenario ) {
                return;
            }
            String name = scenario.getRemarkName();
            String serial = scenario.getSerial();
            String beginIndex = String.valueOf( scenario.getBeginIndex() );
            String createDate = scenario.getFormattedCreateDate();
            String modifyDate = scenario.getFormattedModifyDate();

            resetTextField( nameField, name );
            resetTextField( serialField, serial );
            resetTextField( beginIndexField, beginIndex );
            resetTextField( createDateField, createDate );
            resetTextField( modifyDateField, modifyDate );
        }

        private void updateContentPane(Scenario scenario) {
            Set<String> attrNames = scenario.getAttrNames();
            int len = attrNames.size();
            JLabel[] labels = new JLabel[len];
            JTextField[] fields = new JTextField[len];
            int counter = 0;
            for ( String name : attrNames ) {
                String attr = scenario.getAttr( name );
                labels[counter] = getLabel( name );
                fields[counter] = getField( name, attr );
                counter++;
            }
            updateContentPane(labels, fields);
        }

        private void updateContentPane(JLabel[] labels, JTextField[] fields) {
            contentPane.removeAll();
            int len = labels.length;
            for ( int i=0; i < len; i++ ) {
                if ( i % 2 == 0) {
                    contentPane.add( labels[i],
                            new GBC( 0 , i ).setAnchor(GBC.WEST)
                                    .setInsets(4, 8, 4, 8)
                                    .setFill(GBC.HORIZONTAL)
                    );
                    contentPane.add( fields[i],
                            new GBC( 1 , i ).setAnchor(GBC.WEST)
                                    .setInsets(4, 8, 4, 8)
                    );
                } else {
                    contentPane.add(labels[i],
                            new GBC(2, i - 1).setAnchor(GBC.WEST)
                                    .setInsets(4, 8, 4, 8)
                                    .setFill(GBC.HORIZONTAL)
                    );
                    contentPane.add(fields[i],
                            new GBC(3, i - 1).setAnchor(GBC.WEST)
                                    .setInsets(4, 8, 4, 8)
                    );
                }
            }
            contentPane.revalidate();
        }

        private JLabel getLabel(String name) {
            JLabel label = labelCache.get( name );
            if ( null == label ) {
                String localName = LocaleUtil.getLocalName( name );
                label = new JLabel( localName );
                label.setPreferredSize( new Dimension( 80, 20 ));
                labelCache.put( name, label );
            }

            return label;
        }

        private JTextField getField(String name, String text) {
            JTextField textField = fieldCache.get( name );
            if ( null == textField ) {
                textField = new JTextField();
                textField.setEditable( false );
                textField.setPreferredSize( new Dimension( 160, 20 ));
                fieldCache.put( name , textField );
            }
            textField.setText( text );

            return textField;
        }

        private void resetTextField( JTextField jtf, String text ) {
            if ( null != text && null != jtf ) {
                if ( !text.equals( jtf.getText() ) ) {
                    jtf.setText( text );
                }
            }
        }

        private JLabel newLabel(String name) {
            JLabel label = new JLabel( name );
            label.setPreferredSize( new Dimension( 80, 20 ) );
            return label;
        }

        private JTextField newField() {
            JTextField field = new JTextField();
            field.setPreferredSize( new Dimension( 160, 20 ) );
            field.setEditable( false );
            return field;
        }

    }

}
