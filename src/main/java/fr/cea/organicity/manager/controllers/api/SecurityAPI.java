package fr.cea.organicity.manager.controllers.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.domain.OCSecurityInfo;
import fr.cea.organicity.manager.domain.OCService;
import fr.cea.organicity.manager.domain.OCSite;
import fr.cea.organicity.manager.exceptions.local.LocalException;
import fr.cea.organicity.manager.exceptions.local.NotFoundLocalException;
import fr.cea.organicity.manager.exceptions.remote.BadRequestRemoteException;
import fr.cea.organicity.manager.exceptions.token.RoleComputationTokenException;
import fr.cea.organicity.manager.repositories.OCServiceRepository;
import fr.cea.organicity.manager.repositories.OCSiteRepository;
import fr.cea.organicity.manager.services.clientmanager.ClientManager;
import fr.cea.organicity.manager.services.clientmanager.OCClient;
import fr.cea.organicity.manager.services.rolemanager.RoleManager;
import fr.cea.organicity.manager.services.rolemanager.SiteRoleManager;
import fr.cea.organicity.manager.services.userlister.User;
import fr.cea.organicity.manager.services.userlister.UserLister;
import lombok.Data;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/security", produces = MediaType.APPLICATION_JSON_VALUE)
public class SecurityAPI {

	@Autowired private OCSiteRepository siterepository;
	@Autowired private OCServiceRepository servicerepository;
	@Autowired private ClientManager clientManager;
	@Autowired private UserLister userLister;
	@Autowired private RoleManager roleManager;
	
	@GetMapping("sites/{siteName}")
	@PreAuthorize("hasPermission(#siteName, 'sitemanager')")
	public OCSecurityInfo getSecuritySiteInfo(@PathVariable("siteName") String siteName) throws LocalException, BadRequestRemoteException {
		String urn = OCSite.computeUrn(siteName);
		OCSite site = siterepository.findOne(urn);
		if (site == null)
			throw new NotFoundLocalException(urn, OCSite.class);
		
		Set<String> managers = site.getManagers();
		OCClient client = clientManager.getOrCreateClient(site.getClientId());
		
		return new OCSecurityInfo(client, managers);
	}
	
	@GetMapping("sites/{siteName}/managers")
	@PreAuthorize("hasPermission(#siteName, 'sitemanager')")
	public Set<String> getSiteManagers(@PathVariable("siteName") String siteName) throws LocalException, BadRequestRemoteException {
		String urn = OCSite.computeUrn(siteName);
		OCSite site = siterepository.findOne(urn);
		if (site == null)
			throw new NotFoundLocalException(urn, OCSite.class);
		
		return site.getManagers();
	}
	
	@GetMapping("sites/{siteName}/services/{serviceName}")
	@PreAuthorize("hasPermission(#siteName + '/' + #serviceName, 'servicemanager')")
	public OCSecurityInfo getSecurityServiceInfo(@PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName) throws LocalException, BadRequestRemoteException {
		String urn = OCService.computeUrn(siteName, serviceName);
		OCService service = servicerepository.findOne(urn);
		if (service == null)
			throw new NotFoundLocalException(urn, OCService.class);
		
		Set<String> managers = service.getManagers();
		OCClient client = clientManager.getOrCreateClient(service.getClientId());
		
		return new OCSecurityInfo(client, managers);
	}
	
	@GetMapping("sites/{siteName}/services/{serviceName}/managers")
	@PreAuthorize("hasPermission(#siteName + '/' + #serviceName, 'servicemanager')")
	public Set<String> getServiceManagers(@PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName) throws LocalException, BadRequestRemoteException {
		String urn = OCService.computeUrn(siteName, serviceName);
		OCService service = servicerepository.findOne(urn);
		if (service == null)
			throw new NotFoundLocalException(urn, OCService.class);
		
		return service.getManagers();
	}
	
	@GetMapping("users/{userSub}")
	@PreAuthorize("hasRole('APP:role-admin')")
	public List<SiteRight> getPermissions(@PathVariable("userSub") String userSub) throws LocalException, RoleComputationTokenException {
		
		List<SiteRight> result = new ArrayList<>();
		
		// checks if the user exists
		if (!userLister.getUser(userSub).hasAlreadySucceed())
			throw new NotFoundLocalException(userSub, User.class);
		if (userLister.getUser(userSub).getLastSuccessResult() == null)
			throw new NotFoundLocalException(userSub, User.class);
		
		for (OCSite site : siterepository.findAll()) {
			String sitename = site.getName();
			boolean isAllowed = roleManager.isSiteManagerOrAdmin(userSub, sitename);
			result.add(new SiteRight(sitename, isAllowed));
		}
		
		return result;
	}
	
	@Data
	private class SiteRight {
		private final String site;
		private final boolean isallowed;
	}	
}