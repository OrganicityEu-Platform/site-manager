package fr.cea.organicity.manager.controllers.ui;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/links")
public class LinksControllerUI {

	private final String title = "Dev links";
	
	@GetMapping
	@PreAuthorize("hasRole('APP:developer')")
	public String links(Model model) {
		model.addAttribute("title", title);
		return "thlinks";
	}
}
