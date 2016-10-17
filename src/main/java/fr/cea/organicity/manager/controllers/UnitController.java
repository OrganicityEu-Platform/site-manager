package fr.cea.organicity.manager.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.domain.OCUnit;
import fr.cea.organicity.manager.repositories.OCUnitRepository;
import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/dictionary/units", produces = MediaType.APPLICATION_JSON_VALUE)
public class UnitController {

	@Autowired
	private OCUnitRepository unitrepository;
	
	private static final Logger log = LoggerFactory.getLogger(UnitController.class);
	
	@RequestMapping(method = RequestMethod.GET)
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public ResponseEntity<Iterable<OCUnit>> getUnits(@RequestHeader(value = "Authorization", required = false) String auth) {	
		return new ResponseEntity<>(unitrepository.findAll(), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "{unitName}", method = RequestMethod.GET)
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public ResponseEntity<OCUnit> getUnitByName(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("unitName") String unitName) {
		try {
			OCUnit unit = unitrepository.findOne(OCUnit.computeUrn(unitName));
			if (unit == null)
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return new ResponseEntity<>(unit, HttpStatus.OK);
		} catch (Exception e) {
			String message = "Unexpected error in server. Recieved exception " + e.toString();
			log.error(message);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
