package com.bdcom.dce.biz.script;

import com.bdcom.dce.biz.script.session.CrtSession;
import com.bdcom.dce.sys.AppContent;
import com.bdcom.dce.sys.ApplicationConstants;
import com.bdcom.dce.sys.configure.ScriptEnvConfig;
import com.bdcom.dce.util.logger.ErrorLogger;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-9-11    <br/>
 * Time: 17:28  <br/>
 */
public class ScriptExecutor0 implements ApplicationConstants {

    private Map<String, CrtSession> crtSessions;

    private final AppContent app;

    public ScriptExecutor0(AppContent app) {
        this.app = app;
        crtSessions = new ConcurrentHashMap<String, CrtSession>();
    }

    public void execute(String scriptPath, String[] sessions) {
        for ( String sessionName : sessions ) {
            CrtSession crtSession = crtSessions.get( sessionName );
            if ( null == crtSession ) {
                crtSession = new CrtSession();
                crtSession.setCrtSessionName(sessionName);
                crtSessions.put(sessionName, crtSession);
            }

            try {
                crtSession.runScript(scriptPath);
            } catch (IOException e) {
                ErrorLogger.log(e.getMessage());
            } catch (InterruptedException e) {
                ErrorLogger.log(e.getMessage());
            }

        }
    }

    public void waitFor() throws InterruptedException {
        Set<String> sessionNameSet = crtSessions.keySet();
        for ( String name : sessionNameSet ) {
            CrtSession session = crtSessions.get( name );
            synchronized ( session ) {
                while( session.isRunning() ) {
                    session.wait();
                }
            }
        }
    }

    public boolean isAnyRunning() {
        boolean running = false;
        if ( crtSessions.isEmpty() ) {
            return false;
        }
        Set<String> sessionNameSet = crtSessions.keySet();
        for ( String name : sessionNameSet ) {
            CrtSession session = crtSessions.get( name );
            boolean isThisRunning = session.isRunning();
            if ( !isThisRunning ) {
                crtSessions.remove( name );
            }
            running = running || isThisRunning;
        }
        return running;
    }

    public void killAllRunningScript() {
        if ( crtSessions.isEmpty() ) {
            return;
        }
        for (Map.Entry<String, CrtSession> e : crtSessions.entrySet()) {
            CrtSession session = e.getValue();
            if ( session.isRunning() ) {
                session.killRunningProcess();
            }
        }
    }

    private ScriptEnvConfig getScriptEnvConfig(AppContent app) {
        return (ScriptEnvConfig) app.getAttribute( CONFIG.SCRIPT_ENV_CONFIG );
    }

}
