package fr.cea.organicity.manager.controllers.ui;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import fr.cea.organicity.manager.services.experimentlister.ExperimentLister;
import fr.cea.organicity.manager.services.rolemanager.ClaimsParser;
import fr.cea.organicity.manager.services.rolemanager.OCClaims;
import fr.cea.organicity.manager.services.rolemanager.Role;
import fr.cea.organicity.manager.services.rolemanager.RoleManager;

@Controller
public class AccountControllerUI {

	@Autowired private ClaimsParser claimsParser;
	@Autowired private RoleManager roleManager;
	@Autowired private ExperimentLister experimentLister;
	
	private final String title = "My Account";	
	
	@GetMapping("/info")
	public String info(HttpServletRequest request, Model model) {
		
		String id_token = request.getParameter("id_token");
		String access_token = request.getParameter("access_token");
		OCClaims claims;
		try {
			claims = claimsParser.getClaimsFromHeader(id_token);
		} catch (Exception e) {
			model.addAttribute("title", title);
			model.addAttribute("subtitle", "Not available");
			model.addAttribute("message", e.getMessage());
			return "thmessage";
		}
		
		model.addAttribute("title", title);
		model.addAttribute("id_token", id_token);
		model.addAttribute("access_token", access_token);
		model.addAttribute("claims", claims);

		List<Role> roles = roleManager.getRolesForRequest(request);
		model.addAttribute("roles_global", displayRoles("global", roles.stream().filter(Role::isRealmRole).collect(Collectors.toList())));
		model.addAttribute("roles_local", displayRoles("local", roles.stream().filter(r -> r.isRealmRole() == false).collect(Collectors.toList())));
		model.addAttribute("experiments", experimentLister.getExperimentsByUser(claims.getSub()).getLastSuccessResult());
		
		return "thinfo";
	}
	
	private static String displayRoles(String name, List<Role> roles) {
		String content = "<strong>User has " + roles.size() + " " + name + " role";
		if (roles.size() != 1)
			content += "s";
		content += "</strong>";
		if (roles.size() != 0) {
			content += ": ";
			for (int i=0; i< roles.size(); i++) {
				if (i != 0)
					content += ", ";
				content += roles.get(i).getRoleName();
			}
		}
		content += "<br/>";

		return content;
	}
}
