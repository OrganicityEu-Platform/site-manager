package fr.cea.organicity.manager.controllers.ui;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dictionaries")
public class DicoRootControllerUI implements Dico {
	
	@GetMapping
	@PreAuthorize("hasRole('APP:dictionary-user')")
	public String dictionaries(Model model) {
		model.addAttribute("title", title);
		return "thdico";
	}	
}
