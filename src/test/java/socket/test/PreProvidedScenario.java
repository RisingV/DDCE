package socket.test;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2012-12-4 <br>
 * Auto-Generated by eclipse Juno <br>
 */

public class PreProvidedScenario implements Serializable {
	
	private static final long serialVersionUID = 458346062352593578L;
	
	private static long _maxId = -1; //can't Serialize, should init by hand
	
	private long id = -1;

	private String scenarioName;
	
	private Map<String, String> preProvidedAttrs = new HashMap<String, String>();
	
	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public void idAutoIncrease() {
		_maxId ++;
		id = _maxId;
	}

	public static long getMaxId() {
		return _maxId;
	}

	public static void setMaxId(long maxId) {
		_maxId = maxId;
	}
	
	public static void calcMaxId(Collection<PreProvidedScenario> collec) {
		if ( null != collec && !collec.isEmpty() ) {
			long maxId = -1;
			for ( PreProvidedScenario sce : collec ) {
				long id = sce.getId();
				if ( maxId < id) {
					maxId = id;
				}
			}
			_maxId = maxId;
		}
	}

	public void setPreProvidedAttrs(Map<String, String> attrs) {
		preProvidedAttrs = attrs;
	}

	public void putAttr(String attrName, String attrValue) {
		preProvidedAttrs.put(attrName, attrValue);
	}
	
	public String getAttr(String attrName) {
		return preProvidedAttrs.get(attrName);
	}
	
	public Set<String> getAttrNames() {
		return preProvidedAttrs.keySet();
	}
	
	public void rmAttr(String attrName) {
		preProvidedAttrs.remove(attrName);
	}
	
	public boolean isNoAttrAdded() {
		return preProvidedAttrs.isEmpty();
	}
	
	public void clearAllAttr() {
		preProvidedAttrs.clear();
	}
	
	public long getHashCode() {
		long h = 0;
		long t = 1;
		if ( null == preProvidedAttrs ||
				preProvidedAttrs.isEmpty() ) {
			return 0;
		} else {
			for ( Entry<String, String> entry : preProvidedAttrs.entrySet() ) {
				for ( int i=0; i<preProvidedAttrs.size(); i++ ) {
					t += t*2 + entry.getKey().length()*3 + 
							entry.getValue().hashCode()*2;
					h += t;
				}
			}
		}
		
		if ( h < 0 ) {
			h = -h;
		}
		String hs = String.valueOf(h);
		if ( hs.length() > 10) {
			String ps = hs.substring(hs.length()- 10, hs.length());
			while ( ps.startsWith("0")) {
				ps = ps.substring(1, ps.length());
			}
			return Long.parseLong(ps);
		}
		return h;
	}
}