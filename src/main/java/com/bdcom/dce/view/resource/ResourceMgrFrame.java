package com.bdcom.dce.view.resource;

import com.bdcom.dce.biz.pojo.Scenario;
import com.bdcom.dce.biz.pojo.Script;
import com.bdcom.dce.biz.storage.Item;
import com.bdcom.dce.biz.storage.StorableMgr;
import com.bdcom.dce.nio.client.ClientProxy;
import com.bdcom.dce.nio.exception.GlobalException;
import com.bdcom.dce.sys.AppContent;
import com.bdcom.dce.sys.ApplicationConstants;
import com.bdcom.dce.util.LocaleUtil;
import com.bdcom.dce.util.StringUtil;
import com.bdcom.dce.view.ViewTab;
import com.bdcom.dce.view.util.GBC;
import com.bdcom.dce.view.util.MsgDialogUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
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
 * Date: 13-9-1    <br/>
 * Time: 10:51  <br/>
 */

public class ResourceMgrFrame extends JPanel implements ViewTab, ApplicationConstants {

    private static final long serialVersionUID = 5870259224282336031L;
    private static final String REFRESH = "Refresh";
    private static final String DETAIL = "Detail";
    private static final String MODIFY = "Modify";
    private static final String DELETE = "Delete";
    private static final String ADD_SCENARIO = "Add Scenario";
    private static final String ADD_SCRIPT = "Add Script";
    private static final String UPLOAD = "Upload";
    private static final String DOWNLOAD = "Download";

    private static final String SCENARIO_NAME_IS_BLANK = "scenario name is blank!";
    private static final String NO_ATTR_IN_SCENARIO = "no attribute added in scenario!";
    private static final String SCRIPT_NAME_IS_BLANK = "script name is blank!";
    private static final String SERIAL_IS_BLANK = "serial is blank!";
    private static final String SERIAL_DUPLICATED = "serial duplicated!";
    private static final String BEGIN_INDEX_BELOW_ZERO = "begin index is below zero!";
    private static final String SCRIPT_PATH_IS_BLANK = "script path is blank!";
    private static final String SCRIPT_FILE_NOT_EXIST = "script file is not exist!";
    private static final String CANT_FIND_FILE = "can't find file:";
    private static final String FINISH_UPLOADING = "Finish Uploading!";
    private static final String FINISH_DOWNLOADING = "Finish Downloading!";

    private JTable table;
    private ResourceTableModel model;
    private JButton addScenarioBt;
    private JButton addScriptBt;
    private JButton modifyBt;
    private JButton deleteBt;
    private JButton refreshBt;
    private JButton detailBt;
    private JButton uploadBt;
    private JButton downloadBt;

    private JScrollPane tablePane;
    private JPanel buttonPane;

    private ScriptDetailDialog scriptDetailDialog;
    private ScenarioDetailDialog scenarioDetailDialog;

    private ScenarioEditDialog addScenarioDialog;
    private ScenarioEditDialog modifyScenarioDialog;
    private ScriptEditDialog addScriptDialog;
    private ScriptEditDialog modifyScriptDialog;
    private ModifyAction modifyScenarioAction;
    private ModifyAction modifyScriptAction;

    private final AppContent app;
    private StorableMgr storableMgr;

    public ResourceMgrFrame(AppContent app) {
        this.app = app;
        initUI();
    }

    private void initUI() {
        initTable();
        initDialogs();
        initButtons();
        setLayout( new GridBagLayout() );
        add( tablePane, new GBC(0, 0) );
        add(buttonPane, new GBC(0, 1));
    }

