package com.bdcom.dce.biz.script;

import com.bdcom.dce.datadispacher.CommunicateStatus;
import com.bdcom.dce.sys.gui.Application;
import com.bdcom.dce.sys.config.PathConfig;
import com.bdcom.dce.biz.script.session.DefaultCrtSessions;
import com.bdcom.dce.util.StringUtil;
import com.bdcom.dce.util.XmlUtil;
import com.bdcom.dce.util.log.ErrorLogger;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-12    <br/>
 * Time: 13:55  <br/>
 */
public class ScriptMgr
        implements ScriptXmlConfConstants, CommunicateStatus, DefaultCrtSessions {

    private static final String SPT = File.separator;

    private static final DateFormat DATE_FORMAT = Application.DATE_FORMAT;

    private static final long FILE_LENGTH_LIMIT = 1024*1024*6;

    private static final String ANY_SCRIPT = "*";

    private static final String CONFIG_DIR_LOCAL = "Local" + SPT;

    private static final String CONFIG_DIR_SCRIPT = "Script"+ SPT;

    private static final String LOCAL_CONFIG_FILE_NAME = "script-config";

    private static final String END_FIX = ".xml";

    private Map<String, String> crtSessionContainer;

    private Map<String, String> scriptContainer;

    private Map<String, Integer> indexContainer;

    private Map<String, File> fileAssociater;

    private File defaultConfFile = null;

    private boolean isDefaultConfigFileExists;

    private boolean isUserDefaultRawDataPath;

    private String rawDataPath = null;

    private String defaultIptPath = null;

    private String interactorType = null;

    private String[] serialNums = null;

    private final PathConfig pathConfig;

    public ScriptMgr(PathConfig pathConfig) {
        this.pathConfig = pathConfig;
        crtSessionContainer = new HashMap<String, String>();
        scriptContainer = new HashMap<String, String>();
        indexContainer = new HashMap<String, Integer>();
        fileAssociater = new HashMap<String, File>();
    }

    public boolean isDefaultConfigFileExists() {
        return isDefaultConfigFileExists;
    }

    public boolean isUserDefaultRawDataPath() {
        return isUserDefaultRawDataPath;
    }

    public void  isUserDefaultRawDataPath(boolean is ) {
        isUserDefaultRawDataPath = is;
    }

    public String getDefaultConfigedInteractorType() {
        return interactorType;
    }

    public void reloadScripts() {
        crtSessionContainer.clear();
        scriptContainer.clear();
        indexContainer.clear();
        fileAssociater.clear();
        loadScripts();
    }

    private void loadScripts() {
        defaultConfFile = getLocalConfigFile();

        if ( defaultConfFile.exists() ) {
            interactorType = getDefaultConfigedInteractorType(defaultConfFile);
            defaultIptPath = getDefaultConfigedIptPath(defaultConfFile);
            String rawDataPathSetted = getDefaultConfigedRawDataPath(defaultConfFile);
            if ( StringUtil.isNotBlank(rawDataPathSetted) &&
                    StringUtil.isVaildFilePath(rawDataPathSetted)
                    ) {
                File rawDataDir = new File(rawDataPathSetted);
                if ( !rawDataDir.exists() ) {
                    try {
                        if ( rawDataDir.createNewFile() ) {
                            rawDataPath = rawDataPathSetted;
                        }
                    } catch (IOException e) {
                        ErrorLogger.log(e.getMessage());
                    }
                } else {
                    rawDataPath = rawDataPathSetted;
                }
            }
            assemblyCrtSessions(defaultConfFile);
        }

        File[] confFiles = getScriptConfigFiles();

        if ( null != confFiles && confFiles.length > 0) {
            for ( File xml : confFiles ) {
                assemblyScripts(xml);
            }
        }

        updateSerialNums();
    }

    private File getLocalConfigFile() {
        File localConfigFile = new File(
                getConfigFileDir( CONFIG_DIR_LOCAL )
                        + LOCAL_CONFIG_FILE_NAME + END_FIX
        );
        isDefaultConfigFileExists = true;
        if ( !localConfigFile.exists() ) {
            isDefaultConfigFileExists = false;
            isUserDefaultRawDataPath = true;

            boolean isFileCreated = false;
            try {
                isFileCreated = localConfigFile.createNewFile();
            } catch (IOException e) {
                ErrorLogger.log(e.getMessage());
            } finally {
                if ( isFileCreated ) {
                    isDefaultConfigFileExists = true;

                    String[] elemChain = {_SETTING};
                    String[] subElems = {RAW_DATA_PATH};
                    String[] subElemsValue = {""};
                    XmlUtil.addElem(elemChain, subElems,
                            subElemsValue, localConfigFile);


                    String[] subElems2 = {_INTERACTOR};
                    String[] subElemsValue2 = {_DEFAULT_ACTOR};
                    XmlUtil.addElem(elemChain, subElems2,
                            subElemsValue2, localConfigFile);

                    String[] subElems3 = {DEFAULT_IPT_PATH};
                    String[] subElemsValue3 = {""};
                    XmlUtil.addElem(elemChain, subElems3,
                            subElemsValue3, localConfigFile);

                    addCrtSession(DL_CRT, "", localConfigFile);
                    addCrtSession(TT_CRT, "", localConfigFile);
                    addCrtSession(RT_CRT, "", localConfigFile);
                    addCrtSession(EO_CRT, "", localConfigFile);
                }
            }
        }

        return localConfigFile;
    }

    private void updateSerialNums() {
        if ( !scriptContainer.isEmpty() ) {
            Set<String> serialSet = scriptContainer.keySet();
            serialNums = new String[serialSet.size()];
            int i = 0;
            for ( String serial : serialSet) {
                serialNums[i] = serial;
                i++;
            }
        }
    }

    public String getDefaultConfigedRawDataPath() {
        return rawDataPath;
    }

    public Set<String> getScriptInterpreterNames() {
        if ( !crtSessionContainer.isEmpty() ) {
            return crtSessionContainer.keySet();
        } else {
            return null;
        }
    }

    public Set<String> getCrtSessions() {
        if ( !crtSessionContainer.isEmpty() ) {
            return crtSessionContainer.keySet();
        } else {
            return null;
        }
    }

    public String getCrtSession(String name) {
        String path = crtSessionContainer.get(name);
        if ( !StringUtil.isNotBlank(path) ) {
            return defaultIptPath;
        } else {
            return path;
        }
    }

    private int addCrtSession(String iname, String ipath, File xmlFile) {
        int status = _NOT_DONE_YET;

        String[] elemChain = { _SETTING, _CRT_SESSIONS, _CRT_SESSION};
        String[] subElems = {_NAME, _PATH};
        String[] subElemsValue = {iname, ipath};
        status = XmlUtil.addElem(elemChain, subElems, subElemsValue, xmlFile);
        if ( status > 0 ) {
            crtSessionContainer.put(iname, ipath);
            return _CRT_SESSION_ADD_SUCCESS;
        } else {
            return status;
        }
    }

    public int addCrtSession(String iname, String ipath) {

        if ( !StringUtil.isVaildFilePath(ipath) ) {
            return _INVAILD_PATH;
        }

        if ( StringUtil.isNotBlank(ipath) ) {
            File dir = new File(ipath);
            if ( !dir.exists() ) {
                return _DIR_NOT_EXSIT;
            } else if ( dir.isFile() ) {
                return _IS_FILE_NOT_DIR;
            }
        }

        if ( !defaultConfFile.exists() ) {
            defaultConfFile = getLocalConfigFile();
        }

        if ( defaultConfFile.exists() ) {
            return addCrtSession(iname, ipath, defaultConfFile);
        } else {
            return _CONF_FILE_CREATE_FAIL;
        }

    }

    public int setDefaultIptPath(String path) {
        int status = _NOT_DONE_YET;
        if ( !StringUtil.isVaildFilePath(path) ) {
            return _INVAILD_PATH;
        }

        if ( !defaultConfFile.exists() ) {
            defaultConfFile = getLocalConfigFile();
        }
        if ( defaultConfFile.exists() ) {
            String[] elemChain = {DEFAULT_IPT_PATH};
            status = XmlUtil.setElemText(elemChain, path, defaultConfFile);

            if ( status > 0 ) {
                defaultIptPath = path;
                return _DIR_SAVE_SUCCESS;
            } else {
                return status;
            }
        } else {
            return _CONF_FILE_CREATE_FAIL;
        }
    }

    public int setRawDataPath(String path) {
        int status = _NOT_DONE_YET;
        if ( !StringUtil.isVaildFilePath(path) ) {
            return _INVAILD_PATH;
        }
        if ( StringUtil.isNotBlank(path) ) { //allow set null,to use default;
            File dir = new File(path);
            if ( !dir.exists() ) {
                if ( !dir.mkdirs() ) {
                    return _DIR_CREATE_FAIL;
                }
            }
        }

        if ( !defaultConfFile.exists() ) {
            defaultConfFile = getLocalConfigFile();
        }
        if ( defaultConfFile.exists() ) {
            String[] elemChain = {RAW_DATA_PATH};
            status = XmlUtil.setElemText(elemChain, path, defaultConfFile);

            if ( status > 0 ) {
                rawDataPath = path;
                return _DIR_SAVE_SUCCESS;
            } else {
                return status;
            }
        } else {
            return _CONF_FILE_CREATE_FAIL;
        }
    }

    public String getScriptPath(String serial) {
        return scriptContainer.get( serial );
    }

    public String getBeginIndex(String serial) {
        return String.valueOf( indexContainer.get(serial) );
    }

    public boolean isSerialNumUsed(String serial) {
        String sp = scriptContainer.get(serial);
        if ( StringUtil.isNotBlank(sp) ) {
            return true;
        } else {
            return false;
        }
    }

    public String getScriptBySerialNum(String serial) {
        if ( !scriptContainer.isEmpty() ) {
            for (String ser : serialNums) {
                int beginIndex = indexContainer.get(ser);
                if ( beginIndex + ser.length() > serial.length() ) {
                    continue;
                } else {
                    String matchingStr = serial.substring(beginIndex, beginIndex + ser.length());
                    if (matchingStr.equals(ser)) {
                        return scriptContainer.get(ser);
                    }
                }
            }
            return scriptContainer.get(ANY_SCRIPT);
        } else {
            return null;
        }
    }

    private String getDefaultConfigedInteractorType(File xmlFile) {
        if ( null == xmlFile || !xmlFile.exists() ) {
            return null;
        }

        String[] elemChain = {_INTERACTOR };
        String interactor = XmlUtil.getElemValue(elemChain, xmlFile);

        if (!StringUtil.isNotBlank(rawDataPath) ||
                !StringUtil.isVaildFilePath(rawDataPath)) {
            rawDataPath = null;
            isUserDefaultRawDataPath = true;
        } else {
            File testFile = new File(rawDataPath);
            if ( testFile.exists() ) {
                isUserDefaultRawDataPath = false;
            }
        }

        return interactor;
    }

    public String getDefaultConfigedIptPath() {
        if ( null == defaultIptPath) {
            return "";
        }
        return defaultIptPath;
    }

    private String getDefaultConfigedIptPath(File xmlFile) {
        if ( null == xmlFile || !xmlFile.exists() ) {
            return null;
        }

        String[] elemChain = {DEFAULT_IPT_PATH};
        String iptPath = XmlUtil.getElemValue(elemChain, xmlFile);

        if (!StringUtil.isNotBlank(iptPath) ||
                !StringUtil.isVaildFilePath(iptPath)) {
            iptPath = null;
        }

        return iptPath;
    }

    private String getDefaultConfigedRawDataPath(File xmlFile) {
        if ( null == xmlFile || !xmlFile.exists() ) {
            return null;
        }

        String[] elemChain = {RAW_DATA_PATH};
        String rawDataPath = XmlUtil.getElemValue(elemChain, xmlFile);

        if (!StringUtil.isNotBlank(rawDataPath) ||
                !StringUtil.isVaildFilePath(rawDataPath)) {
            rawDataPath = null;
            isUserDefaultRawDataPath = true;
        } else {
            File testFile = new File(rawDataPath);
            if ( testFile.exists() ) {
                isUserDefaultRawDataPath = false;
            }
        }

        return rawDataPath;
    }

    private void assemblyCrtSessions(File xmlFile) {
        if ( null == xmlFile || !xmlFile.exists() ) {
            return;
        }

        String[] elemChain = {_CRT_SESSIONS, _CRT_SESSION};
        Set<Map<String,String>> scripts = XmlUtil.getElemValueSet(elemChain, xmlFile);
        if ( null == scripts ) {
            return;
        }
        for ( Map<String, String> script : scripts ) {
            String name = script.get(_NAME);
            String path = script.get(_PATH);
            if ( StringUtil.isNotBlank(name) ) {
                if ( StringUtil.isNotBlank(path) ) {
                    crtSessionContainer.put(name, path);
                } else {
                    if ( StringUtil.isNotBlank(defaultIptPath) ) {
                        crtSessionContainer.put(name, defaultIptPath);
                    } else {
                        crtSessionContainer.put(name, "");
                    }
                }
            }
        }
    }

    private void assemblyScripts(File xmlFile) {
        if ( null == xmlFile || !xmlFile.exists() ) {
            return;
        }

        String[] elemChain = {_SCRIPT};
        Set<Map<String,String>> scripts = XmlUtil.getElemValueSet(elemChain, xmlFile);
        if ( null == scripts ) {
            return;
        }
        for ( Map<String, String> script : scripts ) {
            String serial = script.get(_SERIAL);
            String path = script.get(_PATH);
            String beginIndex = script.get(_BEGIN_INDEX);
            if (StringUtil.isNotBlank(serial) &&
                    StringUtil.isNotBlank(path) &&
                    StringUtil.isNotBlank(beginIndex)
                    ) {
                scriptContainer.put(serial, path);
                indexContainer.put(serial, new Integer(beginIndex) );
                fileAssociater.put(serial, xmlFile);
            }
        }
    }

    private File[] getScriptConfigFiles() {
        File scirptSettingDir = new File(getConfigFileDir(CONFIG_DIR_SCRIPT));
        File[] confFiles = null;
        if (scirptSettingDir.isDirectory()) {
            confFiles = scirptSettingDir.listFiles(
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
        return confFiles;
    }

    private String getConfigFileDir(String dir) {
        StringBuffer sb = new StringBuffer();
        sb.append( pathConfig.getConfDir() )
                .append(dir);
        File sceDir = new File(sb.toString());
        if ( !sceDir.exists() ) {
            sceDir.mkdirs();
        }

        return sb.toString();
    }

    public Set<String> indexingSerials(String serial) {

//		if ( !StringUtil.isNotBlank(serial) ) {
//			return null;
//		}

        if ( !scriptContainer.isEmpty() ) {
            Set<String> keys = scriptContainer.keySet();
            if ( !StringUtil.isNotBlank(serial) ) {
                return keys;
            }
            Set<String> serialSet = new LinkedHashSet<String>();
            for ( String key : keys ) {
                if ( key.indexOf(serial) >= 0) {
                    serialSet.add(key);
                }
            }
            return serialSet;
        } else {
            return null;
        }
    }

    public int removeScript(String serial) {
        int status = _NOT_DONE_YET;
        File fileSaved = null;
        if ( !fileAssociater.isEmpty() ) {
            File associatedFile = fileAssociater.get(serial);
            if ( null != associatedFile && associatedFile.exists() ) {
                fileSaved = associatedFile;
            } else {
                return _SCRIPT_REMOVE_SUCCESS;
            }
        } else {
            return _SCRIPT_REMOVE_SUCCESS;
        }

        String[] elemChain = {_SCRIPT, _SERIAL};
        status = XmlUtil.removeElemByText(elemChain, serial, fileSaved);
        scriptContainer.remove(serial);
        fileAssociater.remove(serial);

        return status;
    }

    public int updateScript(String originalSerial, String newSerial, String newPath, String beginIndex) {
        int errCode = removeScript(originalSerial);
        if ( errCode < 0 ) {
            return errCode;
        }
        return addScript(newSerial, newPath, beginIndex) - errCode;
    }

    public int addScript(String serial, String path, String beginIndex) {
        if ( isScriptSerialReduplicated( serial ) ) {
            return _SCRIPT_SERIAL_REDUPLICATED;
        }

        File testFile = new File(path);

        if ( !testFile.exists() || !testFile.isFile()) {
            return _SCRIPT_FILE_NOT_FOUND;
        }

        int status = _NOT_DONE_YET;
        File file2save = null;
        try {
            file2save = getFileToSave( serial );
        } catch (IOException e) {
            status = _SCRIPT_CONF_FILE_CREATE_FAIL;
            ErrorLogger.log(e.getMessage());
        } finally {
            if ( status < 0 ) {
                return status;
            }
        }

        if ( null == file2save ) {
            return _SCRIPT_CONF_FILE_CREATE_FAIL;
        }

        String[] elemChain = {_SCRIPTS, _SCRIPT};
        String[] subElems = {_SERIAL, _PATH, _BEGIN_INDEX};
        String[] subElemsValue = {serial, path, beginIndex};
        status = XmlUtil.addElem(elemChain, subElems, subElemsValue, file2save);

        if ( status > 0 ) {
            scriptContainer.put(serial, path);
            indexContainer.put(serial, new Integer(beginIndex));
            fileAssociater.put(serial, file2save);
            updateSerialNums();
            return _SCRIPT_ADDED_SUCCESS;
        } else {
            return status;
        }
    }

    private File getFileToSave(String serial) throws IOException {
        if ( !fileAssociater.isEmpty() ) {
            File associatedFile = fileAssociater.get(serial);
            if ( null != associatedFile && associatedFile.exists() ) {
                return associatedFile;
            }
        }

        File[] files = getScriptConfigFiles();
        if ( files != null ) {
            for ( File file : files ) {
                if ( file.length() < FILE_LENGTH_LIMIT ) {
                    return file;
                }
            }
        }

        File newConf = new File( getConfigFileDir(CONFIG_DIR_SCRIPT) +
                DATE_FORMAT.format(new Date()) + END_FIX );

        if ( newConf.createNewFile() ) {
            return newConf;
        } else {
            return null;
        }
    }

    public File newEmptyScriptConfFile(String extra) throws IOException {
        File newConf = new File( getConfigFileDir(CONFIG_DIR_SCRIPT) +
                DATE_FORMAT.format(new Date()) + extra + END_FIX );
        newConf.createNewFile();

        return newConf;
    }

    public File[] getScriptConfFiles() {
        if ( !fileAssociater.isEmpty() ) {
            Collection<File> collec = fileAssociater.values();
            File[] sfs = new File[collec.size()];
            collec.toArray(sfs);
            return sfs;
        } else {
            return new File[0];
        }
    }

    public void removeAll() {
        File[] files = getScriptConfFiles();
        for (File file : files ) {
            if (  null != file && file.exists() ) {
                file.delete();
            }
        }
    }

    private boolean isScriptSerialReduplicated(String name) {
        String path = scriptContainer.get(name);
        if ( StringUtil.isNotBlank(path) ) {
            return true;
        } else {
            return false;
        }
    }

}

