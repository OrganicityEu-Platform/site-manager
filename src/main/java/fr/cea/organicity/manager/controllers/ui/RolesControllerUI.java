package fr.cea.organicity.manager.controllers.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.cea.organicity.manager.exceptions.token.RoleComputationTokenException;
import fr.cea.organicity.manager.services.rolemanager.Role;
import fr.cea.organicity.manager.services.rolemanager.RoleManager;
import fr.cea.organicity.manager.services.rolemanager.SiteRoleManager;
import fr.cea.organicity.manager.services.userlister.UserLister;

@Controller
@RequestMapping("/roles")
public class RolesControllerUI {

	private final String title = "Roles";
	
	@Autowired private UserLister userLister;
	@Autowired private RoleManager roleManager;
	@Autowired private SiteRoleManager siteRoleManager;

	@GetMapping
	@PreAuthorize("hasRole('APP:role-admin')")
	public String users(Model model) {
		model.addAttribute("title", title);
		model.addAttribute("users", userLister.getUsers().getLastSuccessResult());

		return "thusers";
	}
		
	@GetMapping("{userId}")
	@PreAuthorize("hasRole('APP:role-admin')")
	public String user(@PathVariable("userId") String userId, Model model) throws RoleComputationTokenException {
		addAttributes(model, userId, null);
		return "thuserdetails";
	}
	
	@RequestMapping("{userId}/addrole/{roleName}")
	@PreAuthorize("hasRole('APP:role-admin')")
	public String userAddRole(@PathVariable("userId") String userId, @PathVariable("roleName") String roleName, Model model) throws RoleComputationTokenException {
		
		String message = null;
		try {
			Role role = new Role(roleName);
			roleManager.addRole(userId, role);
			message = "SUCCESS: role " + role.getQualifiedName() + " added";
		} catch (Exception e) {
			message = "ERROR: " + e.getMessage();
		}
		
		addAttributes(model, userId, message);		
		return "thuserdetails";
	}

	@RequestMapping("{userId}/removerole/{roleName}")
	@PreAuthorize("hasRole('APP:role-admin')")
	public String userRemoveRole(@PathVariable("userId") String userId, @PathVariable("roleName") String roleName, Model model) throws RoleComputationTokenException {
		
		String message = null;
		try {
			Role role = new Role(roleName);
			roleManager.removeRole(userId, role);
			message = "SUCCESS: role " + role.getQualifiedName() + " removed";
		} catch (Exception e) {
			message = "ERROR: " + e.getMessage();
		}
		
		addAttributes(model, userId, message);
		return "thuserdetails";
	}
	
	private void addAttributes(Model model, String userId, String message) throws RoleComputationTokenException {
		model.addAttribute("title", title);
		model.addAttribute("sub", userId);
		model.addAttribute("user", userLister.getUser(userId).getLastSuccessResult());
		model.addAttribute("message", message);
		model.addAttribute("allGlobalRoles", roleManager.getGlobalRoles());
		model.addAttribute("allSiteManagerRoles", siteRoleManager.getSiteManagerRoles());
		model.addAttribute("userRoles", roleManager.getRolesForSub(userId));
	}
}
