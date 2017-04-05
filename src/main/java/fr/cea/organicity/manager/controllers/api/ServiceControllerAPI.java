package fr.cea.organicity.manager.controllers.api;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.domain.OCService;
import fr.cea.organicity.manager.domain.OCSite;
import fr.cea.organicity.manager.exceptions.local.BadRequestLocalException;
import fr.cea.organicity.manager.exceptions.local.LocalException;
import fr.cea.organicity.manager.exceptions.local.MethodNotAllowedLocalException;
import fr.cea.organicity.manager.exceptions.local.NotFoundLocalException;
import fr.cea.organicity.manager.exceptions.local.ServerErrorLocalException;
import fr.cea.organicity.manager.repositories.OCServiceRepository;
import fr.cea.organicity.manager.repositories.OCSiteRepository;
import lombok.extern.log4j.Log4j;

@Log4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/sites/{siteName}/services", produces = MediaType.APPLICATION_JSON_VALUE)
public class ServiceControllerAPI {

	@Autowired private OCServiceRepository servicerepository;
	@Autowired private OCSiteRepository siterepository;
	
	@GetMapping
	// TODO @RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-user")
	public Collection<OCService> getServices(@PathVariable("siteName") String siteName) throws NotFoundLocalException {
		String urn = OCSite.computeUrn(siteName);
		OCSite site = siterepository.findOne(urn);
		if (site == null)
			throw new NotFoundLocalException(urn, OCSite.class);
		return site.getServices();
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	// TODO @RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-admin")
	public OCService createService(@PathVariable("siteName") String siteName, @RequestBody OCService service) throws NotFoundLocalException, MethodNotAllowedLocalException {
		String urn = OCSite.computeUrn(siteName);
		OCSite site = siterepository.findOne(urn);
		if (site == null)
			throw new NotFoundLocalException(urn, OCSite.class);		
		try {
			service.setSite(site);
			service = servicerepository.save(service);
			return service;
		} catch (Exception e) {
			log.error("can't persist " + service + " : " + e.getMessage());
			throw new MethodNotAllowedLocalException(urn);
		}
	}

	@DeleteMapping("{serviceName}")
	// TODO @RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-admin")
	public String deleteService(@PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName) throws NotFoundLocalException, ServerErrorLocalException {
		String urn = OCService.computeUrn(siteName, serviceName);
		OCService service = servicerepository.findOne(urn);
		if (service == null)
			throw new NotFoundLocalException(urn, OCService.class);

		try {
			// TODO JPA -> try to remove next line
			siterepository.findOne(OCSite.computeUrn(siteName)).getServices().remove(service);
			servicerepository.delete(urn);
			return null;
		} catch (EmptyResultDataAccessException e) {
			throw new NotFoundLocalException(serviceName, OCService.class);
		} catch (Exception e) {
			String message = "Unexpected error in server. Recieved exception " + e.toString();
			log.error(message);
			throw new ServerErrorLocalException(urn, e);
		}
	}
	
	@GetMapping("{serviceName}")
	// TODO @RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-user")
	public OCService getServiceByName(@PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName) throws NotFoundLocalException {
		String urn = OCService.computeUrn(siteName, serviceName);
		OCService service = servicerepository.findOne(urn);
		if (service == null)
			throw new NotFoundLocalException(urn, OCService.class);
		return service;
	}
	
	@PutMapping(value = "{serviceName}", consumes = MediaType.APPLICATION_JSON_VALUE)
	// TODO @RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-admin")
	public OCService updateService(@PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName, @RequestBody OCService service) throws LocalException {
		String urn = OCService.computeUrn(siteName, serviceName);
		OCService repoService = servicerepository.findOne(urn);
		if (repoService == null)
			throw new NotFoundLocalException(urn, OCService.class);
	
		// TODO remove this unsafe code (static list of fields...)
		repoService.setDescription(service.getDescription());
		repoService.setRelated(service.getRelated());
		
		try {
			return servicerepository.save(repoService);
		} catch (TransactionSystemException e) {
			// JPA error : usually a rule violation (@size, @email,...) 
			throw new BadRequestLocalException(urn);
		} catch (Exception e) {
			String message = "Unexpected error in server. Recieved exception " + e.toString();
			log.error(message);
			throw new ServerErrorLocalException(urn, e);
		}
	}
}
