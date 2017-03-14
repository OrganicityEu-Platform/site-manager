package fr.cea.organicity.manager.services.experimentlister;

import lombok.Data;

@Data
public class UDOExperiment {
	private String id;
	private String type;
	private UDOExperimentContext context;
}
