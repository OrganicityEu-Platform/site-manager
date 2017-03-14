package fr.cea.organicity.manager.controllers.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.domain.OCUnit;
import fr.cea.organicity.manager.exceptions.local.NotFoundLocalException;
import fr.cea.organicity.manager.repositories.OCUnitRepository;
import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/dictionary/units", produces = MediaType.APPLICATION_JSON_VALUE)
public class DicoUnitControllerAPI {

	@Autowired
	private OCUnitRepository unitrepository;
	
	@GetMapping
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public Iterable<OCUnit> getUnits(@RequestHeader(value = "Authorization", required = false) String auth) {	
		return unitrepository.findAll();		
	}
	
	@GetMapping("{unitName}")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public OCUnit getUnitByName(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("unitName") String unitName) throws NotFoundLocalException	 {
		String urn = OCUnit.computeUrn(unitName);
		OCUnit unit = unitrepository.findOne(urn);
		if (unit == null)
			throw new NotFoundLocalException(urn, OCUnit.class);
		return unit;
	}
}
