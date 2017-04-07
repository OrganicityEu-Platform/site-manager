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
import fr.cea.organicity.manager.exceptions.local.MethodNotAllowedLocalException;
import fr.cea.organicity.manager.exceptions.local.NotFoundLocalException;
import fr.cea.organicity.manager.exceptions.token.RoleComputationTokenException;
import fr.cea.organicity.manager.repositories.OCServiceRepository;
import fr.cea.organicity.manager.repositories.OCSiteRepository;
import fr.cea.organicity.manager.security.Identity;
import fr.cea.organicity.manager.services.rolemanager.RoleManager;
import fr.cea.organicity.manager.services.rolemanager.SiteRoleManager;

@Controller
@RequestMapping("/sites")
public class SitesControllerUI {
	
	@Autowired private RoleManager rolemanager;
	@Autowired private SiteRoleManager sitemanager;

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
	public String siteGET(@AuthenticationPrincipal Identity identity, @PathVariable("siteName") String siteName, Model model) throws NotFoundLocalException, RoleComputationTokenException {
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		boolean isManagerOrAdmin = rolemanager.isSiteManagerOrAdmin(identity.getSub(), siteName);
		boolean isAdmin = rolemanager.isAdmin(identity.getSub());

		model.addAttribute("title", title);
		model.addAttribute("element", site);
		model.addAttribute("services", site.getServices());
		model.addAttribute("isManagerOrAdmin", isManagerOrAdmin);
		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("managers", sitemanager.getSiteManagers(siteName));

		return "thsitedetails";
	}

	@PostMapping("{siteName}")
	@PreAuthorize("hasPermission(#siteName, 'manager')")
	public String sitePOST(@AuthenticationPrincipal Identity identity, HttpServletRequest request, @PathVariable("siteName") String siteName, Model model) throws NotFoundLocalException, RoleComputationTokenException {	
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
		
		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("element", site);
		model.addAttribute("services", site.getServices());
		model.addAttribute("isManagerOrAdmin", isManagerOrAdmin);
		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("managers", sitemanager.getSiteManagers(siteName));

		return "thsitedetails";
	}
	
	@RequestMapping("{siteName}/{serviceName}")
	public String serviceGET(@AuthenticationPrincipal Identity identity, @PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName, Model model) throws NotFoundLocalException, RoleComputationTokenException {
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		boolean isManagerOrAdmin = rolemanager.isSiteManagerOrAdmin(identity.getSub(), siteName);
		OCService service = site.getService(serviceName);
		
		model.addAttribute("title", title);
		model.addAttribute("element", service);
		model.addAttribute("site", site);
		model.addAttribute("isManagerOrAdmin", isManagerOrAdmin);

		return "thsiteservicedetails";
	}

	@PostMapping("{siteName}/{serviceName}")
	@PreAuthorize("hasPermission(#siteName, 'manager')")
	public String servicePOST(@AuthenticationPrincipal Identity identity, HttpServletRequest request, @PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName, Model model) throws NotFoundLocalException, RoleComputationTokenException {	
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		boolean isManagerOrAdmin = rolemanager.isSiteManagerOrAdmin(identity.getSub(), siteName);
		OCService service = site.getService(serviceName);

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
				
		return "thsiteservicedetails";
	}
		
	@RequestMapping("{siteName}/{serviceName}/delete")
	@PreAuthorize("hasPermission(#siteName, 'manager')")
	public String serviceDELETE(@AuthenticationPrincipal Identity identity, @PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName, Model model) throws NotFoundLocalException, RoleComputationTokenException {
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		boolean isManagerOrAdmin = rolemanager.isSiteManagerOrAdmin(identity.getSub(), siteName);
		OCService service = site.getService(serviceName);		

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

		return "thsitedetails";
	}
	
	private List<OCSite> getSiterepository() {
		return getRepositoryContent(siterepository, (o1, o2) -> o1.getUrn().compareTo(o2.getUrn())); 	
	}

	private static <T extends Object> List<T> getRepositoryContent(CrudRepository<T, String> repository, Comparator<? super T> comparator) {
		 return StreamSupport.stream(repository.findAll().spliterator(), false).sorted(comparator).collect(Collectors.toList());
	}
}
