package com.bdcom.view.itester;

import com.bdcom.biz.pojo.ITesterRecord;
import com.bdcom.itester.api.ITesterAPI;
import com.bdcom.itester.api.wrapper.DeviceStatus;
import com.bdcom.itester.api.wrapper.ITesterAPIWrapper;
import com.bdcom.itester.api.wrapper.ITesterException;
import com.bdcom.itester.api.wrapper.TestCaseConfig;
import com.bdcom.nio.client.ClientProxy;
import com.bdcom.nio.exception.GlobalException;
import com.bdcom.nio.exception.ResponseException;
import com.bdcom.sys.ApplicationConstants;
import com.bdcom.util.LocaleUtil;
import com.bdcom.util.StringUtil;
import com.bdcom.util.log.ErrorLogger;
import com.bdcom.view.ViewTab;
import com.bdcom.view.itester.tree.DeviceInfoTreeBuilder;
import com.bdcom.view.util.GBC;
import com.bdcom.view.util.MsgDialogUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-8-14    <br/>
 * Time: 15:45  <br/>
 */
public class ITesterFrame extends JPanel
        implements ViewTab, ApplicationConstants {

    private static final String ADD_TEST_SERVER = "Add Test Server";
    private static final String ADD_TEST_CASE = "Add Test Case";
    private static final String COMMIT_TEST_RESULT = "Commit Test Result";
    private static final String COMMITTED_NUM = "Committed Number:";
    private static final String UNCOMMITTED_NUM = "Uncommitted Number:";
    private static final String SEQUENTIAL_MODE = "Sequential Mode";
    private static final String COCURRENT_MODE = "Cocurrent Mode";
    private static final String START_ALL = "Start All";
    private static final String EXE_MODE = "Execution Mode";
    private static final String SERVER_PORTS_STATUS = "Server Ports Status";
    private static final String TEST_CASE_LIST = "Test Case List";
    private static final String TEST_CONFIG = "Test Config";

    private final ITesterAPI api;
    private final ClientProxy client;

    private ITesterAPIWrapper apiWrapper;

    private JPanel frame = this;
    private JScrollPane leftPane;
    private JPanel leftInnerPane;
    private JPanel rightPane;
    private JPanel buttonPane;
    private JPanel modePane;
    private JScrollPane progressesPane;
    private TestProgressTable progressesTable;

    private JButton addServerBt;
    private JButton addTestBt;
    private JButton commitBt;
    private JButton startAllBt;

    private ButtonGroup modeGroup;
    private JRadioButton sequentialMode;
    private JRadioButton cocurrentMode;

    private AddServerDialog addServerDialog;
    private WorkOrderDialog workOrderDialog;
    private TestConfigDialog testConfigDialog;
    private CommitDialog commitDialog;

    private List<TestProgressRow> tprList = new ArrayList<TestProgressRow>();
    private Map<String, DeviceStatus> dsMap = new HashMap<String, DeviceStatus>();
    private List<JTree> diTreeList = new ArrayList<JTree>();

    public ITesterFrame(ITesterAPI api, ClientProxy client) {
        this.api = api;
        this.client = client;
        this.apiWrapper = new ITesterAPIWrapper( api );
        initUI();
    }

    private void initUI() {
        initLeftPane();
        initRightPane();
        frame.add( leftPane, BorderLayout.WEST );
        frame.add( rightPane, BorderLayout.EAST );
    }

    private void initDialogs() {
        addServerDialog = new AddServerDialog( frame, dsMap );
        testConfigDialog = new TestConfigDialog( frame, dsMap );
        workOrderDialog = new WorkOrderDialog( frame, testConfigDialog );
        commitDialog = new CommitDialog( frame );
    }

    private void initLeftPane() {
        String serverPortsStatus = LocaleUtil.getLocalName( SERVER_PORTS_STATUS );

        leftInnerPane = new JPanel();
        leftPane = new JScrollPane( leftInnerPane );
        leftPane.setPreferredSize(new Dimension(220, 600));
        leftInnerPane.setLayout(new GridBagLayout());
        Border bd = BorderFactory.createTitledBorder( serverPortsStatus );
        leftPane.setBorder(bd);
    }

    private void initRightPane() {
        initDialogs();
        initButtonPane();
        initModePane();
        initProgressesPane();

        rightPane = new JPanel();
        rightPane.setLayout( new GridBagLayout() );
        rightPane.add( buttonPane, new GBC(0, 0) );
        rightPane.add( modePane, new GBC(0, 1) );
        rightPane.add( progressesPane, new GBC(0, 2) );
    }

    private void initButtonPane() {
        String addServ = LocaleUtil.getLocalName( ADD_TEST_SERVER );
        String addTest = LocaleUtil.getLocalName( ADD_TEST_CASE );
        String commitResult = LocaleUtil.getLocalName( COMMIT_TEST_RESULT );

        addServerBt = new JButton( addServ );
        addTestBt = new JButton( addTest );
        commitBt = new JButton( commitResult );
        addServerBt.setPreferredSize( new Dimension(120, 30) );
        addTestBt.setPreferredSize(new Dimension(110, 30) );
        commitBt.setPreferredSize(new Dimension(110, 30) );

        addServerBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addServerDialog.display();
            }
        });

        addTestBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                workOrderDialog.display();
            }
        });

        commitBt.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TestProgressRow[] panels = getTestedProgressPanels();
                int total = panels.length;
                ITesterRecord[] records = new ITesterRecord[total];
                for ( int i=0; i < total; i++ ) {
                    records[i] = panels[i].getTestResult();
                }
                List<TestProgressRow> panelsToRemove = new ArrayList<TestProgressRow>();
                try {
                    commitDialog.display();
                    for ( int i=0; i < total; i++ ) {
                        sendITesterRecord( records[i] );
                        panelsToRemove.add(panels[i]);
                    }
                } catch (GlobalException ex) {
                    reportSendException( ex );
                } catch (ResponseException ex) {
                    reportSendException( ex );
                } catch (IOException ex) {
                    reportSendException( ex );
                } finally {
                    int committedNum = panelsToRemove.size();
                    TestProgressRow[] rows = new TestProgressRow[committedNum];
                    rows = panelsToRemove.toArray( rows );
                    removeProgressRows(rows);

                    String committedNumMsg = LocaleUtil.getLocalName( COMMITTED_NUM );
                    String uncommittedNumMsg = LocaleUtil.getLocalName( UNCOMMITTED_NUM );
                    StringBuilder sb = new StringBuilder();
                    sb.append( "<html>" )
                      .append( committedNumMsg )
                      .append( committedNum )
                      .append( "<br/>" )
                      .append( uncommittedNumMsg )
                      .append( total - committedNum )
                      .append( "<br/></html>" );

                    commitDialog.close();
                    MsgDialogUtil.showMsgDialog( sb.toString() );
                }
            }
        });

        String testConfig = LocaleUtil.getLocalName( TEST_CONFIG );
        Border bd = BorderFactory.createTitledBorder( testConfig );
        buttonPane = new JPanel();
        buttonPane.setLayout(new GridBagLayout());
        buttonPane.setPreferredSize(new Dimension(600, 100));
        buttonPane.setBorder(bd);
        buttonPane.add( addServerBt, new GBC(0, 0).setInsets( 5, 10, 5, 10 ) );
        buttonPane.add( addTestBt, new GBC(1, 0).setInsets( 5, 10 ,5, 10 ) );
        buttonPane.add( commitBt, new GBC(2, 0).setInsets( 5, 10, 5, 10 ) );
    }

    private void initModePane() {
        String sequential = LocaleUtil.getLocalName( SEQUENTIAL_MODE );
        String cocurrent = LocaleUtil.getLocalName( COCURRENT_MODE );
        String exeMode = LocaleUtil.getLocalName( EXE_MODE );
        String startAll = LocaleUtil.getLocalName( START_ALL );

        startAllBt = new JButton( startAll );
        startAllBt.setPreferredSize( new Dimension( 120, 30 ) );
        modeGroup = new ButtonGroup();
        sequentialMode = new JRadioButton( sequential );
        cocurrentMode = new JRadioButton( cocurrent );
        modeGroup.add( sequentialMode );
        modeGroup.add( cocurrentMode );
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( sequentialMode.isSelected() ||
                        cocurrentMode.isSelected() ) {
                    startAllBt.setEnabled( true );
                } else {
                    startAllBt.setEnabled( false );
                }
            }
        };
        sequentialMode.addActionListener( al );
        cocurrentMode.addActionListener(al);

        startAllBt.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( sequentialMode.isSelected() ) {
                    TestProgressRow[] panels = getUnTestedProgressPanels();
                    new TestExecutor( panels, TestExecutor.SYNC ).start();
                }
                if ( cocurrentMode.isSelected() ) {
                    TestProgressRow[] panels = getUnTestedProgressPanels();
                    new TestExecutor( panels, TestExecutor.ASYNC ).start();
                }
            }
        });
        startAllBt.setEnabled( false );

        modePane = new JPanel();
        Border titledBorder = BorderFactory.createTitledBorder( exeMode );
        modePane.setBorder( titledBorder );

        modePane.setLayout( new GridBagLayout() );
        modePane.setPreferredSize( new Dimension( 600, 100 ) );
        modePane.add( sequentialMode, new GBC(0, 0).setInsets( 5, 10, 5, 10) );
        modePane.add( cocurrentMode, new GBC(1, 0).setInsets( 5, 10, 5, 10) );
        modePane.add( startAllBt, new GBC(2, 0).setInsets( 5, 10, 5, 10) );
    }

    private void initProgressesPane() {
        String testCaseList = LocaleUtil.getLocalName( TEST_CASE_LIST );

        progressesTable = TestProgressTable.newInstance();
        progressesPane = new JScrollPane( progressesTable );
        progressesPane.setPreferredSize( new Dimension( 600, 400 ) );
        Border bd = BorderFactory.createTitledBorder( testCaseList );
        progressesPane.setBorder(bd);
    }

    void addDeviceInfoTree(String ip) {
        if ( dsMap.containsKey( ip ) ) {
            return;
        }

        DeviceStatus deviceStatus = null;
        try {
            deviceStatus = apiWrapper.getDeviceStatus( ip );
            dsMap.put( ip, deviceStatus );
        } catch (ITesterException e) {
            ITesterUtil.reportTestException( e, this );
        }
        if ( null == deviceStatus ) {
            return;
        }

        JTree deviceInfoTree = DeviceInfoTreeBuilder.buildTree( deviceStatus );
        diTreeList.add( deviceInfoTree );

        leftInnerPane.removeAll();
        int num = diTreeList.size();
        for ( int i=0; i < num; i++ ) {
            leftInnerPane.add(deviceInfoTree, new GBC(0, i));
        }
    }

    void addTestCase(ITesterRecord record, TestCaseConfig testConfig) {
        TestProgressRow testProgressRow = new TestProgressRow( record, apiWrapper, testConfig );
        testProgressRow.setPanelOwner( this );

        tprList.add( testProgressRow );
        progressesTable.addRow( testProgressRow );
    }

    private TestProgressRow[] getTestedProgressPanels() {
        List<TestProgressRow> tpl = new ArrayList<TestProgressRow>();
        for ( TestProgressRow row : tprList) {
            if ( row.isTested() ) {
                tpl.add(row);
            }
        }
        TestProgressRow[] rows = new TestProgressRow[tpl.size()];
        rows = tpl.toArray( rows );
        return rows;
    }

    private TestProgressRow[] getUnTestedProgressPanels() {
        List<TestProgressRow> tpRows = new ArrayList<TestProgressRow>();
        for ( TestProgressRow pane : tprList) {
            if ( !pane.isTested() ) {
                tpRows.add(pane);
            }
        }
        TestProgressRow[] rows = new TestProgressRow[tpRows.size()];
        rows = tpRows.toArray( rows );
        return rows;
    }

    private void removeProgressRows(TestProgressRow... rows) {
        if ( null == rows || rows.length == 0 || tprList.isEmpty() ) {
            return;
        }
        int len = rows.length;
        for ( int i=0; i < len; i++ ) {
            tprList.remove(rows[i]);
            progressesTable.removeRow( rows[i] );
        }
    }

    ITesterRecord sendITesterRecord(ITesterRecord itr)
            throws GlobalException, IOException, ResponseException {
        itr = client.sendITesterRecord( itr );
        return itr;
    }

    void reportSendException(Exception e) {
        if ( null == e ) {
            return;
        }
        if ( e instanceof GlobalException ) {
            MsgDialogUtil.reportGlobalException( (GlobalException) e );
        }
        if ( e instanceof ResponseException ) {
            MsgDialogUtil.reportResponseException( (ResponseException) e );
        }
    }

    class TestExecutor extends Thread {
        static final int SYNC = 0xA;
        static final int ASYNC = 0xB;

        private final TestProgressRow[] rows;
        private final int mode;
        TestExecutor(TestProgressRow[] rows, int mode) {
            this.rows = rows;
            this.mode = mode;
        }

        @Override
        public void run() {
            if ( mode == SYNC ) {
                syncTest();
            } else {
                asyncTest();
            }
        }

        private void syncTest() {
            if ( null == rows || rows.length == 0 ) {
                return;
            }
            for (TestProgressRow row : rows)  {
                if ( null != row && !row.isTested()
                        && !row.isRunning() ) {
                    row.startTest();
                    int timeoutCounter = 0;
                    while( true ) {
                        try {
                            TimeUnit.SECONDS.sleep( 5 );
                        } catch (InterruptedException e) {
                            ErrorLogger.log( "TestExecutor Thread interrupted: "
                                    + e.getMessage() );
                        }
                        if ( row.isTested() || timeoutCounter > 8 ) {
                            break;
                        }
                        timeoutCounter++;
                    }
                }
            }
        }

        private void asyncTest() {
            if ( null == rows || rows.length == 0 ) {
                return;
            }
            for ( TestProgressRow row: rows) {
                if ( null != row && !row.isTested()
                        && !row.isRunning() ) {
                    row.startTest();
                }
            }
        }
    }

    private String tabTitle;
    private String tabTip;
    private Icon tabIcon;
    private static final String ITESTER_STREAM_TEST = "ITester Stream Test";

    @Override
    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
    }

    @Override
    public String getTabTitle() {
        if ( null == tabTitle ) {
            tabTitle = LocaleUtil.getLocalName( ITESTER_STREAM_TEST );
        }
        return tabTitle;
    }

    @Override
    public void setTabIcon(Icon tabIcon) {
        this.tabIcon = tabIcon;
    }

    @Override
    public Icon getTabIcon() {
        return tabIcon;
    }

    @Override
    public Component getTabComponent() {
        return frame;
    }

    @Override
    public void setTabTip(String tabTip) {
        this.tabTip = tabTip;
    }

    @Override
    public String getTabTip() {
        return tabTip;
    }

    class CommitDialog extends IDialog {

        private static final long serialVersionUID = -1016258074484354448L;
        private static final String COMMITTING = "Committing...";

        private JLabel msgLabel;

        CommitDialog(JComponent fatherFrame) {
            super(fatherFrame);
        }

        private void initUI() {
            String msg = LocaleUtil.getLocalName( COMMITTING );
            msgLabel = new JLabel( msg );
            add( msgLabel, BorderLayout.CENTER );
            dialog.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
            dialog.addWindowListener( new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    //do nothing
                }
            });
        }

    }

    class AddServerDialog extends IDialog {

        private static final long serialVersionUID = -4657767652960369415L;
        private static final String ADD_TEST_SERVER = "Add test server";
        private static final String SERVER_ADDR = "server address";
        private static final String CONFIRM = "confirm";
        private static final String INVALID_IP = "ip address is invalid!";
        private static final String ADDR_ADDED = "this address is added!";

        private JLabel ipLabel;
        private JTextField ipField;
        private JButton okBt;

        private JPanel inputPane;
        private JPanel buttonPane;

        private final Map<String, DeviceStatus> dsMap;

        public AddServerDialog(JComponent fatherFrame, Map<String, DeviceStatus> dsMap) {
            super(fatherFrame);
            this.dsMap = dsMap;
            initUI();
        }

        void initUI() {
            String dialogTitle = LocaleUtil.getLocalName( ADD_TEST_SERVER );
            String label = LocaleUtil.getLocalName( SERVER_ADDR );
            String confirm = LocaleUtil.getLocalName( CONFIRM );

            setTitle( dialogTitle );
            ipLabel = new JLabel( label );
            ipField = new JTextField();
            ipField.setPreferredSize( new Dimension( 150, 20 ) );
            okBt = new JButton( confirm );
            okBt.addActionListener( this );

            inputPane = new JPanel();
            inputPane.setLayout( new GridBagLayout() );
            inputPane.add( ipLabel, new GBC(0, 0).setInsets( 5, 10, 5, 10 ) );
            inputPane.add( ipField, new GBC(1, 0).setInsets( 5, 10, 5, 10) );

            buttonPane = new JPanel();
            buttonPane.setLayout( new GridBagLayout() );
            buttonPane.add(okBt, new GBC(0, 0).setInsets(5, 10, 5, 10));
            buttonPane.add( cancelBt, new GBC(1, 0).setInsets( 5, 10, 5, 10) );

            setLayout( new GridBagLayout() );
            add( inputPane, new GBC(0, 0) );
            add( buttonPane, new GBC(0, 1) );
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String ip = ipField.getText();
            if ( !StringUtil.isVaildIp(ip) ) {
                String msg = LocaleUtil.getLocalName( INVALID_IP );
                MsgDialogUtil.showMsgDialog( dialog, msg );
                return;
            }
            if ( dsMap.containsKey( ip) ) {
                String msg = LocaleUtil.getLocalName( ADDR_ADDED );
                MsgDialogUtil.showMsgDialog( dialog, msg );
                return;
            }

            super.actionPerformed( e );
            addDeviceInfoTree(ip);
        }

    }

    class WorkOrderDialog extends IDialog {

        private static final long serialVersionUID = 4041091311759430438L;
        private static final String NEXT = "next";
        private static final String WORK_ORDER_IS_BLANK = "work order is blank!";
        private static final String BAR_CODE_IS_BLANK = "bar code is blank!";
        private static final String INVALID_WORK_ORDER = "invalid work order";
        private static final String EVER_TESTED = "this order has been tested";
        private static final String WORK_ORDER_INFO = "work order info";

        static final String WORK_ORDER_NUM = "work order number:";
        static final String BAR_CODE = "bar code:";

        private final TestConfigDialog tcDialog;
        private JLabel barCodeLabel;
        private JTextField barCodeField;
        private JButton nextBt;
        private WorkOrderPanel workOrderPane;
        private JPanel barCodePane;
        private JPanel buttonPane;

        private WorkOrderHistory workOrderHistory = new WorkOrderHistory();

        public WorkOrderDialog(JComponent fatherFrame, TestConfigDialog tcDialog) {
            super(fatherFrame);
            this.tcDialog = tcDialog;
            initUI();
        }

        private void initUI() {
            String dialogTitle = LocaleUtil.getLocalName( WORK_ORDER_INFO );
            String workOder = LocaleUtil.getLocalName( WORK_ORDER_NUM );
            String barCode = LocaleUtil.getLocalName( BAR_CODE );
            String next = LocaleUtil.getLocalName( NEXT );

            setTitle( dialogTitle );
            Border workOrderTitle = BorderFactory.createTitledBorder( workOder );
            Border barCodeTitle = BorderFactory.createTitledBorder( barCode );
            workOrderPane = new WorkOrderPanel();
            //workOrderPane.setBorder( workOrderTitle );
            barCodePane = new JPanel();
            barCodePane.setBorder( barCodeTitle );
            buttonPane = new JPanel();

            barCodeLabel = new JLabel( barCode );
            barCodeField = new JTextField();
            barCodeField.setPreferredSize( new Dimension( 150, 20 ) );

            nextBt = new JButton( next );
            nextBt.setPreferredSize( new Dimension( 70, 30 ) );
            nextBt.addActionListener(this);

            barCodePane.setLayout( new GridBagLayout() );
            barCodePane.setPreferredSize( new Dimension( 300, 150 ) );
            barCodePane.add( barCodeLabel, new GBC(0, 0).setInsets(10) );
            barCodePane.add( barCodeField, new GBC(1, 0).setInsets(10) );

            buttonPane.setLayout( new GridBagLayout() );
            buttonPane.setPreferredSize( new Dimension( 300, 70 ) );
            buttonPane.add( nextBt, new GBC(0, 0).setInsets(5, 10, 5, 10) );
            buttonPane.add( cancelBt, new GBC(1, 0).setInsets(5, 10, 5, 10) );

            setLayout( new GridBagLayout() );
            add( workOrderPane, new GBC(0, 0) );
            add( barCodePane, new GBC(0, 1) );
            add( buttonPane, new GBC(0, 2) );
        }

        @Override
        public void display() {
            String[] wos = workOrderHistory.getAll();
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for ( int i=0; i < wos.length; i++) {
                sb.append( wos[i] )
                  .append(", ");
            }
            sb.append("]");
            System.out.println(sb.toString());
            workOrderPane.update(wos);
            workOrderPane.clearInputCompos();
            super.display();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if ( blankCheck() ) {
                return;
            }
            String workOrder = workOrderPane.getSelectedWorkOrder();
            String barCode = barCodeField.getText();
            ITesterRecord itr = ITesterRecord
                    .checkWorkOrderInstance(workOrder, barCode);

            boolean sendSuccess = true;
            try {
                itr = sendITesterRecord( itr );
            } catch (Exception ex) {
                sendSuccess = false;
                reportSendException( ex );
            } finally {
                if ( !sendSuccess ) {
                    return;
                }
            }

            if ( !itr.isWorkOrderValid() ) {
                String invalidWorkOder = LocaleUtil.getLocalName( INVALID_WORK_ORDER );
                MsgDialogUtil.showMsgDialog( dialog, invalidWorkOder );
                return;
            }
            if ( itr.isEverTested() ) {
                String everTested = LocaleUtil.getLocalName( EVER_TESTED );
                MsgDialogUtil.showMsgDialog( dialog, everTested );
                return;
            }

            super.actionPerformed( e );
            workOrderHistory.add( itr.getWorkOrder() );
            itr = ITesterRecord.commitTestResultInstance( itr );
            tcDialog.display( itr );
        }

        private boolean blankCheck() {
            String workOrder = workOrderPane.getSelectedWorkOrder();
            if ( !StringUtil.isNotBlank( workOrder ) ) {
                String blankWorkOrder = LocaleUtil.getLocalName( WORK_ORDER_IS_BLANK  );
                MsgDialogUtil.showMsgDialog( dialog, blankWorkOrder );
                return true;
            }
            String barCode = barCodeField.getText();
            if ( !StringUtil.isNotBlank( barCode ) ) {
                String blankBarCode = LocaleUtil.getLocalName( BAR_CODE_IS_BLANK );
                MsgDialogUtil.showMsgDialog( dialog, blankBarCode );
                return true;
            }
            return false;
        }

        class WorkOrderHistory {
            private static final int LIMIT = 3;
            private String[] queue = new String[LIMIT];
            private int counter = 0;

            void add(String wo) {
                if ( counter < LIMIT ) {
                    queue[ counter++ ] = wo;
                } else {
                    for ( int i= LIMIT-1; i > 0; i-- ) {
                        queue[i] = queue[i-1];
                    }
                    queue[0] = wo;
                }
            }

            String[] getAll() {
                String[] current = null;
                if ( counter < LIMIT ) {
                    current = new String[counter];
                    for ( int i=0; i < counter; i++ ) {
                        current[i] = queue[i];
                    }
                } else {
                    current = queue;
                }
                return current;
            }

        }

        class WorkOrderPanel extends JPanel implements ActionListener {

            private static final long serialVersionUID = -1839680750314193585L;
            private static final String NO_RECENT_INPUT = "No recent input work order";
            private static final String RECENT_INPUT_WORK_ORDER = "Recent input work orders:";
            private static final String INPUT_NEW_WORK_ORDER = "Input new work order:";

            private ButtonGroup buttonGroup;
            private JRadioButton inputNewWorkOrder;
            private JLabel emptyLabel;
            private JLabel workOrderLabel;
            private JTextField workOrderField;
            private JScrollPane historyPane;
            private JPanel historyInnerPane;
            private JPanel inputPane;

            private Map<String, JRadioButton> jrbMap = new HashMap<String, JRadioButton>();
            private String selectedWorkOrder;

            WorkOrderPanel() {
                initUI();
            }

            String getSelectedWorkOrder() {
                if ( inputNewWorkOrder.isSelected() ) {
                    selectedWorkOrder = workOrderField.getText();
                }
                return selectedWorkOrder;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                if ( inputNewWorkOrder.isSelected() || jrbMap.isEmpty() ) {
                    workOrderField.setEnabled( true );
                    selectedWorkOrder = workOrderField.getText();
                } else {
                    workOrderField.setEnabled( false );
                    for( Map.Entry<String, JRadioButton> entry : jrbMap.entrySet() ) {
                        JRadioButton jrb = entry.getValue();
                        if ( jrb.isSelected() ) {
                            selectedWorkOrder = jrb.getText();
                            break;
                        }
                    }
                }
            }

            private void initUI() {
                String noRecentInput = LocaleUtil.getLocalName( NO_RECENT_INPUT );
                String recentInput = LocaleUtil.getLocalName( RECENT_INPUT_WORK_ORDER );
                String inputNew = LocaleUtil.getLocalName( INPUT_NEW_WORK_ORDER );
                String workOrderNum = LocaleUtil.getLocalName( WORK_ORDER_NUM );

                emptyLabel = new JLabel( noRecentInput );
                workOrderLabel = new JLabel( workOrderNum );
                workOrderField = new JTextField();
                workOrderField.setPreferredSize( new Dimension( 150, 20) );

                buttonGroup = new ButtonGroup();
                inputNewWorkOrder = new JRadioButton( inputNew );
                inputNewWorkOrder.setSelected( true );
                inputNewWorkOrder.addActionListener( this );
                buttonGroup.add( inputNewWorkOrder );

                historyInnerPane = new JPanel();
                historyPane = new JScrollPane( historyInnerPane );
                historyPane.setPreferredSize( new Dimension( 300, 150 ) );
                Border titledBd = BorderFactory.createTitledBorder( recentInput );
                historyPane.setBorder( titledBd );
                historyInnerPane.setLayout(new GridBagLayout());
                historyInnerPane.add( emptyLabel, new GBC(0, 0) );

                inputPane = new JPanel();
                inputPane.setLayout( new GridBagLayout() );
                //inputPane.add( inputNewWorkOrder, new GBC(0, 0).setInsets( 10,1,10,1 ) );
                inputPane.add( workOrderLabel, new GBC(0, 1).setInsets( 10 ) );
                inputPane.add( workOrderField, new GBC(1, 1).setInsets( 10 ) );

                setLayout( new GridBagLayout() );
                add( historyPane, new GBC(0, 0)
                        .setInsets(10)
                    );
                add(inputNewWorkOrder, new GBC(0, 1)
                        .setInsets(10)
                        .setAnchor(GBC.WEST));
                add( inputPane, new GBC(0, 2)
                        .setInsets( 10 )
                    );
            }

            void update(String[] wos) {
                if ( null == wos || wos.length == 0 ) {
                    return;
                }
                int len = wos.length;
                JRadioButton[] jrbs = new JRadioButton[len];
                for ( int i = 0; i < len; i++ ) {
                    String wo = wos[i];
                    JRadioButton jrb = jrbMap.get( wo );
                    if ( null == jrb ) {
                        jrb = new JRadioButton( wos[i] );
                        jrb.addActionListener( this );
                    }
                    jrbs[i] = jrb;
                }
                for( Map.Entry<String, JRadioButton> e : jrbMap.entrySet() ) {
                    buttonGroup.remove( e.getValue() );
                }
                jrbMap.clear();
                for ( JRadioButton bt : jrbs ) {
                    jrbMap.put( bt.getText(), bt );
                    buttonGroup.add( bt );
                }
                resetLayout( jrbs );
            }

            private void clearInputCompos() {
                String wo = workOrderField.getText();
                if ( !"".equals( wo ) ) {
                    workOrderField.setText( "" );
                }
                String bc = barCodeField.getText();
                if ( !"".equals( bc ) ) {
                    barCodeField.setText( "" );
                }
            }

            private void resetLayout(JRadioButton[] jrbs) {
                historyInnerPane.removeAll();
                for ( int i = 0; i < jrbs.length; i++ ) {
                    historyInnerPane.add(jrbs[i], new GBC(0, i)
                            .setAnchor(GBC.WEST)
                            .setInsets(5, 10, 5, 10));
                }
                historyInnerPane.revalidate();
            }
        }

    }

    class TestConfigDialog extends IDialog {

        private static final long serialVersionUID = 2040643775295809820L;
        private static final String SRC_PORT = "source port";
        private static final String DST_PORT = "destination port";
        private static final String TEST_SERVER_LIST = "test server list";
        private static final String TEST_TIME = "test time(s)";
        private static final String SELECT_SRC_PORT = "please select source port!";
        private static final String SELECT_DST_PORT = "please select destination port!";
        private static final String SELECT_DIFF_PORT = "please select different port!";
        private static final String CONFIRM = "confirm";
        private static final String TEST_OPTION = "test option";

        private final Map<String, DeviceStatus> dsMap;

        private JList srcPortList;
        private JList dstPortList;
        private JScrollPane srcPortPane;
        private JScrollPane dstPortPane;
        private JLabel secondsLabel;
        private JComboBox secondsBox;
        private JButton okBt;

        private ServerListPane serverListPane;
        private JPanel portListPane;
        private JPanel secondsPane;
        private JPanel buttonPane;

        private ITesterRecord itr;

        public TestConfigDialog(JComponent fatherFrame, Map<String, DeviceStatus> dsMap) {
            super(fatherFrame);
            this.dsMap = dsMap;
            initUI();
        }

        private void initUI() {
            setDialogTitle();
            initServerListPane();
            initPortListPane();
            initSecondsPane();
            initButtonPane();
            setLayout(new GridBagLayout());
            add(serverListPane, new GBC(0, 0)
                    .setAnchor(GBC.WEST)
                    .setInsets(5,20,5,10));
            add(portListPane, new GBC(0, 1)
                    .setAnchor(GBC.WEST)
                    .setInsets(5,10,5,10));
            add( secondsPane, new GBC(0, 2)
                    .setAnchor( GBC.WEST )
                    .setInsets(5,10,5,10) );
            add(buttonPane, new GBC(0, 3)
                    //.setAnchor( GBC.WEST )
                    .setInsets(5, 10, 5, 10));
        }

        private void setDialogTitle() {
            String dialogTitle = LocaleUtil.getLocalName( TEST_OPTION );
            setTitle( dialogTitle );
        }

        private void initServerListPane() {
            serverListPane = new ServerListPane( getIpList(), new JPanel() );
            String title = LocaleUtil.getLocalName( TEST_SERVER_LIST );
            Border titledBorder = BorderFactory.createTitledBorder( title );
            serverListPane.setBorder( titledBorder );
        }

        private void initPortListPane() {
            DefaultListModel listModel0 = new DefaultListModel();
            DefaultListModel listModel1 = new DefaultListModel();
            srcPortList = new JList(listModel0);
            dstPortList = new JList(listModel1);
            srcPortList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
            dstPortList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

            srcPortPane = new JScrollPane( srcPortList );
            dstPortPane = new JScrollPane( dstPortList );
            srcPortPane.setPreferredSize( new Dimension( 150, 200 ) );
            dstPortPane.setPreferredSize( new Dimension( 150, 200 ) );
            String srcPort = LocaleUtil.getLocalName( SRC_PORT );
            String dstPort = LocaleUtil.getLocalName( DST_PORT );
            Border srcPortBorder = BorderFactory.createTitledBorder( srcPort );
            Border dstPortBorder = BorderFactory.createTitledBorder( dstPort );
            srcPortPane.setBorder( srcPortBorder );
            dstPortPane.setBorder( dstPortBorder );

            portListPane = new JPanel();
            portListPane.setLayout( new GridBagLayout() );
            portListPane.add( srcPortPane, new GBC(0, 0).setInsets(10) );
            portListPane.add( dstPortPane, new GBC(1, 0).setInsets(10) );
        }

        private void initSecondsPane() {
            String title = LocaleUtil.getLocalName( TEST_TIME );
            secondsLabel = new JLabel( title );
            secondsBox = new JComboBox( new String[] {"30", "20", "10"} );
            secondsPane = new JPanel();
            secondsPane.setLayout( new GridBagLayout() );
            secondsPane.add( secondsLabel, new GBC(0, 0).setInsets( 5, 10, 5, 10 ) );
            secondsPane.add( secondsBox, new GBC(1, 0).setInsets( 5, 10, 5, 10 ) );
        }

        private void initButtonPane() {
            String confirm = LocaleUtil.getLocalName( CONFIRM );
            okBt = new JButton( confirm );
            okBt.setPreferredSize( new Dimension(70, 30) );
            okBt.addActionListener( this );

            buttonPane = new JPanel();
            buttonPane.setLayout( new GridBagLayout() );

            buttonPane.add( okBt, new GBC(0, 0).setInsets(5, 10, 5, 10) );
            buttonPane.add( cancelBt, new GBC(1, 0).setInsets(5, 10, 5, 10) );
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if ( isNoPortSelected() || isSamePortSelected() ) {
                return;
            }
            super.actionPerformed( e );
            addTestCase( itr );
        }

        private void addTestCase(ITesterRecord itr) {
            DeviceStatus.PortLocation src =
                    (DeviceStatus.PortLocation) srcPortList.getSelectedValue();
            DeviceStatus.PortLocation dst =
                    (DeviceStatus.PortLocation) dstPortList.getSelectedValue();
            String ip = serverListPane.getSelectedServerIP();
            int seconds = Integer.parseInt(
                    (String) secondsBox.getSelectedItem()
            );

            TestCaseConfig tcConfig = new TestCaseConfig.Builder()
                    .ip( ip )
                    .srcCardId( src.getCardId() )
                    .srcPortId( src.getPortId() )
                    .dstCardId( dst.getCardId() )
                    .dstPortId( dst.getPortId() )
                    .seconds( seconds ).build();

            ITesterFrame.this.addTestCase( itr, tcConfig );
        }

        private boolean isNoPortSelected() {
            if ( srcPortList.getSelectedIndex() < 0 ) {
                String msg = LocaleUtil.getLocalName( SELECT_SRC_PORT );
                MsgDialogUtil.showMsgDialog( dialog, msg );
                return true;
            }
            if ( dstPortList.getSelectedIndex() < 0 ) {
                String msg = LocaleUtil.getLocalName( SELECT_DST_PORT );
                MsgDialogUtil.showMsgDialog( dialog, msg );
                return true;
            }
            return false;
        }

        private boolean isSamePortSelected() {
            DeviceStatus.PortLocation src =
                    (DeviceStatus.PortLocation) srcPortList.getSelectedValue();
            DeviceStatus.PortLocation dst =
                    (DeviceStatus.PortLocation) dstPortList.getSelectedValue();
            if ( src.equals( dst ) ) {
                String msg = LocaleUtil.getLocalName( SELECT_DIFF_PORT );
                MsgDialogUtil.showMsgDialog( dialog, msg );
                return true;
            }
            return false;
        }

        public void display(ITesterRecord itr) {
            this.itr = itr;
            serverListPane.update( getIpList() );
            String serverIP = serverListPane.getSelectedServerIP();
            if ( null != serverIP ) {
                updatePortLists( serverIP );
            }
            super.display();
        }

        void updatePortLists(String serverIP) {
            DeviceStatus ds = dsMap.get( serverIP );
            if ( null == ds ) {
                return;
            }
            DeviceStatus.PortLocation[] ports = ds.getLinkedAndNotUsedPorts();
            updatePortList( srcPortList, ports );
            updatePortList( dstPortList, ports );
        }

        private void updatePortList(JList list, DeviceStatus.PortLocation[] ports) {
            DefaultListModel model = (DefaultListModel) list.getModel();
            model.removeAllElements();
            for ( int i=0; i < ports.length; i++ ) {
                model.add( i, ports[i] );
            }
            list.revalidate();
        }

        private String[] getIpList() {
            String[] ipList = new String[ dsMap.size() ];
            Set<String> ipSet =  dsMap.keySet();
            ipList = ipSet.toArray( ipList );
            return ipList;
        }

        class ServerListPane extends JScrollPane implements ActionListener {

            private static final long serialVersionUID = 6724875493333867111L;

            private ButtonGroup buttonGroup;
            private Map<String, JRadioButton> jrbMap = new HashMap<String, JRadioButton>();
            private String selectedServerIP;
            private JPanel innerPane;

            ServerListPane(String[] ipList, JPanel innerPane) {
                super( innerPane );
                this.innerPane = innerPane;
                this.buttonGroup = new ButtonGroup();
                this.innerPane.setLayout(new GridBagLayout());
                update( ipList );
                setPreferredSize( new Dimension( 318, 150 ));
            }

            String getSelectedServerIP() {
                return selectedServerIP;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                for( Map.Entry<String, JRadioButton> entry: jrbMap.entrySet() ) {
                    JRadioButton jrb = entry.getValue();
                    if ( jrb.isSelected() ) {
                        selectedServerIP = jrb.getText();
                        updatePortLists( selectedServerIP );
                        break;
                    }
                }
            }

            void update(String[] ipList) {
                int len = ipList.length;
                JRadioButton[] jrbs = new JRadioButton[len];
                for ( int i=0; i < len; i++ ) {
                    String ip = ipList[i];
                    JRadioButton jrb = jrbMap.get( ip );
                    if ( null == jrb ) {
                        jrb = new JRadioButton(ip);
                        jrb.addActionListener( this );
                    }
                    jrbs[i] = jrb;
                }
                for( Map.Entry<String, JRadioButton> e : jrbMap.entrySet() ) {
                    buttonGroup.remove( e.getValue() );
                }
                jrbMap.clear();
                for ( JRadioButton bt : jrbs ) {
                    jrbMap.put( bt.getText(), bt );
                    buttonGroup.add( bt );
                }
                resetLayout( jrbs );
            }

            private void resetLayout( JRadioButton[] jrbs ) {
                if ( null == jrbs || jrbs.length == 0 ) {
                    return;
                }
                innerPane.removeAll();
                for( int i = 0; i < jrbs.length; i++ ) {
                   innerPane.add(jrbs[i], new GBC(0, i)
                           .setAnchor(GBC.WEST)
                           .setInsets(5, 10, 5, 10));
                }
            }

        }

    }

    abstract class IDialog extends JDialog implements ActionListener {

        private static final String CANCEL = "cancel";

        private final JComponent fatherFrame;

        protected JDialog dialog = this;
        protected JButton cancelBt;

        public IDialog(JComponent fatherFrame) {
            this.fatherFrame = fatherFrame;
            initUI();
        }

        private void initUI() {
            String cancel = LocaleUtil.getLocalName(CANCEL);
            cancelBt = new JButton( cancel );
            cancelBt.setPreferredSize( new Dimension(70, 30) );
            cancelBt.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    close();
                }
            });
            addWindowListener( new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    if ( null != fatherFrame ) {
                        fatherFrame.setEnabled( true );
                    }
                }
            });
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            close();
        }

        public void display() {
            if ( null != fatherFrame ) {
                fatherFrame.setEnabled( false );
            }
            dialog.setAlwaysOnTop( true );
            dialog.pack();
            dialog.setResizable(false);
            dialog.setLocationRelativeTo( null );
            dialog.setVisible( true );
        }

        public void close() {
            if ( null != fatherFrame ) {
                fatherFrame.setEnabled( true );
            }
            dialog.setVisible( false );
        }

    }

}
