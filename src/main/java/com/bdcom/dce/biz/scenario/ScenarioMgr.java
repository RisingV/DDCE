package com.bdcom.dce.biz.scenario;

import com.bdcom.dce.biz.pojo.Scenario;
import com.bdcom.dce.sys.config.PathConfig;
import com.bdcom.dce.util.SerializeUtil;
import com.bdcom.dce.util.StringUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-7-9    <br/>
 * Time: 15:30  <br/>
 */
public class ScenarioMgr {

    public static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    public static final String CURRENT_DIR = System.getProperty("user.dir");

    private static final String SCENARIO_DIR = "Scenarios" + File.separator;

    private static final String END_FIX = ".sce";

    private Map<String, Scenario> scenarios;

    private Map<Long, String> scenarioIdFastEntry;

    private Map<String, Scenario> serialMapping;

    private PathConfig pathConfig;

    public ScenarioMgr(PathConfig pathConfig) {
        this.pathConfig = pathConfig;
    }

    public void reloadScenarios() {
        if ( null != scenarios) {
            scenarios.clear();
        }
        if ( null != scenarioIdFastEntry) {
            scenarioIdFastEntry.clear();
        }
        if ( null != serialMapping) {
            serialMapping.clear();
        }
        loadSavedScenarios();
    }

    public void addScenario(Scenario scenario) {
        addScenario(
                scenario.getScenarioName(),
                scenario
        );
    }

    public void addScenario(String scenarioName,
                            Scenario scenario) {

        if (null == scenarioName) {
            scenarioName = "SEC" + DATE_FORMAT.format(new Date());
            scenario.setScenarioName(scenarioName);
        }

        String serial = scenario.getSerialNum();
        addToScenarioEntry( scenarioName, scenario );
        addToSerialMapping( serial, scenario );

        saveScenarioToFile(scenarioName);
    }

    private void addToSerialMapping(String serial,
                                           Scenario scenario) {
        if ( null == serialMapping) {
            serialMapping = new HashMap<String, Scenario>();
        }
        serialMapping.put(serial, scenario);
    }

    public Scenario getScenarioByFullSerial(String serial) {
        if ( null != serialMapping && !serialMapping.isEmpty() ) {
            return serialMapping.get(serial);
        }
        return null;
    }

    public Scenario getScenarioBySerial(String serial) {
        if ( null != serialMapping && !serialMapping.isEmpty() ) {
            Set<String> serialNumSet = serialMapping.keySet();
            for (String ser : serialNumSet) {
                Scenario sce = serialMapping.get(ser);
                int beginIndex = sce.getBeginIndex();
                if ( beginIndex + ser.length() > serial.length() ) {
                    continue;
                } else {
                    String matchingStr = serial.substring(beginIndex, beginIndex + ser.length());
                    if (matchingStr.equals(ser)) {
                        return sce;
                    }
                }
            }
        }
        return null;
    }

    private void addToScenarioEntry(String scenarioName,
                                           Scenario scenario) {

        if (null == scenarios) {
            scenarios = new HashMap<String, Scenario>();
        }
        if (null == scenarioIdFastEntry) {
            scenarioIdFastEntry = new HashMap<Long, String>();
        }

        long sid = scenario.getId();
        if ( sid < 0 ) {
            scenario.idAutoIncrease();
        } else { //update ScenarioName
            String oldSceName = scenarioIdFastEntry.get(
                    new Long(sid)
            );
            scenarios.remove(oldSceName);
        }

        scenarios.put(scenarioName, scenario);
        scenarioIdFastEntry.put(new Long(scenario.getId()),
                scenarioName
        );
    }

    public Scenario getScenarioByName(String sceName) {
        if ( null == scenarios) {
            return null;
        } else {
            Scenario sce = scenarios.get(sceName);
            return sce;
        }
    }

    public Set<String> getScenarioNameList() {
        if ( null == scenarios) {
            return new LinkedHashSet<String>();
        } else {
            return scenarios.keySet();
        }
    }

    public boolean isScenarioNameReduplicated(Scenario scenario) {
        String currentName = scenario.getScenarioName();
        Scenario savedSce = getScenarioByName(currentName);
        if ( null == savedSce ) {
            return false;
        } else {
            if ( scenario.getId() == savedSce.getId() ) {
                return false;
            } else {
                return true;
            }
        }

    }

    public void removeScenario(String scenarioName) {
        if ( null == scenarios) {
            return;
        }

        scenarios.remove(scenarioName);
        SerializeUtil.delSerializedFile(
                getPathToSave(scenarioName)
        );

    }

    public void removeAll() {
        reloadScenarios();
        for (String name : getScenarioNameList() ) {
            removeScenario(name);
        }
    }

    public void saveScenarioToFile(String scenarioName) {
        Scenario sce = null;
        if ( null != scenarios) {
            sce = scenarios.get(scenarioName);
            if (null != sce) {
                SerializeUtil.serializeToFile(
                        sce,
                        getPathToSave(
                                sce.getScenarioName()
                        )
                );
            }
        }

    }

    public void saveAllScenarioToFile() {

        if ( null == scenarios) {
            return;
        }

        for ( Map.Entry<String, Scenario> entry :
                scenarios.entrySet() ) {
            SerializeUtil.serializeToFile(
                    entry.getValue(),
                    getPathToSave(
                            entry.getKey()
                    )
            );
        }

    }

    private String getPathToSave(String sName) {
        StringBuffer sb = new StringBuffer();
        sb.append( pathConfig.getConfDir() )
                .append(SCENARIO_DIR);
        File sceDir = new File(sb.toString());
        if ( !sceDir.exists() ) {
            sceDir.mkdir();
        }
        sb.append(sName)
                .append(END_FIX);

        return sb.toString();
    }

    private static String generateSessionId() {
        return StringUtil.getRandomString(15);
    }

    private void loadSavedScenarios() {
        StringBuffer sb = new StringBuffer();
        sb.append(pathConfig.getConfDir()).append(SCENARIO_DIR);

        File sceDir = new File(sb.toString());
        if (sceDir.isDirectory()) {
            File[] savedFiles = sceDir.listFiles(
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
            for ( File savedFile : savedFiles ) {
                Object obj = SerializeUtil.deserializeFromFile(savedFile);
                Scenario sce = (Scenario) obj;
                if ( null == scenarios) {
                    scenarios = new HashMap<String, Scenario>();
                }
                scenarios.put(
                        sce.getScenarioName(),
                        sce
                );
                addToSerialMapping(
                        sce.getSerialNum(),
                        sce
                );
            }
        }
        if ( null != scenarios) {
            Collection<Scenario> preSces = scenarios.values();
            Scenario.calcMaxId(preSces);
            if ( null == scenarioIdFastEntry) {
                scenarioIdFastEntry = new HashMap<Long, String>();
            }
            for ( Map.Entry<String, Scenario> entry :
                    scenarios.entrySet() ) {
                scenarioIdFastEntry.put(new Long(
                        entry.getValue().getId()),
                        entry.getKey());

            }
        }
    }

}
