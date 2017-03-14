package fr.cea.organicity.manager.controllers.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;
import lombok.Data;

@CrossOrigin(origins = "*")
@RestController
public class QuotaControllerAPI {

	@RoleGuard(roleName=SecurityConstants.EXPERIMENT_USER)
	@GetMapping(value = "/v1/quota", produces = MediaType.APPLICATION_JSON_VALUE)
	public Quota getQuota(@RequestHeader(value = "Authorization", required = false) String auth) {
		return new Quota(1000);
	}
	
	@Data
	class Quota {
		private final long quota; 
	}
}

