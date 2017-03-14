package fr.cea.organicity.manager.services.userlister;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import fr.cea.organicity.manager.exceptions.AppException;
import fr.cea.organicity.manager.security.SecurityConstants;
import fr.cea.organicity.manager.services.ThirdPartyResult;

public class UserLister {

	private final RestTemplate restTemplate;
	private static final long AUTOREFRESH_DELAY_MILLIS = 60000;
	private static final int maxUsersPerRequest = 50;
	
	private ThirdPartyResult<List<User>> value = new ThirdPartyResult<List<User>>(null, 0, false, 0, 0);
	
	private static final Logger log = LoggerFactory.getLogger(UserLister.class);

	public UserLister(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public ThirdPartyResult<List<User>> getUsers() {
		return value;
	}

	public String getUserNameOrSub(String id) {
		User user = getUser(id).getLastSuccessResult();
		if (user == null)
			return id;
		else
			return user.getName();		
	}
	
	public ThirdPartyResult<User> getUser(String id) {
		// by this way, no need to synchronize because ThirdPartyResult is immutable
		ThirdPartyResult<List<User>> curVal = value; 
		
		// never succeed :-(
		if ( ! curVal.hasAlreadySucceed()) {
			return new ThirdPartyResult<User>(null, 0, false, curVal.getLastCallduration(), curVal.getLastCallTimestamp());
		} 
		
		// Already succeed :-)
		User user = curVal.getLastSuccessResult().stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
		return new ThirdPartyResult<User>(user, curVal.getLastSuccessTimestamp(), curVal.isLastCallSucess(), curVal.getLastCallduration(), curVal.getLastCallTimestamp());
	}
	
	@Scheduled(fixedDelay = AUTOREFRESH_DELAY_MILLIS)
	public void updateInfo() throws IOException {
		// by this way, no need to synchronize because ThirdPartyResult is immutable
		ThirdPartyResult<List<User>> curVal = value; 
		
		List<User> users = curVal == null ? null : curVal.getLastSuccessResult();
		long lastSuccessTimestamp = curVal == null ? 0 : curVal.getLastSuccessTimestamp();
		long start = System.currentTimeMillis();
		
		boolean lastCallSucess = true;
		try {
			users = retrieveUsers();
			lastSuccessTimestamp = start;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			lastCallSucess = false;
		}
		long duration = System.currentTimeMillis() - start;
		
		value = new ThirdPartyResult<List<User>>(users, lastSuccessTimestamp, lastCallSucess, duration, start);

		if (lastCallSucess)
			log.debug("[AUTOREFRESH] successful for users after " + duration + "ms");
		else
			log.error("[AUTOREFRESH] error for users after " + duration + "ms");
	}

	private List<User> retrieveUsers() throws AppException {
		List<User> users = new ArrayList<>();
		int offset = 0;
		int count = maxUsersPerRequest;

		while (true) {
			List<User> cur = retrieveUsers(offset, count);
			if (cur.isEmpty())
				return users;
			users.addAll(cur);
			offset += count;
		}
	}

	private List<User> retrieveUsers(int offset, int count) throws AppException {
		List<User> users = new ArrayList<>();

		String url = SecurityConstants.usersUrl + "?offset=" + offset + "&count=" + count;
		
		try {
			ParameterizedTypeReference<List<User>> typeRef = new ParameterizedTypeReference<List<User>>() {};
			ResponseEntity<List<User>> response = restTemplate.exchange(url, HttpMethod.GET, null, typeRef);
			users =  response.getBody();
		} catch(Exception e) {
			if (e instanceof AppException) {
				HttpStatus status = ((AppException) e).getStatus();
				if (status != HttpStatus.NOT_FOUND) {
					String message = "Request on " + url + " returned " + status;
					throw new RuntimeException(message);
				}			
			}
		}
		
		return users;
	}
}
