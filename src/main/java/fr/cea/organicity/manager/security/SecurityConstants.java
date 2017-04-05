package fr.cea.organicity.manager.security;

import fr.cea.organicity.manager.services.rolemanager.Role;
import fr.cea.organicity.manager.services.rolemanager.RoleScope;

public class SecurityConstants {
	
	public static final String rootUrl = "https://accounts.organicity.eu"; 

	// realm URL
	public final static String realmUrl = rootUrl + "/realms";
	public final static String connectUrl = realmUrl + "/organicity/protocol/openid-connect/token";
	
	// permission URL
	public final static String permUrl = rootUrl + "/permissions";
	public final static String usersUrl = permUrl + "/users";
	public final static String permWithUserUrl = usersUrl + "/{userId}/roles";
	public final static String permWithUserAndRoleUrl = usersUrl + "/{userId}/roles/{roleName}";

	/* ============ */
	/* GLOBAL ROLES */
	/* ============ */
	
	// Global roles names
	public final static String ADMINISTRATOR    = "administrator";
	public final static String SITEMANAGER      = "site-manager";
	public final static String SERVICEPROVIDER  = "service-provider";
	public final static String PROVIDER         = "provider";
	public final static String EXPERIMENTER     = "experimenter";
	public final static String PARTICIPANT      = "participant";
	public final static String OFFLINE          = "offline_access";	

	// Global roles
	public final static Role ADMINISTRATOR_ROLE   = new Role(RoleScope.GLOBAL, ADMINISTRATOR);
	public final static Role SITEMANAGER_ROLE     = new Role(RoleScope.GLOBAL, SITEMANAGER);
	public final static Role SERVICEPROVIDER_ROLE = new Role(RoleScope.GLOBAL, SERVICEPROVIDER);
	public final static Role PROVIDER_ROLE        = new Role(RoleScope.GLOBAL, PROVIDER);
	public final static Role EXPERIMENTER_ROLE    = new Role(RoleScope.GLOBAL, EXPERIMENTER);
	public final static Role PARTICIPANT_ROLE     = new Role(RoleScope.GLOBAL, PARTICIPANT);
	public final static Role OFFLINE_ROLE         = new Role(RoleScope.GLOBAL, OFFLINE);
	
	
	/* ========= */
	/* APP ROLES */
	/* ========= */
	
	// Local roles names
	public final static String ROLE_ADMIN       = "role-admin";
	public final static String EXPERIMENT_USER  = "experiment-user";
	public final static String METRICS_USER     = "metrics";
	public final static String DICTIONARY_USER  = "dictionary-user";
	public final static String DICTIONARY_ADMIN = "dictionary-admin";
	public final static String DEVELOPER        = "developer";
	public final static String USER_VIEWER      = "user-viewer";
	
	// local roles
	public final static Role ROLE_ADMIN_ROLE       = new Role(RoleScope.APP, ROLE_ADMIN);
	public final static Role EXPERIMENT_USER_ROLE  = new Role(RoleScope.APP, EXPERIMENT_USER);
	public final static Role METRICS_USER_ROLE     = new Role(RoleScope.APP, METRICS_USER);
	public final static Role DICTIONARY_USER_ROLE  = new Role(RoleScope.APP, DICTIONARY_USER);
	public final static Role DICTIONARY_ADMIN_ROLE = new Role(RoleScope.APP, DICTIONARY_ADMIN);
	public final static Role DEVELOPER_ROLE        = new Role(RoleScope.APP, DEVELOPER);
	public final static Role USER_VIEWER_ROLE      = new Role(RoleScope.APP, USER_VIEWER);	
}
