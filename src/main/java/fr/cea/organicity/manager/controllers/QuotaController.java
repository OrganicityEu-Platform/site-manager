package fr.cea.organicity.manager.controllers;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;

@CrossOrigin(origins = "*")
@RestController
public class QuotaController {

	@RoleGuard(roleName=SecurityConstants.EXPERIMENT_USER)
	@RequestMapping(value = "/v1/quota", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getQuota(@RequestHeader(value = "Authorization", required = false) String auth) {
		JSONObject json = new JSONObject();
		json.put("quota", getQuota());	
		return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
	}
	
	private long getQuota() {
		return 1000;
	}
}

