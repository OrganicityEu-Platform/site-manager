package fr.cea.organicity.manager.controllers.ui;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.cea.organicity.manager.domain.OCService;
import fr.cea.organicity.manager.domain.OCSite;
import fr.cea.organicity.manager.repositories.OCServiceRepository;
import fr.cea.organicity.manager.repositories.OCSiteRepository;
import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConfig;
import fr.cea.organicity.manager.security.SecurityConstants;
import fr.cea.organicity.manager.services.rolemanager.Role;
import fr.cea.organicity.manager.services.rolemanager.RoleManager;

@Controller
@RequestMapping("/sites")
public class SitesControllerUI {

	@Autowired private RoleManager roleManager;
	@Autowired private OCSiteRepository siterepository;
	@Autowired private OCServiceRepository serviceRepository;
	@Autowired private SecurityConfig securityconfig;

	private final String title = "Sites";
	
	@GetMapping
	@RoleGuard
	public String sites(HttpServletRequest request, Model model) {
		model.addAttribute("title", title);
		model.addAttribute("elements", getSiterepository());
		
		return "thsites";
	}

	@GetMapping("{siteName}")
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-user")
	public String siteGET(HttpServletRequest request, @PathVariable("siteName") String siteName, Model model) {
		Role adminRole = new Role(securityconfig.getClientName() + ":site-" + siteName + "-admin");
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		boolean isAdmin = hasRole(request, adminRole);

		model.addAttribute("title", title);
		model.addAttribute("element", site);
		model.addAttribute("services", site.getServices());
		model.addAttribute("isAdmin", isAdmin);

		return "thsitedetails";
	}

	@PostMapping("{siteName}")
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-admin")
	public String sitePOST(HttpServletRequest request, @PathVariable("siteName") String siteName, Model model) {	
		Role adminRole = new Role(securityconfig.getClientName() + ":site-" + siteName + "-admin");
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		boolean isAdmin = hasRole(request, adminRole);
		
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
		model.addAttribute("isAdmin", isAdmin);

		return "thsitedetails";
	}
	
	@RequestMapping("{siteName}/{serviceName}")
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-user")
	public String serviceGET(HttpServletRequest request, @PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName, Model model) {
		Role adminRole = new Role(securityconfig.getClientName() + ":site-" + siteName + "-admin");
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		OCService service = site.getService(serviceName);
		boolean isAdmin = hasRole(request, adminRole);
		
		model.addAttribute("title", title);
		model.addAttribute("element", service);
		model.addAttribute("site", site);
		model.addAttribute("isAdmin", isAdmin);

		return "thsiteservicedetails";
	}

	@PostMapping("{siteName}/{serviceName}")
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-admin")
	public String servicePOST(HttpServletRequest request, @PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName, Model model) {	
		Role adminRole = new Role(securityconfig.getClientName() + ":site-" + siteName + "-admin");
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		OCService service = site.getService(serviceName);
		boolean isAdmin = hasRole(request, adminRole);

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
		model.addAttribute("isAdmin", isAdmin);

		return "thsiteservicedetails";
	}
		
	@RequestMapping("{siteName}/{serviceName}/delete")
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-admin")
	public String serviceDELETE(HttpServletRequest request, @PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName, Model model) {
		Role adminRole = new Role(securityconfig.getClientName() + ":site-" + siteName + "-admin");
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		OCService service = site.getService(serviceName);
		boolean isAdmin = hasRole(request, adminRole);

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
		model.addAttribute("isAdmin", isAdmin);

		return "thsitedetails";
	}
	
	private List<OCSite> getSiterepository() {
		return getRepositoryContent(siterepository, (o1, o2) -> o1.getUrn().compareTo(o2.getUrn())); 	
	}
	
	
	// ===================

	private static <T extends Object> List<T> getRepositoryContent(CrudRepository<T, String> repository, Comparator<? super T> comparator) {
		 return StreamSupport.stream(repository.findAll().spliterator(), false).sorted(comparator).collect(Collectors.toList());
	}
	
	private boolean hasRole(HttpServletRequest request, Role role) {
		List<Role> roles = roleManager.getRolesForRequest(request);
		return (roles != null && roles.contains(role));
	}
}
