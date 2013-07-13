package com.bdcom.dataparser;

import com.bdcom.util.log.ErrorLogger;
import com.bdcom.pojo.BaseTestRecord;
import com.bdcom.service.AppSession;
import com.bdcom.service.script.FileRawDataFetcher;
import com.bdcom.util.StringUtil;
import org.jargp.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2012-11-21 <br>
 * Auto-Generated by eclipse Juno <br>
 */

public class FileParser implements DataParser {
	
	private static final ParameterDef[] BASE_PARM_DEFS = {
		new StringDef('p', "testerNum", "number of the tester"),
		new StringDef('t', "type", "test type"),
		new StringDef('b', "beginTime", "begin time of test"),
		new StringDef('s', "script", "name of test script"),
		new StringDef('c', "consoleName", "name of console"),
		new StringDef('n', "serialNumber", "serial number"),
		new StringDef('i', "id", "ID"),
		new StringDef('e', "verOfEPROM", "version of EPROM"),
		new StringDef('f', "volOfFlash", "volume of flash"),
		new StringDef('r', "volOfDRam", "volume of SDRam"),
		new StringDef('v', "softwareInfo", "information of software"),
		new StringDef('h', "hardwareInfo", "information of hardware"),
		new StringDef('m', "modelType", "model type"),
		new StringDef('d', "endTime", "end time of test"),
		new StringDef('a', "status", "test status"),
		new BoolDef('?', "helpFlag", "display usage information"),
		new BoolDef('$', "ifCommit", "if commit this test record")
	};
	
	private ArgumentProcessor proc;

	private FileRawDataFetcher fileRawDataFetcher;
	
	private List<String> stringContainer;
	
	public FileParser() {
		proc = new ArgumentProcessor(BASE_PARM_DEFS);
		fileRawDataFetcher = new FileRawDataFetcher();
		stringContainer = new ArrayList<String>();
	}
	
	public BaseTestRecord parse() {
		BaseTestRecord record = new BaseTestRecord();
		String[] simulatedArgs = simulateCmdArgs();
		
		proc.processArgs(simulatedArgs, record);
		StringTracker xargs = proc.getArgs();
		while (xargs.hasNext()) {
			ErrorLogger.log("extra argument: " + xargs.next());
		}
		wrap(record);
		return record;
	}
	
	private String[] simulateCmdArgs() {
		File[] rawDataFiles = fileRawDataFetcher.fetch();
		String[] simulatedArgs = null;
		if ( null != rawDataFiles ) {
			BufferedReader reader = null;
			for ( File raw : rawDataFiles ) {
				if ( null != raw ) {
					try {
						String tmpStr = null;
						reader = new BufferedReader(new FileReader(raw));
						while ( (tmpStr = reader.readLine() ) != null ) {
							String[] partOfAllArgs = rawString2Args(tmpStr);
							if ( null != partOfAllArgs ) {
								for ( String arg : partOfAllArgs) {
									if ( null != arg && StringUtil.isNotBlank(arg) ) {
										stringContainer.add(arg.trim());
									}
								}
							}
						}
					} catch (IOException e) {
						ErrorLogger.log(e.getMessage());
					} finally {
						if ( null != reader ) {
							try {
								reader.close();
							} catch (IOException e) {
								ErrorLogger.log(e.getMessage());
							}
						}
					}
				}
			}
			
			simulatedArgs = new String[stringContainer.size()];
			stringContainer.toArray( simulatedArgs );
			stringContainer.clear();
		} 
		return simulatedArgs; 
	}
	
	private String[] rawString2Args(String rowString) {
		List<String> argList = new ArrayList<String>();
		String[] strs = StringUtil.split(rowString);
		StringBuffer sb = new StringBuffer();
		for ( String str : strs ) {
			if ( str.startsWith("-") && 2 == str.length()) {
				argList.add(str);
			} else if ( str.startsWith("\"") && 
					str.endsWith("\"")) {
				argList.add(
						str.substring(1, str.length() - 1)
								);
			} else {
				if ( str.endsWith("\"") ) {
					sb.append(str);
					argList.add( sb.substring(1, sb.length() - 1 ) );
					sb.delete( 0, sb.length() );
				} else {
					sb.append(str).append(" ");
				}
			}
		}
		String[] ret = new String[argList.size()];
		argList.toArray(ret);
		return ret;
	}
	
	private BaseTestRecord wrap(BaseTestRecord dr) {
		dr.setTesterNum(
				AppSession.getTestNum()
				);
		return dr;
	}
	
}
