package fr.cea.organicity.manager.controllers.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.domain.OCAppType;
import fr.cea.organicity.manager.domain.OCTool;
import fr.cea.organicity.manager.domain.OCUserInterest;
import fr.cea.organicity.manager.exceptions.local.NotFoundLocalException;
import fr.cea.organicity.manager.repositories.OCAppTypeRepository;
import fr.cea.organicity.manager.repositories.OCToolRepository;
import fr.cea.organicity.manager.repositories.OCUserInterestRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/dictionary/", produces = MediaType.APPLICATION_JSON_VALUE)
public class DicoOtherControllerAPI {

	@Autowired private OCAppTypeRepository apptyperepository;
	@Autowired private OCToolRepository toolrepository;
	@Autowired private OCUserInterestRepository userInterestRepository;
	
	@GetMapping("applicationtypes")
	@PreAuthorize("hasRole('APP:dictionary-user')")
	public Iterable<OCAppType> getAppTypes() {
		return apptyperepository.findAll();
	}

	@GetMapping("tools")
	@PreAuthorize("hasRole('APP:dictionary-user')")
	public Iterable<OCTool> getTools() {
		return toolrepository.findAll();		
	}
	
	@GetMapping("userinterests")
	@PreAuthorize("hasRole('APP:dictionary-user')")
	public Iterable<OCUserInterest> getUserInterests() {	
		return userInterestRepository.findAll();	
	}
	
	@GetMapping("userinterests/{unitName}")
	@PreAuthorize("hasRole('APP:dictionary-user')")
	public OCUserInterest getUserInterestByName(@PathVariable("unitName") String unitName) throws NotFoundLocalException {
		String urn = OCUserInterest.computeUrn(unitName);
		OCUserInterest userInterest = userInterestRepository.findOne(urn);
		if (userInterest == null)
			throw new NotFoundLocalException(urn, OCUserInterest.class);
		return userInterest;
	}
}
