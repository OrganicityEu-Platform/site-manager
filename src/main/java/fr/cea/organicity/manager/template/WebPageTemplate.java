package fr.cea.organicity.manager.template;

import java.io.IOException;
import java.util.List;

import fr.cea.organicity.manager.security.Role;

public abstract class WebPageTemplate {
	
	public static String generateHTML(TemplateEngine templateService, List<Role> roles, String title, String path) throws IOException {
		String content = templateService.stringFromTemplate(path);
		return templateService.generateWebPage(title, content, roles);
	}	
	
	public static String basicTemplate(TemplateEngine templateService, List<Role> roles, String title, String message) throws IOException {
		String content = "<h2>" + title + "</h2>\n" ;
		if (message != null)
			content += "<p>" + message + "</p>\n";
		return templateService.generateWebPage(title, content, roles);
	}
	
	public static String generateUnauthorizedHTML(TemplateEngine templateService, List<Role> roles, String reason) throws IOException {
		return templateService.generateWebPage("Unauthorized", reason, roles);
	}
	
	public static String generateNotYetImplementedHTML(TemplateEngine templateService, List<Role> roles, String title) throws IOException {
		return templateService.generateWebPage(title, "Not yet implemented", roles);
	}
	
	public static String generateNotAvailable(TemplateEngine templateService, List<Role> roles, String reason) throws IOException {
		return templateService.generateWebPage("Not available", reason, roles);
	}
}
