package fr.cea.organicity.manager.controllers.ui;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.cea.organicity.manager.domain.OCService;
import fr.cea.organicity.manager.domain.OCSite;
import fr.cea.organicity.manager.exceptions.local.NotFoundLocalException;
import fr.cea.organicity.manager.exceptions.remote.BadRequestRemoteException;
import fr.cea.organicity.manager.exceptions.token.RoleComputationTokenException;
import fr.cea.organicity.manager.repositories.OCServiceRepository;
import fr.cea.organicity.manager.repositories.OCSiteRepository;
import fr.cea.organicity.manager.security.Identity;
import fr.cea.organicity.manager.services.clientmanager.ClientManager;
import fr.cea.organicity.manager.services.rolemanager.RoleManager;
import fr.cea.organicity.manager.services.rolemanager.SiteRoleManager;
import fr.cea.organicity.manager.services.userlister.UserLister;

@Controller
@RequestMapping("/sites")
public class SitesControllerUI {
	
	@Autowired private RoleManager rolemanager;
	@Autowired private SiteRoleManager sitemanager;
	@Autowired private ClientManager clientmanager;
	@Autowired private UserLister userlister;
	
	@Autowired private OCSiteRepository siterepository;
	@Autowired private OCServiceRepository serviceRepository;

	private final String title = "Sites";
	
	@GetMapping
	public String sites(Model model) {
		model.addAttribute("title", title);
		model.addAttribute("elements", getSiterepository());
		return "thsites";
	}

	@GetMapping("{siteName}")
	public String siteGET(@AuthenticationPrincipal Identity identity, @PathVariable("siteName") String siteName, Model model) throws NotFoundLocalException, RoleComputationTokenException, BadRequestRemoteException {
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		boolean isManagerOrAdmin = rolemanager.isSiteManagerOrAdmin(identity.getSub(), siteName);
		boolean isAdmin = rolemanager.isAdmin(identity.getSub());	
		List<OCService> services = serviceRepository.findAll().stream().filter(s -> s.getSite().getName().equals(siteName)).collect(Collectors.toList());
		
		model.addAttribute("title", title);
		model.addAttribute("element", site);
		model.addAttribute("services", services);
		model.addAttribute("isManagerOrAdmin", isManagerOrAdmin);
		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("managers", sitemanager.getSiteManagers(siteName));
		model.addAttribute("nonmanagers", sitemanager.getNonSiteManagers(siteName));
		model.addAttribute("client", clientmanager.getOrCreateClient((site.getClientId())));

		return "thsitedetails";
	}

	@PostMapping("{siteName}")
	@PreAuthorize("hasPermission(#siteName, 'sitemanager')")
	public String sitePOST(@AuthenticationPrincipal Identity identity, HttpServletRequest request, @PathVariable("siteName") String siteName, Model model) throws NotFoundLocalException, RoleComputationTokenException, BadRequestRemoteException {	
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		boolean isManagerOrAdmin = rolemanager.isSiteManagerOrAdmin(identity.getSub(), siteName);
		boolean isAdmin = rolemanager.isAdmin(identity.getSub());
		
		String message = null;
		
		// basic info update
		if (request.getParameter("email") != null) {
			site.setEmail(request.getParameter("email"));
			site.setRelated(request.getParameter("related"));
				
			try {
				site = siterepository.saveAndFlush(site);
				message = "Site " + site.getName() + " updated";	
			} catch (Exception e) {
				site = siterepository.getOne(OCSite.computeUrn(siteName));
				message = "Site " + site.getName() + " NOT updated";
			}
		}

		// new service 
		else {
			OCService service = new OCService();
			service.setName(request.getParameter("name"));
			service.setDescription(request.getParameter("description"));
			service.setRelated(request.getParameter("related"));
			service.setSite(site);			
			
			try {
				service = serviceRepository.saveAndFlush(service);
				site.getServices().add(service);
				message = "Service " + service.getName() + " created";	
			} catch (Exception e) {
				message = "Service " + service.getName() + " NOT created";
			}
		}
		
		List<OCService> services = serviceRepository.findAll().stream().filter(s -> s.getSite().getName().equals(siteName)).collect(Collectors.toList());
		
		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("element", site);
		model.addAttribute("services", services);
		model.addAttribute("isManagerOrAdmin", isManagerOrAdmin);
		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("managers", sitemanager.getSiteManagers(siteName));
		model.addAttribute("nonmanagers", sitemanager.getNonSiteManagers(siteName));
		model.addAttribute("client", clientmanager.getOrCreateClient(site.getClientId()));
		
		return "thsitedetails";
	}
	
