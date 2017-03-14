package fr.cea.organicity.manager.services.tagdomain;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import fr.cea.organicity.manager.exceptions.AppException;

public class TagDomainService {

	private final RestTemplate restTemplate;
	
	public static final String url = "https://annotations.organicity.eu/tagDomains";

	public TagDomainService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public List<TagDomain> retrieveTagDomains() throws AppException {
		ParameterizedTypeReference<List<TagDomain>> typeRef = new ParameterizedTypeReference<List<TagDomain>>() {};
		ResponseEntity<List<TagDomain>> response = restTemplate.exchange(url, HttpMethod.GET, null, typeRef);
		return response.getBody();
	}

	public TagDomain retrieveTagDomains(int id) throws AppException {
		for (TagDomain tagDomain : retrieveTagDomains())
			if (tagDomain.getId() == id)
				return tagDomain;
		return null;
	}
}
