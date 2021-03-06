package com.bdcom.dce.datadispacher;

import com.bdcom.dce.sys.AppSession;
import com.bdcom.dce.sys.ConfigPath;
import com.bdcom.dce.util.CommuniConstants;
import com.bdcom.dce.util.CommunicateStatus;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2012-12-17 <br>
 * Auto-Generated by eclipse Juno <br>
 */

public class ScriptSender implements
        CommunicateStatus, CommuniConstants {
	
	private static final DateFormat DATE_FORMAT = AppSession.DATE_FORMAT;
	
	private static final String CONFIG_DIR_SCRIPT = "Script\\";
	
	private static final String END_FIX = ".xml";
	
	private InetAddress ip;
	
	private int port;
	
	public ScriptSender(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	private Socket newConnection(int offset) {
		Socket server = null;
		try {
			server = new Socket(ip,port + offset);
		} catch (IOException e) {
		}
		return server;
	}
	
	public void uploadScript() {
		File[] files = getScriptConfFiles();
		if ( null != files && files.length > 0) {
			Socket server = newConnection(0);
			DataOutputStream ps = null;
			DataInputStream dis = null;
			int bufferSize = 8192;
            byte[] buf = new byte[bufferSize];
			try {
				dis = new DataInputStream(new BufferedInputStream(server.getInputStream()));
				ps = new DataOutputStream(server.getOutputStream());
				ps.writeInt(files.length);
			} catch (IOException e) {
			}
			for ( File f : files ) {
				if ( null != f ) {
					DataInputStream fis = null;
					try {
						fis = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
	                	dis.readLong();
		                ps.writeLong(f.length());
		                ps.flush();
					} catch (FileNotFoundException e) {
					} catch (IOException e) {
					}
	                
					long totallen = 0;
                	try {
		                while (true) {
		                	int read = 0;
		                	if (fis != null) {
		                        try {
									read = fis.read(buf);
								} catch (IOException e) {
								}
		                    } else {
		                    	break;
		                    }
		                	totallen += read;
		                	if ( read == -1 ) {
		                		break;
		                	}
							ps.write(buf, 0, read);
							ps.flush();
		                	if ( totallen >= f.length() ) {
		                		break;
		                	}
							dis.readLong();
		                }
						fis.close();
					} catch (IOException e) {
					}
				}
			}
	        try {
	        	if ( null != ps) {
		        	ps.close();
	        	}
	        	if ( null != dis ) {
		        	dis.close();
	        	}
	        	if ( null != server ) {
					server.close();
	        	}
			} catch (IOException e) {
			} 
		}
	}
	
	public void downldScripts() {
		deleteSavedScriptConfFiles();
		Socket server = newConnection(1);
		 
        DataInputStream dis = null;
        DataOutputStream fileOut = null;
		DataOutputStream ps = null;
		int bufferSize = 8192;
        byte[] buf = new byte[bufferSize];
        long totallen = 0;
        long passedlen = 0;
        int fNum = -1;
		try {
			dis = new DataInputStream(new BufferedInputStream(server.getInputStream()));
			ps = new DataOutputStream(server.getOutputStream());
			fNum = dis.readInt();
//			System.out.println("fNum: " + fNum);
		} catch (IOException e) {
		}
		for (int i=0; i < fNum; i++) {
			
			File saveFile = new File( getConfigFileDir(CONFIG_DIR_SCRIPT) + 
					DATE_FORMAT.format(new Date()) + "d" + i + END_FIX );
			
//			System.out.println("i: "+i+" saveFile path: " + saveFile.getPath());
			
	        passedlen = 0;
			try {
				ps.writeLong(passedlen);
				ps.flush();
		        totallen = dis.readLong(); 
//				System.out.println("i: " + i + " totallen: " + totallen);
				fileOut = new DataOutputStream(
						   		new BufferedOutputStream(
						   				new BufferedOutputStream(
						   						new FileOutputStream(saveFile))));
				while (true) {
					int read = 0;
					if ( dis != null ) {
						read = dis.read(buf);
//						System.out.println("i: " + i + " read: " + read);
					} else {
						break;
					}
					passedlen += read;
					if ( read == -1 ) {
						break;
					}
					fileOut.write(buf, 0, read);
					fileOut.flush();
					
					if (passedlen >= totallen) {
						break;
					}
					ps.writeLong(passedlen);
					ps.flush();
				}
				fileOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		if ( dis != null) {
			try {
				dis.close();
			} catch (IOException e) {
			}
		}
		if ( null != server ) {
			try {
				server.close();
			} catch (IOException e) {
			}
		}
	}
	
	private String getConfigFileDir(String dir) {
		StringBuffer sb = new StringBuffer();
		sb.append(ConfigPath.getConfDir())
		  .append(dir);
		File sceDir = new File(sb.toString());
		if ( !sceDir.exists() ) {
			sceDir.mkdirs();
		}
		
		return sb.toString();
	}
	
	private void deleteSavedScriptConfFiles() {
		File[] files = getScriptConfFiles();
		if ( null != files && files.length > 0 ) {
			for (File f : files ) {
				if ( null!=f && f.exists() ) {
					f.delete();
				}
			}
		}
	}
	
	private File[] getScriptConfFiles() {
		File dir = new File(ConfigPath.getConfDir() 
				+ CONFIG_DIR_SCRIPT);
		if ( dir.isDirectory() ) {
			File[] savedFiles = dir.listFiles(
					new FilenameFilter() {
						public boolean accept(File dir, String name) {
							if ( name.endsWith( END_FIX ) ) {
								return true;
							} else {
								return false;
							}
						}
					}
					);
			return savedFiles;
		} else {
			return null;
		}
	}
}