	@PostMapping("{siteName}/createmanager")
	@PreAuthorize("hasPermission(#siteName, 'sitemanager')")
	public String siteManagerPOST(@AuthenticationPrincipal Identity identity, HttpServletRequest request, @PathVariable("siteName") String siteName, Model model) throws NotFoundLocalException, RoleComputationTokenException, BadRequestRemoteException {	
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		boolean isManagerOrAdmin = rolemanager.isSiteManagerOrAdmin(identity.getSub(), siteName);
		boolean isAdmin = rolemanager.isAdmin(identity.getSub());	
		List<OCService> services = serviceRepository.findAll().stream().filter(s -> s.getSite().getName().equals(siteName)).collect(Collectors.toList());
		
		String sub = request.getParameter("sub");
		site.getManagers().add(sub);
		
		String message = null;
		try {
			site = siterepository.save(site);
			message = "Manager " + userlister.getUserNameOrSub(sub) + " added";	
		} catch (Exception e) {
			message = "Manager " + userlister.getUserNameOrSub(sub) + " has NOT been added";
		}
		
		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("element", site);
		model.addAttribute("services", services);
		model.addAttribute("isManagerOrAdmin", isManagerOrAdmin);
		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("managers", sitemanager.getSiteManagers(siteName));
		model.addAttribute("nonmanagers", sitemanager.getNonSiteManagers(siteName));
		model.addAttribute("client", clientmanager.getOrCreateClient((site.getClientId())));

		return "thsitedetails";		
	}
	
	@PostMapping("{siteName}/deletemanager")
	@PreAuthorize("hasPermission(#siteName, 'sitemanager')")
	public String siteManagersDELETE(@AuthenticationPrincipal Identity identity, HttpServletRequest request, @PathVariable("siteName") String siteName, Model model) throws NotFoundLocalException, RoleComputationTokenException, BadRequestRemoteException {	
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		boolean isManagerOrAdmin = rolemanager.isSiteManagerOrAdmin(identity.getSub(), siteName);
		boolean isAdmin = rolemanager.isAdmin(identity.getSub());	
		List<OCService> services = serviceRepository.findAll().stream().filter(s -> s.getSite().getName().equals(siteName)).collect(Collectors.toList());
		
		String sub = request.getParameter("sub");
		site.getManagers().remove(sub);
				
		String message = null;
		try {
			site = siterepository.save(site);
			message = "Manager " + userlister.getUserNameOrSub(sub) + " removed";	
		} catch (Exception e) {
			message = "Manager " + userlister.getUserNameOrSub(sub) + " has NOT been removed";
		}

		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("element", site);
		model.addAttribute("services", services);
		model.addAttribute("isManagerOrAdmin", isManagerOrAdmin);
		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("managers", sitemanager.getSiteManagers(siteName));
		model.addAttribute("nonmanagers", sitemanager.getNonSiteManagers(siteName));
		model.addAttribute("client", clientmanager.getOrCreateClient((site.getClientId())));

		return "thsitedetails";
	}
	
	@RequestMapping("{siteName}/{serviceName}")
	public String serviceGET(@AuthenticationPrincipal Identity identity, @PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName, Model model) throws NotFoundLocalException, RoleComputationTokenException, BadRequestRemoteException {
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		OCService service = serviceRepository.getOne(OCService.computeUrn(siteName, serviceName));
		boolean isManagerOrAdmin = rolemanager.isServiceManagerOrAdmin(identity.getSub(), siteName, serviceName);
		
		model.addAttribute("title", title);
		model.addAttribute("element", service);
		model.addAttribute("site", site);
		model.addAttribute("isManagerOrAdmin", isManagerOrAdmin);
		model.addAttribute("managers", sitemanager.getServiceManagers(siteName, serviceName));
		model.addAttribute("nonmanagers", sitemanager.getNonServiceManagers(siteName, serviceName));
		model.addAttribute("client", clientmanager.getOrCreateClient(service.getClientId()));
		
		return "thsiteservicedetails";
	}
	
	@PostMapping("{siteName}/{serviceName}")
	@PreAuthorize("hasPermission(#siteName + '/' + #serviceName, 'servicemanager')")
	public String servicePOST(@AuthenticationPrincipal Identity identity, HttpServletRequest request, @PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName, Model model) throws NotFoundLocalException, RoleComputationTokenException, BadRequestRemoteException {	
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		OCService service = serviceRepository.getOne(OCService.computeUrn(siteName, serviceName));
		boolean isManagerOrAdmin = rolemanager.isServiceManagerOrAdmin(identity.getSub(), siteName, serviceName);
		
		service.setDescription(request.getParameter("description"));
		service.setRelated(request.getParameter("related"));

		String message = null;
		try {
			site = siterepository.saveAndFlush(site);
			message = "Service " + service.getName() + " updated";	
		} catch (Exception e) {
			message = "Service " + service.getName() + " NOT updated";
		}
			
		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("element", service);
		model.addAttribute("site", site);
		model.addAttribute("isManagerOrAdmin", isManagerOrAdmin);
		model.addAttribute("managers", sitemanager.getServiceManagers(siteName, serviceName));
		model.addAttribute("nonmanagers", sitemanager.getNonServiceManagers(siteName, serviceName));
		model.addAttribute("client", clientmanager.getOrCreateClient(service.getClientId()));
		
		return "thsiteservicedetails";
	}
		
