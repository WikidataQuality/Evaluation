package eval;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.*;


class Analysis {
	
	private final Session[] sessions;
	
	Analysis(Session[] sessions) {
		this.sessions = sessions;
	}
	
	public void run() {
		int numberOfSessions = findNumberOfSessions();
		HashMap<String, ArrayList<Session>> distributionOfSessionsPerEntity = findDistributionOfSessionsPerEntity();
		ArrayList<HashMap<String, HashMap<String, Integer>>> distributionOfViolationsPerSessionStart = findDistributionOfViolationsPerSessionStart();
	}

	private int findNumberOfSessions() {
		return sessions.length;
	}
	
	private HashMap<String, ArrayList<Session>> findDistributionOfSessionsPerEntity() {
		HashMap<String, ArrayList<Session>> distribution = new HashMap<String, ArrayList<Session>>();
		for (Session session: sessions) {
			String entityId = session.getEntityId();
			if (!distribution.containsKey(entityId)) {
				distribution.put(entityId, new ArrayList<Session>());
			}
			distribution.get(entityId).add(session);
		}
		return distribution;
	}
	
	private ArrayList<HashMap<String, HashMap<String, Integer>>> findDistributionOfViolationsPerSessionStart() {
		ArrayList<HashMap<String, HashMap<String, Integer>>> distribution = new ArrayList<HashMap<String, HashMap<String, Integer>>>();
		for (Session session: sessions) {
			JSONObject resultSummary = session.getStartLog().getJSONObject("result_summary");
			//TODO
		}
		return distribution;
	}
	
}
