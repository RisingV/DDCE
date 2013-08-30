package com.bdcom.dce.view;

import com.bdcom.dce.view.util.MessageUtil;
import com.bdcom.dce.sys.Applicable;
import com.bdcom.dce.sys.ApplicationConstants;
import com.bdcom.dce.biz.script.ScriptExecutor;
import com.bdcom.dce.util.LocaleUtil;
import com.bdcom.dce.util.logger.ErrorLogger;
import com.bdcom.dce.util.logger.MsgLogger;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import static com.bdcom.dce.datadispacher.CommunicateStatus.NO_RUNNING_SCRIPT;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2012-12-10 <br>
 * Auto-Generated by eclipse Juno <br>
 */

public class MsgTable extends JFrame implements AbstractFrame, ApplicationConstants {
	
	private static final long serialVersionUID = 3620791577756543685L;
	
	public static final DateFormat DATE_FORMAT = 
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	
	private Image image;
	
	private JTable table;
	
	private MsgTableModel model;
	
	private JScrollPane span;
	
	private Thread listener;

    private final Applicable app;
	
	public MsgTable(Applicable app) {
        this.app = app;
		init();
	}
	
	public void setImage(Image image) {
		this.image = image;
	}

	private void init() {
		this.setTitle(
				LocaleUtil.getLocalName(MSG_LIST)
				);
		Container con = this.getContentPane();
		
		model = new MsgTableModel();
		table = new JTable(model);
		table.setBackground(Color.white);
		
		TableColumnModel tcm = table.getColumnModel();
		tcm.getColumn(0).setPreferredWidth(30);
		tcm.getColumn(1).setPreferredWidth(140);
		tcm.getColumn(2).setPreferredWidth(50);

		span = new JScrollPane(table);
        span.setPreferredSize(new Dimension(240, 500));
		
		con.add(span, BorderLayout.CENTER);
		this.pack();
	}
	
	public void addSysMsg(String msg) {
		Date time = new Date();
		String msgType = LocaleUtil.getLocalName(_SYS);
		addMsg(msgType, msg, time);
		
		StringBuffer sb = new StringBuffer();

        String testerNum = app.getStringAttr(USER.USER_NUM);
		sb.append(testerNum)
		  .append(_TAB)
		  .append(msgType)
		  .append(_TAB)
		  .append(msg)
		  .append(_TAB)
		  .append(DATE_FORMAT.format(time));
		  
		MsgLogger.log(sb.toString());
	}
	
	public void addErrMsg(String msg) {
		Date time = new Date();
		String msgType = LocaleUtil.getLocalName(_ERROR);
		addMsg(msgType, msg, time);

        String testerNum = app.getStringAttr(USER.USER_NUM);
		StringBuffer sb = new StringBuffer();
		sb.append(testerNum)
		  .append(_TAB)
		  .append(msgType)
		  .append(_TAB)
		  .append(msg)
		  .append(_TAB)
		  .append(DATE_FORMAT.format(time));
		  
		MsgLogger.log(sb.toString());
	}
	
	public void addMsg(int status) {
		String msg = null;
		String msgType = null;
		if ( NO_RUNNING_SCRIPT == status ) {
			return;
		}
		msg = MessageUtil.getMessageByStatusCode(status);
		if ( status > 0 ) {
			msgType = LocaleUtil.getLocalName(_MESSAGE);
		} else {
			msgType = LocaleUtil.getLocalName(_ERROR);
		}
		Date time = new Date();
		addMsg(msgType, msg, time);

        String testerNum = app.getStringAttr(USER.USER_NUM);
		StringBuffer sb = new StringBuffer();
		sb.append(testerNum)
		  .append(_TAB)
		  .append(msgType)
		  .append(_TAB)
		  .append(msg)
		  .append(_TAB)
		  .append(DATE_FORMAT.format(time));
		  
		MsgLogger.log(sb.toString());
	}
	
	public void startListener() {
		listener = new Thread(
					new Runnable() {
						@Override
						public void run() {
							int status = 0;
							String msg = null;
							String msgType = null;
							int timecounter = 0;
							while ( true ) {
                                ScriptExecutor scriptExecutor = (ScriptExecutor)
                                        app.getAttribute( COMPONENT.SCRIPT_EXECUTOR );
								status = scriptExecutor.getSendResult();
								if ( NO_RUNNING_SCRIPT == status ) {
									try {
										TimeUnit.SECONDS.sleep(1);
									} catch (InterruptedException e) {
										ErrorLogger.log(e.getMessage());
									}
									timecounter++;
									System.out.println("MsgTable listening thread: wait for "
										+ timecounter + " s");
									continue;
								}
								msg = MessageUtil.getMessageByStatusCode(status);
								if ( status > 0 ) {
									msgType = LocaleUtil.getLocalName(_MESSAGE);
								} else {
									msgType = LocaleUtil.getLocalName(_ERROR);
								}
								addMsg(msgType, msg, new Date());
							}
						}
					}
				);
		listener.setDaemon(true);
		listener.start();
	}
	
	@SuppressWarnings("deprecation")
	public void stopListener() {
		if ( null == listener ) {
			return;
		}
		listener.stop();
	}
	
	public void display() {
		if ( null != image ) {
			this.setIconImage(image);
		}
		this.setVisible(true);
	}
	
	public void hideFrame() {
		this.setVisible(false);
	}

    @Override
    public Component getSelfFrame() {
        return this;
    }

    @Override
    public void refresh() {
        //nothing
    }

    public void addMsg(String msgType, String msg, Date time) {
		if ( model.getRowCount() > 30 ) {
			model.removeAll();
		}
		model.addRow(msgType, msg, DATE_FORMAT.format(time));
		table.updateUI();
	}
	
	public void clearAllMsg() {
		model.removeAll();
		table.updateUI();
	}

	class MsgTableModel extends AbstractTableModel implements ApplicationConstants {
		
		private static final long serialVersionUID = 5393179649072177826L;

		private Vector<Vector<String>> content;
		
		private String[] titleNames = {
				LocaleUtil.getLocalName(_MSG_TYPE),
				LocaleUtil.getLocalName(_MSG),
				LocaleUtil.getLocalName(_TIME)
		};
		
		public MsgTableModel() {
			content = new Vector<Vector<String>>();
		}
		
		public void addRow(String msgType, String msg, String time) {
			  Vector<String> v = new Vector<String>(3);
			  v.add(0, msgType);
			  v.add(1, msg);
			  v.add(2, time);
			  content.add(v);
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
