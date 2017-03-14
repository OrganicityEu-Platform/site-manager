package fr.cea.organicity.manager.services.tokenmanager;

import java.util.Base64;

import lombok.Data;

@Data
public class Credentials {

	private final String user;
	private final String password;
		
	public String getAuthString() {
		return Base64.getEncoder().encodeToString((user + ":" + password).getBytes()).toString();
	}
}
