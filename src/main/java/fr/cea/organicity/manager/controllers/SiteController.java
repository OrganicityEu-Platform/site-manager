package fr.cea.organicity.manager.controllers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import fr.cea.organicity.manager.domain.OCSite;
import fr.cea.organicity.manager.repositories.OCSiteRepository;
import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/sites", produces = MediaType.APPLICATION_JSON_VALUE)
public class SiteController {

	@Autowired
	private OCSiteRepository siterepository;

	private static final Logger log = LoggerFactory.getLogger(SiteController.class);

	@RequestMapping(method = RequestMethod.GET)
	@RoleGuard
	public ResponseEntity<String> getSites(@RequestHeader(value = "Authorization", required = false) String auth) {
		
		JSONArray json = new JSONArray();
		for (OCSite site : siterepository.findAll()) {
			JSONObject s = new JSONObject();
			s.put("urn", site.getUrn());
			s.put("name", site.getName());
			json.put(s);
		}
		
		return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
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

	@RequestMapping(value = "{siteName}", method = RequestMethod.GET)
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-user")
	public ResponseEntity<OCSite> getSiteByName(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("siteName") String siteName) {
		try {
			OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
			if (site == null)
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			else
				return new ResponseEntity<>(site, HttpStatus.OK);
		} catch (Exception e) {
			String message = "Unexpected error in server. Recieved exception (" + e.getClass().getCanonicalName() + ") " + e.toString();
			log.error(message);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "{siteName}/quota/increment", method = RequestMethod.GET)
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-admin")
	public ResponseEntity<OCSite> incrementRemainingQuota(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("siteName") String siteName) {
		try {
			OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
			if (site == null)
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			else {
				long remaining = site.getRemQuota();
				if (remaining >= site.getQuota())
					return new ResponseEntity<>(HttpStatus.FORBIDDEN);
				else {
					site.setRemQuota(remaining + 1);
					site = siterepository.save(site);
					return new ResponseEntity<>(site, HttpStatus.OK);					
				}
			}
		} catch (Exception e) {
			String message = "Unexpected error in server. Recieved exception (" + e.getClass().getCanonicalName() + ") " + e.toString();
			log.error(message);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "{siteName}/quota/decrement", method = RequestMethod.GET)
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-admin")
	public ResponseEntity<OCSite> decrementRemainingQuota(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("siteName") String siteName) {
		try {
			OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
			if (site == null)
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			else {
				long remaining = site.getRemQuota();
				if (remaining <= 0)
					return new ResponseEntity<>(HttpStatus.FORBIDDEN);
				else {
					site.setRemQuota(remaining - 1);
					site = siterepository.save(site);
					return new ResponseEntity<>(site, HttpStatus.OK);					
				}
			}
		} catch (Exception e) {
			String message = "Unexpected error in server. Recieved exception (" + e.getClass().getCanonicalName() + ") " + e.toString();
			log.error(message);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "{siteName}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-admin")
	public ResponseEntity<OCSite> updateSite(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable("siteName") String siteName, @RequestBody OCSite site) {
		try {
			OCSite repoSite = siterepository.findOne(OCSite.computeUrn(siteName));
			if (repoSite == null)
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);

			// TODO remove this unsafe code (static list of fields...)
			repoSite.setEmail(site.getEmail());
			repoSite.setRelated(site.getRelated());
				
			repoSite = siterepository.save(repoSite);
			
			return new ResponseEntity<>(repoSite, HttpStatus.OK);
		} catch (TransactionSystemException e) {
			// JPA error : usually a rule violation (@size, @email,...)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			String message = "Unexpected error in server. Recieved exception (" + e.getClass().getCanonicalName() + ") " + e.toString();
			log.error(message);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
