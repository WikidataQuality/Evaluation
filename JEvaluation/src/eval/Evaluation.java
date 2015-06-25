package eval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import org.json.*;

import fileio.FileIO;
import static java.lang.System.getProperty;
import static java.lang.System.out;


public class Evaluation {
	
	public static final String fileSep = getProperty("file.separator");
	public static final String lineSep = getProperty("line.separator");
	public static final String baseDir = getProperty("user.home");
	
	private static String logFileName;
	private static String specialPageName;
	
	public static void main(String[] args) {
		if (args.length > 0) {
			logFileName = args[0];
		} else {
			logFileName = "wbq_evaluation";
		}
		if (args.length > 1) {
			specialPageName = args[1];
		} else {
			specialPageName = "SpecialConstraintReport";
		}
		
		writeDummyLog();
		
		JSONObject[] logs = parseLogFile();
		logs = sortLogs(logs);
		Session[] sessions = identifySessions(logs);
		Analysis analysis = new Analysis(sessions);
		analysis.run();
		
		for (Session session: sessions) {
			out.println("Entity: " + session.getEntityId() +
					". Session started: " + session.getStartTimestamp() +
					", ended: " + session.getEndTimestamp()
			);
			out.print("Deferred logs:");
			for (JSONObject log: session.getDeferredLogs()) {
				out.print(" ");
				out.print(log.getString("insertion_timestamp"));
			}
			out.println("\n");
		}
	}
	
