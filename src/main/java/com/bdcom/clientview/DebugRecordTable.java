package com.bdcom.clientview;

import com.bdcom.clientview.util.MsgDialogUtil;
import com.bdcom.datadispacher.http.HttpClientWrapper;
import com.bdcom.pojo.BaseTestRecordForDebug;
import com.bdcom.service.Application;
import com.bdcom.service.ApplicationConstants;
import com.bdcom.util.LocaleUtil;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.Vector;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2013-3-18 <br>
 * Auto-Generated by eclipse Juno <br>
 */

public class DebugRecordTable extends JFrame implements ApplicationConstants {

	private static final long serialVersionUID = -100242882200232794L;
	
//	private JFrame thisFrame = this;
	
	private Image image;

	private JTable table;
	
	private TableModel model;
	
	private JScrollPane span;
	
	private JButton refBt;
	
	private JButton delBt;
	
	private HttpClientWrapper httpClient;
	
	public DebugRecordTable() {
		model = new TableModel();
		table = new JTable(model);
		table.setBackground(Color.white);
		
		span = new JScrollPane(table);
		span.setPreferredSize( new Dimension(1200, 800) );
		TableColumnModel tcm = table.getColumnModel();
		for (int i=0; i < model.getColumnCount(); i ++) {
			tcm.getColumn(i).setPreferredWidth(60);
		}
		
		Container con = this.getContentPane();
		JSeparator sepH0 = new JSeparator(SwingConstants.HORIZONTAL);
		sepH0.setPreferredSize( new Dimension(810,2) );
		sepH0.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		con.add(span, BorderLayout.CENTER);
		
//		con.setLayout(new GridBagLayout());
//		con.add(span, new GBC(0,0));
//		con.add(sepH0,new GBC(0,1).setInsets(5));
		
		initButtons();
		JPanel btPane = new JPanel();
		con.add(btPane, BorderLayout.SOUTH);
		btPane.add(refBt, BorderLayout.WEST);
		btPane.add(delBt, BorderLayout.WEST);
//		btPane.setLayout(new GridBagLayout());
//		this.add(btPane,new GBC(0,2).setInsets(5, 640, 5, 50));
		
//		btPane.add(refBt,new GBC(0,0).setInsets(5, 620, 5, 20));
//		btPane.add(delBt,new GBC(1,0).setInsets(5, 20, 5, 50));
	}
	
	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	private void initButtons() {
		if ( null == refBt ) {
			refBt = new JButton(LocaleUtil.getLocalName(REFRESH_BT));
			refBt.addActionListener( new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					model.removeAll();
					table.updateUI();

                    @SuppressWarnings("unchecked")
                    Set<String> serialNumSet =
                            (Set<String>) Application.getAttribute(CONTENT.SERIAL_NUM_SET);
                    if ( null == serialNumSet || serialNumSet.isEmpty() ) {
						MsgDialogUtil.showMsgDialogLocalised(null, NO_DBG_DATA);
						return;	
					} else {
                        for ( String serialNum : serialNumSet ) {
                            BaseTestRecordForDebug dr = getRecord(serialNum);
                            addRow(dr);
                        }
                    }
				}
			});
		}
		if ( null == delBt ) {
			final JTable jtable = table;
			delBt = new JButton(LocaleUtil.getLocalName(DEL_BT));
			delBt.addActionListener( new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					int[] rowIndexes = jtable.getSelectedRows();
					int length = rowIndexes.length;
					if ( length <= 0 ) {
						MsgDialogUtil.showMsgDialogLocalised(null, SELECT_ROW_TO_DEL);
						return;
					}
					
					Vector[] rowVec = new Vector[length];
					for (int i=0; i < length; i++ ) {
						rowVec[i] = model.removeRow(rowIndexes[i]);
					}
					table.updateUI();
					for (int j=0; j < length; j++ ) {
						if ( null != rowVec ) {
							String serialNum = (String) rowVec[j].get(0);
							String randomId = (String) rowVec[j].get(17);
							if ( !delRecord(serialNum, randomId) ) {
								StringBuffer msg = new StringBuffer();
								
								msg.append(LocaleUtil.getLocalName(SERIAL_NUM))
								   .append(": ").append(serialNum).append(" ")
								   .append(LocaleUtil.getLocalName(DELETE_FAIL));
								
								MsgDialogUtil.showErrorDialog(null, msg.toString());
							} else {
                                @SuppressWarnings("unchecked")
                                Set<String> serialNumSet =
                                        (Set<String>) Application.getAttribute(CONTENT.SERIAL_NUM_SET);
								if ( null == serialNumSet || serialNumSet.isEmpty() ) {
									return;	
								} else {
                                    serialNumSet.remove(serialNum);
                                }
							}
						}
					}
					MsgDialogUtil.showMsgDialogLocalised(null, DELETE_DONE);
				}
			});
		}
	}
	
	private BaseTestRecordForDebug getRecord(String serialNum) {
		if ( null == httpClient ) {
			httpClient = HttpClientWrapper.getInstance();
		}
		return httpClient.getRecordViaHttp(serialNum);
	}
	
	private boolean delRecord(String serialNum, String randomId) {
		if ( null == httpClient ) {
			httpClient = HttpClientWrapper.getInstance();
		}
		return httpClient.delRecordViaHttp(serialNum, randomId);
	}
	
	public void addRow(BaseTestRecordForDebug dr) {
		if ( null == dr ) {
			return;
		}
		model.addRow(dr);
		table.updateUI();
	}
	
	public void display() {
		if ( null != image ) {
			this.setIconImage(image);
		}
		this.setTitle(LocaleUtil.getLocalName(DEBUG_DR_LIST));
		this.pack();
		this.setVisible(true);
	}
	
	public void hideFrame() {
		this.setVisible(false);
	}

	class TableModel extends AbstractTableModel implements ApplicationConstants {

		private static final long serialVersionUID = 7730896620697100336L;

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
				LocaleUtil.getLocalName("workTime"),
				LocaleUtil.getLocalName("status")
		};
		
		public TableModel() {
			content = new Vector<Vector<String>>();
		}
		
		public void addRow(BaseTestRecordForDebug dr) {
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
			v.add(18, dr.getWorkTime());
			v.add(19, dr.getStatus());
			content.add(v);
		}
		
		public void removeAll() {
			content.removeAllElements();
		}
		
		public Vector<String> removeRow(int row) {
			  return content.remove(row);
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
