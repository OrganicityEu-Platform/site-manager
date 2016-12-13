package fr.cea.organicity.manager.tagdomain;

import java.util.List;

import lombok.Data;

@Data
public class TagDomain {
	private int id;
	private String urn;
	private String description;
	
	private List<Service> services;
	private List<Tag> tags;
}
