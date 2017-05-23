package fr.cea.organicity.manager.config;

import java.io.IOException;
import java.net.URI;
import java.util.Date;

import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import fr.cea.organicity.manager.domain.OCApiCall;
import fr.cea.organicity.manager.exceptions.remote.BadRequestRemoteException;
import fr.cea.organicity.manager.exceptions.remote.MethodNotAllowedRemoteException;
import fr.cea.organicity.manager.exceptions.remote.NotFoundRemoteException;
import fr.cea.organicity.manager.exceptions.remote.ServerErrorRemoteException;
import fr.cea.organicity.manager.exceptions.remote.UserNotAuthorizedRemoteException;
import fr.cea.organicity.manager.repositories.OCApiCallRepository;
import fr.cea.organicity.manager.security.SecurityConstants;
import fr.cea.organicity.manager.services.tokenmanager.TokenManager;
import lombok.Data;
import lombok.extern.log4j.Log4j;

@Log4j
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

	private final TokenManager tokenManager;
	private final OCApiCallRepository callRepository;
	
	public RestTemplateInterceptor(TokenManager tokenManager, OCApiCallRepository callRepository) {
		this.tokenManager = tokenManager;
		this.callRepository = callRepository;
	}
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		addAuthHeader(request);
		CallResult result = executeRequest(request, body, execution);
		logResult(result);
		if (needToPersist(result))
			persistResult(result);

		if (!result.isSuccess()) {
			String url = result.getUri().toString();
			int code = result.getHttpCode();
			
			if (code != 0) {
				switch (HttpStatus.valueOf(code)) {
				case BAD_REQUEST: throw new BadRequestRemoteException(url);
				case METHOD_NOT_ALLOWED: throw new MethodNotAllowedRemoteException(url);
				case NOT_FOUND: throw new NotFoundRemoteException(url, null, null);
				case UNAUTHORIZED: throw new UserNotAuthorizedRemoteException(url);
				default: throw new ServerErrorRemoteException(url);
				}
			}
			else {
				log.error("call to " + url + " returned 0. It may be a timeout.");
				throw new ServerErrorRemoteException(url);
			}
		}
		
        return result.getResponse();
	}

	private void addAuthHeader(HttpRequest request) {
		request.getHeaders().add("Authorization", "Bearer " + tokenManager.getAuthToken());
	}
	
	private boolean needToPersist(CallResult result) {
		String url = result.getUri().toString();
		return ! url.startsWith(SecurityConstants.usersUrl + "?");
	}
	
	private OCApiCall persistResult(CallResult result) {
		OCApiCall call = new OCApiCall();
		call.setDate(new Date());
		call.setDuration(result.duration);
		call.setUrl(result.getUri().toString());
		call.setSuccess(result.isSuccess());
		return callRepository.save(call);
	}

	private void logResult(CallResult result) {
		String status = result.isSuccess() ? "SUCCESS" : "FAILURE";
		log.debug("[API CALL] " + status + " " + result.duration + "ms for " + result.getUri() + " " + result.message);
	}

	private CallResult executeRequest(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
		ClientHttpResponse response = null;
		String message;
		int httpCode;
		boolean success;
		long duration;
		long start = System.currentTimeMillis();
		try {
			response = execution.execute(request, body);
			message = response.getStatusCode().toString();
			httpCode = response.getStatusCode().value();
			if (httpCode >= 200 && httpCode <= 299) {
				success = true;
			} else {
				success = false;
			}
		} catch (HttpClientErrorException e) {
			message = e.getMessage();
			httpCode = e.getStatusCode().value();
			success = false;
		} catch (RestClientException | IOException e) {
			message = e.getMessage();
			httpCode = 0;
			success = false;
		} 
		duration = System.currentTimeMillis() - start;
		
		return new CallResult(request.getURI(), response, message, httpCode, success, duration);
	}
	
	@Data
	class CallResult {
		private final URI uri;
		private final ClientHttpResponse response;
		private final String message;
		private final int httpCode;
		private final boolean success;
		private final long duration;
	}	
}
