package fr.cea.organicity.manager.controllers.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.domain.OCDataType;
import fr.cea.organicity.manager.exceptions.local.NotFoundLocalException;
import fr.cea.organicity.manager.repositories.OCDataTypeRepository;
import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/dictionary/datatypes", produces = MediaType.APPLICATION_JSON_VALUE)
public class DicoDataTypeControllerAPI {

	@Autowired
	private OCDataTypeRepository datatyperepository;

	@GetMapping
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public Iterable<OCDataType> getDataTypes(@RequestHeader(value = "Authorization", required = false) String auth) {
		return datatyperepository.findAll();
	}

	@GetMapping("{typeName}")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public OCDataType getDataTypeByName(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("typeName") String typeName) throws NotFoundLocalException {
		String urn = OCDataType.computeUrn(typeName);
		OCDataType dataType = datatyperepository.findOne(urn);
		if (dataType == null)
			throw new NotFoundLocalException(urn, OCDataType.class);
		return dataType;
	}
}
