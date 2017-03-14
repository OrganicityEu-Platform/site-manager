package fr.cea.organicity.manager.services.experimentlister;

import lombok.Data;

@Data
public class UDOExperimentContext {
	private String experimenter;
	private String experiment;
	private String name;
	private String reputation;
	private String scope;
	private String last_updated_at;
}
