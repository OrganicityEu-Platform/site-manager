package fr.cea.organicity.manager.services.userlister;

import lombok.Data;

@Data
public class User {
	private final String id;
	private final String name;
	private final String firstName;
	private final String lastName;
	private final String email;
}