    private void initTable() {
        model = new ResourceTableModel();
        table = new JTable( model );
        table.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRowCount = table.getSelectedRowCount();
                detailBt.setEnabled( 1 == selectedRowCount );
                modifyBt.setEnabled( 1 == selectedRowCount );
                deleteBt.setEnabled( selectedRowCount > 0 );
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

        addScenarioDialog = new ScenarioEditDialog( this );
        modifyScenarioDialog = new ScenarioEditDialog( this );

        addScriptDialog = new ScriptEditDialog( this );
        modifyScriptDialog = new ScriptEditDialog( this );

        ActionListener addScenarioAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Scenario scenario = addScenarioDialog.gatherAsScenario();
                if ( scenarioValidation( scenario, addScenarioDialog ) ) {
                    addScenarioDialog.close();
                    StorableMgr mgr = getStorableMgr( app );
                    mgr.addItem( scenario );
                    model.addRow( scenario );
                }
            }
        };
        modifyScenarioAction = new ModifyAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StorableMgr mgr = getStorableMgr( app );
                Scenario scenario = (Scenario) getItemToModify();
                mgr.removeItem( scenario );
                Scenario gathered = modifyScenarioDialog.gatherAsScenario();
                if ( scenarioValidation( gathered, modifyScenarioDialog ) ) {
                    scenario = modifyScenarioDialog.gatherAsScenario( scenario );
                    modifyScenarioDialog.close();
                }
                mgr.addItem( scenario );
                int rowIndex = table.getSelectedRow();
                model.updateRow( scenario, rowIndex );
            }
        };
        ActionListener addScriptAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Script script = addScriptDialog.gatherAsScript();
                if ( scriptValidation( script, addScriptDialog ) ) {
                    addScriptDialog.close();
                    StorableMgr mgr = getStorableMgr( app );
                    mgr.addItem( script );
                    model.addRow( script );
                }
            }
        };
        modifyScriptAction = new ModifyAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StorableMgr mgr = getStorableMgr( app );
                Script script = (Script) getItemToModify();
                mgr.removeItem( script );
                Script gathered = modifyScriptDialog.gatherAsScript();
                if( scriptValidation( gathered, modifyScriptDialog ) ) {
                    script = modifyScriptDialog.gatherAsScript( script );
                    modifyScriptDialog.close();
                }
                mgr.addItem( script );
                int rowIndex = table.getSelectedRow();
                model.updateRow( script, rowIndex );
            }
        };

        addScenarioDialog.setConfirmAction( addScenarioAction );
        modifyScenarioDialog.setConfirmAction( modifyScenarioAction );

        addScriptDialog.setConfirmAction( addScriptAction );
        modifyScriptDialog.setConfirmAction( modifyScriptAction );
    }

    private boolean scenarioValidation(Scenario scenario, JDialog dialog) {
        if ( null == scenario ) {
            return false;
        }

        if ( !StringUtil.isNotBlank( scenario.getRemarkName() ) ) {
            String msg = LocaleUtil.getLocalName( SCENARIO_NAME_IS_BLANK );
            MsgDialogUtil.showMsgDialog( dialog, msg );
            return false;
        }
        if ( !StringUtil.isNotBlank( scenario.getSerial() ) ) {
            String msg = LocaleUtil.getLocalName( SERIAL_IS_BLANK );
            MsgDialogUtil.showMsgDialog( dialog, msg );
            return false;
        }
        if ( scenario.getBeginIndex() < 0 ) {
            String msg = LocaleUtil.getLocalName(BEGIN_INDEX_BELOW_ZERO);
            MsgDialogUtil.showMsgDialog( dialog, msg );
            return false;
        }

        StorableMgr mgr = getStorableMgr( app );
        if ( null != mgr.getByFullSerial( scenario.getSerial() ) ) {
            String msg = LocaleUtil.getLocalName( SERIAL_DUPLICATED );
            MsgDialogUtil.showMsgDialog( dialog, msg );
            return false;
        }

        Set<String> attributes = scenario.getAttrNames();
        if ( null == attributes || attributes.isEmpty() ) {
            String msg = LocaleUtil.getLocalName( NO_ATTR_IN_SCENARIO );
            MsgDialogUtil.showMsgDialog( dialog, msg );
            return false;
        }

        return true;
    }

    private boolean scriptValidation(Script script, JDialog dialog) {
        if ( null == script ) {
            return false;
        }

        if ( !StringUtil.isNotBlank(script.getRemarkName()) ) {
            String msg = LocaleUtil.getLocalName( SCRIPT_NAME_IS_BLANK );
            MsgDialogUtil.showMsgDialog( dialog, msg );
            return false;
        }
        if ( !StringUtil.isNotBlank(script.getSerial()) ) {
            String msg = LocaleUtil.getLocalName( SERIAL_IS_BLANK );
            MsgDialogUtil.showMsgDialog( dialog, msg );
            return false;
        }
        if ( script.getBeginIndex() < 0 ) {
            String msg = LocaleUtil.getLocalName( BEGIN_INDEX_BELOW_ZERO );
            MsgDialogUtil.showMsgDialog( dialog, msg );
            return false;
        }

        StorableMgr mgr = getStorableMgr( app );
        if ( null != mgr.getByFullSerial(script.getSerial()) ) {
            String msg = LocaleUtil.getLocalName( SERIAL_DUPLICATED );
            MsgDialogUtil.showMsgDialog( dialog, msg );
            return false;
        }

        String path = script.getPath();
        if ( !StringUtil.isNotBlank( path ) ) {
            String msg = LocaleUtil.getLocalName( SCRIPT_PATH_IS_BLANK );
            MsgDialogUtil.showMsgDialog( dialog, msg );
            return false;
        }

        File scriptFile = new File( path );
        if ( !scriptFile.exists() ) {
            String msg = LocaleUtil.getLocalName( CANT_FIND_FILE );
            MsgDialogUtil.showMsgDialog( dialog, msg + path );
            return false;
        }

        String secondPath = script.getSecondPath();
        if ( StringUtil.isNotBlank( secondPath ) ) {
            File f = new File( secondPath );
            if ( !f.exists() ) {
                String msg = LocaleUtil.getLocalName( CANT_FIND_FILE );
                MsgDialogUtil.showMsgDialog( dialog, msg + secondPath );
                return false;
            }
        }

        return true;
    }

    private void initButtons() {
        String refresh = LocaleUtil.getLocalName( REFRESH );
        String detail = LocaleUtil.getLocalName( DETAIL );
        String modify = LocaleUtil.getLocalName( MODIFY );
        String delete = LocaleUtil.getLocalName( DELETE );
        String addScenario = LocaleUtil.getLocalName( ADD_SCENARIO );
        String addScript = LocaleUtil.getLocalName( ADD_SCRIPT );
        String upload = LocaleUtil.getLocalName( UPLOAD );
        String download = LocaleUtil.getLocalName( DOWNLOAD );

        refreshBt = new JButton( refresh );
        detailBt = new JButton( detail );
        modifyBt = new JButton( modify );
        deleteBt = new JButton( delete );
        addScenarioBt = new JButton( addScenario );
        addScriptBt = new JButton( addScript );
        uploadBt = new JButton( upload );
        downloadBt = new JButton( download);

        refreshBt.setPreferredSize( new Dimension(80, 25) );
        detailBt.setPreferredSize( new Dimension(80, 25) );
        modifyBt.setPreferredSize( new Dimension(80, 25) );
        deleteBt.setPreferredSize( new Dimension(80, 25) );
        addScenarioBt.setPreferredSize( new Dimension(100, 25) );
        addScriptBt.setPreferredSize( new Dimension(80, 25) );
        uploadBt.setPreferredSize(new Dimension(80, 25));
        downloadBt.setPreferredSize(new Dimension(80, 25));

        detailBt.setEnabled( false );
        modifyBt.setEnabled(false);
        deleteBt.setEnabled(false);

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
                    Item row = model.getRow( index );
                    if ( row instanceof Scenario ) {
                        scenarioDetailDialog.display( (Scenario) row );
                    } else if ( row instanceof Script ) {
                        scriptDetailDialog.display( (Script) row );
                    }
                }
            }
        });

        modifyBt.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowCount = table.getSelectedRowCount();
                if ( 1 == selectedRowCount ) {
                    int rowIndex = table.getSelectedRow();
                    Item i = model.getRow( rowIndex );
                    if ( i instanceof Scenario ) {
                        showScenarioModifyDialog( (Scenario) i );
                    } else if ( i instanceof Script ) {
                        showScriptModifyDialog( (Script) i );
                    }
                }
            }
        });


        addScenarioBt.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addScenarioDialog.clear();
                addScenarioDialog.display();
            }
        });

        addScriptBt.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addScriptDialog.clear();
                addScriptDialog.display();
            }
        });

        deleteBt.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final int selectedRowCount = table.getSelectedRowCount();
                final int first = table.getSelectedRow();

                Item[] items = new Item[selectedRowCount];
                for ( int i = first; i -first < selectedRowCount; i++ ) {
                    items[ i - first ] = model.getRow( i );
                }

                StorableMgr mgr = getStorableMgr( app );
                for ( Item item: items ) {
                    if ( null != item ) {
                        mgr.removeItem( item );
                        model.removeRow( item );
                    }
                }

            }
        });

        uploadBt.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StorableMgr mgr = getStorableMgr( app );
                ClientProxy client = getClientProxy( app );

                try {
                    uploadBt.setEnabled( false );
                    if ( !mgr.isStorageLoaded() ) {
                        mgr.loadStorage();
                    }
                    client.uploadResource( mgr );
                } catch (IOException ex) {
                    //TODO
                } catch (GlobalException ex) {
                    MsgDialogUtil.reportGlobalException( ex );
                } finally {
                    uploadBt.setEnabled( true );
                    String msg = LocaleUtil.getLocalName( FINISH_UPLOADING );
                    MsgDialogUtil.showMsgDialog( msg );
                }
            }
        });

        downloadBt.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StorableMgr mgr = getStorableMgr( app );
                ClientProxy client = getClientProxy( app );

                try {
                    downloadBt.setEnabled( false );
                    client.downloadResource( mgr );
                } catch (IOException ex) {
                    //TODO
                } catch (GlobalException ex) {
                    MsgDialogUtil.reportGlobalException( ex );
                } finally {
                    if ( !mgr.isStorageLoaded() ) {
                        mgr.loadStorage();
                    }
                    mgr.saveToLocalStorage();

                    downloadBt.setEnabled( true );
                    String msg = LocaleUtil.getLocalName( FINISH_DOWNLOADING );
                    MsgDialogUtil.showMsgDialog( msg );
                }
            }
        });

        buttonPane = new JPanel();
        buttonPane.setLayout( new GridBagLayout() );
        buttonPane.add(refreshBt, new GBC(0, 0).setInsets(8));
        buttonPane.add(detailBt, new GBC(1, 0).setInsets(8));
        buttonPane.add(addScenarioBt, new GBC(2, 0).setInsets(8) );
        buttonPane.add(addScriptBt, new GBC(3, 0).setInsets(8) );
        buttonPane.add(modifyBt, new GBC(4, 0).setInsets(8) );
        buttonPane.add(deleteBt, new GBC(5, 0).setInsets(8) );
        buttonPane.add(uploadBt, new GBC(6, 0).setInsets(8) );
        buttonPane.add(downloadBt, new GBC(7, 0).setInsets(8) );
    }

    private void showScenarioModifyDialog(Scenario scenario) {
        modifyScenarioAction.setItemToModify(scenario);
        modifyScenarioDialog.update(scenario);
        modifyScenarioDialog.display();
    }

    private void showScriptModifyDialog(Script script) {
        modifyScriptAction.setItemToModify( script );
        modifyScriptDialog.update(script);
        modifyScriptDialog.display();
    }

    private void clear() {
        model.removeAll();
    }

    private void refresh() {
        clear();
        loadAllItems(model);
    }

    private void loadAllItems(ResourceTableModel model) {
        StorableMgr mgr = getStorableMgr( app );
        if ( null != mgr ) {
            if ( !mgr.isStorageLoaded() ) {
                mgr.loadStorage();
            }
            Item[] items = mgr.getAll();
            for ( Item i : items ) {
                model.addRow( i );
            }
        }
    }

    private StorableMgr getStorableMgr(AppContent app) {
        if ( null == storableMgr ) {
            storableMgr = (StorableMgr)
                    app.getAttribute( COMPONENT.STORABLE_MGR);
        }
        return storableMgr;
    }

    private ClientProxy getClientProxy(AppContent app) {
        return (ClientProxy) app.getAttribute( COMPONENT.NIO_CLIENT );
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

    abstract class ModifyAction implements ActionListener {

        private Item itemToModify;

        Item getItemToModify() {
            return itemToModify;
        }

        void setItemToModify(Item itemToModify) {
            this.itemToModify = itemToModify;
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

        private List<Item> rows = new ArrayList<Item>();

        public Item getRow(int index) {
            Item row = null;
            if ( index < getRowCount() ) {
                row = rows.get( index );
            }
            return row;
        }

        public void addRow(Item i) {
            if ( null != i ) {
                rows.add( i );
                int rowIndex = rows.indexOf( i );
                fireTableRowsInserted( rowIndex, rowIndex );
            }
        }

        public void updateRow(Item i, int rowIndex) {
            if ( null != i &&
                    0 <= rowIndex && rowIndex < getRowCount() ) {
                rows.set( rowIndex, i );
                fireTableDataChanged();
            }
        }

        public void removeRow(Item r) {
            int rowIndex = rows.indexOf( r );
            removeRow( rowIndex );
        }

        public void removeRow(int rowIndex) {
            if ( 0 <= rowIndex && rowIndex < getRowCount() ) {
                rows.remove( rowIndex );
                fireTableRowsDeleted( rowIndex, rowIndex );
            }
        }

        public void removeAll() {
            int rowCount = getRowCount();
            if ( rowCount > 0 ) {
                rows.clear();
                fireTableRowsDeleted( 0, rowCount - 1 );
            }
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
            Item i = rows.get( rowIndex );
            switch ( columnIndex ) {
                case 0: {
                    if ( i instanceof Scenario ) {
                        return LocaleUtil.getLocalName( SCENARIO );
                    } else if ( i instanceof Script) {
                        return LocaleUtil.getLocalName( SCRIPT );
                    }
                    break;
                }
                case 1: {
                    return i.getSerial();
                }
                case 2: {
                    return i.getBeginIndex();
                }
                case 3: {
                    return i.getFormattedCreateDate();
                }
                case 4: {
                    return i.getFormattedModifyDate();
                }
            }
            return "";
        }

    }

    class ScriptDetailDialog extends DetailDialog {

        private static final long serialVersionUID = 4989622146279192593L;

        private static final String SCRIPT_PATH = "First Script Path";
        private static final String SECOND_SCRIPT_PATH = "Second Script Path";
        private static final String DATE_CREATE = "Create Date";
        private static final String DATE_MODIFY = "Modify Date";
        private static final String BASIC_INFO = "Basic Info";
        private static final String CONTENT = "Content";

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

        private JLabel pathLabel;
        private JLabel secondPathLabel;
        private JTextField pathField;
        private JTextField secondPathField;

        private JPanel basicPane;
        private JPanel contentPane;
        private JPanel buttonPane;

        public ScriptDetailDialog(JComponent fatherFrame) {
            super(fatherFrame);
            initUI();
        }

        private void initUI() {
            initBasicPane();
            initContentPane();

            buttonPane = new JPanel();
            buttonPane.add( confirmBt, BorderLayout.CENTER );
            setLayout( new GridBagLayout()  );
            add( basicPane, new GBC(0, 0) );
            add( contentPane, new GBC(0, 1) );
            add( buttonPane, new GBC(0, 2) );
        }

        public void display(Script script) {
            update( script );
            super.display();
        }

        private void initContentPane() {
            String path = LocaleUtil.getLocalName( SCRIPT_PATH );
            String secondPath = LocaleUtil.getLocalName( SECOND_SCRIPT_PATH );
            String content = LocaleUtil.getLocalName( CONTENT );

            pathLabel = new JLabel( path );
            secondPathLabel = new JLabel( secondPath );
            pathLabel.setPreferredSize(new Dimension(120, 20));
            secondPathLabel.setPreferredSize(new Dimension(120, 20));

            pathField = new JTextField();
            secondPathField = new JTextField();
            pathField.setEditable(false);
            secondPathField.setEditable(false);
            pathField.setPreferredSize(new Dimension(380, 20));
            secondPathField.setPreferredSize(new Dimension(380,20));

            Border titledBorder = BorderFactory.createTitledBorder( content );
            contentPane = new JPanel();
            contentPane.setBorder( titledBorder );
            contentPane.setLayout(new GridBagLayout());
            contentPane.setPreferredSize( new Dimension( 555, 100 ) );

            contentPane.add(pathLabel, new GBC(0, 0) );
            contentPane.add(pathField, new GBC(1, 0) );
            contentPane.add(secondPathLabel, new GBC(0, 1) );
            contentPane.add(secondPathField, new GBC(1, 1) );
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

        private void updateBasicPane(Script script) {
            if ( null == script ) {
                return;
            }
            String name = script.getRemarkName();
            String serial = script.getSerial();
            String beginIndex = String.valueOf( script.getBeginIndex() );
            String createDate = script.getFormattedCreateDate();
            String modifyDate = script.getFormattedModifyDate();

            resetTextField( nameField, name );
            resetTextField( serialField, serial );
            resetTextField( beginIndexField, beginIndex );
            resetTextField( createDateField, createDate );
            resetTextField( modifyDateField, modifyDate );
        }

        public void update(Script script) {
            updateBasicPane( script );
            resetTextField( pathField, script.getPath() );
            resetTextField( secondPathField, script.getSecondPath() );
        }

    }

    class ScenarioDetailDialog extends DetailDialog {

        private static final long serialVersionUID = -8191433373460432589L;
        private static final String DATE_CREATE = "Create Date";
        private static final String DATE_MODIFY = "Modify Date";
        private static final String BASIC_INFO = "Basic Info";
        private static final String CONTENT = "Content";

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

        private JPanel basicPane;
        private JPanel contentPane;
        private JPanel buttonPane;

        public ScenarioDetailDialog(JComponent fatherFrame) {
            super(fatherFrame);
            initUI();
        }

        private void initUI() {
            initBasicPane();

            contentPane = new JPanel();
            buttonPane = new JPanel();
            contentPane.setLayout( new GridBagLayout() );
            buttonPane.add( confirmBt, BorderLayout.CENTER );

            String content = LocaleUtil.getLocalName( CONTENT );
            Border titledBorder = BorderFactory.createTitledBorder( content );
            contentPane.setBorder( titledBorder );

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
            updateBasicPane( scenario );
            updateContentPane(labels, fields);
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
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    if (null != fatherFrame) {
                        fatherFrame.setEnabled(true);
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
            dialog.setVisible(true);
        }

        public void close() {
            if ( null != fatherFrame ) {
                fatherFrame.setEnabled( true );
            }
            dialog.setVisible( false );
        }

        protected void resetTextField( JTextField jtf, String text ) {
            if ( null != text && null != jtf ) {
                if ( !text.equals( jtf.getText() ) ) {
                    jtf.setText( text );
                }
            }
        }

        protected JLabel newLabel(String name) {
            JLabel label = new JLabel( name );
            label.setPreferredSize( new Dimension( 80, 20 ) );
            return label;
        }

        protected JTextField newField() {
            JTextField field = new JTextField();
            field.setPreferredSize( new Dimension( 160, 20 ) );
            field.setEditable( false );
            return field;
        }

    }

}
