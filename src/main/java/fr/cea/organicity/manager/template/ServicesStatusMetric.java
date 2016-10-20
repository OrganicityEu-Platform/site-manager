package fr.cea.organicity.manager.template;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import fr.cea.organicity.manager.otherservices.Experiment;
import fr.cea.organicity.manager.otherservices.ThirdPartyResult;
import fr.cea.organicity.manager.otherservices.User;
import fr.cea.organicity.manager.security.Role;

public class ServicesStatusMetric {
	
	public static String generateHTML(TemplateEngine templateService, List<Role> roles, ThirdPartyResult<List<User>> users, ThirdPartyResult<List<Experiment>> experiments) throws IOException {

		String title = "External services status";
			
		String content = "<h2>" + title + "</h2>\n";
		
		content += displayStats("Users API", users); 
		content += displayStats("Experiments API", experiments);
		
		return templateService.generateWebPage("Metrics", content, roles);
	}

	private static String displayStats(String title, ThirdPartyResult<?> thirdParty) {
		String content = "<p><strong>" + title + "</strong></p>\n";
		
		String lastSuccess = thirdParty.getLastSuccessTimestamp() == 0 ? "no success" : new Date(thirdParty.getLastSuccessTimestamp()).toString();
		String lastCall;
		if (thirdParty.getLastCallTimestamp() == 0) {
			lastCall = "no calls already performed";
		} else {
			lastCall = thirdParty.isLastCallSucess() ? "SUCCESS" : "FAILURE";
			lastCall += " " + thirdParty.getLastCallduration() + "ms";
			lastCall += " " + new Date(thirdParty.getLastCallTimestamp()); 
		}
		
		content += "<ul>\n";
		content += TemplateEngine.createKeyValue("Last success", lastSuccess);
		content += TemplateEngine.createKeyValue("Last call", lastCall);
		content += "</ul>\n";
		
		return content;
	}
}
