package com.bdcom.dce.biz.pojo;

import com.bdcom.dce.biz.storage.StorableItem;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2012-11-14 <br>
 * Auto-Generated by eclipse Juno <br>
 */

public class Scenario extends StorableItem implements Serializable {
	
	private static final long serialVersionUID = 458346062352593578L;

	private static long _maxId = -1; //can't Serialize, should int by hand
	
	private long id = -1;

	private Map<String, String> restrictAttr = new HashMap<String, String>();

    public Scenario() {
        this("", 0);
    }

    public Scenario(String serial, int beginIndex) {
        super(serial, beginIndex);
    }

    public String getScenarioName() {
		return getRemarkName();
	}

	public void setScenarioName(String scenarioName) {
        setRemarkName( scenarioName );
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
	
	public static void calcMaxId(Collection<Scenario> collec) {
		if ( null != collec && !collec.isEmpty() ) {
			long maxId = -1;
			for ( Scenario sce : collec ) {
				long id = sce.getId();
				if ( maxId < id) {
					maxId = id;
				}
			}
			_maxId = maxId;
		}
	}

	public void setRestrictAttr(Map<String, String> attrs) {
		restrictAttr = attrs;
        updateDateModify();
	}

	public void putAttr(String attrName, String attrValue) {
		restrictAttr.put(attrName, attrValue);
        updateDateModify();
	}
	
	public String getAttr(String attrName) {
		return restrictAttr.get(attrName);
	}
	
	public Set<String> getAttrNames() {
		return restrictAttr.keySet();
	}
	
	public void rmAttr(String attrName) {
		restrictAttr.remove(attrName);
        updateDateModify();
	}
	
	public boolean isNoAttrAdded() {
		return restrictAttr.isEmpty();
	}
	
	public void clearAllAttr() {
		restrictAttr.clear();
        updateDateModify();
	}

	public long getHashCode() {
		long h = 0;
		long t = 1;
		if ( null == restrictAttr ||
				restrictAttr.isEmpty() ) {
			return 0;
		} else {
			for ( Entry<String, String> entry : restrictAttr.entrySet() ) {
				for ( int i=0; i< restrictAttr.size(); i++ ) {
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
