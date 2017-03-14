package fr.cea.organicity.manager.services.tagdomain;

import java.util.List;

import lombok.Data;

@Data
public class TagDomain {
	private int id;
	private String urn;
	private String description;
	private List<Tag> tags;
	private List<Service> services;
}
