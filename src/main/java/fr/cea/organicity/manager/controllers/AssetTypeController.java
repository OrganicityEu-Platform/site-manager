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

import fr.cea.organicity.manager.domain.OCAssetType;
import fr.cea.organicity.manager.repositories.OCAssetTypeRepository;
import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/dictionary/assettypes", produces = MediaType.APPLICATION_JSON_VALUE)
public class AssetTypeController {

	@Autowired
	private OCAssetTypeRepository assettyperepository;
	
	private static final Logger log = LoggerFactory.getLogger(AssetTypeController.class);
	
	@RequestMapping(method = RequestMethod.GET)
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public ResponseEntity<Iterable<OCAssetType>> getAssetTypes(@RequestHeader(value = "Authorization", required = false) String auth) {
		return new ResponseEntity<Iterable<OCAssetType>>(assettyperepository.findAll(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "{typeName}", method = RequestMethod.GET)
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public ResponseEntity<OCAssetType> getAssetTypeByName(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("typeName") String typeName) {
		try {
			OCAssetType assetType = assettyperepository.findOne(OCAssetType.computeUrn(typeName));
			if (assetType == null)
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return new ResponseEntity<>(assetType, HttpStatus.OK);
		} catch (Exception e) {
			String message = "Unexpected error in server. Recieved exception " + e.toString();
			log.error(message);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
