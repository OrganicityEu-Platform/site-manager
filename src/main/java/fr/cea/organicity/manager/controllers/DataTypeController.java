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

import fr.cea.organicity.manager.domain.OCDataType;
import fr.cea.organicity.manager.repositories.OCDataTypeRepository;
import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/dictionary/datatypes", produces = MediaType.APPLICATION_JSON_VALUE)
public class DataTypeController {

	@Autowired
	private OCDataTypeRepository datatyperepository;

	private static final Logger log = LoggerFactory.getLogger(DataTypeController.class);

	@RequestMapping(method = RequestMethod.GET)
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public ResponseEntity<Iterable<OCDataType>> getDataTypes(@RequestHeader(value = "Authorization", required = false) String auth) {
		return new ResponseEntity<>(datatyperepository.findAll(), HttpStatus.OK);
	}

	@RequestMapping(value = "{typeName}", method = RequestMethod.GET)
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public ResponseEntity<OCDataType> getDataTypeByName(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("typeName") String typeName) {
		try {
			OCDataType dataType = datatyperepository.findOne(OCDataType.computeUrn(typeName));
			if (dataType == null)
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return new ResponseEntity<>(dataType, HttpStatus.OK);
		} catch (Exception e) {
			String message = "Unexpected error in server. Recieved exception " + e.toString();
			log.error(message);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
