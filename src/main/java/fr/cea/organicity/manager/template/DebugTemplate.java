package fr.cea.organicity.manager.template;

import java.io.IOException;
import java.util.List;

import fr.cea.organicity.manager.security.Role;

public class DebugTemplate {

	public static String generateHTML(TemplateEngine templateService, List<Role> roles) throws IOException {
		String content = "<h2>Configuration information</h2>\n";		
		content += templateService.getDictionnaryContentAsList();
		return templateService.generateWebPage("Debug", content, roles);
	}
}
