package fr.cea.organicity.manager.controllers.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.domain.OCAttributeType;
import fr.cea.organicity.manager.exceptions.local.NotFoundLocalException;
import fr.cea.organicity.manager.repositories.OCAttributeTypeRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/dictionary/attributetypes", produces = MediaType.APPLICATION_JSON_VALUE)
public class DicoAttributeTypeControllerAPI {

	@Autowired
	private OCAttributeTypeRepository attributetyperepository;

	@GetMapping
	@PreAuthorize("hasRole('APP:dictionary-user')")
	public Iterable<OCAttributeType> getAttributeTypes() {
		return attributetyperepository.findAll();
	}

	@GetMapping("{typeName}")
	@PreAuthorize("hasRole('APP:dictionary-user')")
	public OCAttributeType getDataTypeByName( @PathVariable("typeName") String typeName) throws NotFoundLocalException {
		String urn = OCAttributeType.computeUrn(typeName);
		OCAttributeType attributeType = attributetyperepository.findOne(urn);
		if (attributeType == null)
			throw new NotFoundLocalException(urn, OCAttributeType.class);
		return attributeType;
	}
}
