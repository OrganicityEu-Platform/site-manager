package fr.cea.organicity.manager.security;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import fr.cea.organicity.manager.config.environment.EnvService;
import fr.cea.organicity.manager.services.rolemanager.Role;

@Component
public class SecurityConfig {
	
	private final EnvService env;

	public final String clientName;
	
	// Local roles
	public final Role ROLE_ADMIN_ROLE;
	public final Role EXPERIMENT_USER_ROLE;
	public final Role METRICS_USER_ROLE;
	public final Role DICTIONARY_USER_ROLE;
	public final Role DICTIONARY_ADMIN_ROLE;
	public final Role DEVELOPER_ROLE;
	
	// Global roles
	public final Role ADMINISTRATOR_ROLE = new Role("administrator");
	public final Role SITEMANAGER_ROLE = new Role("site-manager");
	public final Role SERVICEPROVIDER_ROLE = new Role("service-provider");
	public final Role PROVIDER_ROLE = new Role("provider");
	public final Role EXPERIMENTER_ROLE = new Role("experimenter");
	public final Role PARTICIPANT_ROLE = new Role("participant");
	public final Role OFFLINE_ROLE = new Role("offline_access");
	
	private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
	
	@Autowired
	public SecurityConfig(EnvService env) {
		this.env = env;
		clientName = env.getBackendSettings().getClientId();
		ROLE_ADMIN_ROLE       = new Role(SecurityConstants.ROLE_ADMIN.replace(SecurityConstants.clientNameKey, clientName));
		EXPERIMENT_USER_ROLE  = new Role(SecurityConstants.EXPERIMENT_USER.replace(SecurityConstants.clientNameKey, clientName));
		METRICS_USER_ROLE     = new Role(SecurityConstants.METRICS_USER.replace(SecurityConstants.clientNameKey, clientName));
		DICTIONARY_USER_ROLE  = new Role(SecurityConstants.DICTIONARY_USER.replace(SecurityConstants.clientNameKey, clientName));
		DICTIONARY_ADMIN_ROLE = new Role(SecurityConstants.DICTIONARY_ADMIN.replace(SecurityConstants.clientNameKey, clientName));
		DEVELOPER_ROLE        = new Role(SecurityConstants.DEVELOPER.replace(SecurityConstants.clientNameKey, clientName));
	}
	
	public boolean isBackendSecured() {
		return env.getBackendSettings().isSecured();
	}
	
	public String getClientName() {
		return clientName;
	}
	
	public List<Role> getLocalRoles_TEMPORARY() {
		List<Role> roles = new ArrayList<>();
		roles.add(ADMINISTRATOR_ROLE);
		roles.add(SITEMANAGER_ROLE);
		roles.add(SERVICEPROVIDER_ROLE);
		roles.add(PROVIDER_ROLE);
		roles.add(EXPERIMENTER_ROLE);
		roles.add(PARTICIPANT_ROLE);
		roles.add(OFFLINE_ROLE);
		
		return roles;
	}
	
	public String getUrl(HttpServletRequest request) {
	
		String path = null;
		if (request != null)
			path = request.getRequestURI();
		return getUrl("/callback?path=" + path);
	}
	
	public String getUrl(String path) {
		
		String loginUrl = "https://accounts.organicity.eu/realms/organicity/protocol/openid-connect/auth";
		String responseType = "id_token+token";
		String redirectUri=null;
		try {
			String protocol = env.getServerSettings().getProtocol();
			String host = env.getServerSettings().getHost();
			int port = env.getServerSettings().getExposedPort();
			
			String url = null;
			if (protocol.equals("http") && port == 80)
				url = protocol + "://" + host;	
			else if (protocol.equals("https") && port == 443)
				url = protocol + "://" + host;
			else
				url = protocol + "://" + host + ":" + port;

			if (! Strings.isNullOrEmpty(path)) {
				if (path.startsWith("/"))
					url += path;
				else
					url += "/" + path;
			}
			redirectUri = URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			log.error("url encoding failed", e);
		}
		String url = loginUrl + "?response_type=" + responseType + "&client_id=" + clientName + "&redirect_uri=" + redirectUri;
		return url;
	}
}
