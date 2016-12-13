package fr.cea.organicity.manager.tagdomain;

import lombok.Data;

@Data
public class Service {
	private int id;
	private String urn;
	private String description;
	private String user;
}
