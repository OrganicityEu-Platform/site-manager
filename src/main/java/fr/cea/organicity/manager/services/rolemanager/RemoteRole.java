package fr.cea.organicity.manager.services.rolemanager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;

import lombok.EqualsAndHashCode;

/**
 * Role Object to be used with keycloak server. 
 * 
 * OF COURSE, having a single Sting named role is quite stupid, but it has been designed this way
 * to stay in line with OrganiCity keycloak implem and to simplify Jackson work.
 */
@EqualsAndHashCode
class RemoteRole {
	
	/**
	 * Role string as defined in the keycloak server.
	 */
	private String role;

	public RemoteRole() {
		role = null; // not yet initialized !
	}
	
	public RemoteRole(String role) {
		setRole(role);
	}
	
	public RemoteRole(String client, String name) {
		if (Strings.isNullOrEmpty(name))
			throw new IllegalArgumentException("name can't be null or empty");
		
		if (Strings.isNullOrEmpty(client))
			role = name;
		else
			role = client + ":" + name;
	}	
	
	public void setRole(String role) {
		if (Strings.isNullOrEmpty(role))
			throw new IllegalArgumentException("role can't be null or empty");
		
		if (role.startsWith(":") || role.endsWith(":"))
			throw new IllegalArgumentException("role can't start or end with ':'");
		
		this.role = role;
	}

	public String getRole() {
		checkInitialized();
		return role;
	}
	
	@JsonIgnore
	public String getClientId() {
		checkInitialized();
		if (role.contains(":"))
			return role.substring(0, role.indexOf(':'));
		else
			return "";
	}

	@JsonIgnore
	public String getRoleName() {
		checkInitialized();
		if (role.contains(":"))
			return role.substring(role.indexOf(':') + 1);
		else
			return role;
	}

	@JsonIgnore
	public Boolean isRealmRole() {
		checkInitialized();
		return getClientId().isEmpty();
	}
	
	@Override
	public String toString() {
		checkInitialized();
		return role;
	}
	
	private void checkInitialized() {
		if (role == null)
			throw new IllegalArgumentException("role has NOT been initialized");
	}
}
