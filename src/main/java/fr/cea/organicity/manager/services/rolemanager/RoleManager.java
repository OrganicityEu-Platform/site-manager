package fr.cea.organicity.manager.services.rolemanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.cea.organicity.manager.config.environment.EnvService;
import fr.cea.organicity.manager.exceptions.local.NotFoundLocalException;
import fr.cea.organicity.manager.exceptions.token.RoleComputationTokenException;
import fr.cea.organicity.manager.security.SecurityConstants;

public class RoleManager {
	
	private final String clientId;
	
	private final RemoteRoleManager remotemanager;
	private final SiteRoleManager sitemanager;
	
	public RoleManager(RemoteRoleManager remotemanager, EnvService env, SiteRoleManager sitemanager) {
		this.remotemanager = remotemanager;
		this.clientId = env.getBackendSettings().getClientId();
		this.sitemanager = sitemanager;
	}
	
	public List<Role> getGlobalRoles() {
		List<Role> roles = new ArrayList<>();
		roles.add(SecurityConstants.ADMINISTRATOR_ROLE);
		roles.add(SecurityConstants.SITEMANAGER_ROLE);
		roles.add(SecurityConstants.SERVICEPROVIDER_ROLE);
		roles.add(SecurityConstants.PROVIDER_ROLE);
		roles.add(SecurityConstants.EXPERIMENTER_ROLE);
		roles.add(SecurityConstants.PARTICIPANT_ROLE);
		roles.add(SecurityConstants.OFFLINE_ROLE);
		return roles;
	}
		
	public List<Role> getRolesForSub(String userId) throws RoleComputationTokenException {
		List<Role> roles = new ArrayList<>();
		
		// remote roles
		List<RemoteRole> rRoles = remotemanager.getRolesForSub(userId);
		for (RemoteRole rRole : rRoles) {
			if (rRole.isRealmRole())
				roles.add(new Role(RoleScope.GLOBAL, rRole.getRoleName()));
			else if (rRole.getClientId().equals(clientId)) {
				roles.add(new Role(RoleScope.APP, rRole.getRoleName()));
			}
		}
		
		// local roles
		List<Role> lRoles = sitemanager.getRolesForSub(userId);
		roles.addAll(lRoles);
		
		return roles;
	}
	
	public boolean hasRole(String userId, Role role) throws RoleComputationTokenException {
		return getRolesForSub(userId).contains(role);
	}
	
	public boolean isAdmin(String userId) throws RoleComputationTokenException {
		return hasRole(userId, SecurityConstants.ADMINISTRATOR_ROLE);
	}
	
	public boolean isSiteManagerOrAdmin(String userId, String sitename) throws NotFoundLocalException, RoleComputationTokenException {
		
		// explicit sitemanager
		if (sitemanager.isSiteManager(userId, sitename))
			return true;
		
		// implicit site manager : admin
		if (isAdmin(userId))
			return true;
		
		return false;
	}
	
	public boolean isServiceManagerOrAdmin(String userId, String sitename, String servicename) throws NotFoundLocalException, RoleComputationTokenException {
		
		// Site manager or admin
		boolean sitemanageroradmin = isSiteManagerOrAdmin(userId, sitename);
		if (sitemanageroradmin)
			return true;
		
		// explicit service manager
		return sitemanager.isServiceManager(userId, sitename, servicename);
	}
	
	public void addRole(String userId, Role role) throws IOException {
		switch (role.getScope()) {
		case GLOBAL:
			remotemanager.addRole(userId, new RemoteRole(role.getName()));
			break;
		case APP:
			remotemanager.addRole(userId, new RemoteRole(clientId, role.getName()));
			break;
		case LOCAL:
			sitemanager.addRole(userId, role);
		}
	}
	
	public void removeRole(String userId, Role role) throws IOException {
		switch (role.getScope()) {
		case GLOBAL:
			remotemanager.removeRole(userId, new RemoteRole(role.getName()));
			break;
		case APP:
			remotemanager.removeRole(userId, new RemoteRole(clientId, role.getName()));
			break;
		case LOCAL:
			sitemanager.removeRole(userId, role);
		}
	}
	
	public RemoteRoleManager getRemotemanager() {
		return remotemanager;
	}
}
