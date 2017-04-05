package fr.cea.organicity.manager.controllers.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.domain.OCAssetType;
import fr.cea.organicity.manager.domain.OCUnregisteredAssetType;
import fr.cea.organicity.manager.exceptions.local.AlreadyExistsLocalException;
import fr.cea.organicity.manager.repositories.OCAssetTypeRepository;
import fr.cea.organicity.manager.repositories.OCUnregisteredAssetTypeRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/dictionary/unregisteredassettype", produces = MediaType.APPLICATION_JSON_VALUE)
public class DicoUnregisteredAssetTypeControllerAPI {

	@Autowired private OCAssetTypeRepository assettyperepository;
	@Autowired private OCUnregisteredAssetTypeRepository unregisteredassettyperepository;
	
	@GetMapping
	@PreAuthorize("hasRole('APP:dictionary-user')")
	public Iterable<OCUnregisteredAssetType> getUnregisteredAssetTypes() {
		return unregisteredassettyperepository.findAll();
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('APP:dictionary-admin')")
	public OCUnregisteredAssetType createUnregisteredAssetType(@RequestBody OCUnregisteredAssetType newAssetType) throws AlreadyExistsLocalException {
		String urn = OCAssetType.computeUrn(newAssetType.getName());
		OCAssetType existing = assettyperepository.findOne(urn);
		OCUnregisteredAssetType nonExisting = unregisteredassettyperepository.findOne(urn);
		
		// already known in the data types repository. 
		// Does NOT make sense to add it as an unknown data type!
		if (existing != null) 
			throw new AlreadyExistsLocalException(urn);
		
		// already in the list of unregistered data types 
		if (nonExisting != null)
			return nonExisting;

		return unregisteredassettyperepository.save(newAssetType);
	}
}
