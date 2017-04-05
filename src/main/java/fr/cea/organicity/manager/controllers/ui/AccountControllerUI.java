package fr.cea.organicity.manager.controllers.ui;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import fr.cea.organicity.manager.security.Identity;
import fr.cea.organicity.manager.services.experimentlister.ExperimentLister;
import fr.cea.organicity.manager.services.rolemanager.ClaimsExtractor;
import fr.cea.organicity.manager.services.rolemanager.OCClaims;
import fr.cea.organicity.manager.services.rolemanager.Role;
import fr.cea.organicity.manager.services.rolemanager.RoleManager;
import fr.cea.organicity.manager.services.rolemanager.RoleScope;
import io.jsonwebtoken.Claims;

@Controller
public class AccountControllerUI {

	@Autowired private ClaimsExtractor claimsExtractor;
	@Autowired private RoleManager roleManager;
	@Autowired private ExperimentLister experimentLister;
	
	private final String title = "My Account";	
	
	@GetMapping("/info")
	public String info(@AuthenticationPrincipal Identity identity, Model model) {
		
		String id_token = identity.getIdToken(); 
		String access_token = identity.getAccessToken();
		
		OCClaims claims;
		try {
			Claims c = claimsExtractor.getClaimsFromToken(id_token);
			claims = new OCClaims(c, roleManager.getRemotemanager());
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

		List<Role> roles = identity.getRoles();
		model.addAttribute("roles_global", displayRoles("global", roles.stream().filter(r -> r.getScope() == RoleScope.GLOBAL).collect(Collectors.toList())));
		model.addAttribute("roles_local", displayRoles("local", roles.stream().filter(r -> r.getScope() == RoleScope.APP).collect(Collectors.toList())));
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
				content += roles.get(i).getName();
			}
		}
		content += "<br/>";

		return content;
	}
}
