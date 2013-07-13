package com.bdcom.service.script;

import com.bdcom.service.Application;
import com.bdcom.service.ApplicationConstants;
import com.bdcom.service.ConfigPath;
import com.bdcom.util.StringUtil;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2012-11-21 <br>
 * Auto-Generated by eclipse Juno <br>
 */

public class FileRawDataFetcher implements ApplicationConstants {
	
	private static final String CURRENT_DIR = Application.CURRENT_DIR;
	
	public static final String RAW_DATA_DIR = ConfigPath._SPT+"RawData"+ConfigPath._SPT;
	
	private static final String END_FIX = ".raw";
	
	private static String savePath;

    private static ScriptMgr scriptMgr;

	private File[] rawDataFiles;


	public static String getRawDataPath() {
		updateSavePath();
		return savePath;
	}
	
	private static void updateSavePath() {
        ScriptMgr scriptMgr = getScriptMgr();
		if ( !scriptMgr.isUserDefaultRawDataPath() ) {
			savePath = scriptMgr.getDefaultConfigedRawDataPath();
			if ( !StringUtil.isNotBlank(savePath) ) {
				savePath = getPathToSave();
			}
		} else {
			savePath = getPathToSave();
		}
	}
	
	public File[] fetch() {
		updateSavePath();
		File rawDataDir = new File(savePath);
		if (rawDataDir.isDirectory()) {
			rawDataFiles = rawDataDir.listFiles(
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
		}
		return rawDataFiles;
	}
	
	public boolean destroyCachedRawData() {
		if ( null == rawDataFiles ) {
			return true;
		}
		
		for ( File cache : rawDataFiles ) {
			if (!cache.delete()) {
				return false;
			}
		}
		rawDataFiles = null;
		
		return true;
	}
	
	private static String getPathToSave() {
		StringBuffer sb = new StringBuffer();
		sb.append(CURRENT_DIR)
		  .append(RAW_DATA_DIR);
		File sceDir = new File(sb.toString());
		if ( !sceDir.exists() ) {
			sceDir.mkdir();
		}
		
		return sb.toString();
	}

    private static ScriptMgr getScriptMgr() {
        if ( null == scriptMgr ) {
            scriptMgr =
                (ScriptMgr) Application.getAttribute(COMPONENT.SCRIPT_MGR);
        }
        return scriptMgr;
    }
}
