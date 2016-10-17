package fr.cea.organicity.manager.template;

import java.io.IOException;
import java.util.List;

import fr.cea.organicity.manager.otherservices.Experiment;
import fr.cea.organicity.manager.otherservices.User;
import fr.cea.organicity.manager.otherservices.UserLister;
import fr.cea.organicity.manager.security.Role;

public class Experiments {

	public static String generateHTML(TemplateEngine templateService, List<Role> roles, List<Experiment> experiments) throws IOException {
		
		StringBuilder sb = new StringBuilder();
		sb.append("<h2>Experiments list</h2>\n");

		sb.append("<table>\n");
		sb.append("  <tr><th>name</th><th>status</th><th>remaining</th><th>initial quota</th></tr>\n");		
		for (Experiment experiment : experiments) {
			String id = experiment.getId();
			String name = experiment.getName();
			String status = experiment.getStatus();
			long nb = experiment.getRemQuota();
			long max = experiment.getQuota();
			sb.append("  <tr><td>" + TemplateEngine.createNavigateLink("/experiments/" + id, name) + "</td><td>" + status + "</td><td>" + nb + "</td><td>" + max + "</td></tr>\n");
		}		
		sb.append("</table>\n");		
		
		return templateService.generateWebPage("Experiments", sb.toString(), roles);
	}

	public static String generateHTML(TemplateEngine templateService, UserLister userLister, List<Role> roles, Experiment experiment, List<String> sources) throws IOException {
		if (experiment == null)
			return null;
		
		String content = "";
		
		content += "<h2>Experiment details</h2>\n";	

		content += TemplateEngine.createKeyValue("name", experiment.getName());
		User owner = userLister.getElement(experiment.getMainExperimenter());
		content += TemplateEngine.createKeyValue("owner", owner == null? experiment.getMainExperimenter() : owner.getName());
		content += TemplateEngine.createKeyValue("id", experiment.getId());
		content += TemplateEngine.createKeyValue("description", experiment.getDescription());
		content += TemplateEngine.createKeyValue("status", experiment.getStatus());
		content += TemplateEngine.createKeyValue("registered", experiment.getRegistered());
		content += TemplateEngine.createKeyValue("startDate", experiment.getStartDate());
		content += TemplateEngine.createKeyValue("endDate", experiment.getEndDate());
		content += "<br/>\n";
		
		content += TemplateEngine.createKeyValue("Avalilable quota", experiment.getRemQuota() + "/" + experiment.getQuota());
		content += "<br/>\n";
		
		List<String> experimenters = experiment.getExperimenters();
		content += TemplateEngine.createListSize(experimenters, "experimenter");
		content += "<ul>\n";
		for (String experimenterId : experimenters) {
			User user = userLister.getElement(experimenterId);
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
		
		return templateService.generateWebPage("Experiment details", content, roles);
	}
}
