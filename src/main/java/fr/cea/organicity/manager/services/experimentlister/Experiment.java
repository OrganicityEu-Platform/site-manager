package fr.cea.organicity.manager.services.experimentlister;

import java.util.List;

import lombok.Data;

@Data
public class Experiment {

	private String _id;
	private String experimentId;
	private String mainExperimenterId;
	private String name;
	private boolean assetsPublic;
	private long remQuota;
	private long quota;
	private String logo;
	private String description;
	private String status;
	private String endDate;
	private String startDate;
	private final String registered;
	private final List<String> experimenterIds;
	
	public boolean hasExperimenter(String userId) {
		return experimenterIds.contains(userId);
	}

}
