package com.bdcom.view.itester;

import com.bdcom.biz.pojo.ITesterRecord;
import com.bdcom.itester.api.wrapper.ITesterAPIWrapper;
import com.bdcom.itester.api.wrapper.ITesterException;
import com.bdcom.itester.api.wrapper.TestCaseConfig;
import com.bdcom.itester.api.wrapper.TestSession;
import com.bdcom.itester.lib.PortStats;
import com.bdcom.util.LocaleUtil;
import com.bdcom.util.log.ErrorLogger;
import com.bdcom.view.util.GBC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-8-20    <br/>
 * Time: 17:34  <br/>
 */
public class ProgressPanel extends JPanel
                           implements ActionListener, PropertyChangeListener {

    private static final String UNTESTED = "Untested";
    private static final String SUCCESS = "Success!";
    private static final String FAIL = "Fail!";

    private static final String START = "Start";
    private static final String RESTART = "Restart";
    private static final String DETAIL = "Detail";

    private final ITesterRecord iTesterRecord;
    private final ITesterAPIWrapper apiWrapper;
    private final TestCaseConfig testConfig;

    private TestTask testTask;
    private TestSession testSession;

    private JProgressBar progressBar;
    private JButton startButton;
    private JButton detailButton;
    private JLabel statusLabel;
    private DetailDialog detailDialog;
    private boolean tested;

    ProgressPanel(ITesterRecord iTesterRecord, ITesterAPIWrapper apiWrapper,
                  TestCaseConfig testConfig) {
        this.iTesterRecord = iTesterRecord;
        this.apiWrapper = apiWrapper;
        this.testConfig = testConfig;
        initUI();
    }

    public void startTest() {
        startButton.setEnabled( false );
        detailButton.setEnabled( false );
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        boolean startFail = false;
        try {
            testSession = apiWrapper.startTest(
                    testConfig.getIp(),
                    testConfig.getSrcCardId(),
                    testConfig.getSrcPortId(),
                    testConfig.getDstCardId(),
                    testConfig.getDstPortId(),
                    testConfig.getSeconds()
            );
            testTask = new TestTask( testSession );
            testTask.addPropertyChangeListener(this);
            testTask.execute();
        } catch (ITesterException ex) {
            startFail = true;
            reportTestException( ex );
        } finally {
            if ( startFail ) {
                startButton.setEnabled( true );
            }
        }
    }

    public boolean isTested() {
        return tested;
    }

    public ITesterRecord getTestResult() {
        if ( !tested ) {
            return null;
        }
        return iTesterRecord;
    }

    private void initUI() {
        detailDialog = new DetailDialog();

        String start = LocaleUtil.getLocalName( START );
        startButton = new JButton( start );
        startButton.addActionListener( this );

        String detail = LocaleUtil.getLocalName( DETAIL );
        detailButton = new JButton( detail );
        detailButton.addActionListener( detailDialog );
        detailButton.setEnabled(false);

        progressBar = new JProgressBar( 0, 100 );
        progressBar.setValue( 0 );
        progressBar.setStringPainted( true );

        String untested = LocaleUtil.getLocalName( UNTESTED );
        statusLabel = new JLabel( untested );

        setLayout( new GridBagLayout() );
        this.add( progressBar, new GBC(0, 0).setInsets( 5, 10, 5, 10 ) );
        this.add( statusLabel, new GBC(1, 0).setInsets( 5, 10, 5, 10 ) );
        this.add( startButton, new GBC(2, 0).setInsets( 5, 10, 5, 10 ) );
        this.add( detailButton, new GBC(3, 0).setInsets( 5, 10, 5, 10 ) );
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

    private void reportTestException(ITesterException e) {
        ITesterUtil.reportTestException( e, this );
    }

    class TestTask extends SwingWorker<Void, Void> {

        private final TestSession session;

        TestTask(TestSession session) {
            this.session = session;
        }

        @Override
        protected Void doInBackground() throws Exception {
            int progress = 0;
            while( progress < 100 ) {
                setProgress( progress );
                progress = session.getProgressPercent();
                try {
                    TimeUnit.MILLISECONDS.sleep( 800 );
                } catch (InterruptedException e) {
                    ErrorLogger.log("ProgressBar updating thread interrupted: "
                            + e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void done() {
            super.done();

            startButton.setEnabled(true);
            detailButton.setEnabled( true );

            String restart = LocaleUtil.getLocalName( RESTART );
            if ( !restart.equals( startButton.getText() ) ) {
                startButton.setText( restart );
            }

            boolean isPass = session.isTestPass();
            iTesterRecord.setTestPass( isPass );
            if ( isPass ) {
                String success = LocaleUtil.getLocalName( SUCCESS );
                statusLabel.setText( success );
                progressBar.setForeground( Color.green );
            } else {
                String fail = LocaleUtil.getLocalName( FAIL );
                statusLabel.setText( fail );
                progressBar.setForeground(Color.red);
            }

            PortStats ps = session.getPortStats();
            detailDialog.updateStats( ps );
            tested = true;
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

        private JLabel txPacketsLabel;
        private JLabel txPpsLabel;
        private JLabel txBpsLabel;
        private JLabel rxPacketsLabel;
        private JLabel rxFcsErrorsLabel;
        private JLabel rxPayloadErrorsLabel;
        private JLabel rxPpsLabel;
        private JLabel rxBpsLabel;

        private JTextField txPacketsField;
        private JTextField txPpsField;
        private JTextField txBpsField;
        private JTextField rxPacketsField;
        private JTextField rxFcsErrorsField;
        private JTextField rxPayloadErrorsField;
        private JTextField rxPpsField;
        private JTextField rxBpsField;

        DetailDialog() {
            String txPackets = LocaleUtil.getLocalName( TX_PACKETS );
            String txPps = LocaleUtil.getLocalName( TX_PPS );
            String txBps = LocaleUtil.getLocalName( TX_BPS );
            String rxPackets = LocaleUtil.getLocalName( RX_PACKETS );
            String rxFcsErrors = LocaleUtil.getLocalName( RX_FCS_ERRORS );
            String rxPayloadErrors = LocaleUtil.getLocalName( RX_PAYLOAD_ERRORS );
            String rxPps = LocaleUtil.getLocalName( RX_PPS );
            String rxBps = LocaleUtil.getLocalName( RX_BPS );

            txPacketsLabel = newLabel(txPackets);
            txPpsLabel = newLabel(txPps);
            txBpsLabel = newLabel(txBps);
            rxPacketsLabel = newLabel( rxPackets );
            rxFcsErrorsLabel = newLabel(rxFcsErrors);
            rxPayloadErrorsLabel = newLabel(rxPayloadErrors);
            rxPpsLabel = newLabel(rxPps);
            rxBpsLabel = newLabel(rxBps);

            txPacketsField = newField();
            txPpsField = newField();
            txBpsField = newField();
            rxPacketsField = newField();
            rxFcsErrorsField = newField();
            rxPayloadErrorsField = newField();
            rxPpsField = newField();
            rxBpsField = newField();

            setLayout( new GridBagLayout() );

            add( txPacketsLabel, loc(0, 0) );
            add( txPacketsField, loc(1, 0) );

            add( txPpsLabel, loc(0, 1) );
            add( txPpsField, loc(1, 1) );

            add( txBpsLabel, loc(0, 2) );
            add( txBpsField, loc(1, 2) );

            add( rxPacketsLabel, loc(0, 3) );
            add( rxPacketsField, loc(1, 3) );

            add( rxFcsErrorsLabel, loc(0, 4) );
            add( rxFcsErrorsField, loc(1, 4) );

            add( rxPayloadErrorsLabel, loc(0, 5) );
            add( rxPayloadErrorsField, loc(1, 5) );

            add( rxPpsLabel, loc(0, 6) );
            add( rxPpsField, loc(1, 6) );

            add( rxBpsLabel, loc(0, 7) );
            add( rxBpsField, loc(0, 7) );

            pack();
        }

        void updateStats(PortStats ps) {
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
            l.setPreferredSize( new Dimension( 80, 20 ) );
            return l;
        }

        private JTextField newField() {
            JTextField f = new JTextField();
            f.setPreferredSize( new Dimension( 80, 20 ));
            return f;
        }

        private GBC loc(int x, int y) {
            return new GBC(x, y).setInsets( 10 );
        }

        @Override public void actionPerformed(ActionEvent e) {
            this.setVisible( true );
        }

    }

}