	private static void writeDummyLog() {
		String fileString = "";
		
		Random random = new Random();
		int k = 1000;
		for (int i = 0; i < k; i++) {
			String header = "testlog: ";
			
			String specialPageId = "\"special_page_id\":";
			String specialPageIdValue = "\"" + specialPageName + "\"";
			
			String entityId = "\"entity_id\":";
			String entityIdValue = "\"Q" + (random.nextInt(3)+1) + "\"";
			
			String insertionTimestamp = "\"insertion_timestamp\":";
			String insertionTimestampValue = "\"" + ((Session.maxLength-500)*i) + "\"";
			
			String referenceTimestamp = "\"reference_timestamp\":";
			String referenceTimestampValue = random.nextBoolean() ? "\"" + ((Session.maxLength-500)*(i-(random.nextInt(3)))) + "\"" : "null";
			
			String resultSummary = "\"result_summary\":";
			String resultSummaryValue = "";
			
			fileString +=
				header + "{" +
				specialPageId + specialPageIdValue + "," +
				entityId + entityIdValue + "," +
				insertionTimestamp + insertionTimestampValue + "," +
				referenceTimestamp + referenceTimestampValue + "," +
				resultSummary + "{" +
					resultSummaryValue +
				"}" + 
			"}" +
			lineSep;
		}
		
		try {
			FileIO.writeFile(fileSep + "logs", logFileName, "log", fileString);
		} catch (java.io.IOException ex) {
			out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	private static JSONObject[] parseLogFile() {	
		String[] logStrings = null;
		
		out.println("Reading logfile '" + logFileName + "'...");

		String logFileString = null;
		try {
			logFileString = FileIO.readFile(fileSep + "logs", logFileName, "log");
		} catch(java.io.IOException ex) {
			out.println(ex.getMessage());
			ex.printStackTrace();
		}
		
		out.println("Parsing logs...");
		
		if (!logFileString.equals(null)) {
			logStrings = logFileString.split(lineSep);
		}
		
		JSONObject[] logs = new JSONObject[logStrings.length];
		for (int i = 0; i < logStrings.length; i++) {
			logStrings[i] = logStrings[i].substring(logStrings[i].indexOf("{"));
			logs[i] = new JSONObject(logStrings[i]);
		}
		
		out.println("Done. " + logs.length + " logs found.");
		out.println();
		
		return logs;
	}
	
	private static JSONObject[] sortLogs(JSONObject[] logs) {
		Comparator<JSONObject> logComparator = new Comparator<JSONObject>() {
		    @Override
		    public int compare(JSONObject log1, JSONObject log2) {
		    	long log1timestamp = Long.parseLong(log1.getString("insertion_timestamp"));
		    	long log2timestamp = Long.parseLong(log2.getString("insertion_timestamp"));
		        return Long.compare(log1timestamp, log2timestamp);
		    }
		};
		
		out.println("Sorting logs chronologically...");
		
		Arrays.sort(logs, logComparator);
		
		out.println("Done. Logs sorted.");
		out.println();
		
		return logs;
	}
	
	private static Session[] identifySessions(JSONObject[] logs) {
		ArrayList<JSONObject> logList = new ArrayList<JSONObject>(Arrays.asList(logs));
		@SuppressWarnings("unchecked")
		ArrayList<JSONObject> shortLogList = (ArrayList<JSONObject>)logList.clone();

		HashMap<String, Session> sessions = new HashMap<String, Session>();
		
		//iterate all logs and find sessions
		HashMap<String, JSONObject> sessionStartLogs = new HashMap<String, JSONObject>();
		HashMap<String, JSONObject> sessionEndLogs = new HashMap<String, JSONObject>();
		
		long i = 0;
		
		for (JSONObject log: logList) {
			out.println("Searching logs for sessions:  " + ++i + "/" + logList.size() + " (" + (int)((double)i/logList.size()*100) + "%)");
			
			if (!log.getString("special_page_id").equals(specialPageName)) {
				shortLogList.remove(log);
				continue;
			}
			if (!(log.isNull("reference_timestamp"))) {
				continue;
			}
			String entityId = log.getString("entity_id");
			if (!sessionStartLogs.containsKey(entityId)) {
				sessionStartLogs.put(entityId, log);
				sessionEndLogs.put(entityId, log);
			} else {
				long currentLogTimestamp = Long.parseLong(log.getString("insertion_timestamp"));
		    	long sessionEndLogTimestamp = Long.parseLong(sessionEndLogs.get(entityId).getString("insertion_timestamp"));
				if (currentLogTimestamp - sessionEndLogTimestamp < Session.maxLength) {
					sessionEndLogs.put(entityId, log);
				} else {
					Session session = new Session(entityId, sessionStartLogs.get(entityId), sessionEndLogs.get(entityId));
					sessions.put(session.getEntityId() + "@" + session.getEndTimestamp(), session);
					sessionStartLogs.put(entityId, log);
					sessionEndLogs.put(entityId, log);
				}
			}
			shortLogList.remove(log);
		}
		
		//handle logs which are still in the list at the end
		out.println();
		i = 0;
		
		for (String entityId: sessionStartLogs.keySet()) {
			out.println("Finishing remaining sessions: " + ++i + "/" + sessionStartLogs.size() + " (" + (int)((double)i/sessionStartLogs.size()*100) + "%)");

			JSONObject sessionStartLog = sessionStartLogs.get(entityId);
			JSONObject sessionEndLog = sessionEndLogs.containsKey(entityId) ? sessionEndLogs.get(entityId) : sessionStartLog;
			
			Session session = new Session(entityId, sessionStartLog, sessionEndLog);
			sessions.put(session.getEntityId() + "@" + session.getEndTimestamp(), session);
			sessionEndLogs.remove(entityId);
		}
		
		//add deferred job logs to their respective sessions
		HashMap<String, ArrayList<JSONObject>> deferredJobLogs = new HashMap<String, ArrayList<JSONObject>>();
		
		out.println();
		i = 0;
		
		for (JSONObject log: shortLogList) {
			out.println("Finding deferred job logs:    " + ++i + "/" + shortLogList.size() + " (" + (int)((double)i/shortLogList.size()*100) + "%)");

			String sessionKey = log.getString("entity_id") + "@" + log.getString("reference_timestamp");
			if (sessions.containsKey(sessionKey)) {
				if (!deferredJobLogs.containsKey(sessionKey)) {
					deferredJobLogs.put(sessionKey, new ArrayList<JSONObject>());
				}
				deferredJobLogs.get(sessionKey).add(log);
			}
		}
		
		out.println();
		i = 0;
		
		for (String sessionKey: deferredJobLogs.keySet()) {
		out.println("Assigning logs to sessions:   " + ++i + "/" + deferredJobLogs.size() + " (" + (int)((double)i/deferredJobLogs.size()*100) + "%)");

		sessions.get(sessionKey).addDeferredLogs(deferredJobLogs.get(sessionKey));
		}
		
		out.println();
		out.println("A total of " + sessions.size() + " sessions has been identified.");
		out.println();
				
		Session[] allSessions = new Session[sessions.size()];
		allSessions = sessions.values().toArray(allSessions);
		return allSessions;
	}

}
