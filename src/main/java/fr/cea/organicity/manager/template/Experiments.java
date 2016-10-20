package fr.cea.organicity.manager.template;

import java.io.IOException;
import java.util.List;

import fr.cea.organicity.manager.otherservices.Experiment;
import fr.cea.organicity.manager.otherservices.ThirdPartyResult;
import fr.cea.organicity.manager.otherservices.User;
import fr.cea.organicity.manager.otherservices.UserLister;
import fr.cea.organicity.manager.security.Role;

public class Experiments {

	public static String generateHTML(TemplateEngine templateService, List<Role> roles, ThirdPartyResult<List<Experiment>> experiments) throws IOException {
		
		StringBuilder sb = new StringBuilder();
		sb.append("<h2>Experiments list</h2>\n");

		if (experiments.hasAlreadySucceed()) {
			if ( ! experiments.isLastCallSucess()) {
				sb.append("<p>WARNING : the following list may be outdated since a third party service is down</p>\n");
			}
			sb.append("<table>\n");
			sb.append("  <tr><th>name</th><th>status</th><th>remaining</th><th>initial quota</th></tr>\n");		
			for (Experiment experiment : experiments.getLastSuccessResult()) {
				String id = experiment.getId();
				String name = experiment.getName();
				String status = experiment.getStatus();
				long nb = experiment.getRemQuota();
				long max = experiment.getQuota();
				sb.append("  <tr><td>" + TemplateEngine.createNavigateLink("/experiments/" + id, name) + "</td><td>" + status + "</td><td>" + nb + "</td><td>" + max + "</td></tr>\n");
			}		
			sb.append("</table>\n");
		}
		// never succeed :-(
		else {
			sb.append("<p>This informaion is not available because a third party service is down</p>\n");
		}
		
		return templateService.generateWebPage("Experiments", sb.toString(), roles);
	}

	public static String generateHTML(TemplateEngine templateService, UserLister userLister, List<Role> roles, ThirdPartyResult<Experiment> experiment, List<String> sources) throws IOException {
		
		String content = "<h2>Experiment details</h2>\n";	

		if (experiment.hasAlreadySucceed()) {
			if ( ! experiment.isLastCallSucess()) {
				content += "<p>WARNING : the following information may be outdated since a third party service is down</p>\n";
			}
			
			Experiment exp = experiment.getLastSuccessResult();			
			content += TemplateEngine.createKeyValue("name", exp.getName());
			User owner = userLister.getUser(exp.getMainExperimenter()).getLastSuccessResult();
			content += TemplateEngine.createKeyValue("owner", owner == null? exp.getMainExperimenter() : owner.getName());
			content += TemplateEngine.createKeyValue("id", exp.getId());
			content += TemplateEngine.createKeyValue("description", exp.getDescription());
			content += TemplateEngine.createKeyValue("status", exp.getStatus());
			content += TemplateEngine.createKeyValue("registered", exp.getRegistered());
			content += TemplateEngine.createKeyValue("startDate", exp.getStartDate());
			content += TemplateEngine.createKeyValue("endDate", exp.getEndDate());
			content += "<br/>\n";
			
			content += TemplateEngine.createKeyValue("Avalilable quota", exp.getRemQuota() + "/" + exp.getQuota());
			content += "<br/>\n";
			
			List<String> experimenters = exp.getExperimenters();
			content += TemplateEngine.createListSize(experimenters, "experimenter");
			content += "<ul>\n";
			for (String experimenterId : experimenters) {
				User user = userLister.getUser(experimenterId).getLastSuccessResult();
				if (user == null)
					content += "	<li>" + experimenterId + "</li>\n";
				else
					content += "	<li>" + user.getName() + "</li>\n";		
			}
			content += "</ul>\n";
			
			content += "<br/>\n";
					
			content += TemplateEngine.createListSize(sources, "data sources");
			content += "<ul>\n";
			for (String urn : sources) {
				content += "	<li>" + urn + "</li>\n";
			}
			content += "</ul>\n";						
		}
		// never succeed :-(
		else {
			content += "<p>This informaion is not available because a third party service is down</p>\n";
		}
		
		return templateService.generateWebPage("Experiment details", content, roles);
	}
}
