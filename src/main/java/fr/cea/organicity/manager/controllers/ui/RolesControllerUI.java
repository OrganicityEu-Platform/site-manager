package fr.cea.organicity.manager.controllers.ui;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.cea.organicity.manager.exceptions.token.RoleComputationTokenException;
import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConfig;
import fr.cea.organicity.manager.security.SecurityConstants;
import fr.cea.organicity.manager.services.rolemanager.Role;
import fr.cea.organicity.manager.services.rolemanager.RoleManager;
import fr.cea.organicity.manager.services.userlister.UserLister;

@Controller
@RequestMapping("/roles")
public class RolesControllerUI {

	private final String title = "Roles";
	
	@Autowired private UserLister userLister;
	@Autowired private SecurityConfig secuConfig;
	@Autowired private RoleManager roleManager;

	@GetMapping
	@RoleGuard(roleName=SecurityConstants.ROLE_ADMIN)
	public String users(HttpServletRequest request, Model model) {
		model.addAttribute("title", title);
		model.addAttribute("users", userLister.getUsers().getLastSuccessResult());

		return "thusers";
	}
		
	@GetMapping("{userId}")
	@RoleGuard(roleName=SecurityConstants.ROLE_ADMIN)
	public String user(HttpServletRequest request, @PathVariable("userId") String userId, Model model) throws RoleComputationTokenException {
		addAttributes(model, userId, null);
		return "thuserdetails";
	}
	
	@RequestMapping("{userId}/addrole/{roleName}")
	@RoleGuard(roleName=SecurityConstants.ROLE_ADMIN)
	public String userAddRole(HttpServletRequest request, @PathVariable("userId") String userId, @PathVariable("roleName") String roleName, Model model) throws RoleComputationTokenException {
		
		String message = null;
		try {
			Role role = new Role(roleName);
			roleManager.addRole(userId, role);
			message = "SUCCESS: role " + role.getRoleName() + " added";
		} catch (Exception e) {
			message = "ERROR: " + e.getMessage();
		}
		
		addAttributes(model, userId, message);		
		return "thuserdetails";
	}

	@RequestMapping("{userId}/removerole/{roleName}")
	@RoleGuard(roleName=SecurityConstants.ROLE_ADMIN)
	public String userRemoveRole(HttpServletRequest request, @PathVariable("userId") String userId, @PathVariable("roleName") String roleName, Model model) throws RoleComputationTokenException {
		
		String message = null;
		try {
			Role role = new Role(roleName);
			roleManager.removeRole(userId, role);
			message = "SUCCESS: role " + role.getRoleName() + " removed";
		} catch (Exception e) {
			message = "ERROR: " + e.getMessage();
		}
		
		addAttributes(model, userId, message);
		return "thuserdetails";
	}
	
	private void addAttributes(Model model, String userId, String message) throws RoleComputationTokenException {
		List<Role> allRoles = secuConfig.getLocalRoles_TEMPORARY();
		
		model.addAttribute("title", title);
		model.addAttribute("user", userLister.getUser(userId).getLastSuccessResult());
		model.addAttribute("message", message);
		model.addAttribute("allRoles", allRoles);
		model.addAttribute("displayedUserRoles", roleManager.getRolesForSub(userId));
	}
}
