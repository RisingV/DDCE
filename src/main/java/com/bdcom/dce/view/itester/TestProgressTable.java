package com.bdcom.dce.view.itester;

import com.bdcom.dce.util.LocaleUtil;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-8-28    <br/>
 * Time: 14:17  <br/>
 */

public class TestProgressTable extends JTable {

    public static TestProgressTable newInstance() {
        return new TestProgressTable();
    }

    private final ProgressTableModel tableModel;

    private TestProgressTable() {
        tableModel = new ProgressTableModel( this );
        setModel( tableModel );
        initUI();
    }

    public void addRow(TestProgressRow row) {
        if ( null != row ) {
            tableModel.addRow( row );
            tableModel.fireTableDataChanged();
        //    updateUI();
        }
    }

    public void removeRow(TestProgressRow row) {
        if ( null != row ) {
            tableModel.removeRow(row);
            tableModel.fireTableDataChanged();
//            if ( 0 == tableModel.getRowCount() ) {
//                updateUI();
//            } else {
//                SwingUtilities.invokeLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        updateUI();
//                    }
//                });
//            }
        }
    }

    public void removeRow(int rowIndex) {
        tableModel.removeRow( rowIndex );
        tableModel.fireTableDataChanged();
//            if ( 0 == tableModel.getRowCount() ) {
//                updateUI();
//            } else {
//                SwingUtilities.invokeLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        updateUI();
//                    }
//                });
//            }
    }

    private void initUI() {

        String progress = LocaleUtil.getLocalName( ProgressTableModel.PROGRESS );
        String status = LocaleUtil.getLocalName( ProgressTableModel.STATUS );
        String OPERATION = LocaleUtil.getLocalName( ProgressTableModel.OPERATION );

        TableColumn progressTc = getColumn( progress );
        TableColumn statusTc = getColumn( status );
        TableColumn operationTc = getColumn( OPERATION );

        setRowHeight( 28 );
        progressTc.setPreferredWidth( 220 );
        statusTc.setPreferredWidth( 30 );
        operationTc.setPreferredWidth( 250 );

        ProgressCell progressCell = new ProgressCell( tableModel );
        StatusLabelCell statusLabelCell = new StatusLabelCell( tableModel );
        ButtonPanelCell buttonPanelCell = new ButtonPanelCell( tableModel );

        progressTc.setCellRenderer( progressCell );
        statusTc.setCellRenderer( statusLabelCell );
        operationTc.setCellRenderer( buttonPanelCell );

        progressTc.setCellEditor( progressCell );
        statusTc.setCellEditor( statusLabelCell );
        operationTc.setCellEditor( buttonPanelCell );
    }

    private class ProgressCell extends AbstractCellEditor
            implements TableCellRenderer, TableCellEditor {

        private static final long serialVersionUID = 5146252751560386125L;

        private final ProgressTableModel tableModel;
        private JProgressBar progressBar;

        private ProgressCell(ProgressTableModel tableModel) {
            this.tableModel = tableModel;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                   boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex) {
            if ( null != tableModel ) {
                TestProgressRow row = tableModel.getRow(rowIndex);
                if ( null != row ) {
                    progressBar = row.getProgressBar();
                }
            }
            return progressBar;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                         boolean isSelected, int rowIndex, int columnIndex) {
            if ( null != tableModel ) {
                TestProgressRow row = tableModel.getRow(rowIndex);
                if ( null != row ) {
                    progressBar = row.getProgressBar();
                }
            }
            return progressBar;
        }

        @Override
        public Object getCellEditorValue() {
            return progressBar;
        }
    }

    private class StatusLabelCell extends AbstractCellEditor
            implements TableCellRenderer, TableCellEditor {

        private static final long serialVersionUID = -6292285208444411957L;

        private final ProgressTableModel tableModel;
        private JLabel label;

        private StatusLabelCell(ProgressTableModel tableModel) {
            this.tableModel = tableModel;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                       boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex) {
            if ( null != tableModel ) {
                TestProgressRow row = tableModel.getRow(rowIndex);
                if ( null != row ) {
                    label = row.getStatusLabel();
                }
            }
            return label;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                         boolean isSelected, int rowIndex, int columnIndex) {
            if ( null != tableModel ) {
                TestProgressRow row = tableModel.getRow(rowIndex);
                if ( null != row ) {
                    label = row.getStatusLabel();
                }
            }
            return label;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }
    }

    private class ButtonPanelCell extends AbstractCellEditor
            implements TableCellRenderer, TableCellEditor {

        private static final long serialVersionUID = 8049632214177210572L;

        private final ProgressTableModel tableModel;
        private JPanel pane;

        private ButtonPanelCell(ProgressTableModel tableModel) {
            this.tableModel = tableModel;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                       boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex) {
            if ( null != tableModel ) {
                TestProgressRow row = tableModel.getRow(rowIndex);
                if ( null != row ) {
                    pane = row.getButtonPanel();
                }
            }
            return pane;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                         boolean isSelected, int rowIndex, int columnIndex) {
            if ( null != tableModel ) {
                TestProgressRow row = tableModel.getRow(rowIndex);
                if ( null != row ) {
                    pane = row.getButtonPanel();
                }
            }
            return pane;
        }

        @Override
        public Object getCellEditorValue() {
            return pane;
        }
    }

    private static class ProgressTableModel extends AbstractTableModel {

        static final String PROGRESS = "Progress";
        static final String STATUS = "Status";
        static final String OPERATION = "Operation";

        private String[] titles = {
                LocaleUtil.getLocalName(PROGRESS),
                LocaleUtil.getLocalName( STATUS ),
                LocaleUtil.getLocalName( OPERATION )
        };

        private List<TestProgressRow> rowList;
        private final TestProgressTable tableRef;

        ProgressTableModel( TestProgressTable tableRef) {
            this.tableRef = tableRef;
            rowList = new ArrayList<TestProgressRow>();
        }

        TestProgressRow getRow(int rowIndex ) {
            if ( rowIndex < getRowCount() ) {
                return rowList.get( rowIndex );
            } else {
                return null;
            }
        }

        void addRow( TestProgressRow row ) {
            rowList.add( row );
            row.setUpdateAction( new UpdateAction( tableRef ) );
            row.setRemoveAction( new RemoveAction( tableRef, row ) );
        }

        void removeRow( TestProgressRow row ) {
            rowList.remove( row );
        }

        void removeRow( int rowIndex ) {
            if ( rowIndex < getRowCount() ) {
                rowList.remove( rowIndex );
            }
        }

        @Override
        public int getRowCount() {
            return rowList.size();
        }

        @Override
        public String getColumnName(int column) {
            return titles[column];
        }

        @Override
        public int getColumnCount() {
            return titles.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return null;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        class UpdateAction extends AbstractAction {
            final TestProgressTable tableRef;
            UpdateAction(TestProgressTable tableRef) {
                this.tableRef = tableRef;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater( new Runnable() {
                    @Override
                    public void run() {
                        tableRef.updateUI();
                    }
                });
            }
        }

        class RemoveAction implements ActionListener {
            final TestProgressTable tableRef;
            final TestProgressRow rowToRemove;
            RemoveAction(TestProgressTable tableRef, TestProgressRow rowToRemove) {
                this.tableRef = tableRef;
                this.rowToRemove = rowToRemove;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                tableRef.removeRow(rowToRemove);
            }
        }

    }

}
