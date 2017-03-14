package fr.cea.organicity.manager.controllers.ui;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.cea.organicity.manager.exceptions.AppException;
import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;
import fr.cea.organicity.manager.services.tagdomain.TagDomainService;

@Controller
@RequestMapping("/dictionaries")
public class DicoOtherControllerUI implements Dico {
	
	@Autowired private DictionaryHelper dicoHelper;
	@Autowired private TagDomainService tagdomainservice;
	
	@RequestMapping("tools")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String tools(HttpServletRequest request, Model model) {
		model.addAttribute("title", title);
		model.addAttribute("elements", dicoHelper.getToolRepository());
		return "thdicotools";
	}
	
	@RequestMapping("applicationtypes")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String applicationTypes(HttpServletRequest request, Model model) {
		model.addAttribute("title", title);
		model.addAttribute("elements", dicoHelper.getAppTypeRepository());
		return "thdicoapptypes";
	}
	
	@RequestMapping("tagdomains")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String tagdomains(HttpServletRequest request, Model model) throws AppException {
		model.addAttribute("title", title);
		model.addAttribute("elements", tagdomainservice.retrieveTagDomains());
		return "thdicotagdomains";
	}
	
	@RequestMapping("tagdomains/{id}")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String tagdomain(HttpServletRequest request, @PathVariable("id") int id, Model model) throws AppException {
		model.addAttribute("title", title);
		model.addAttribute("element", tagdomainservice.retrieveTagDomains(id));
		return "thdicotagdomaindetails";
	}

	@RequestMapping("userinterests")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String userInterests(HttpServletRequest request, Model model) {
		model.addAttribute("title", title);
		model.addAttribute("elements", dicoHelper.getUserInterestRepository());
		return "thdicointerests";
	}
}
