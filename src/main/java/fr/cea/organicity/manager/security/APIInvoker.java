package fr.cea.organicity.manager.security;

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
	
	public Response invoke(Invocation invocation, String url) {
		return invoke(invocation, url, true);
	}
	
	public Response invoke(Invocation invocation, String url, boolean persist) {
		long start = System.currentTimeMillis(); 
		Response res = invocation.invoke();
		long duration = System.currentTimeMillis() - start;
		
		log.debug("[API CALL] " + duration + "ms for " + url);
		
		OCApiCall call = new OCApiCall();
		call.setDate(new Date());
		call.setDuration(duration);
		call.setUrl(url);
		
		if (persist)
			repository.save(call);
		
		return res;
	}

	public Response defaultGet(HttpClient client, String url, boolean persist) {
		WebTarget target = client.buildTarget(url);
		Builder builder = client.buildHeaders(target);
		Invocation invocation = builder.buildGet();
		return invoke(invocation, url, persist);
	}
	
	public Response defaultGet(HttpClient client, String url) {
		return defaultGet(client, url, true);
	}
}
