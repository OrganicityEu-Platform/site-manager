package fr.cea.organicity.manager.controllers.ui;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;

@Controller
@RequestMapping("/dictionaries")
public class DicoRootControllerUI implements Dico {
	
	@GetMapping
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String dictionaries(HttpServletRequest request, Model model) {
		model.addAttribute("title", title);
		return "thdico";
	}	
}
