package com.bdcom.dce.sys.configure;

import com.bdcom.dce.util.StringUtil;
import com.bdcom.dce.util.XmlUtil;
import com.bdcom.dce.util.logger.ErrorLogger;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-14    <br/>
 * Time: 18:09  <br/>
 */
public class PathConfig implements Serializable {

    private static final long serialVersionUID = -5779224876302921651L;

    public static final String _SPT = File.separator;

    private static final String _SETTING = "Setting";

    private static final String _CONF_PATH = "ConfPath";

    //	private static final String DEFAULT_PATH = "\\Config\\";
    private static final String DEFAULT_PATH = _SPT + "Config" + _SPT;

    //	private static final String LOCAL_CONF_PATH = "\\LocalConfig\\";
    private static final String LOCAL_CONF_PATH = _SPT + "LocalConfig" + _SPT;

	private static final String DLL_LIB_PATH = _SPT + "Lib" + _SPT;

	private static final String DEFAULT_CONF_FILE_NAME = "local.xml";

    private File confFile;

	private boolean isFileCreated;

    private final String currentDir;

    public PathConfig(String currentDir) {
        this.currentDir =currentDir;
    }


	private void createConfigFile() {
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

	public String getLocalConfDir() {
		return getConfigFileDir(LOCAL_CONF_PATH);
	}

	public String getDllLibPath() {
		return getConfigFileDir(DLL_LIB_PATH);
	}

	public String getConfDir() {
		String rawConfDir = getRawConfDir();
		if ( rawConfDir.endsWith( _SPT ) ) {
			return rawConfDir;
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append(rawConfDir).append( _SPT );
			return sb.toString();
		}
	}

	private String getRawConfDir() {
		createConfigFile();
		if ( isFileCreated ) {
			String configedConfDir = getConfDir(confFile);
			if ( StringUtil.isNotBlank(configedConfDir) ) {
				return configedConfDir;
			}
		}
		return  getConfigFileDir(DEFAULT_PATH);
	}

	private String getConfDir(File xmlFile) {
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

	private String getConfigFileDir(String dir) {
		StringBuffer sb = new StringBuffer();
		sb.append(currentDir)
		  .append(dir);
		File sceDir = new File(sb.toString());
		if ( !sceDir.exists() ) {
			sceDir.mkdirs();
		}

		return sb.toString();
	}
}
