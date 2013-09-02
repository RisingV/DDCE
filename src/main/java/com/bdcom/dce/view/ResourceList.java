package com.bdcom.dce.view;

import com.bdcom.dce.biz.pojo.Scenario;
import com.bdcom.dce.biz.scenario.ScenarioMgr;
import com.bdcom.dce.biz.script.ScriptMgr;
import com.bdcom.dce.sys.AppContent;
import com.bdcom.dce.sys.ApplicationConstants;
import com.bdcom.dce.util.LocaleUtil;
import com.bdcom.dce.util.StringUtil;
import com.bdcom.dce.view.util.GBC;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-1    <br/>
 * Time: 10:51  <br/>
 */

public class ResourceList  extends JPanel implements ViewTab, ApplicationConstants {

    private static final long serialVersionUID = 5870259224282336031L;
    private static final String REFRESH = "Refresh";
    private static final String DETAIL = "Detail";

    private JTable table;
    private ResourceTableModel model;
    private JButton refreshBt;
    private JButton detailBt;

    private JScrollPane tablePane;
    private JPanel buttonPane;

    private ScriptDetailDialog scriptDetailDialog;
    private ScenarioDetailDialog scenarioDetailDialog;

    private ScriptMgr scriptMgr;
    private ScenarioMgr scenarioMgr;

    private final AppContent app;

    public ResourceList(AppContent app) {
        this.app = app;
        initUI();
    }

    private void initUI() {
        initTable();
        initDialogs();
        initButtons();
        setLayout( new GridBagLayout() );
        add( tablePane, new GBC(0, 0) );
        add( buttonPane, new GBC(0, 1) );
    }

