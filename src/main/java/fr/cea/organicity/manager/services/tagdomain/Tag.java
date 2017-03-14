package fr.cea.organicity.manager.services.tagdomain;

import lombok.Data;

@Data
public class Tag {
	private int id;
	private String urn;
	private String name;
	private String user;
}
