package fr.cea.organicity.manager.services.security;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import fr.cea.organicity.manager.security.Identity;
import fr.cea.organicity.manager.services.rolemanager.RoleManager;

@Component
public class SitePermissionEvaluator implements PermissionEvaluator {

	@Autowired RoleManager rolemanager;
	
	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		try {
			Identity identity = (Identity) authentication.getPrincipal();
			String siteName = (String) targetDomainObject;
			return rolemanager.isSiteManagerOrAdmin(identity.getSub(), siteName);
		}catch (Exception e) {
			return false; 
		}
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		throw new RuntimeException("Not used");
	}
}
