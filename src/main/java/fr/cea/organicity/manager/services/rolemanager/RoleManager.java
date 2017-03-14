package fr.cea.organicity.manager.services.rolemanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import fr.cea.organicity.manager.exceptions.token.RoleComputationTokenException;
import fr.cea.organicity.manager.security.SecurityConstants;
import lombok.extern.log4j.Log4j;

@Log4j
public class RoleManager {

	private final RestTemplate restTemplate;
	private final ClaimsParser claimsParser;
	
	private final int CACHE_SIZE = 1000;
	private final int CACHE_EXPIRE_TIME_IN_SECONDS = 30;
	private final LoadingCache<String, List<Role>> cache;

	public RoleManager(ClaimsParser claimsParser, RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		this.claimsParser = claimsParser;
		this.cache = createCache(CACHE_SIZE, CACHE_EXPIRE_TIME_IN_SECONDS);
	}

	private LoadingCache<String, List<Role>> createCache(int cacheSize, int expireTimeInSeconds) {
		return CacheBuilder.newBuilder().maximumSize(cacheSize).expireAfterWrite(expireTimeInSeconds, TimeUnit.SECONDS)
				.build(new CacheLoader<String, List<Role>>() {
					public List<Role> load(String userId) throws IOException {
						return retrieveRoles(userId);
					}
				});
	}

	public List<Role> getRolesForSub(String userId) throws RoleComputationTokenException {
		try {
			return cache.get(userId);
		} catch (Exception e) {
			throw new RoleComputationTokenException(userId);
		}
	}

	public List<Role> getRolesForRequest(HttpServletRequest request) {
		try {
			OCClaims claims = claimsParser.getClaimsFromRequest(request);
			String sub = claims.getSub();
			return getRolesForSub(sub);
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}
	
	public void addRole(String userId, Role role) throws IOException {		

		// TODO update this to use the spring template way for replacing variables
		String url = SecurityConstants.permWithUserUrl.replace("{userId}", userId);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Role> entity = new HttpEntity<>(role, headers);		
		
		try {
			restTemplate.postForObject(url, entity, String.class);
			cache.invalidate(userId);
		} catch (Exception e) {
			String message = "Error while adding role " + role.getRole() + " for user " + userId + " : " + e.getMessage();
			log.error(message);
			throw new RuntimeException(message);
		}
	}
	
	public void removeRole(String userId, Role role) throws IOException {

		// TODO update this to use the spring template way for replacing variables
		String url = SecurityConstants.permWithUserAndRoleUrl.replace("{userId}", userId);
		url = url.replace("{roleName}", role.getRole());

		try {
			ParameterizedTypeReference<String> typeRef = new ParameterizedTypeReference<String>() {};
			restTemplate.exchange(url, HttpMethod.DELETE, null, typeRef);
			cache.invalidate(userId);
		} catch (Exception e) {
			String message = "Error while removing role " + role.getRole() + " for user " + userId + " : " + e.getMessage();
			log.error(message);
			throw new RuntimeException(message);
		}
	}
	
	private List<Role> retrieveRoles(String userId) throws IOException {

		// TODO update this to use the spring template way for replacing variables
		String url = SecurityConstants.permWithUserUrl.replace("{userId}", userId);

		try {
			ParameterizedTypeReference<List<Role>> typeRef = new ParameterizedTypeReference<List<Role>>() {};
			ResponseEntity<List<Role>> response = restTemplate.exchange(url, HttpMethod.GET, null, typeRef);
			List<Role> body = response.getBody();
			return body;
		} catch (Exception e) {
			String message = "Can't get roles for user " + userId + " using url " + url;
			log.error(message);
			throw new RuntimeException(message);
		}
	}
}
