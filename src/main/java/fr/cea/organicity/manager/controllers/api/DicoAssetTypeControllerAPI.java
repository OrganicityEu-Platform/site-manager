package fr.cea.organicity.manager.controllers.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.domain.OCAssetType;
import fr.cea.organicity.manager.exceptions.local.NotFoundLocalException;
import fr.cea.organicity.manager.repositories.OCAssetTypeRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/dictionary/assettypes", produces = MediaType.APPLICATION_JSON_VALUE)
public class DicoAssetTypeControllerAPI {

	@Autowired
	private OCAssetTypeRepository assettyperepository;
	
	@GetMapping
	@PreAuthorize("hasRole('APP:dictionary-user')")
	public Iterable<OCAssetType> getAssetTypes() {
		return assettyperepository.findAll();
	}
	
	@GetMapping("{typeName}")
	@PreAuthorize("hasRole('APP:dictionary-user')")
	public OCAssetType getAssetTypeByName(@PathVariable("typeName") String typeName) throws NotFoundLocalException {
		String urn = OCAssetType.computeUrn(typeName);
		OCAssetType assetType = assettyperepository.findOne(urn);
		if (assetType == null)
			throw new NotFoundLocalException(urn, OCAssetType.class);
		return assetType;
	}
}
