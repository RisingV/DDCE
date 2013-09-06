package com.bdcom.dce.view.itester;

import com.bdcom.dce.biz.pojo.ITesterRecord;
import com.bdcom.dce.itester.api.wrapper.*;
import com.bdcom.dce.itester.lib.PortStats;
import com.bdcom.dce.util.LocaleUtil;
import com.bdcom.dce.util.StringUtil;
import com.bdcom.dce.util.logger.ErrorLogger;
import com.bdcom.dce.view.util.GBC;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-8-20    <br/>
 * Time: 17:34  <br/>
 */
public class TestProgressRow implements
        ActionListener, PropertyChangeListener {

    private static final String UNTESTED = "Untested";
    private static final String SUCCESS = "Success!";
    private static final String FAIL = "Fail!";
    private static final String ABNORMAL = "Abnormal!";

    private static final String START = "Start";
    private static final String RESTART = "Restart";
    private static final String DETAIL = "Detail";
    private static final String DELETE = "Delete";

    private final ITesterRecord iTesterRecord;
    private final ITesterAPIWrapper apiWrapper;
    private final TestCaseConfig testConfig;

    private TestTask testTask;
    private TestSession testSession;

    private JProgressBar progressBar;
    private JButton startButton;
    private JButton detailButton;
    private JButton removeButton;
    private ActionListener removeAction;
    private AbstractAction updateAction;
    private JLabel statusLabel;
    private DetailDialog detailDialog;
    private JPanel buttonPanel;
    private JComponent panelOwner;
    private boolean tested;
    private boolean running;

    TestProgressRow(ITesterRecord iTesterRecord, ITesterAPIWrapper apiWrapper,
                    TestCaseConfig testConfig) {
        iTesterRecord.setTestTime(
                testConfig.getSeconds()
        );

        this.iTesterRecord = iTesterRecord;
        this.apiWrapper = apiWrapper;
        this.testConfig = testConfig;

        initUI();
    }

    public void setUpdateAction(AbstractAction action) {
        updateAction = action;
    }

    public void setPanelOwner( JComponent panelOwner ) {
        this.panelOwner = panelOwner;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JLabel getStatusLabel() {
        return statusLabel;
    }

    public JPanel getButtonPanel() {
        return buttonPanel;
    }

    public void startTest() {
        tested = false;
        running = true;
        startButton.setEnabled( false );
        detailButton.setEnabled( false );
        removeButton.setEnabled( false );
//        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            testSession = apiWrapper.startTest(
                    testConfig.getIp(),
                    testConfig.getSrcCardId(),
                    testConfig.getSrcPortId(),
                    testConfig.getDstCardId(),
                    testConfig.getDstPortId(),
                    testConfig.getSeconds()
            );
            progressBar.setForeground( Color.blue );
            testTask = new TestTask( testSession );
            testTask.addPropertyChangeListener(this);
            testTask.execute();
        } catch (ITesterException ex) {
            handleTestException(ex);
        }
    }

    public void setRemoveAction(ActionListener removeAction) {
        if  ( null != this.removeAction ) {
            removeButton.removeActionListener( this.removeAction );
        }
        this.removeAction = removeAction;
        removeButton.addActionListener( this.removeAction );
    }

    public boolean isTested() {
        return tested;
    }

    public boolean isRunning() {
        return running;
    }

    public ITesterRecord getTestResult() {
        if ( !tested ) {
            return null;
        }
        return iTesterRecord;
    }

    private void initUI() {
        detailDialog = new DetailDialog( iTesterRecord, testConfig );

        String start = LocaleUtil.getLocalName( START );
        startButton = new JButton( start );
        startButton.addActionListener( this );

        String detail = LocaleUtil.getLocalName( DETAIL );
        detailButton = new JButton( detail );
        detailButton.addActionListener( detailDialog );
        //detailButton.setEnabled(false);

        String delete = LocaleUtil.getLocalName( DELETE );
        removeButton = new JButton( delete );

        progressBar = new JProgressBar( 0, 100 );
        progressBar.setValue( 0 );
        progressBar.setStringPainted( true );
        progressBar.setPreferredSize( new Dimension( 180, 15 ) );

        String untested = LocaleUtil.getLocalName( UNTESTED );
        Font f = new Font( null, Font.PLAIN, 20 );
        statusLabel = new JLabel( untested );
        statusLabel.setFont( f );
        detailDialog.updateTestStatus(untested);

        buttonPanel = new JPanel();
        buttonPanel.setLayout( new GridBagLayout() );
        buttonPanel.add(startButton, new GBC(0, 0).setInsets(2, 6, 2, 6));
        buttonPanel.add(detailButton, new GBC(1, 0).setInsets(2, 6, 2, 6));
        buttonPanel.add(removeButton, new GBC(2, 0).setInsets(2, 6, 2, 6));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        startTest();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ( "progress".equals( evt.getPropertyName() ) ) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue( progress );
        }
    }

    private void handleTestException(ITesterException e) {
        String abnormal = LocaleUtil.getLocalName( ABNORMAL );
        statusLabel.setText( abnormal );
        progressBar.setForeground( Color.yellow );
        if ( null != updateAction ) {
            updateAction.actionPerformed( null );
        }
        startButton.setEnabled( true );
        detailButton.setEnabled( true );
        removeButton.setEnabled( true );
        running = false;

        String status = ITesterUtil.reportTestException( e, panelOwner );
        detailDialog.updateTestStatus( status );
    }

    class TestTask extends SwingWorker<Void, Void> {

        private final TestSession session;

        TestTask(TestSession session) {
            this.session = session;
        }

        @Override
        protected Void doInBackground() throws Exception {
            int progress = 0;
            do {
                boolean interrupted = false;
                try {
                    progress = session.getProgressPercent();
                } catch (ITesterException e) {
                    interrupted = true;
                    handleTestException(e);
                } finally {
                    if ( interrupted ) {
                        break;
                    }
                }
                setProgress( progress );
                try {
                    TimeUnit.MILLISECONDS.sleep( 800 );
                } catch (InterruptedException e) {
                    ErrorLogger.log("ProgressBar updating thread interrupted: "
                            + e.getMessage());
                }
                if ( null != updateAction ) {
                    updateAction.actionPerformed( null );
                }
            } while ( progress < 100 );
            return null;
        }

        @Override
        protected void done() {
            super.done();
            if ( null != updateAction ) {
                updateAction.actionPerformed( null );
            }
//            TestProgressRow.this.setCursor(
//                    Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR) );

            startButton.setEnabled( true );
            detailButton.setEnabled( true );
            removeButton.setEnabled( true );

            String restart = LocaleUtil.getLocalName( RESTART );
            if ( !restart.equals( startButton.getText() ) ) {
                startButton.setText( restart );
            }

            try {
                boolean isPass = session.isTestPass( testConfig.getPercent() );
                iTesterRecord.setTestPass( isPass );
                String status = null;
                if ( isPass ) {
                    status = LocaleUtil.getLocalName( SUCCESS );
                    progressBar.setForeground( Color.green );
                } else {
                    status = LocaleUtil.getLocalName( FAIL );
                    progressBar.setForeground(Color.red);
                }
                statusLabel.setText( status );
                detailDialog.updateTestStatus( status );

                PortStats ps0 = session.getSrcPortStats();
                PortStats ps1 = session.getDstPortStats();
                detailDialog.updateStats( ps0, ps1 );
                tested = true;
            } catch (ITesterException e) {
                handleTestException( e );
            } finally {
                running = false;
            }
        }

    }

    class DetailDialog extends JDialog implements ActionListener {

        private static final long serialVersionUID = 3562480069831475350L;
        private static final String TX_PACKETS = "TX_PACKETS";
        private static final String TX_PPS = "TX_PPS";
        private static final String TX_BPS = "TX_BPS";
        private static final String RX_PACKETS = "RX_PACKETS";
        private static final String RX_FCS_ERRORS = "RX_FCS_ERRORS";
        private static final String RX_PAYLOAD_ERRORS = "RX_PAYLOAD_ERRORS";
        private static final String RX_PPS = "RX_PPS";
        private static final String RX_BPS = "RX_BPS";

        private static final String WORK_ORDER_NUM = "work order number:";
        private static final String BAR_CODE = "bar code:";
        private static final String SRC_PORT = "source port";
        private static final String DST_PORT = "destination port";
        private static final String TEST_TIME = "test time(s)";
        private static final String STREAM_PERCENT = "stream percent";
        private static final String TEST_OPTION = "test option";
        private static final String TEST_STATS = "test data stats";
        private static final String TEST_STATUS = "Test Status";

        private JLabel testStatusLabel;
        private JLabel workOrderLabel;
        private JLabel barCodeLabel;
        private JLabel srcPortLabel;
        private JLabel dstPortLabel;
        private JLabel testTimeLabel;
        private JLabel percentLabel;
        private JLabel txPacketsLabel;
        private JLabel txPpsLabel;
        private JLabel txBpsLabel;
        private JLabel rxPacketsLabel;
        private JLabel rxFcsErrorsLabel;
        private JLabel rxPayloadErrorsLabel;
        private JLabel rxPpsLabel;
        private JLabel rxBpsLabel;

        private JTextField workOrderField;
        private JTextField barCodeField;
        private JTextField srcPortField;
        private JTextField dstPortField;
        private JTextField testTimeField;
        private JTextField percentField;
        private JTextField txPacketsField;
        private JTextField txPpsField;
        private JTextField txBpsField;
        private JTextField rxPacketsField;
        private JTextField rxFcsErrorsField;
        private JTextField rxPayloadErrorsField;
        private JTextField rxPpsField;
        private JTextField rxBpsField;

        private ButtonGroup buttonGroup;
        private JRadioButton srcPortMode;
        private JRadioButton dstPortMode;
        private JPanel modePane;

        private JPanel testConfigPane;
        private JPanel testStatsPane;
        private JPanel testStatusPane;
        private JPanel testStatusOuterPane;

        private final ITesterRecord itr;
        private final TestCaseConfig testConfig;

        private PortStats srcPortStats;
        private PortStats dstPortStats;

        DetailDialog( ITesterRecord itr, TestCaseConfig testConfig ) {
            this.itr = itr;
            this.testConfig = testConfig;
            initUI();
            updateTestOption( this.itr, this.testConfig );
        }

        private void initUI() {
            String txPackets = LocaleUtil.getLocalName( TX_PACKETS );
            String txPps = LocaleUtil.getLocalName( TX_PPS );
            String txBps = LocaleUtil.getLocalName( TX_BPS );
            String rxPackets = LocaleUtil.getLocalName( RX_PACKETS );
            String rxFcsErrors = LocaleUtil.getLocalName( RX_FCS_ERRORS );
            String rxPayloadErrors = LocaleUtil.getLocalName( RX_PAYLOAD_ERRORS );
            String rxPps = LocaleUtil.getLocalName( RX_PPS );
            String rxBps = LocaleUtil.getLocalName( RX_BPS );

            String workOrderNum = LocaleUtil.getLocalName( WORK_ORDER_NUM );
            String barCode = LocaleUtil.getLocalName( BAR_CODE );
            String srcPort = LocaleUtil.getLocalName( SRC_PORT );
            String dstPort = LocaleUtil.getLocalName( DST_PORT );
            String testTime = LocaleUtil.getLocalName( TEST_TIME );
            String streamPercent = LocaleUtil.getLocalName( STREAM_PERCENT );
            String testOption = LocaleUtil.getLocalName( TEST_OPTION );
            String testStats = LocaleUtil.getLocalName( TEST_STATS );
            String testStatus = LocaleUtil.getLocalName( TEST_STATUS );

            workOrderLabel = newLabel( workOrderNum );
            barCodeLabel = newLabel( barCode );
            srcPortLabel = newLabel( srcPort );
            dstPortLabel = newLabel( dstPort );
            testTimeLabel = newLabel( testTime );
            percentLabel = newLabel( streamPercent );

            txPacketsLabel = newLabel(txPackets);
            txPpsLabel = newLabel(txPps);
            txBpsLabel = newLabel(txBps);
            rxPacketsLabel = newLabel( rxPackets );
            rxFcsErrorsLabel = newLabel(rxFcsErrors);
            rxPayloadErrorsLabel = newLabel(rxPayloadErrors);
            rxPpsLabel = newLabel(rxPps);
            rxBpsLabel = newLabel(rxBps);

            workOrderField = newField();
            barCodeField = newField();
            srcPortField = newField();
            dstPortField = newField();
            testTimeField = newField();
            percentField = newField();

            txPacketsField = newField();
            txPpsField = newField();
            txBpsField = newField();
            rxPacketsField = newField();
            rxFcsErrorsField = newField();
            rxPayloadErrorsField = newField();
            rxPpsField = newField();
            rxBpsField = newField();

            buttonGroup = new ButtonGroup();
            srcPortMode = new JRadioButton( srcPort );
            dstPortMode = new JRadioButton( dstPort );
            buttonGroup.add( srcPortMode );
            buttonGroup.add( dstPortMode );
            modePane = new JPanel();
            modePane.setLayout( new GridBagLayout() );
            modePane.add(srcPortMode, new GBC(0, 0).setInsets(5, 20, 5, 20));
            modePane.add( dstPortMode, new GBC( 1,0 ).setInsets( 5, 20, 5, 20 ) );

            testConfigPane = new JPanel();
            testConfigPane.setLayout( new GridBagLayout() );
            testConfigPane.setBorder(
                    BorderFactory.createTitledBorder( testOption )
            );

            testConfigPane.add( workOrderLabel, loc(0, 0) );
            testConfigPane.add( workOrderField, loc(1, 0) );
            testConfigPane.add( barCodeLabel, loc(2, 0) );
            testConfigPane.add( barCodeField, loc(3, 0) );
            testConfigPane.add( srcPortLabel, loc(0, 2) );
            testConfigPane.add( srcPortField, loc(1, 2) );
            testConfigPane.add( dstPortLabel, loc(2, 2) );
            testConfigPane.add( dstPortField, loc(3, 2) );
            testConfigPane.add( testTimeLabel, loc(0, 3) );
            testConfigPane.add( testTimeField, loc(1, 3) );
            testConfigPane.add( percentLabel, loc(2, 3) );
            testConfigPane.add( percentField, loc(3, 3) );

            testStatsPane = new JPanel();
            testStatsPane.setLayout(new GridBagLayout());

            testStatsPane.add(txPacketsLabel, loc(0, 0));
            testStatsPane.add(txPacketsField, loc(1, 0));
            testStatsPane.add(rxPacketsLabel, loc(2, 0));
            testStatsPane.add(rxPacketsField, loc(3, 0));

            testStatsPane.add(txBpsLabel, loc(0, 1));
            testStatsPane.add(txBpsField, loc(1, 1));
            testStatsPane.add(txPpsLabel, loc(2, 1));
            testStatsPane.add(txPpsField, loc(3, 1));

            testStatsPane.add(rxFcsErrorsLabel, loc(0, 2));
            testStatsPane.add(rxFcsErrorsField, loc(1, 2));
            testStatsPane.add(rxPayloadErrorsLabel, loc(2, 2));
            testStatsPane.add(rxPayloadErrorsField, loc(3, 2));

            testStatsPane.add(rxPpsLabel, loc(0, 3));
            testStatsPane.add(rxPpsField, loc(1, 3));
            testStatsPane.add(rxBpsLabel, loc(2, 3));
            testStatsPane.add(rxBpsField, loc(3, 3));

            testStatusLabel = new JLabel();
            testStatusLabel.setPreferredSize( new Dimension( 588, 35 ) );
            testStatusPane = new JPanel();
            Border statusTitle = BorderFactory.createTitledBorder( testStatus );
            testStatusPane.setBorder(statusTitle);
            testStatusPane.add( testStatusLabel, BorderLayout.CENTER );

            testStatusOuterPane = new JPanel();
            testStatusOuterPane.setLayout( new GridBagLayout() );
            testStatusOuterPane.setBorder(
                    BorderFactory.createTitledBorder(testStats)
            );
            testStatusOuterPane.add(modePane, new GBC(0, 0));
            testStatusOuterPane.add( testStatsPane, new GBC( 0,1 ) );

            setLayout(new GridBagLayout());
            add( testStatusPane, loc(0, 0) );
            add( testConfigPane, loc(0, 1) );
            add( testStatusOuterPane, loc(0, 2) );

            addWindowListener( new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    if ( null != TestProgressRow.this.panelOwner ) {
                        TestProgressRow.this.panelOwner.setEnabled( true );
                    }
                }
            });

            bindModeListeners();
            pack();
        }

        void bindModeListeners() {
            ActionListener al = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if ( srcPortMode.isSelected() ) {
                        if ( null != srcPortStats ) {
                            updateStats( srcPortStats );
                        }
                    } else if ( dstPortMode.isSelected() ) {
                        if ( null != dstPortStats ) {
                            updateStats( dstPortStats );
                        }
                    }
                }
            };
            srcPortMode.addActionListener( al );
            dstPortMode.addActionListener( al );
        }

        void updateTestStatus( String status ) {
            if ( !StringUtil.isNotBlank( status ) ) {
                return;
            }
            String old = testStatusLabel.getText();
            if ( !status.equals( old ) ) {
                testStatusLabel.setText(status);
            }
        }

        void updateTestOption(ITesterRecord itr, TestCaseConfig testConfig ) {

            StringBuilder sb0 =new StringBuilder();
            sb0.append( "(" )
               .append( testConfig.getSrcCardId() )
               .append( "," )
               .append( testConfig.getSrcPortId() )
               .append(")");
            StringBuilder sb1 = new StringBuilder();
            sb1.append( "(" )
               .append( testConfig.getDstCardId() )
               .append( "," )
               .append( testConfig.getDstPortId() )
               .append( ")" );

            String workOrder = itr.getWorkOrder();
            String barCode = itr.getBarCode();
            String srcPort = sb0.toString();
            String dstPort = sb1.toString();
            String testTime = String.valueOf( testConfig.getSeconds() );
            String streamPercent = String.valueOf( testConfig.getPercent() );

            workOrderField.setText( workOrder );
            barCodeField.setText( barCode );
            srcPortField.setText( srcPort );
            dstPortField.setText( dstPort );
            testTimeField.setText( testTime );
            percentField.setText( streamPercent );
        }

        void updateStats(PortStats srcPortStats, PortStats dstPortStats) {
            this.srcPortStats = srcPortStats;
            this.dstPortStats = dstPortStats;
            srcPortMode.setSelected( true );
            updateStats( srcPortStats );
        }
        private void updateStats(PortStats ps) {
            long[] stats = ps.getStats();
            setField( txPacketsField, stats[0] );
            setField( txPpsField, stats[1] );
            setField( txBpsField, stats[2] );
            setField( rxPacketsField, stats[3] );
            setField( rxFcsErrorsField, stats[4] );
            setField( rxPayloadErrorsField, stats[5] );
            setField( rxPpsField, stats[6] );
            setField( rxBpsField, stats[7] );
        }

        private void setField(JTextField f, long l) {
            f.setText( String.valueOf( l ) );
        }

        private JLabel newLabel(String text) {
            JLabel l = new JLabel( text );
            l.setPreferredSize( new Dimension( 110, 20 ) );
            return l;
        }

        private JTextField newField() {
            JTextField f = new JTextField();
            f.setPreferredSize( new Dimension( 150, 20 ));
            f.setEditable( false );
            f.setText("0");
            return f;
        }

        private GBC loc(int x, int y) {
            return new GBC(x, y).setInsets( 10 );
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if ( null != TestProgressRow.this.panelOwner ) {
                TestProgressRow.this.panelOwner.setEnabled( false );
            }
            setAlwaysOnTop( true );
            pack();
            setResizable( false );
            setLocationRelativeTo( null );
            setVisible( true );
        }

    }

}
