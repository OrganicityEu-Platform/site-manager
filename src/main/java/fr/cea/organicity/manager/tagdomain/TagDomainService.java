package fr.cea.organicity.manager.tagdomain;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.cea.organicity.manager.security.APIInvoker;
import fr.cea.organicity.manager.security.HttpClient;

public class TagDomainService {

	private final HttpClient client;
	private final APIInvoker invoker;
	
	String url = "https://annotations.organicity.eu/tagDomains";
	
	private static final Logger log = LoggerFactory.getLogger(TagDomainService.class);

	public TagDomainService(HttpClient client, APIInvoker invoker) {
		this.client = client;
		this.invoker = invoker;
	}

	public List<TagDomain> retrieveTagDomains() throws IOException {
		Response res = invoker.defaultGet(client, url, false);
		if (res.getStatusInfo().equals(Status.OK)) {
			try {
				return res.readEntity(new GenericType<List<TagDomain>>(){});
			} catch (Exception e) {
				throw new RuntimeException("Json parsing failed", e);
			}
		} else {
			log.error("[HTTP ERROR] Request on " + url + " returned " + res.getStatusInfo());
			throw new RuntimeException("Error while getting information from " + url);
		}
	}

	public TagDomain retrieveTagDomains(int id) throws IOException {
		for (TagDomain tagDomain : retrieveTagDomains())
			if (tagDomain.getId() == id)
				return tagDomain;
		return null;
	}
}
