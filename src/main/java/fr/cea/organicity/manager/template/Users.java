package fr.cea.organicity.manager.template;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import fr.cea.organicity.manager.otherservices.User;
import fr.cea.organicity.manager.security.Role;

public class Users {
	
	private static final String TITLE = "Permissions";
	
	public static String generateUserList(TemplateEngine templateService, List<Role> roles, List<User> users) throws IOException {
		
		String content = "<h2>Users list</h2>\n";
		
		content += "<ul>\n";
		for (User user : users) {
			content += "	<li>" + TemplateEngine.createNavigateLink("users/" + user.getId(), user.getName()) + "</li>\n";		
		}		
		content += "</ul>\n";
		
		return templateService.generateWebPage(TITLE, content, roles);
	}	
	
	public static String generateUserPermissionDetails(TemplateEngine templateService, List<Role> allRoles, String userName, String userId, List<Role> userRoles, String message) throws ExecutionException, IOException {

		String SITE_PREFIX = "site-";
		
		String content = "<h2>Permission details for " + userName + "</h2>\n";
		
		if (message != null)
			content += "<p><strong>" + message + "</strong></p>\n";
		
		List<Role> nonSitesRoles = allRoles.stream().filter(r -> ! r.getRoleName().startsWith(SITE_PREFIX)).collect(Collectors.toList());
		for (Role role : nonSitesRoles) {
			
			if (userRoles.contains(role)) {
				content += "<span  class=\"rolename\" style=\"color:green\">";
			} else {
				content += "<span  class=\"rolename\" style=\"color:red\">";
			}
			content += role.getRoleName() + "</span>";
			
			if (userRoles.contains(role)) {
				content += "<span  class=\"rolecolumn\">" + TemplateEngine.createNavigateLink("/users/" + userId + "/removerole/" + role.getFullName(), "revoke permission")+ "</span><br/>\n";
			} else {
				content += "<span  class=\"rolecolumn\">" + TemplateEngine.createNavigateLink("/users/" + userId + "/addrole/" + role.getFullName(), "add permission")+ "</span><br/>";
			}
		}
		
		List<String> siteNames = allRoles.stream()
				.map(r -> r.getRoleName())
				.filter(n -> n.startsWith(SITE_PREFIX))
				.map(n -> n.substring(SITE_PREFIX.length()))
				.filter(n -> n.contains("-"))
				.map(n -> n.substring(0, n.indexOf("-")))
				.distinct()
				.sorted()
				.collect(Collectors.toList());
		
		content += "<h2>Sites permission management</h3>\n";
		
		for (String siteName : siteNames) {
			content += "<span  class=\"rolename\">site " + siteName + "</span>";
			
			List<Role> siteRoles = allRoles.stream()
				.filter(r -> r.getRoleName()
				.startsWith(SITE_PREFIX + siteName))
				.sorted((r1, r2) -> r1.getFullName().compareTo(r2.getFullName()))
				.collect(Collectors.toList());
			
			for (Role role : siteRoles) {
				
				String name = role.getRoleName().substring(role.getRoleName().lastIndexOf("-") + 1);
				
				
				if (userRoles.contains(role)) {
					content += "<span  class=\"rolecolumn\">" + TemplateEngine.createNavigateLink("/users/" + userId + "/removerole/" + role.getFullName(), "revoke " + name)+ "</span>\n";
				} else {
					content += "<span  class=\"rolecolumn\">" + TemplateEngine.createNavigateLink("/users/" + userId + "/addrole/" + role.getFullName(), "add " + name)+ "</span>";
				}
			}
			content += "<br/>\n";
		}
		
		return templateService.generateWebPage(TITLE, content, userRoles);
	}
}
