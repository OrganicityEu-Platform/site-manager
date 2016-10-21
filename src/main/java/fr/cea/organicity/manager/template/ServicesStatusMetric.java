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
		
		String lastSuccess;
		if (thirdParty.getLastSuccessTimestamp() == 0) {
			lastSuccess = "no success";
		} else {
			long ts = thirdParty.getLastSuccessTimestamp();
			lastSuccess = generateAgo(ts);
		}
		
		String lastCall;
		if (thirdParty.getLastCallTimestamp() == 0) {
			lastCall = "no calls already performed";
		} else {
			lastCall = thirdParty.isLastCallSucess() ? "SUCCESS" : "FAILURE";
			long ts = thirdParty.getLastSuccessTimestamp();
			lastCall += " - " + thirdParty.getLastCallduration() + "ms";
			lastCall += " - " + generateAgo(ts);
		}
		
		content += "<ul>\n";
		content += TemplateEngine.createKeyValue("Last success", lastSuccess);
		content += TemplateEngine.createKeyValue("Last call", lastCall);
		content += "</ul>\n";
		
		return content;
	}
	
	private static String generateAgo(long timestamp) {
		long cur = System.currentTimeMillis();
		long s = (cur - timestamp) / 1000;
		return s + "s ago (" + new Date(timestamp).toString() + ")";
	}	
}
