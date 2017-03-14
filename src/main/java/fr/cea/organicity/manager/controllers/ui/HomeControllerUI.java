package fr.cea.organicity.manager.controllers.ui;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeControllerUI {

	private final String title = "Facility Management Portal";
	
	@GetMapping("/")
	public String getRoot(HttpServletRequest request, Model model) {
		model.addAttribute("title", title);
		return "thindex";
	}
	
 	@RequestMapping("/callback")
	public String getCallback(HttpServletRequest request, Model model) {
 		model.addAttribute("title", title);
 		model.addAttribute("subtitle", "Redirrect");
		model.addAttribute("message", "You will be redirrected to the page you wanted to visit. Please wait...");
		return "thmessage";
 	}	
}
