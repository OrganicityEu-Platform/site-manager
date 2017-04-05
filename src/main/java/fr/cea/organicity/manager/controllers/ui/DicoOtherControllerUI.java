package fr.cea.organicity.manager.controllers.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.cea.organicity.manager.exceptions.AppException;
import fr.cea.organicity.manager.services.tagdomain.TagDomainService;

@Controller
@RequestMapping("/dictionaries")
public class DicoOtherControllerUI implements Dico {
	
	@Autowired private DictionaryHelper dicoHelper;
	@Autowired private TagDomainService tagdomainservice;
	
	@RequestMapping("tools")
	@PreAuthorize("hasRole('APP:dictionary-user')")
	public String tools(Model model) {
		model.addAttribute("title", title);
		model.addAttribute("elements", dicoHelper.getToolRepository());
		return "thdicotools";
	}
	
	@RequestMapping("applicationtypes")
	@PreAuthorize("hasRole('APP:dictionary-user')")
	public String applicationTypes(Model model) {
		model.addAttribute("title", title);
		model.addAttribute("elements", dicoHelper.getAppTypeRepository());
		return "thdicoapptypes";
	}
	
	@RequestMapping("tagdomains")
	@PreAuthorize("hasRole('APP:dictionary-user')")
	public String tagdomains(Model model) throws AppException {
		model.addAttribute("title", title);
		model.addAttribute("elements", tagdomainservice.retrieveTagDomains());
		return "thdicotagdomains";
	}
	
	@RequestMapping("tagdomains/{id}")
	@PreAuthorize("hasRole('APP:dictionary-user')")
	public String tagdomain(@PathVariable("id") int id, Model model) throws AppException {
		model.addAttribute("title", title);
		model.addAttribute("element", tagdomainservice.retrieveTagDomains(id));
		return "thdicotagdomaindetails";
	}

	@RequestMapping("userinterests")
	@PreAuthorize("hasRole('APP:dictionary-user')")
	public String userInterests(Model model) {
		model.addAttribute("title", title);
		model.addAttribute("elements", dicoHelper.getUserInterestRepository());
		return "thdicointerests";
	}
}
