package fr.cea.organicity.manager.template;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.cea.organicity.manager.security.Role;

public class Home {

	public static String generateHTML(TemplateEngine templateService, List<Role> roles, String url) throws IOException {

		String TITLE = "OrganiCity Site Manager";
		
		// renew variable
		Map<String, String> dictionary = new HashMap<>();
		if (roles == null) {
			String renew = "<div style=\"text-align: center;\">\n";
			renew += "<a href=\"" + url + "\" class=\"btn validate\">renew your token</a>\n";
			renew += "</div>\n";
			dictionary.put("renew", renew);
		} else {
			dictionary.put("renew", "");
		}
		
		String content = templateService.stringFromTemplate("/templates/home.html", dictionary);
		
		return templateService.generateWebPage(TITLE, content, roles);
	}
}
