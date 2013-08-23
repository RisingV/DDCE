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
import com.bdcom.view.ViewTab;
import com.bdcom.view.itester.tree.DeviceInfoTreeBuilder;
import com.bdcom.view.util.GBC;
import com.bdcom.view.util.MsgDialogUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-8-14    <br/>
 * Time: 15:45  <br/>
 */
public class ITesterFrame extends JPanel
        implements ViewTab, ApplicationConstants {

    private final ITesterAPI api;
    private final ClientProxy client;

    private ITesterAPIWrapper apiWrapper;

    private JScrollPane leftPane;
    private JPanel rightPane;
    private JPanel buttonPane;
    private JPanel modePane;
    private JPanel progressesPane;

    private JButton addServerBt;
    private JButton addTestBt;
    private JButton startAllBt;

    private Map<String, DeviceStatus> dsMap = new HashMap<String, DeviceStatus>();

    public ITesterFrame(ITesterAPI api, ClientProxy client) {
        this.api = api;
        this.client = client;
        this.apiWrapper = new ITesterAPIWrapper( api );
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
        leftPane.add( deviceInfoTree );
    }

    void addTestCase(ITesterRecord record, TestCaseConfig testConfig) {
        ProgressPanel progressPanel = new ProgressPanel( record, apiWrapper, testConfig );
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

        public AddServerDialog(JFrame fatherFrame, Map<String, DeviceStatus> dsMap) {
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
                MsgDialogUtil.showMsgDialog( msg );
                return;
            }
            if ( dsMap.containsKey( ip) ) {
                String msg = LocaleUtil.getLocalName( ADDR_ADDED );
                MsgDialogUtil.showMsgDialog( msg );
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

        public WorkOrderDialog(JFrame fatherFrame, TestConfigDialog tcDialog) {
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
            workOrderPane.setBorder( workOrderTitle );
            barCodePane = new JPanel();
            barCodePane.setBorder( barCodeTitle );
            buttonPane = new JPanel();

            barCodeLabel = new JLabel( barCode );
            barCodeField = new JTextField();

            nextBt = new JButton( next );
            nextBt.addActionListener(this);

            barCodePane.setLayout( new GridBagLayout() );
            barCodePane.add( barCodeLabel, new GBC(0, 0).setInsets(5, 10, 5, 10) );
            barCodePane.add( barCodeField, new GBC(1, 0).setInsets(5, 10, 5, 10) );

            buttonPane.setLayout( new GridBagLayout() );
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
            workOrderPane.update( wos );
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
                MsgDialogUtil.showMsgDialog( invalidWorkOder );
                return;
            }
            if ( itr.isEverTested() ) {
                String everTested = LocaleUtil.getLocalName( EVER_TESTED );
                MsgDialogUtil.showMsgDialog( everTested );
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
                MsgDialogUtil.showMsgDialog( blankWorkOrder );
                return true;
            }
            String barCode = barCodeField.getText();
            if ( !StringUtil.isNotBlank( barCode ) ) {
                String blankBarCode = LocaleUtil.getLocalName( BAR_CODE_IS_BLANK );
                MsgDialogUtil.showMsgDialog( blankBarCode );
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
                    for ( int i= LIMIT-1; i > 1; i-- ) {
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
            private JPanel historyPane;
            private JPanel inputPane;

            private Map<String, JRadioButton> jrbMap = new HashMap<String, JRadioButton>();
            private String selectedWorkOrder;

            WorkOrderPanel() {
                initUI();
            }

            String getSelectedWorkOrder() {
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

                buttonGroup = new ButtonGroup();
                inputNewWorkOrder = new JRadioButton( inputNew );
                inputNewWorkOrder.setSelected( true );
                inputNewWorkOrder.addActionListener( this );
                buttonGroup.add( inputNewWorkOrder );

                historyPane = new JPanel();
                Border titledBd = BorderFactory.createTitledBorder( recentInput );
                historyPane.setBorder( titledBd );
                historyPane.setLayout( new GridBagLayout() );
                historyPane.add( emptyLabel, new GBC(0, 0) );

                inputPane = new JPanel();
                inputPane.setLayout( new GridBagLayout() );
                inputPane.add( inputNewWorkOrder, new GBC(0, 0).setInsets( 10 ) );
                inputPane.add( workOrderLabel, new GBC(0, 1).setInsets( 10) );
                inputPane.add( workOrderField, new GBC(1, 1).setInsets( 10 ) );

                add( historyPane, BorderLayout.NORTH );
                add( inputPane, BorderLayout.SOUTH );
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

            private void resetLayout(JRadioButton[] jrbs) {
                historyPane.removeAll();
                for ( int i = 0; i < jrbs.length; i++ ) {
                    historyPane.add( jrbs[i], new GBC(0, i).setInsets( 5, 10, 5, 10 ) );
                }
                historyPane.revalidate();
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

        public TestConfigDialog(JFrame fatherFrame, Map<String, DeviceStatus> dsMap) {
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
            add(serverListPane, new GBC(0, 0));
            add( portListPane, new GBC(0, 1) );
            add( secondsPane, new GBC(0, 2) );
            add( buttonPane, new GBC(0, 3) );
        }

        private void setDialogTitle() {
            String dialogTitle = LocaleUtil.getLocalName( TEST_OPTION );
            setTitle( dialogTitle );
        }

        private void initServerListPane() {
            serverListPane = new ServerListPane( getIpList() );
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
            String srcPort = LocaleUtil.getLocalName( SRC_PORT );
            String dstPort = LocaleUtil.getLocalName( DST_PORT );
            Border srcPortBorder = BorderFactory.createTitledBorder( srcPort );
            Border dstPortBorder = BorderFactory.createTitledBorder( dstPort );
            srcPortPane.setBorder( srcPortBorder );
            dstPortPane.setBorder( dstPortBorder );

            portListPane = new JPanel();
            portListPane.add( srcPortPane, BorderLayout.WEST );
            portListPane.add( dstPortPane, BorderLayout.EAST );
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
                MsgDialogUtil.showMsgDialog( msg );
                return true;
            }
            if ( dstPortList.getSelectedIndex() < 0 ) {
                String msg = LocaleUtil.getLocalName( SELECT_DST_PORT );
                MsgDialogUtil.showMsgDialog( msg );
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
                MsgDialogUtil.showMsgDialog( msg );
                return true;
            }
            return false;
        }

        public void display(ITesterRecord itr) {
            this.itr = itr;
            serverListPane.update( getIpList() );
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

        class ServerListPane extends JPanel implements ActionListener {

            private static final long serialVersionUID = 6724875493333867111L;

            private ButtonGroup buttonGroup;
            private Map<String, JRadioButton> jrbMap = new HashMap<String, JRadioButton>();
            private String selectedServerIP;

            ServerListPane(String[] ipList) {
                this.buttonGroup = new ButtonGroup();
                setLayout( new GridBagLayout() );
                update( ipList );
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
                removeAll();
                for( int i = 0; i < jrbs.length; i++ ) {
                   add( jrbs[i], new GBC(0, i).setInsets(5, 10, 5, 10) );
                }
            }

        }

    }

    abstract class IDialog extends JDialog implements ActionListener {

        private static final String CANCEL = "cancel";

        private final JFrame fatherFrame;

        protected JDialog dialog = this;
        protected JButton cancelBt;

        public IDialog(JFrame fatherFrame) {
            this.fatherFrame = fatherFrame;
            initUI();
        }

        private void initUI() {
            String cancel = LocaleUtil.getLocalName(CANCEL);
            cancelBt = new JButton( cancel );
            cancelBt.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    close();
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
            dialog.setResizable( false );
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
