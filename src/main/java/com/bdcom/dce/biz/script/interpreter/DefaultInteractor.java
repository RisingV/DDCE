package com.bdcom.dce.biz.script.interpreter;

import com.bdcom.dce.sys.gui.Application;
import com.bdcom.dce.sys.ApplicationConstants;
import com.bdcom.dce.biz.script.FileRawDataFetcher;
import com.bdcom.dce.biz.script.ScriptMgr;
import com.bdcom.dce.util.log.ErrorLogger;

import java.io.*;
import java.util.List;

import static com.bdcom.dce.biz.script.ScriptXmlConfConstants.*;

public class DefaultInteractor implements ScriptInteractor,ApplicationConstants {
	
	private String[] input;
	
	private String[] output;
	
	private List<String> outputArray;
	
	private String defaultSwitchFile = "switch.raw";
	
	public void setInput(String[] input) {
		setInput( input, getInteractType() );
	}
	
	public String[] getOutput() {
		return output;
	}
	
	private void setInput(String[] input, String interactType) {
		if ( null == input || input.length == 0 ) {
			return;
		}
		if ( _INPUT_ACTOR.equals(interactType) ) {
			this.input = input;
		} else if (_SWITCH_ACTOR.equals(interactType) ) {
			this.input = null;
			saveToSwitchFile(input);
		} else {
			setInput(input, _DEFAULT_ACTOR);
		}
	}

	private void saveToSwitchFile(String[] input) {
		
		StringBuffer sb = new StringBuffer();
		sb.append(getRawDataPath()).append(defaultSwitchFile);
		
		File switchFile = new File(sb.toString());
		if ( !switchFile.exists() ) {
			boolean fileCreation = false;
			try {
				fileCreation = switchFile.createNewFile();
			} catch (IOException e) {
				ErrorLogger.log(e.getMessage());
			} finally {
				if ( !fileCreation ) {
					ErrorLogger.log("switchFile create fail!");
					return;
				}
			}
		}
		
		//clear file content
		FileOutputStream fs = null;
		PrintWriter out = null;
		try {
			fs = new FileOutputStream(switchFile);
			out = new PrintWriter(switchFile);
		} catch (FileNotFoundException e) {
				ErrorLogger.log(e.getMessage());
		}
		try {
			fs.write(new String("").getBytes());
		} catch (IOException e) {
				ErrorLogger.log(e.getMessage());
		}
		
		for (String in : input ) {
			out.println( in );
		}
		out.flush();
		
		if ( null != out ) {
			out.close();
		}
		
		if ( null != fs ) {
			try {
				fs.close();
			} catch (IOException e) {
				ErrorLogger.log(e.getMessage());
			}
		}
	}

	@Override
	public void interact(Process process) {
        //Nothing
	}
	
	private String getRawDataPath() {
		String rp = FileRawDataFetcher.getRawDataPath();
		
		StringBuffer sb = new StringBuffer();
		if ( rp.endsWith(File.separator) ) {
			sb.append(rp);
		} else {
			sb.append(rp).append(File.separator);
		}
		
		return sb.toString();
	}
	
	private String getInteractType() {
        ScriptMgr scriptMgr =
                (ScriptMgr) Application.instance.getAttribute(COMPONENT.SCRIPT_MGR);
		return scriptMgr.getDefaultConfigedInteractorType();
	}

}
