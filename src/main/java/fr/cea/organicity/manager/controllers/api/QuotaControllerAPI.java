package fr.cea.organicity.manager.controllers.api;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

@CrossOrigin(origins = "*")
@RestController
public class QuotaControllerAPI {

	public static final long DEFAULT_QUOTA = 1000;
	
	@PreAuthorize("hasRole('APP:experiment-user')")
	@GetMapping(value = "/v1/quota", produces = MediaType.APPLICATION_JSON_VALUE)
	public Quota getQuota() {
		return new Quota(DEFAULT_QUOTA);
	}
	
	@Data
	class Quota {
		private final long quota; 
	}
}

