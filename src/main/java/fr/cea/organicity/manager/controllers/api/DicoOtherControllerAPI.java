package fr.cea.organicity.manager.controllers.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.domain.OCAppType;
import fr.cea.organicity.manager.domain.OCTool;
import fr.cea.organicity.manager.domain.OCUserInterest;
import fr.cea.organicity.manager.exceptions.local.NotFoundLocalException;
import fr.cea.organicity.manager.repositories.OCAppTypeRepository;
import fr.cea.organicity.manager.repositories.OCToolRepository;
import fr.cea.organicity.manager.repositories.OCUserInterestRepository;
import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/dictionary/", produces = MediaType.APPLICATION_JSON_VALUE)
public class DicoOtherControllerAPI {

	@Autowired private OCAppTypeRepository apptyperepository;
	@Autowired private OCToolRepository toolrepository;
	@Autowired private OCUserInterestRepository userInterestRepository;
	
	@GetMapping("applicationtypes")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public Iterable<OCAppType> getAppTypes(@RequestHeader(value = "Authorization", required = false) String auth) {
		return apptyperepository.findAll();
	}

	@GetMapping("tools")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public Iterable<OCTool> getTools(@RequestHeader(value = "Authorization", required = false) String auth) {
		return toolrepository.findAll();		
	}
	
	@GetMapping("userinterests")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public Iterable<OCUserInterest> getUserInterests(@RequestHeader(value = "Authorization", required = false) String auth) {	
		return userInterestRepository.findAll();	
	}
	
	@GetMapping("userinterests/{unitName}")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public OCUserInterest getUserInterestByName(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("unitName") String unitName) throws NotFoundLocalException {
		String urn = OCUserInterest.computeUrn(unitName);
		OCUserInterest userInterest = userInterestRepository.findOne(urn);
		if (userInterest == null)
			throw new NotFoundLocalException(urn, OCUserInterest.class);
		return userInterest;
	}
}
