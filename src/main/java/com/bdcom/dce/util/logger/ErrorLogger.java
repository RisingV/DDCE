package com.bdcom.dce.util.logger;

import com.bdcom.dce.sys.gui.Application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2012-11-1 <br>
 * Auto-Generated by eclipse Juno <br>
 */

public abstract class ErrorLogger {

    private static final String _SPT = File.separator;
	
	private static final String CURRENT_DIR = Application.CURRENT_DIR;
	
	private static final DateFormat DATE_FORMAT = Application.DATE_FORMAT;
	
	public static final DateFormat DATA_FILE_DATE_FORMAT = 
							new SimpleDateFormat("yyyy-MM-dd"); 
	
	private static final String LOG_DIR = _SPT+"log"+_SPT+"err"+_SPT;
	
	private static final String DATA_DIR = _SPT+"log"+_SPT+"data"+_SPT;
	
	private static final String _END_FIX = ".log.txt";
	
	private static String LOG_FILE_NAME;
	
	private static String DATA_FILE_NAME;
	
	private static File logFile;
	
	private static File dataFile;
	
	private static boolean logCreateSuccess;
	
	private static boolean dataFileCreateSuccess;
	
	static {
		LOG_FILE_NAME = DATE_FORMAT.format( new Date() );
		DATA_FILE_NAME =DATA_FILE_DATE_FORMAT.format(new Date());
		logFile = new File(getPathToSave(LOG_DIR) + LOG_FILE_NAME + _END_FIX);
		if (!logFile.exists()) {
			try {
				if ( logFile.createNewFile() ) {
					logCreateSuccess = true;
				} else {
					logCreateSuccess = false;
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		} else {
			logCreateSuccess = true;
		}
		dataFile= new File(getPathToSave(DATA_DIR) + DATA_FILE_NAME + _END_FIX);
		if (!dataFile.exists()) {
			try {
				if ( dataFile.createNewFile() ) {
					dataFileCreateSuccess= true;
				} else {
					dataFileCreateSuccess= false;
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		} else {
			dataFileCreateSuccess= true;
		}
	}
	
	public static void recordData(String msg) {
		if (!dataFileCreateSuccess) {
			return;
		}
		FileWriter fw;
		try {
			fw = new FileWriter(dataFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("[ " + DATE_FORMAT.format(new Date()) + " logged: ] " +
					msg);
			bw.newLine();
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e) {
		}
	}
	
	public static void log(String msg) {
		if (!logCreateSuccess) {
			return;
		}
//		
//		PrintWriter out = null;
//		try {
//			out = new PrintWriter(logFile);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		out.println( "[ " + DATE_FORMAT.format(new Date()) + " logged: ] " + msg
//				);
		FileWriter fw;
		try {
			fw = new FileWriter(logFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("[ " + DATE_FORMAT.format(new Date()) + " logged: ] " +
					msg);
			bw.newLine();
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e) {
		}
	}
	
	private static String getPathToSave(String dir) {
		StringBuffer sb = new StringBuffer();
		sb.append(CURRENT_DIR)
		  .append(dir);
		File sceDir = new File(sb.toString());
		if ( !sceDir.exists() ) {
			sceDir.mkdirs();
		}
		
		return sb.toString();
	}
}
