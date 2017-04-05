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

	@PreAuthorize("hasRole('APP:experiment-user')")
	@GetMapping(value = "/v1/quota", produces = MediaType.APPLICATION_JSON_VALUE)
	public Quota getQuota() {
		return new Quota(1000);
	}
	
	@Data
	class Quota {
		private final long quota; 
	}
}

