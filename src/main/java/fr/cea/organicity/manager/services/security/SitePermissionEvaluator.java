package fr.cea.organicity.manager.services.security;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import fr.cea.organicity.manager.security.Identity;
import fr.cea.organicity.manager.services.rolemanager.RoleManager;
import lombok.extern.log4j.Log4j;

@Log4j
@Component
public class SitePermissionEvaluator implements PermissionEvaluator {

	@Autowired RoleManager rolemanager;
	
	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		try {
			Identity identity = (Identity) authentication.getPrincipal();
			String sub = identity.getSub();
			String target = (String) targetDomainObject;
			String perm = (String) permission;
			
			if ("sitemanager".equals(perm)) {
				return rolemanager.isSiteManagerOrAdmin(sub, target);	
			}

			if ("servicemanager".equals(perm)) {
				// no need to check pointers : exception will be catched :-)
				String sitename = target.substring(0, target.indexOf('/'));
				String servicename = target.substring(target.indexOf('/') + 1);				
				return rolemanager.isServiceManagerOrAdmin(sub, sitename, servicename);
			}
		
			log.warn("Unknown perm object " + perm);
			return false;			
		} catch (Exception e) {
			return false; 
		}
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		throw new RuntimeException("Not used");
	}
}
