package fr.cea.organicity.manager.services.tokenmanager;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import fr.cea.organicity.manager.security.SecurityConstants;
import fr.cea.organicity.manager.services.rolemanager.ClaimsParser;
import fr.cea.organicity.manager.services.rolemanager.OCClaims;
import lombok.extern.log4j.Log4j;

@Log4j
public class TokenManager {

	private final PublicKey pk;
	private final Credentials credentials;	
	private final RestTemplate restTemplate;
	
	private String authToken = null;
	
	/** The token must be valid now and during this period in seconds to be considered up to date */ 
	private final long SECURITY_PERIOD = 30;
	
	public TokenManager(PublicKey pk, Credentials credentials) {
		this.pk = pk;
		this.credentials = credentials;
		this.restTemplate = getRestTemplateWithConverters();
	}
	
	private static RestTemplate getRestTemplateWithConverters() {
		RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(new MappingJackson2HttpMessageConverter());    
		messageConverters.add(new FormHttpMessageConverter());
		restTemplate.setMessageConverters(messageConverters);
		return restTemplate;
	}
	
	public String getAuthToken(boolean forceRenew) {
		if (forceRenew || !isUpToDateToken())
			authToken = renewAuthToken(credentials);
		return authToken;
	}
	
	public String getAuthToken() {
		return getAuthToken(false);
	}
	
	private boolean isUpToDateToken() {
		
		if (authToken == null)
			return false;
		
		// Extract claims
		OCClaims claim = null;
		try {
			claim = new ClaimsParser(pk).getClaimsFromIdToken(authToken);
		} catch (Exception e) {
			// token expired
			return false;
		}

		if (claim.getExpiresAt() != null && claim.stillValidDuringInSeconds() < SECURITY_PERIOD)
			return false;

		return true;
	}
	
	private String renewAuthToken(Credentials credentials) {
		String url = SecurityConstants.connectUrl;
		
		// Header
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + credentials.getAuthString());
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		// Form
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("grant_type", "client_credentials");		
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		
		String accessToken;
		try {
			ResponseEntity<Token> response = restTemplate.postForEntity(url, request , Token.class);
			if (response.getStatusCode() == HttpStatus.OK)
				accessToken = response.getBody().getAccess_token();
			else {
				accessToken = null;
				log.error("Token request answered HTTP " + response.getStatusCodeValue() + " :" + response.getBody());
			}
		} catch (HttpClientErrorException e) {
			accessToken = null;
			log.error("BAD token request : HTTP " + e.getStatusCode() + " error");
		}
		
		return accessToken;
	}
}
