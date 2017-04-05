package fr.cea.organicity.manager.services.rolemanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.jsonwebtoken.Claims;
import lombok.Data;

@Data
public class OCClaims {

	private final String sub;
	private final String name;
	private final String preferred_username;
	private final String given_name;
	private final String family_name;
	private final String email;
	private final Date issuedAt;
	private final Date expiresAt;
	private final List<RemoteRole> roles;

	public OCClaims(Claims claims, RemoteRoleManager roleManager) throws Exception {
		sub = claims.getSubject();
		name = (String) claims.get("name");
		preferred_username = (String) claims.get("preferred_username");
		given_name = (String) claims.get("given_name");
		family_name = (String) claims.get("family_name");
		email = (String) claims.get("email");
		issuedAt = claims.getIssuedAt();
		expiresAt = claims.getExpiration();
		roles = roleManager.getRolesForSub(sub); // extractRoles(claims);
	}

	private static List<RemoteRole> extractRoles(Claims claims) {
		List<RemoteRole> roles = new ArrayList<>();

		try {
			List<RemoteRole> r = extractResourcesRoles(claims);
			roles.addAll(r);
		} catch (Exception e) {
			// do nothing
		}

		try {
			List<RemoteRole> r = extractRealmRoles(claims);
			roles.addAll(r);
		} catch (Exception e) {
			// do nothing
		}

		return roles;
	}

	private static List<RemoteRole> extractResourcesRoles(Claims claims) {
		List<RemoteRole> roles = new ArrayList<>();

		HashMap<String, HashMap<String, List<String>>> p = claims.get("resource_access", HashMap.class);

		for (String appName : p.keySet()) {
			List<String> roleNames = p.get(appName).get("roles");
			for (String roleName : roleNames) {
				roles.add(new RemoteRole(appName, roleName));
			}
		}

		return roles;
	}

	private static List<RemoteRole> extractRealmRoles(Claims claims) {
		List<RemoteRole> roles = new ArrayList<>();
		
		HashMap<String, List<String>> p = claims.get("realm_access", HashMap.class);

		List<String> roleNames = p.get("roles");
		for (String roleName : roleNames) {
			roles.add(new RemoteRole(roleName));
		}

		return roles;
	}
}