	@PostMapping("{siteName}/{serviceName}/createmanager")
	@PreAuthorize("hasPermission(#siteName + '/' + #serviceName, 'servicemanager')")
	public String serviceManagersPOST(@AuthenticationPrincipal Identity identity, HttpServletRequest request, @PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName, Model model) throws NotFoundLocalException, RoleComputationTokenException, BadRequestRemoteException {	
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		OCService service = serviceRepository.getOne(OCService.computeUrn(siteName, serviceName));
		boolean isManagerOrAdmin = rolemanager.isServiceManagerOrAdmin(identity.getSub(), siteName, serviceName);

		String sub = request.getParameter("sub");
		service.getManagers().add(sub);
		
		String message = null;
		try {
			site = siterepository.save(site);
			message = "Manager " + userlister.getUserNameOrSub(sub) + " added";	
		} catch (Exception e) {
			message = "Manager " + userlister.getUserNameOrSub(sub) + " has NOT been added";
		}

		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("element", service);
		model.addAttribute("site", site);
		model.addAttribute("isManagerOrAdmin", isManagerOrAdmin);
		model.addAttribute("managers", sitemanager.getServiceManagers(siteName, serviceName));
		model.addAttribute("nonmanagers", sitemanager.getNonServiceManagers(siteName, serviceName));
		model.addAttribute("client", clientmanager.getOrCreateClient(service.getClientId()));
		
		return "thsiteservicedetails";
	}
	
	@PostMapping("{siteName}/{serviceName}/deletemanager")
	@PreAuthorize("hasPermission(#siteName + '/' + #serviceName, 'servicemanager')")
	public String serviceManagersDELETE(@AuthenticationPrincipal Identity identity, HttpServletRequest request, @PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName, Model model) throws NotFoundLocalException, RoleComputationTokenException, BadRequestRemoteException {	
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		OCService service = serviceRepository.getOne(OCService.computeUrn(siteName, serviceName));
		boolean isManagerOrAdmin = rolemanager.isServiceManagerOrAdmin(identity.getSub(), siteName, serviceName);

		String sub = request.getParameter("sub");
		service.getManagers().remove(sub);
				
		String message = null;
		try {
			site = siterepository.save(site);
			message = "Manager " + userlister.getUserNameOrSub(sub) + " removed";	
		} catch (Exception e) {
			message = "Manager " + userlister.getUserNameOrSub(sub) + " has NOT been removed";
		}

		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("element", service);
		model.addAttribute("site", site);
		model.addAttribute("isManagerOrAdmin", isManagerOrAdmin);
		model.addAttribute("managers", sitemanager.getServiceManagers(siteName, serviceName));
		model.addAttribute("nonmanagers", sitemanager.getNonServiceManagers(siteName, serviceName));
		model.addAttribute("client", clientmanager.getOrCreateClient(service.getClientId()));
		
		return "thsiteservicedetails";
	}
	
	@RequestMapping("{siteName}/{serviceName}/delete")
	@PreAuthorize("hasPermission(#siteName + '/' + #serviceName, 'servicemanager')")
	public String serviceDELETE(@AuthenticationPrincipal Identity identity, @PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName, Model model) throws NotFoundLocalException, RoleComputationTokenException, BadRequestRemoteException {
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		boolean isManagerOrAdmin = rolemanager.isServiceManagerOrAdmin(identity.getSub(), siteName, serviceName);
		boolean isAdmin = rolemanager.isAdmin(identity.getSub());
		OCService service = serviceRepository.getOne(OCService.computeUrn(siteName, serviceName));
		
		site.getServices().remove(service);
			
		String message = null;
		try {
			site = siterepository.saveAndFlush(site);
			message = "Service " + service.getName() + " deleted";	
		} catch (Exception e) {
			message = "Service " + service.getName() + " NOT deleted";
		}

		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("element", site);
		model.addAttribute("services", site.getServices());
		model.addAttribute("isManagerOrAdmin", isManagerOrAdmin);
		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("managers", sitemanager.getSiteManagers(siteName));
		model.addAttribute("nonmanagers", sitemanager.getNonSiteManagers(siteName));
		model.addAttribute("client", clientmanager.getOrCreateClient((site.getClientId())));
		
		return "thsitedetails";
	}
	
	private List<OCSite> getSiterepository() {
		return getRepositoryContent(siterepository, (o1, o2) -> o1.getUrn().compareTo(o2.getUrn())); 	
	}

	private static <T extends Object> List<T> getRepositoryContent(CrudRepository<T, String> repository, Comparator<? super T> comparator) {
		 return StreamSupport.stream(repository.findAll().spliterator(), false).sorted(comparator).collect(Collectors.toList());
	}
}
