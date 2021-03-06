package com.bdcom.dce.sys;

import com.bdcom.dce.sys.gui.Application;
import com.bdcom.dce.util.logger.ErrorLogger;
import com.bdcom.dce.util.StringUtil;
import com.bdcom.dce.util.XmlUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2012-12-17 <br>
 * Auto-Generated by eclipse Juno <br>
 */

public abstract class ConfigPath {

    public static final String _SPT = File.separator;
	
	private static final String CURRENT_DIR = Application.CURRENT_DIR;
	
	private static final String _SETTING = "Setting";
	
	private static final String _CONF_PATH = "ConfPath";
	
//	private static final String DEFAULT_PATH = "\\Config\\";
	private static final String DEFAULT_PATH = _SPT + "Config" + _SPT;

//	private static final String LOCAL_CONF_PATH = "\\LocalConfig\\";
	private static final String LOCAL_CONF_PATH = _SPT + "LocalConfig" + _SPT;

	private static final String DLL_LIB_PATH = _SPT + "Lib" + _SPT;
	
	private static final String DEFAULT_CONF_FILE_NAME = "local.xml";
	
	private static File confFile;
	
	private static boolean isFileCreated;

	private static void createConfigFile() {
		confFile = new File(
				getConfigFileDir(LOCAL_CONF_PATH) + DEFAULT_CONF_FILE_NAME
				);
		if ( !confFile.exists() ) {
			try {
				  isFileCreated = confFile.createNewFile();
			} catch (IOException e) {
				ErrorLogger.log(e.getMessage());
			} finally {
				if ( isFileCreated ) {
					String[] elemChain = {_SETTING};
					String[] subElems = {_CONF_PATH};
					String[] subElemsValue = {getConfigFileDir(DEFAULT_PATH)};
					XmlUtil.addElem(elemChain, subElems, 
							subElemsValue, confFile);
				}
			}
		} else {
			isFileCreated = true;
		}
	}
	
	public static String getLocalConfDir() {
		return getConfigFileDir(LOCAL_CONF_PATH);
	}
	
	public static String getDllLibPath() {
		return getConfigFileDir(DLL_LIB_PATH);
	}
	
	public static String getConfDir() {
		String rawConfDir = getRawConfDir();
		if ( rawConfDir.endsWith( _SPT ) ) {
			return rawConfDir;
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append(rawConfDir).append( _SPT );
			return sb.toString();
		}
	}
	
	private static String getRawConfDir() {
		createConfigFile();
		if ( isFileCreated ) {
			String configedConfDir = getConfDir(confFile);
			if ( StringUtil.isNotBlank(configedConfDir) ) {
				return configedConfDir; 
			}
		}
		return  getConfigFileDir(DEFAULT_PATH);
	}
	
	private static String getConfDir(File xmlFile) {
		if ( null == xmlFile || !xmlFile.exists() ) {
			return null;
		}
		
		String[] elemChain = {_CONF_PATH};
		String confPath = XmlUtil.getElemValue(elemChain, xmlFile);
		
		if (StringUtil.isNotBlank(confPath)) {
			File dir = new File(confPath);
			if ( !dir.exists() ) {
				dir.mkdirs();
			}
		}
		return confPath; 
	}
	
	private static String getConfigFileDir(String dir) {
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
