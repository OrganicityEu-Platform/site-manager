package fr.cea.organicity.manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.domain.OCTool;
import fr.cea.organicity.manager.repositories.OCToolRepository;
import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/dictionary/tools", produces = MediaType.APPLICATION_JSON_VALUE)
public class ToolController {

	@Autowired
	private OCToolRepository toolrepository;
		
	@RequestMapping(method = RequestMethod.GET)
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public ResponseEntity<Iterable<OCTool>> getTools(@RequestHeader(value = "Authorization", required = false) String auth) {
		return new ResponseEntity<>(toolrepository.findAll(), HttpStatus.OK);		
	}
}
