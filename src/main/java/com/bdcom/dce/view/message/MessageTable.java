package com.bdcom.dce.view.message;

import com.bdcom.dce.sys.ApplicationConstants;
import com.bdcom.dce.util.LocaleUtil;
import com.bdcom.dce.util.logger.MsgLogger;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-12    <br/>
 * Time: 15:26  <br/>
 */
public class MessageTable extends JFrame {

    private static final long serialVersionUID = -6864643191208370242L;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Image image;
    private MessageTableModel model;
    private JTable table;
    private JScrollPane scrollPane;

    public MessageTable() {
        initUI();
    }

    private void initUI() {
        model = new MessageTableModel();
        table = new JTable( model );
        scrollPane = new JScrollPane( table );
        scrollPane.setPreferredSize( new Dimension( 400, 500 ) );

        Container con = getContentPane();
        con.add( scrollPane, BorderLayout.CENTER );
    }

    public void addMessage(String type, String msg) {
        String time = DATE_FORMAT.format( new Date() );
        model.addRow( type, msg, time );
        StringBuilder sb = new StringBuilder();
        sb.append( type )
          .append(" ")
          .append( msg )
          .append(" ")
          .append( time );

        MsgLogger.log( sb.toString() );
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void display() {
        if ( null != image ) {
            this.setIconImage(image);
        }
        this.pack();
        this.setVisible(true);
    }

    public void close() {
        this.setVisible(false);
    }

    static final class MessageTableModel extends AbstractTableModel
            implements ApplicationConstants {

        private static final long serialVersionUID = 5393179649072177826L;

        private Vector<Vector<String>> content;

        private String[] titleNames = {
                LocaleUtil.getLocalName(_MSG_TYPE),
                LocaleUtil.getLocalName(_MSG),
                LocaleUtil.getLocalName(_TIME)
        };

        public MessageTableModel() {
            content = new Vector<Vector<String>>();
        }

        public void addRow(String msgType, String msg, String time) {
            Vector<String> v = new Vector<String>(3);
            v.add(0, msgType);
            v.add(1, msg);
            v.add(2, time);
            content.add(v);

            int rowIndex = content.indexOf( v );
            fireTableRowsDeleted( rowIndex, rowIndex );
        }

        public void removeAll() {
            content.removeAllElements();
        }

        public void removeRow(int row) {
            content.remove(row);
        }

        public void removeRows(int row, int count) {
            for (int i = 0; i < count; i++) {
                if (content.size() > row) {
                    content.remove(row);
                }
            }
        }

        public String getColumnName(int col) {
            return titleNames[col];
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        public void setValueAt(Object value, int row, int col) {
            ((Vector<String>) content.get(row)).remove(col);
            ((Vector<String>) content.get(row)).add(col, (String)value);
            this.fireTableCellUpdated(row, col);
        }

        @Override
        public int getRowCount() {
            return content.size();
        }

        @Override
        public int getColumnCount() {
            return titleNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return ((Vector<String>) content.get(rowIndex)).get(columnIndex);
        }

    }
}
