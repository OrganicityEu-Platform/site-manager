package fr.cea.organicity.manager.template;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.cea.organicity.manager.security.Role;

public class ErrorTemplate {
		
	public static String generateHTML(TemplateEngine templateService, List<Role> roles, String message) throws IOException {
		return WebPageTemplate.basicTemplate(templateService, roles, "Error", message);
	}
	
	public static String generateHTML(TemplateEngine templateService, List<Role> roles, Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		// Title computation
		String title = "Error";
		if (model.get("status") != null && model.get("error") != null) {
			title = "Error " + model.get("status") + " : " + model.get("error"); 
		}
		
		// Model pretty printing
		String htmlModel = "<ul>";
		for (Object key : model.keySet()) {
			htmlModel += "<li><strong>" + key.toString() + "</strong>=" + model.get(key).toString() + "</li>\n"; 
		}
		htmlModel += "</ul>\n";
		
		// Content creation
		Map<String, String> dictionary = new HashMap<>();
		dictionary.put("title", title);
		dictionary.put("model", htmlModel);		
		String content = templateService.stringFromTemplate("/templates/error.html", dictionary);
		
		return templateService.generateWebPage(title, content, roles);
	}	
}
