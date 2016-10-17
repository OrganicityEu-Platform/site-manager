package fr.cea.organicity.manager.controllers;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.domain.OCService;
import fr.cea.organicity.manager.domain.OCSite;
import fr.cea.organicity.manager.repositories.OCServiceRepository;
import fr.cea.organicity.manager.repositories.OCSiteRepository;
import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/sites/{siteName}/services", produces = MediaType.APPLICATION_JSON_VALUE)
public class ServiceController {

	@Autowired private OCServiceRepository servicerepository;
	@Autowired private OCSiteRepository siterepository;

	private static final Logger log = LoggerFactory.getLogger(ServiceController.class);

	@RequestMapping(method = RequestMethod.GET)
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-user")
	public ResponseEntity<Collection<OCService>> getServices(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("siteName") String siteName) {
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		if (site == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Collection<OCService>>(site.getServices(), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-admin")
	public ResponseEntity<OCService> addService(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("siteName") String siteName, @RequestBody OCService service) {
		try {
			OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));

			if (site == null)
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);

			// no need to persist the site since the collection of services
			// doesn't exists in database
			service.setSite(site);
			service = servicerepository.save(service);

			return new ResponseEntity<>(service, HttpStatus.OK);
		} catch (Exception e) {
			log.error("can't persist " + service + " : " + e.getMessage());
			return new ResponseEntity<OCService>(HttpStatus.METHOD_NOT_ALLOWED);
		}
	}

	@RequestMapping(value = "{serviceName}", method = RequestMethod.DELETE)
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-admin")
	public ResponseEntity<String> deleteService(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName) {
		String serviceUrn = OCService.computeUrn(siteName, serviceName);
		try {
			OCService service = servicerepository.findOne(serviceUrn);
			if (service == null)
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			
			// TODO JPA try to remove next line
			siterepository.findOne(OCSite.computeUrn(siteName)).getServices().remove(service);
			servicerepository.delete(serviceUrn);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (EmptyResultDataAccessException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			String message = "Unexpected error in server. Recieved exception " + e.toString();
			log.error(message);
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "{serviceName}", method = RequestMethod.GET)
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-user")
	public ResponseEntity<OCService> getServiceByName(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName) {
		try {
			OCService service = servicerepository.findOne(OCService.computeUrn(siteName, serviceName));
			if (service == null)
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return new ResponseEntity<>(service, HttpStatus.OK);
		} catch (Exception e) {
			String message = "Unexpected error in server. Recieved exception " + e.toString();
			log.error(message);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "{serviceName}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-admin")
	public ResponseEntity<OCService> updateService(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName, @RequestBody OCService service) {
		String serviceUrn = OCService.computeUrn(siteName, serviceName);
		try {
			OCService repoService = servicerepository.findOne(serviceUrn);
			if (repoService == null)
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			
			// TODO remove this unsafe code (static list of fields...)
			repoService.setDescription(service.getDescription());
			repoService.setRelated(service.getRelated());
			
			repoService = servicerepository.save(repoService);
			
			return new ResponseEntity<>(repoService, HttpStatus.OK);
		} catch (TransactionSystemException e) {
			// JPA error : usually a rule violation (@size, @email,...) 
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			String message = "Unexpected error in server. Recieved exception " + e.toString();
			log.error(message);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