    private void initTable() {
        model = new ResourceTableModel();
        table = new JTable( model );
        table.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRowCount = table.getSelectedRowCount();
                detailBt.setEnabled( 1 == selectedRowCount );
            }
        } );

        TableColumnModel tcm = table.getColumnModel();
        tcm.getColumn(0).setPreferredWidth(50);
        tcm.getColumn(1).setPreferredWidth(200);
        tcm.getColumn(2).setPreferredWidth(100);
        tcm.getColumn(3).setPreferredWidth(200);
        tcm.getColumn(4).setPreferredWidth(200);

        tablePane = new JScrollPane( table );
        tablePane.setPreferredSize( new Dimension( 800, 600 ) );
    }

    private void initDialogs() {
        scriptDetailDialog = new ScriptDetailDialog( this );
        scenarioDetailDialog = new ScenarioDetailDialog( this );
    }

    private void initButtons() {
        String refresh = LocaleUtil.getLocalName( REFRESH );
        String detail = LocaleUtil.getLocalName( DETAIL );
        refreshBt = new JButton( refresh );
        detailBt = new JButton( detail );
        refreshBt.setPreferredSize( new Dimension( 80, 25 ) );
        detailBt.setPreferredSize(new Dimension(80, 25));

        refreshBt.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });

        detailBt.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int count = table.getSelectedRowCount();
                if ( 1 == count ) {
                    int index = table.getSelectedRow();
                    ResourceRow row = model.getRow( index );
                    if ( ResourceRow.SCENARIO == row.getType()) {
                        Scenario scenario = scenarioMgr
                                .getScenarioByFullSerial(row.getSerial());
                        scenarioDetailDialog.display( scenario );
                    } else if ( ResourceRow.SCRIPT == row.getType() ) {
                        String path = scriptMgr.getScriptPath( row.getSerial() );
                        scriptDetailDialog.display( path );
                    }
                }
            }
        });

        buttonPane = new JPanel();
        buttonPane.setLayout( new GridBagLayout() );
        buttonPane.add(refreshBt, new GBC(0, 0).setInsets(10));
        buttonPane.add(detailBt, new GBC(1, 0).setInsets(10));
    }

    private void clear() {
        model.removeAll();
        model.fireTableDataChanged();
    }

    private void refresh() {
        clear();
        loadScripts( model );
        loadScenarios( model );
        model.fireTableDataChanged();
    }

    private void loadScripts(ResourceTableModel model) {
        ScriptMgr scriptMgr = getScriptMgr( app );
        Set<String> set = scriptMgr.indexingSerials("");

        List<String> list = new ArrayList<String>();
        if ( null != set && !set.isEmpty()) {
            for (String s : set) {
                list.add(s);
            }
            Collections.sort(list, Collections.reverseOrder());
        }
        if ( !list.isEmpty() ) {
            for (String num : list) {
                String index = scriptMgr.getBeginIndex(num);
                if ( StringUtil.isNotBlank(index) ) {
                    ResourceRow row =
                            new ResourceRow( ResourceRow.SCRIPT, num,
                                    Integer.parseInt( index ), "", "" );
                    model.addRow( row );
                }
            }
        }
    }

    private void loadScenarios(ResourceTableModel model) {
        ScenarioMgr scenarioMgr = getScenarioMgr( app );
        Set<String> names = scenarioMgr.getScenarioNameList();

        List<String> list = new ArrayList<String>();
        if ( null != names && !names.isEmpty() ) {
            for (String s : names) {
                list.add(s);
            }
            Collections.sort(list, Collections.reverseOrder());
        }
        if ( !list.isEmpty() ) {
            for ( String name: list ) {
                Scenario sce = scenarioMgr.getScenarioByName( name );
                if ( null != sce ) {
                    String serial = sce.getSerialNum();
                    ResourceRow row =
                            new ResourceRow( ResourceRow.SCENARIO, serial,
                                    sce.getBeginIndex(), sce.getFormattedCreateDate(),
                                    sce.getFormattedModifyDate() );
                    model.addRow( row );
                }
            }
        }
    }

    private ScenarioMgr getScenarioMgr(AppContent app) {
        if ( null == scenarioMgr ) {
            scenarioMgr = (ScenarioMgr)
                    app.getAttribute( COMPONENT.SCENARIO_MGR );
        }
        return scenarioMgr;
    }

    private ScriptMgr getScriptMgr(AppContent app) {
        if ( null == scriptMgr ) {
            scriptMgr = (ScriptMgr)
                    app.getAttribute( COMPONENT.SCRIPT_MGR );
        }
        return scriptMgr;
    }

    private String tabTitle;
    private Icon tabIcon;
    private String tabTip;

    @Override
    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
    }

    @Override
    public String getTabTitle() {
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
        return this;
    }

    @Override
    public void setTabTip(String tabTip) {
        this.tabTip = tabTip;
    }

    @Override
    public String getTabTip() {
        return tabTip;
    }

    class ResourceRow {

        static final int SCENARIO = 0xA;
        static final int SCRIPT = 0xB;

        ResourceRow(int type, String serial, int beginIndex,
                String dateCreate, String dateModify) {
            this.type = type;
            this.serial = serial;
            this.beginIndex = beginIndex;
            this.dateCreate = dateCreate;
            this.dateModify = dateModify;
        }

        private int type;
        private String serial;
        private int beginIndex;
        private String dateCreate;
        private String dateModify;

        int getType() {
            return type;
        }

        void setType(int type) {
            this.type = type;
        }

        String getSerial() {
            return serial;
        }

        void setSerial(String serial) {
            this.serial = serial;
        }

        int getBeginIndex() {
            return beginIndex;
        }

        void setBeginIndex(int beginIndex) {
            this.beginIndex = beginIndex;
        }

        String getDateCreate() {
            return dateCreate;
        }

        void setDateCreate(String dateCreate) {
            this.dateCreate = dateCreate;
        }

        String getDateModify() {
            return dateModify;
        }

        void setDateModify(String dateModify) {
            this.dateModify = dateModify;
        }

    }

    class ResourceTableModel extends AbstractTableModel implements ApplicationConstants {

        private static final long serialVersionUID = 596505455635765823L;
        private static final String RESOURCE_TYPE = "Resource Type";
        private static final String SERIAL_NUM = "Serial Number";
        private static final String BEGIN_INDEX = "Begin Index";
        private static final String DATE_CREATE = "Create Date";
        private static final String DATE_MODIFY = "Modify Date";

        private static final String SCENARIO = "Scenario";
        private static final String SCRIPT = "Script";

        private String[] titles = new String[] {
                LocaleUtil.getLocalName( RESOURCE_TYPE ),
                LocaleUtil.getLocalName( SERIAL_NUM ),
                LocaleUtil.getLocalName( BEGIN_INDEX ),
                LocaleUtil.getLocalName( DATE_CREATE ),
                LocaleUtil.getLocalName( DATE_MODIFY )
        };

        private List<ResourceRow> rows = new ArrayList<ResourceRow>();

        public ResourceRow getRow(int index) {
            ResourceRow row = null;
            if ( index < getRowCount() ) {
                row = rows.get( index );
            }
            return row;
        }

        public void addRow(ResourceRow r) {
            rows.add(r);
        }

        public void removeRow(ResourceRow r) {
            rows.remove( r );
        }

        public void removeRow(int i) {
            rows.remove( i );
        }

        public void removeAll() {
            rows.clear();
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return titles.length;
        }

        @Override
        public String getColumnName(int column) {
            return titles[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ResourceRow rr = rows.get( rowIndex );
            switch ( columnIndex ) {
                case 0: {
                    if ( ResourceRow.SCENARIO == rr.getType() ) {
                        return LocaleUtil.getLocalName( SCENARIO );
                    } else if ( ResourceRow.SCRIPT == rr.getType()) {
                        return LocaleUtil.getLocalName( SCRIPT );
                    }
                    break;
                }
                case 1: {
                    return rr.getSerial();
                }
                case 2: {
                    return rr.getBeginIndex();
                }
                case 3: {
                    return rr.getDateCreate();
                }
                case 4: {
                    return rr.getDateModify();
                }
            }
            return "";
        }

    }

    class ScriptDetailDialog extends DetailDialog {

        private static final long serialVersionUID = 4989622146279192593L;

        private static final String SCRIPT_PATH = "Script Path";

        private JLabel label;
        private JTextField field;
        private JPanel contentPane;
        private JPanel buttonPane;

        public ScriptDetailDialog(JComponent fatherFrame) {
            super(fatherFrame);
            initUI();
        }

        private void initUI() {
            String path = LocaleUtil.getLocalName( SCRIPT_PATH );
            label = new JLabel( path );
            label.setPreferredSize( new Dimension( 80, 20 ));
            field = new JTextField();
            field.setEditable( false );
            field.setPreferredSize( new Dimension( 320, 20 ));

            contentPane = new JPanel();
            contentPane.setLayout( new GridBagLayout() );
            contentPane.add( label, new GBC(0, 0) );
            contentPane.add( field, new GBC(1, 0) );

            buttonPane = new JPanel();
            buttonPane.add( confirmBt, BorderLayout.CENTER );

            setLayout( new GridBagLayout() );
            add( contentPane, new GBC(0, 0) );
            add( buttonPane, new GBC(0, 1) );
        }

        public void display(String path) {
            update( path );
            super.display();
        }

        public void update(String path) {
            field.setText( path );
            field.revalidate();
        }

    }

    class ScenarioDetailDialog extends DetailDialog {

        private static final long serialVersionUID = -8191433373460432589L;

        private Map<String, JLabel> labelCache = new HashMap<String, JLabel>();
        private Map<String, JTextField> fieldCache = new HashMap<String, JTextField>();

        private JPanel contentPane;
        private JPanel buttonPane;

        public ScenarioDetailDialog(JComponent fatherFrame) {
            super(fatherFrame);
            initUI();
        }

        private void initUI() {
            contentPane = new JPanel();
            buttonPane = new JPanel();
            contentPane.setLayout( new GridBagLayout() );

            buttonPane.add( confirmBt, BorderLayout.CENTER );

            setLayout( new GridBagLayout() );
            add( contentPane, new GBC(0, 0) );
            add( buttonPane, new GBC(0, 1) );
        }

        public void display(Scenario scenario) {
            update( scenario );
            super.display();
        }

        public void update(Scenario scenario) {
            if ( null == scenario ) {
                return;
            }
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
            update(labels, fields);
        }

        private void update(JLabel[] labels, JTextField[] fields) {
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

    }

    abstract class DetailDialog extends JDialog implements ActionListener {

        private static final String CONFIRM = "confirm";

        private final JComponent fatherFrame;

        protected JDialog dialog = this;
        protected JButton confirmBt;

        public DetailDialog(JComponent fatherFrame) {
            this.fatherFrame = fatherFrame;
            initUI();
        }

        private void initUI() {
            String confirm = LocaleUtil.getLocalName(CONFIRM);
            confirmBt = new JButton( confirm );
            confirmBt.setPreferredSize(new Dimension(80, 25));
            confirmBt.addActionListener(new ActionListener() {
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
