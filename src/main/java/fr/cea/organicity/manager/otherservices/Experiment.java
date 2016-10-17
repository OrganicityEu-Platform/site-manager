package fr.cea.organicity.manager.otherservices;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.Data;

@Data
public class Experiment {

	private final String id;
	private final String name;
	private final String description;
	private final String status;
	private final String registered;
	private final String startDate;
	private final String endDate;
	private final long remQuota;
	private final long quota;
	
	private final String mainExperimenter;
	private final List<String> experimenters;
	
	public boolean hasExperimenter(String userId) {
		return experimenters.contains(userId);
	}
	
	public Experiment(JSONObject jsonExperiment) {
		id = jsonExperiment.getString("experimentId");
		name = jsonExperiment.getString("name");
		description = jsonExperiment.getString("description");
		status = jsonExperiment.getString("status");				
		registered = jsonExperiment.getString("registered");
		startDate = jsonExperiment.getString("startDate");
		endDate = jsonExperiment.getString("endDate");
		
		quota = jsonExperiment.getLong("quota");
		remQuota = jsonExperiment.getLong("remQuota");	
		
		
		mainExperimenter = jsonExperiment.getString("mainExperimenterId");
		JSONArray array = jsonExperiment.getJSONArray("experimenterIds");
		experimenters = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			experimenters.add(array.getString(i));
		}
	}
}
