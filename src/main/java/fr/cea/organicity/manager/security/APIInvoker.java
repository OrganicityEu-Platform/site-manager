package fr.cea.organicity.manager.security;

import java.io.IOException;
import java.util.Date;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.cea.organicity.manager.domain.OCApiCall;
import fr.cea.organicity.manager.repositories.OCApiCallRepository;

public class APIInvoker {
	
	private final OCApiCallRepository repository;
	
	private static final Logger log = LoggerFactory.getLogger(APIInvoker.class);
	
	public APIInvoker(OCApiCallRepository repository) {
		this.repository = repository;
	}
	
	public Response invoke(Invocation invocation, String url) throws IOException {
		return invoke(invocation, url, true);
	}
	
	public Response invoke(Invocation invocation, String url, boolean persist) throws IOException {
		long start = System.currentTimeMillis(); 
		
		// TODO dans le log, mettre si ça a marché ou non...
		Response res = null;
		long duration = 0;
		boolean success;
		try {
			res = invocation.invoke();
			duration = System.currentTimeMillis() - start;
			log.debug("[API CALL] SUCCESS " + duration + "ms for " + url);
			success = true;
		} catch (Exception e) {
			log.error("[API CALL] FAILURE for " + url, e);
			success = false;
		}
		
		OCApiCall call = new OCApiCall();
		call.setDate(new Date());
		call.setDuration(duration);
		call.setUrl(url);
		call.setSuccess(success);
		
		if (persist)
			repository.save(call);
		
		if (success)
			return res;
		else
			throw new IOException("Error when calling " + url);
	}

	public Response defaultGet(HttpClient client, String url, boolean persist) throws IOException {
		WebTarget target = client.buildTarget(url);
		Builder builder = client.buildHeaders(target);
		Invocation invocation = builder.buildGet();
		return invoke(invocation, url, persist);
	}
	
	public Response defaultGet(HttpClient client, String url) throws IOException {
		return defaultGet(client, url, true);
	}
}
