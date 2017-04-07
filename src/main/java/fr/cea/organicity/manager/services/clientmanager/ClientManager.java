package fr.cea.organicity.manager.services.clientmanager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import fr.cea.organicity.manager.exceptions.remote.BadRequestRemoteException;
import fr.cea.organicity.manager.exceptions.remote.NotFoundRemoteException;
import lombok.Data;
import lombok.extern.log4j.Log4j;

@Log4j
public class ClientManager {

	private final RestTemplate restTemplate;
	
	private final String CREATE_URL = "https://accounts.organicity.eu/permissions/clients2";
	private final String GET_URL = "https://accounts.organicity.eu/permissions/clients2/{clientID}";
	
	private final Map<String, OCClient> clients = new HashMap<>();
	

	public ClientManager(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * Gets or create a client on the Account manager.
	 * 
	 * @param clientId
	 * @return 
	 * @return the client description
	 * @throws RuntimeException issue...
	 */
	public OCClient getOrCreateClient(String clientId) throws BadRequestRemoteException {
		try {
			return getClient(clientId);
		} catch (NotFoundRemoteException e1) {
			try {
				return createClient(clientId);
			} catch (BadRequestRemoteException e2) {
				throw new RuntimeException("Error while creating client" + clientId, e2);
			}
		}
	}
	
	/**
	 * Gets an already created client on the Account manager.
	 * 
	 * @param clientId
	 * @return the client description
	 * @throws NotFoundRemoteException Client does not exists
	 * @throws RuntimeException Other issues
	 */
	public OCClient getClient(String clientId) throws NotFoundRemoteException {
		
		// lookup
		if (clients.get(clientId) != null)
			return clients.get(clientId);
				
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<RequestContent> entity = new HttpEntity<>(headers);				
		ParameterizedTypeReference<OCClient> typeRef = new ParameterizedTypeReference<OCClient>() {};
		
		try {
			ResponseEntity<OCClient> response = restTemplate.exchange(GET_URL, HttpMethod.GET, entity, typeRef, clientId);
			OCClient client = response.getBody();
			log.info("FOUND client id in remote repository " + clientId);
			clients.put(clientId, client);
			return client;
		} catch (Exception e) {

			if (e instanceof ResourceAccessException) {
				ResourceAccessException resAccessExeption = (ResourceAccessException) e; 
				Throwable cause = resAccessExeption.getMostSpecificCause();				
				if (cause instanceof NotFoundRemoteException) {
					NotFoundRemoteException notFound = (NotFoundRemoteException) cause;
					HttpStatus status = notFound.getStatus();
					if (status == HttpStatus.NOT_FOUND) {
						throw new NotFoundRemoteException(GET_URL, clientId, OCClient.class);
					}
				}
			}
			
			throw new RuntimeException(e);
		}		
	}
	
	/**
	 * Creates a client on the Account manager.
	 * 
	 * @param clientId
	 * @return the client description
	 * @throws BadRequestRemoteException Client already exists
	 * @throws RuntimeException Other issues
	 */
	public OCClient createClient(String clientId) throws BadRequestRemoteException {
		RequestContent reqContent = new RequestContent(clientId);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<RequestContent> entity = new HttpEntity<>(reqContent, headers);				
		ParameterizedTypeReference<OCClient> typeRef = new ParameterizedTypeReference<OCClient>() {};
		
		try {
			ResponseEntity<OCClient> response = restTemplate.exchange(CREATE_URL, HttpMethod.POST, entity, typeRef);
			OCClient client = response.getBody();
			log.info("CREATED client id in remote repository " + clientId);
			clients.put(clientId, client);
			return client;
		} catch (Exception e) {
			
			if (e instanceof ResourceAccessException) {
				ResourceAccessException resAccessExeption = (ResourceAccessException) e; 
				Throwable cause = resAccessExeption.getMostSpecificCause();				
				if (cause instanceof BadRequestRemoteException) {
					BadRequestRemoteException badRequest = (BadRequestRemoteException) cause;
					HttpStatus status = badRequest.getStatus();
					if (status == HttpStatus.BAD_REQUEST) {
						throw new BadRequestRemoteException(CREATE_URL);
					}
				}
			}
			
			throw new RuntimeException(e);
		}
	}
	
	@Data
	private class RequestContent {	
		private final String clientId;
		private final List<String> roles = Arrays.asList("ocsite");
		public RequestContent(String clientId) {
			this.clientId = clientId;
		}
	}
}
