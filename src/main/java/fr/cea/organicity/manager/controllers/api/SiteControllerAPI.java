package fr.cea.organicity.manager.controllers.api;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.domain.OCSite;
import fr.cea.organicity.manager.exceptions.local.BadRequestLocalException;
import fr.cea.organicity.manager.exceptions.local.LocalException;
import fr.cea.organicity.manager.exceptions.local.NotFoundLocalException;
import fr.cea.organicity.manager.exceptions.local.ServerErrorLocalException;
import fr.cea.organicity.manager.repositories.OCSiteRepository;
import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;
import lombok.extern.log4j.Log4j;

@Log4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/sites", produces = MediaType.APPLICATION_JSON_VALUE)
public class SiteControllerAPI {

	@Autowired
	private OCSiteRepository siterepository;

	@GetMapping
	@RoleGuard
	public String getSites(@RequestHeader(value = "Authorization", required = false) String auth) {
		
		JSONArray json = new JSONArray();
		for (OCSite site : siterepository.findAll()) {
			JSONObject s = new JSONObject();
			s.put("urn", site.getUrn());
			s.put("name", site.getName());
			json.put(s);
		}
		
		return json.toString();
	}

	/* NOT AVAILABLE FOR NOW
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@RoleGuard
	public ResponseEntity<OCSite> addSite(@RequestHeader(value = "Authorization", required = false) String auth, @RequestBody OCSite site) {
		try {
			site = siterepository.save(site);
			return new ResponseEntity<OCSite>(site, HttpStatus.OK);
		} catch (DataIntegrityViolationException e) {
			return new ResponseEntity<OCSite>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("can't persist " + site + " : " + e.getMessage());
			return new ResponseEntity<OCSite>(HttpStatus.METHOD_NOT_ALLOWED);
		}
	}
	*/

	/* NOT AVAILABLE FOR NOW
	@RequestMapping(value = "{siteName}", method = RequestMethod.DELETE)
	@RoleGuard(roleName=SecurityConstants.CLIENT + ":site-{siteName}-admin")
	public ResponseEntity<String> deleteSite(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("siteName") String siteName) {
		try {
			siterepository.delete(OCSite.computeUrn(siteName));
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (EmptyResultDataAccessException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			String message = "Unexpected error in server. Recieved exception " + e.toString();
			log.error(message);
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	*/

	@GetMapping("{siteName}")
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-user")
	public OCSite getSiteByName(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("siteName") String siteName) throws LocalException {
		String urn = OCSite.computeUrn(siteName);
		OCSite site = siterepository.findOne(urn);
		if (site == null)
			throw new NotFoundLocalException(urn, OCSite.class);
		return site;
	}

	/* INCREMENT QUOTA
	@GetMapping("{siteName}/quota/increment")
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-admin")
	public OCSite incrementRemainingQuota(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("siteName") String siteName) throws MethodNotAllowedException, NotFoundEntityException {
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		if (site == null)
			throw new NotFoundEntityException(siteName, OCSite.class);
		
		long remaining = site.getRemQuota();
		if (remaining >= site.getQuota())
			throw new MethodNotAllowedException();
		
		site.setRemQuota(remaining + 1);
		return siterepository.save(site);
	}
	*/
	
	/* DECREMENT QUOTA
	@GetMapping("{siteName}/quota/decrement")
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-admin")
	public OCSite decrementRemainingQuota(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("siteName") String siteName) throws NotFoundEntityException, MethodNotAllowedException {
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		if (site == null)
			throw new NotFoundEntityException(siteName, OCSite.class);
		
		long remaining = site.getRemQuota();
		if (remaining <= 0)
			throw new MethodNotAllowedException();
		
		site.setRemQuota(remaining - 1);
		return siterepository.save(site);
	}
	*/
	
	@PutMapping(value = "{siteName}", consumes = MediaType.APPLICATION_JSON_VALUE)
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-admin")
	public OCSite updateSite(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("siteName") String siteName, @RequestBody OCSite site) throws LocalException {
		String urn = OCSite.computeUrn(siteName);
		OCSite repoSite = siterepository.findOne(urn);
		if (repoSite == null)
			throw new NotFoundLocalException(urn, OCSite.class);
		try {
			// TODO remove this unsafe code (static list of fields...)
			repoSite.setEmail(site.getEmail());
			repoSite.setRelated(site.getRelated());
			return siterepository.save(repoSite);
		} catch (TransactionSystemException e) {
			// JPA error : usually a rule violation (@size, @email,...)
			throw new BadRequestLocalException(urn);
		} catch (Exception e) {
			String message = "Unexpected error in server. Recieved exception (" + e.getClass().getCanonicalName() + ") " + e.toString();
			log.error(message);
			throw new ServerErrorLocalException(urn, e);
		}
	}
}
