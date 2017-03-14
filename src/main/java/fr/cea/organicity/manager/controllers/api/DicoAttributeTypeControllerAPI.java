package fr.cea.organicity.manager.controllers.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.domain.OCAttributeType;
import fr.cea.organicity.manager.exceptions.local.NotFoundLocalException;
import fr.cea.organicity.manager.repositories.OCAttributeTypeRepository;
import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/dictionary/attributetypes", produces = MediaType.APPLICATION_JSON_VALUE)
public class DicoAttributeTypeControllerAPI {

	@Autowired
	private OCAttributeTypeRepository attributetyperepository;

	@GetMapping
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public Iterable<OCAttributeType> getAttributeTypes(@RequestHeader(value = "Authorization", required = false) String auth) {
		return attributetyperepository.findAll();
	}

	@GetMapping("{typeName}")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public OCAttributeType getDataTypeByName(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("typeName") String typeName) throws NotFoundLocalException {
		String urn = OCAttributeType.computeUrn(typeName);
		OCAttributeType attributeType = attributetyperepository.findOne(urn);
		if (attributeType == null)
			throw new NotFoundLocalException(urn, OCAttributeType.class);
		return attributeType;
	}
}
