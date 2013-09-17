package com.bdcom.dce.sys.configure;

import com.bdcom.dce.util.SerializeUtil;

import java.io.File;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-11    <br/>
 * Time: 14:29  <br/>
 */
public class ScriptEnvConfig implements Serializable {

    private static final long serialVersionUID = 539691570018025539L;

    private static final String SCRIPT_ENV_CONF_FILE = "script_env.conf";

    private static final String DL_CRT = "Download";
    private static final String TT_CRT = "Telnet";
    private static final String RT_CRT = "Router";
    private static final String EO_CRT = "EponOnu";

    private static final String[] DEFAULT_CRT_SESSIONS =  {
            DL_CRT, TT_CRT, RT_CRT, EO_CRT
    };

    private static ScriptEnvConfig scriptEnvConfig;

    private final PathConfig pathConfig;

    private String[] crtSessions;

    private String secureCrtPath;

    private ScriptEnvConfig(PathConfig pathConfig) {
        this.pathConfig = pathConfig;
        crtSessions = DEFAULT_CRT_SESSIONS;
    }

    public String[] getCrtSessions() {
        return crtSessions;
    }

    public String getSecureCrtPath() {
        return secureCrtPath;
    }

    public void setSecureCrtPath(String secureCrtPath) {
        this.secureCrtPath = secureCrtPath;
    }

    public void saveToFile() {
        String path = pathConfig.getConfDir() + SCRIPT_ENV_CONF_FILE;
        SerializeUtil.serializeToFile( this, path );
    }

    public static ScriptEnvConfig getInstance(PathConfig pathConfig) {
        if ( null == pathConfig ) {
            return null;
        }

        if ( null == scriptEnvConfig ) {
            String path = pathConfig.getConfDir() + SCRIPT_ENV_CONF_FILE;
            File serializedFile = new File( path );
            if ( serializedFile.exists() ) {
                Object serialized = SerializeUtil.deserializeFromFile( serializedFile );
                if ( serialized instanceof ScriptEnvConfig ) {
                    scriptEnvConfig = (ScriptEnvConfig) serialized;
                }
            }
            if ( null == scriptEnvConfig ) {
                scriptEnvConfig = new ScriptEnvConfig( pathConfig );
            }
        }

        return scriptEnvConfig;
    }

}
