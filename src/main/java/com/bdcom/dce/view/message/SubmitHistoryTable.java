package com.bdcom.dce.view.message;

import com.bdcom.dce.biz.pojo.BaseTestRecord;
import com.bdcom.dce.sys.Applicable;
import com.bdcom.dce.sys.ApplicationConstants;
import com.bdcom.dce.util.LocaleUtil;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-12    <br/>
 * Time: 14:44  <br/>
 */
public class SubmitHistoryTable extends JFrame {

    private static final long serialVersionUID = -3740971442947304967L;

	private Image image;
	private JTable table;
	private TableModel model;
	private JScrollPane span;
    private final Applicable app;

    public SubmitHistoryTable(Applicable app)  {
        this.app = app;
        initUI();
    }

    private void initUI() {
		model = new TableModel();
		table = new JTable(model);
		table.setBackground(Color.white);

		span = new JScrollPane(table);
		span.setPreferredSize( new Dimension(1200, 800) );
		TableColumnModel tcm = table.getColumnModel();
		for (int i=0; i < model.getColumnCount(); i ++) {
			tcm.getColumn(i).setPreferredWidth(60);
		}

//		JSeparator sepH0 = new JSeparator(SwingConstants.HORIZONTAL);
//		sepH0.setPreferredSize( new Dimension(810,2) );
//		sepH0.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        Container con = this.getContentPane();
		con.add(span, BorderLayout.CENTER);

    }

    public void setImage(Image image) {
        this.image = image;
    }


    public void display() {
        if ( null != image ) {
            setIconImage( image );
        }
        pack();
        setVisible( true );
    }

    public void close() {
        setVisible( false );
    }

    public void addBaseTestRecord(BaseTestRecord record) {
        model.addRow( record );
    }

    static final class TableModel extends AbstractTableModel implements ApplicationConstants {

        private static final long serialVersionUID = 7730896620697100336L;
        private static final DateFormat DATE_FORMAT =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        private static final String TIME = "Time";

        private Vector<Vector<String>> content;

        private String[] titleNames = {
                LocaleUtil.getLocalName("serialNumber"),
                LocaleUtil.getLocalName("testerNum"),
                LocaleUtil.getLocalName("type"),
                LocaleUtil.getLocalName("script"),
                LocaleUtil.getLocalName("consoleName"),
                LocaleUtil.getLocalName("id"),
                LocaleUtil.getLocalName("beginTime"),
                LocaleUtil.getLocalName("endTime"),
                LocaleUtil.getLocalName("verOfEPROM"),
                LocaleUtil.getLocalName("volOfFlash"),
                LocaleUtil.getLocalName("volOfDRam"),
                LocaleUtil.getLocalName("softwareInfo"),
                LocaleUtil.getLocalName("hardwareInfo"),
                LocaleUtil.getLocalName("modelType"),
                LocaleUtil.getLocalName("mac"),
                LocaleUtil.getLocalName("step"),
                LocaleUtil.getLocalName("memo"),
                LocaleUtil.getLocalName("randomID"),
                LocaleUtil.getLocalName( TIME ),
                LocaleUtil.getLocalName("status")
        };

        public TableModel() {
            content = new Vector<Vector<String>>();
        }

        public void addRow(BaseTestRecord dr) {
            if ( null == dr ) {
                return;
            }
            Vector<String> v = new Vector<String>();
            v.add(0, dr.getSerialNumber());
            v.add(1, dr.getTesterNum());
            v.add(2, dr.getType());
            v.add(3, dr.getScript());
            v.add(4, dr.getConsoleName());
            v.add(5, dr.getId());
            v.add(6, dr.getBeginTime());
            v.add(7, dr.getEndTime());
            v.add(8, dr.getVerOfEPROM());
            v.add(9, dr.getVolOfFlash());
            v.add(10, dr.getVolOfDRam());
            v.add(11, dr.getSoftwareInfo());
            v.add(12, dr.getHardwareInfo());
            v.add(13, dr.getModelType());
            v.add(14, dr.getMac());
            v.add(15, dr.getStep());
            v.add(16, dr.getMemo());
            v.add(17, dr.getRandomID());
            v.add(18, DATE_FORMAT.format( new Date() ));
            v.add(19, dr.getStatus());
            content.add(v);

            int rowIndex = content.indexOf( v );
            fireTableRowsInserted( rowIndex, rowIndex );
        }

        public void removeAll() {
            int size = content.size();
            content.removeAllElements();
            fireTableRowsDeleted( 0, size-1 );
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
