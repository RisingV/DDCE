package com.bdcom.dataparser;

import java.util.ArrayList;
import java.util.List;

import com.bdcom.util.log.ErrorLogger;
import org.jargp.ArgumentProcessor;
import org.jargp.BoolDef;
import org.jargp.ParameterDef;
import org.jargp.StringDef;
import org.jargp.StringTracker;

import com.bdcom.pojo.BaseTestRecord;
import com.bdcom.service.AppSession;
import com.bdcom.util.StringUtil;

public class StrArrayParser implements DataParser {
	
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
		new StringDef('#', "randomID", "random ID"),
		new BoolDef('?', "helpFlag", "display usage information"),
		new BoolDef('$', "ifCommit", "if commit this test record")
	};
	
	private ArgumentProcessor proc;
	
	private List<String> stringContainer;
	
	String[] rawStrings;
	
	public StrArrayParser() {
		proc = new ArgumentProcessor(BASE_PARM_DEFS);
		stringContainer = new ArrayList<String>();
	}
	
	public void setRawStrings(String[] rawStrings) {
		this.rawStrings = rawStrings;
	}
	
	public BaseTestRecord parse() {
		
		if ( null == rawStrings || rawStrings.length == 0) {
			return null;
		}
		
		BaseTestRecord record = new BaseTestRecord();
		String[] simulatedArgs = simulateCmdArgs(rawStrings);
		
		proc.processArgs(simulatedArgs, record);
		StringTracker xargs = proc.getArgs();
		while (xargs.hasNext()) {
			ErrorLogger.log("extra argument: " + xargs.next());
		}
		wrap(record);
		return record;
	}
	
	private String[] simulateCmdArgs(String[] rawStrings) {
		String[] simulatedArgs = null;
		for (String rawStr : rawStrings ) {
			String[] partOfAllArgs = rawString2Args(rawStr);
			if ( null != partOfAllArgs ) {
				for ( String arg : partOfAllArgs) {
					if ( null != arg && StringUtil.isNotBlank(arg) ) {
						stringContainer.add(arg.trim());
					}
				}
			}
		}
		
		simulatedArgs = new String[stringContainer.size()];
		stringContainer.toArray( simulatedArgs );
		stringContainer.clear();
		
		return simulatedArgs; 
	}
	
	private String[] rawString2Args(String rawString) {
		if ( !StringUtil.isNotBlank(rawString) ) {
			return null;
		}
		List<String> argList = new ArrayList<String>();
		String[] strs = StringUtil.split(rawString);
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
