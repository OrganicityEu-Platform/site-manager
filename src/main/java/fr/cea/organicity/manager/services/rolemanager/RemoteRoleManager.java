package fr.cea.organicity.manager.services.rolemanager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
public class RemoteRoleManager {

	private final RestTemplate restTemplate;
	
	private final int CACHE_SIZE = 1000;
	private final int CACHE_EXPIRE_TIME_IN_SECONDS = 30;
	private final LoadingCache<String, List<RemoteRole>> cache;

	public RemoteRoleManager(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		this.cache = createCache(CACHE_SIZE, CACHE_EXPIRE_TIME_IN_SECONDS);
	}

	private LoadingCache<String, List<RemoteRole>> createCache(int cacheSize, int expireTimeInSeconds) {
		return CacheBuilder.newBuilder().maximumSize(cacheSize).expireAfterWrite(expireTimeInSeconds, TimeUnit.SECONDS)
				.build(new CacheLoader<String, List<RemoteRole>>() {
					public List<RemoteRole> load(String userId) throws IOException {
						return retrieveRoles(userId);
					}
				});
	}

	public List<RemoteRole> getRolesForSub(String userId) throws RoleComputationTokenException {
		try {
			return cache.get(userId);
		} catch (Exception e) {
			throw new RoleComputationTokenException(userId);
		}
	}	
	
	public void addRole(String userId, RemoteRole role) throws IOException {		

		// TODO update this to use the spring template way for replacing variables
		String url = SecurityConstants.permWithUserUrl.replace("{userId}", userId);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<RemoteRole> entity = new HttpEntity<>(role, headers);		
		
		try {
			restTemplate.postForObject(url, entity, String.class);
			cache.invalidate(userId);
		} catch (Exception e) {
			String message = "Error while adding role " + role.getRole() + " for user " + userId + " : " + e.getMessage();
			log.error(message);
			throw new RuntimeException(message);
		}
	}
	
	public void removeRole(String userId, RemoteRole role) throws IOException {

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
	
	private List<RemoteRole> retrieveRoles(String userId) throws IOException {

		// TODO update this to use the spring template way for replacing variables
		String url = SecurityConstants.permWithUserUrl.replace("{userId}", userId);

		try {
			ParameterizedTypeReference<List<RemoteRole>> typeRef = new ParameterizedTypeReference<List<RemoteRole>>() {};
			ResponseEntity<List<RemoteRole>> response = restTemplate.exchange(url, HttpMethod.GET, null, typeRef);
			List<RemoteRole> body = response.getBody();
			return body;
		} catch (Exception e) {
			String message = "Can't get roles for user " + userId + " using url " + url;
			log.error(message);
			throw new RuntimeException(message);
		}
	}
}
