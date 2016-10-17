package fr.cea.organicity.manager.security;

import org.json.JSONObject;

import com.google.common.base.Strings;

import lombok.Data;

@Data
public class Role {

	private final static String JSON_KEY = "role";

	private final String clientId;
	private final String roleName;

	public Role(String client, String name) {
		this.clientId = Strings.nullToEmpty(client);
		this.roleName = name;
		if (Strings.isNullOrEmpty(name))
			throw new IllegalArgumentException("name can't be null or empty");
	}

	public Role(String roleString) {
		if (Strings.isNullOrEmpty(roleString))
			throw new IllegalArgumentException("role string can't be null or empty");
		if (roleString.contains(":")) {
			clientId = roleString.substring(0, roleString.indexOf(':'));
			roleName = roleString.substring(roleString.indexOf(':') + 1);
		} else {
			clientId = "";
			roleName = roleString;
		}
	}

	public Role(JSONObject json) {
		this(json.getString(JSON_KEY));
	}

	public Boolean isRealmRole() {
		return clientId.isEmpty();
	}

	public String getFullName() {
		if (isRealmRole())
			return roleName;
		return clientId + ":" + roleName;
	}
	
	public JSONObject toJsonObject() {
		JSONObject json = new JSONObject();
		json.put("role", getFullName());
		return json;
	}
}
