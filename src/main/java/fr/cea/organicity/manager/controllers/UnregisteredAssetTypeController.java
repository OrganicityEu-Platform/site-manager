package fr.cea.organicity.manager.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.domain.OCAssetType;
import fr.cea.organicity.manager.domain.OCUnregisteredAssetType;
import fr.cea.organicity.manager.repositories.OCUnregisteredAssetTypeRepository;
import fr.cea.organicity.manager.repositories.OCAssetTypeRepository;
import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/dictionary/unregisteredassettype", produces = MediaType.APPLICATION_JSON_VALUE)
public class UnregisteredAssetTypeController {

	@Autowired private OCAssetTypeRepository assettyperepository;
	@Autowired private OCUnregisteredAssetTypeRepository unregisteredassettyperepository;
	
	private static final Logger log = LoggerFactory.getLogger(UnregisteredAssetTypeController.class);
	
	@RequestMapping(method = RequestMethod.GET)
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public ResponseEntity<Iterable<OCUnregisteredAssetType>> getUnregisteredAssetTypes(@RequestHeader(value = "Authorization", required = false) String auth) {
		return new ResponseEntity<Iterable<OCUnregisteredAssetType>>(unregisteredassettyperepository.findAll(), HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public ResponseEntity<OCUnregisteredAssetType> addUnregisteredAssetType(@RequestHeader(value = "Authorization", required = false) String auth, @RequestBody OCUnregisteredAssetType newAssetType) {
		try {
			String urn = OCAssetType.computeUrn(newAssetType.getName());
			OCAssetType existing = assettyperepository.findOne(urn);
			OCUnregisteredAssetType nonExisting = unregisteredassettyperepository.findOne(urn);
			
			if (existing != null) {
				// already known in the data types repository. Does NOT make sense to add it as an unknown data type! 
				return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
			} else if (nonExisting != null) {
				// already in the list of unregistered data types 
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				newAssetType = unregisteredassettyperepository.save(newAssetType);
				return new ResponseEntity<>(newAssetType, HttpStatus.OK);	
			}
		} catch (Exception e) {
			log.error("can't persist " + newAssetType + " : " + e.getMessage());
			return new ResponseEntity<OCUnregisteredAssetType>(HttpStatus.METHOD_NOT_ALLOWED);
		}
	}
}
