package com.bdcom.dce.biz.pojo;

import com.bdcom.dce.util.logger.ErrorLogger;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2012-10-31 <br>
 * Auto-Generated by eclipse Juno <br>
 *<br>
 * fields with <code>Dumb</code> annotation won't become FocusedAttrs <br>
 * @see <code>ScenarioUtil<code>'s getFocusedAttrs method <br>
 * @warning never change name of any fields!
 * */
public class BaseTestRecord implements Serializable {

	@Dumb
	private static final long serialVersionUID = -3184815715296232530L;

//	@Dumb
//	public static final String SEPARATOR = "##";
//	
//	@Dumb
//	public static final String END_FLAG = "@@";
	
	@Dumb
	private boolean helpFlag;
	
	@Dumb
	private boolean ifCommit;
	
	@Dumb
	private boolean isFC;
	
	@Dumb
	@ForceToBeAttr
	private String testerNum;
	
	@Dumb
	@ForceToBeAttr
	private String type;
	
	@Dumb
	@ForceToBeAttr
	private String beginTime;
	
	@Dumb
	private String script;
	
	@Dumb
	private String consoleName;
	
	@Dumb
	private String serialNumber;
	
	@Dumb
	@ForceToBeAttr
	private String id;
	
	private String mac;
	
	private String step;
	
	private String memo;
	
	private String verOfEPROM;
	
	private String volOfFlash;
	
	private String volOfDRam;
	
	private String softwareInfo;
	
	private String hardwareInfo;
	
	private String modelType;
	
	@Dumb
	@ForceToBeAttr
	private String endTime;
	
	@Dumb
	@ForceToBeAttr
	private String status;
	
	@Dumb
	private String randomID;
	
	public Map<String, String> toKVSet() {
		Map<String, String> KV = new HashMap<String, String>();
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			String name = field.getName();
			String value = null;
			if ( !name.equals("helpFlag") && !name.equals("ifCommit") &&
				 !name.equals("serialVersionUID") && !name.equals("SEPARATOR") &&
				 !name.equals("END_FLAG")) {
				try {
					if ( "isFC".equals(name) ) {
						value = String.valueOf( field.getBoolean(this) );
					} else {
						value = ((String) field.get(this));
					}
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(this.getClass().getName() + 
							" throws IllegalArgumentException when getting value of field: " + name);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(this.getClass().getName() + 
							" throws IllegalAccessException when getting value of field: " + name);
				}
				KV.put(name, value);
			}
		}
		return KV;
	}
	
	public String toLogString() {
		Map<String, String> kv = this.toKVSet();
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> entry : kv.entrySet()) {
			sb.append("( ")
			  .append(entry.getKey())
			  .append(" : ")
			  .append(entry.getValue())
			  .append(") ");
		}
		return sb.toString();
	}
	
	public boolean isFC() {
		return isFC;
	}
	
	public void setFC(boolean isFC) {
		this.isFC = isFC;
	}
	
	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public boolean isHelpFlag() {
		return helpFlag;
	}

	public void setHelpFlag(boolean helpFlag) {
		this.helpFlag = helpFlag;
	}

	public boolean isIfCommit() {
		return ifCommit;
	}

	public void setIfCommit(boolean ifCommit) {
		this.ifCommit = ifCommit;
	}

	public String getTesterNum() {
		return testerNum;
	}

	public void setTesterNum(String testerNum) {
		this.testerNum = testerNum;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getConsoleName() {
		return consoleName;
	}

	public void setConsoleName(String consoleName) {
		this.consoleName = consoleName;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVerOfEPROM() {
		return verOfEPROM;
	}

	public void setVerOfEPROM(String verOfEPROM) {
		this.verOfEPROM = verOfEPROM;
	}

	public String getVolOfFlash() {
		return volOfFlash;
	}

	public void setVolOfFlash(String volOfFlash) {
		this.volOfFlash = volOfFlash;
	}

	public String getVolOfDRam() {
		return volOfDRam;
	}

	public void setVolOfDRam(String volOfDRam) {
		this.volOfDRam = volOfDRam;
	}

	public String getSoftwareInfo() {
		return softwareInfo;
	}

	public void setSoftwareInfo(String softwareInfo) {
		this.softwareInfo = softwareInfo;
	}

	public String getHardwareInfo() {
		return hardwareInfo;
	}

	public void setHardwareInfo(String hardwareInfo) {
		this.hardwareInfo = hardwareInfo;
	}

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		if ("OK".equalsIgnoreCase(status)) {
			this.status = "true";
		} else {
			this.status = "false";
		}
	}
	
	public String getRandomID() {
		return randomID;
	}

	public void setRandomID(String randomID) {
		this.randomID = randomID;
	}

	public void setAttrByFieldName(String fieldName, String newValue) {
		Field field = null;
		try {
			field = this.getClass().getDeclaredField(fieldName);
		} catch (SecurityException e) {
			ErrorLogger.log(e.getMessage());
		} catch (NoSuchFieldException e) {
			ErrorLogger.log(e.getMessage());
		}
		
		if ( null != field ) {
			try {
				field.set(this, newValue);
			} catch (IllegalArgumentException e) {
				ErrorLogger.log(e.getMessage());
			} catch (IllegalAccessException e) {
				ErrorLogger.log(e.getMessage());
			}
		}
	}
	
	public String getAttrByFieldName(String fieldName) {
		Field field = null;
		try {
			field = this.getClass().getDeclaredField(fieldName);
		} catch (SecurityException e) {
			ErrorLogger.log(e.getMessage());
		} catch (NoSuchFieldException e) {
			ErrorLogger.log(e.getMessage());
		}
		String attr = null;
		if ( null != field ) {
			try {
				attr = (String) field.get(this);
			} catch (IllegalArgumentException e) {
				ErrorLogger.log(e.getMessage());
			} catch (IllegalAccessException e) {
				ErrorLogger.log(e.getMessage());
			}
			
		}
		return attr;
	}

    public static BaseTestRecord newRecord(String userNum) {
        BaseTestRecord record = new BaseTestRecord();
        record.setTesterNum(userNum);
        return record;
    }

}
