package fr.cea.organicity.manager.controllers.ui;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;

@Controller
@RequestMapping("/links")
public class LinksControllerUI {

	private final String title = "Dev links";
	
	@GetMapping
	@RoleGuard(roleName=SecurityConstants.DEVELOPER)
	public String links(HttpServletRequest request, Model model) {
		model.addAttribute("title", title);
		return "thlinks";
	}
}
