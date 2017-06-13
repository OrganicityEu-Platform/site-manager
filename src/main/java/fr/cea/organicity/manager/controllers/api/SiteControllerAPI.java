package fr.cea.organicity.manager.controllers.api;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.domain.OCSite;
import fr.cea.organicity.manager.exceptions.local.BadRequestLocalException;
import fr.cea.organicity.manager.exceptions.local.LocalException;
import fr.cea.organicity.manager.exceptions.local.MethodNotAllowedLocalException;
import fr.cea.organicity.manager.exceptions.local.NotFoundLocalException;
import fr.cea.organicity.manager.exceptions.local.ServerErrorLocalException;
import fr.cea.organicity.manager.repositories.OCSiteRepository;
import lombok.extern.log4j.Log4j;

@Log4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/sites", produces = MediaType.APPLICATION_JSON_VALUE)
public class SiteControllerAPI {

	@Autowired private OCSiteRepository siterepository;
	
	@GetMapping
	public String getSites() {
		
		JSONArray json = new JSONArray();
		for (OCSite site : siterepository.findAll()) {
			JSONObject s = new JSONObject();
			s.put("urn", site.getUrn());
			s.put("name", site.getName());
			json.put(s);
		}
		
		return json.toString();
	}

	@GetMapping("{siteName}")
	public OCSite getSiteByName(@PathVariable("siteName") String siteName) throws LocalException {
		String urn = OCSite.computeUrn(siteName);
		OCSite site = siterepository.findOne(urn);
		if (site == null)
			throw new NotFoundLocalException(urn, OCSite.class);
		return site;
	}

	@PutMapping(value = "{siteName}", consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasPermission(#siteName, 'sitemanager')")
	public OCSite updateSite(@PathVariable("siteName") String siteName, @RequestBody OCSite site) throws LocalException {
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
	
	@GetMapping("{siteName}/quota/increment")
	@PreAuthorize("hasPermission(#siteName, 'sitemanager')")
	public OCSite incrementRemainingQuota(@PathVariable("siteName") String siteName) throws NotFoundLocalException, MethodNotAllowedLocalException {
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		if (site == null)
			throw new NotFoundLocalException(siteName, OCSite.class);
		
		long remaining = site.getRemQuota();
		if (remaining >= site.getQuota())
			throw new MethodNotAllowedLocalException(OCSite.computeUrn(siteName));
		
		site.setRemQuota(remaining + 1);
		return siterepository.save(site);
	}
	
	@GetMapping("{siteName}/quota/decrement")
	@PreAuthorize("hasPermission(#siteName, 'sitemanager')")
	public OCSite decrementRemainingQuota(@PathVariable("siteName") String siteName) throws NotFoundLocalException, MethodNotAllowedLocalException {
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		if (site == null)
			throw new NotFoundLocalException(siteName, OCSite.class);
		
		long remaining = site.getRemQuota();
		if (remaining <= 0)
			throw new MethodNotAllowedLocalException(OCSite.computeUrn(siteName));
		
		site.setRemQuota(remaining - 1);
		return siterepository.save(site);
	}
}
