package fr.cea.organicity.manager.security;

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
	
	/* ========= */
	/* IMPORTANT */
	/* ========= */
	/* clientNameKey is defined as a constant, OUTSIDE any component, to be used inside the */
	/* annotations.                                                                         */
	
	public final static String clientNameKey = "{clientName}";
	
	public final static String ROLE_ADMIN          = clientNameKey + ":role-admin";
	public final static String EXPERIMENT_USER     = clientNameKey + ":experiment-user";
	public final static String METRICS_USER        = clientNameKey + ":metrics";
	public final static String DICTIONARY_USER     = clientNameKey + ":dictionary-user";
	public final static String DICTIONARY_ADMIN    = clientNameKey + ":dictionary-admin";
	public final static String DEVELOPER           = clientNameKey + ":developer";
}
