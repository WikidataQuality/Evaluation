package eval;

import java.util.ArrayList;

import org.json.*;


public class Session {
	
	static final int maxLength = 1500;
	
	private String entityId;	
	private JSONObject startLog;
	private JSONObject endLog;
	private ArrayList<JSONObject> deferredLogs;
	
	public Session(String entityId, JSONObject startLog, JSONObject endLog) {
		this.entityId = entityId;		
		this.startLog = startLog;
		this.endLog = endLog;
		this.deferredLogs = new ArrayList<JSONObject>();
	}
	
	public String getEntityId() {
		return entityId;
	}
	
	public JSONObject getStartLog() {
		return startLog;
	}
	
	public JSONObject getEndLog() {
		return endLog;
	}
	
	public String getStartTimestamp() {
		return startLog.getString("insertion_timestamp");
	}
	
	public String getEndTimestamp() {
		return endLog.getString("insertion_timestamp");
	}
	
	public ArrayList<JSONObject> getDeferredLogs() {
		return deferredLogs;
	}
	
	public void addDeferredLog(JSONObject deferredLog) {
		deferredLogs.add(deferredLog);
	}
	
	public void addDeferredLogs(ArrayList<JSONObject> deferredLogs) {
		this.deferredLogs.addAll(deferredLogs);
	}
	
}
