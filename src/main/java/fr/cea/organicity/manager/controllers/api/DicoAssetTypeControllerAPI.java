package fr.cea.organicity.manager.controllers.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.domain.OCAssetType;
import fr.cea.organicity.manager.exceptions.local.NotFoundLocalException;
import fr.cea.organicity.manager.repositories.OCAssetTypeRepository;
import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/dictionary/assettypes", produces = MediaType.APPLICATION_JSON_VALUE)
public class DicoAssetTypeControllerAPI {

	@Autowired
	private OCAssetTypeRepository assettyperepository;
	
	@GetMapping
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public Iterable<OCAssetType> getAssetTypes(@RequestHeader(value = "Authorization", required = false) String auth) {
		return assettyperepository.findAll();
	}
	
	@GetMapping("{typeName}")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public OCAssetType getAssetTypeByName(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("typeName") String typeName) throws NotFoundLocalException {
		String urn = OCAssetType.computeUrn(typeName);
		OCAssetType assetType = assettyperepository.findOne(urn);
		if (assetType == null)
			throw new NotFoundLocalException(urn, OCAssetType.class);
		return assetType;
	}
}
