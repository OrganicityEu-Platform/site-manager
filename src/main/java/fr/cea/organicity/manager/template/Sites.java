package fr.cea.organicity.manager.template;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import fr.cea.organicity.manager.domain.OCService;
import fr.cea.organicity.manager.domain.OCSite;
import fr.cea.organicity.manager.security.Role;

public class Sites {

	private static final String TITLE = "Sites";
	
	public static String generateSiteList(TemplateEngine templateService, List<Role> roles, Iterable<OCSite> sites) throws IOException {
		
		String content = "<h2>Sites list</h2>\n";
		
		content += "<p>Federated city sites</p>\n";
		content += "<ul>\n";
		for (OCSite site : sites) {
			if (site.isCity()) {
				content += "	<li>" ;
				content += TemplateEngine.createNavigateLink("/sites/" + site.getName(), site.getUrn());
				content += "\n";
			}
		}
		content += "</ul>\n";
		
		content += "<p>Experimentation sites</p>\n";
		content += "<ul>\n";
		for (OCSite site : sites) {
			if (! site.isCity()) {
				content += "	<li>" ;
				content += TemplateEngine.createNavigateLink("/sites/" + site.getName(), site.getUrn());
				content += "\n";
			}
		}
		content += "</ul>\n";
		
		return templateService.generateWebPage(TITLE, content, roles);
	}	
	
	public static String generateSiteDetails(TemplateEngine templateService, List<Role> roles, String message, boolean isAdmin, OCSite site) throws IOException {
		
		String content = "";
		content += TemplateEngine.createNavigateLink("/sites", "&lt; Back to site list") + "\n";
		content += "<h2>" + site.getName() + "</h2>\n";
		content += TemplateEngine.generateMessageBannerIfNeeded(message);
		
		content += TemplateEngine.createKeyValue("Urn", site.getUrn());
		
		if (isAdmin) {
			content += "<form class=\"editmode\">\n";
			content += TemplateEngine.generateFormInput("email", "Email", site.getEmail());
			content += TemplateEngine.generateFormInput("related", "Related", site.getRelated());
			content += "</form>\n";
		}		
		content += "<div class=\"displaymode\">\n";
		String emailAddress = TemplateEngine.createPlainLink("mailto:" + site.getEmail(), site.getEmail());
		content += TemplateEngine.createKeyValue("Email", emailAddress);
		content += TemplateEngine.createKeyValue("Related", site.getRelated());
		content += "</div>\n";
		content += TemplateEngine.createKeyValue("Created", site.getCreated());
		content += TemplateEngine.createKeyValue("Updated", site.getUpdated());
		content += TemplateEngine.createKeyValue("Quota", site.getRemQuota() + "/" + site.getQuota());
		
		if (isAdmin) {
			content += "<div class=\"btnbar editmode\">\n";
			content += TemplateEngine.generateSubmitBtn("updateSite", "UPDATE " + site.getName() + " site", "'" + site.getName() + "'", "email", "related");
			content += "</div>\n";
			content += "<p class=\"displaymode\"><a href=\"#\" class=\"btn validate\" onclick=\"activateEditMode();\">activate edit mode</a></p>\n";
		}		
		
		content += "<h2>" + site.getName() + " services</h2>\n";
		
		Collection<OCService> services = site.getServices();
		content += TemplateEngine.createListSize(services, "service");
		
		content += "<ul>\n";
		for (OCService service : services) {
			content += "	<li>" + TemplateEngine.createNavigateLink("/sites/" + site.getName() + "/" + service.getName(), service.getUrn()) + "</li>\n";
		}
		content += "</ul>\n";	
		
		if (isAdmin) {
			content += "<h2>New service</h2>\n";
			content += "<p>To add a new entity, please fill the following form</p>\n";
			content += "<form>\n";
			content += TemplateEngine.generateFormInput("srvname", "Name");
			content += TemplateEngine.generateFormInput("srvdescription", "Description");
			content += TemplateEngine.generateFormInput("srvrelated", "Related");
			content += "<div class=\"btnbar\">\n";
			content += TemplateEngine.generateSubmitBtn("newService", "CREATE Service", "'" + site.getName() + "'", "srvname", "srvdescription", "srvrelated");
			content += "</div>\n";
			content += "</form>\n";
		}
				
		return templateService.generateWebPage(TITLE, content, roles);
	}
	
	public static String generateServiceDetails(TemplateEngine templateService, List<Role> roles, String message, boolean isAdmin, OCSite site, OCService service) throws IOException {
		
		String content = "";
		content += TemplateEngine.createNavigateLink("/sites/" + site.getName(), "&lt; Back to " + site.getName() + " site") + "\n";
		content += "<h2>" + service.getName() + "</h2>\n";
		content += TemplateEngine.generateMessageBannerIfNeeded(message);
		
		content += TemplateEngine.createKeyValue("Urn", service.getUrn()); 
				
		if (isAdmin) {
			content += "<form class=\"editmode\">\n";
			content += TemplateEngine.generateFormInput("description", "Description", service.getDescription());
			content += TemplateEngine.generateFormInput("related", "Related", service.getRelated());
			content += "</form>\n";
		} 

		content += "<div class=\"displaymode\">\n";
		content += TemplateEngine.createKeyValue("Description", service.getDescription());
		content += TemplateEngine.createKeyValue("Related", service.getRelated());
		content += "</div>\n";
		
		content += TemplateEngine.createKeyValue("Created", service.getCreated());
		content += TemplateEngine.createKeyValue("Updated", service.getUpdated());
		
		
		if (isAdmin) {
			content += "<div class=\"btnbar editmode\">\n";			
			content += TemplateEngine.generateSubmitBtn("updateService", "UPDATE " + service.getName() + " service basic info", "'" + site.getName() + "','" + service.getName() + "'", "description", "related");
			content += TemplateEngine.generateDeleteBtn("/sites/" + site.getName(), service.getName(), "service");
			content += "</div>\n";
			content += "<p class=\"displaymode\"><a href=\"#\" class=\"btn validate\" onclick=\"activateEditMode();\">activate edit mode</a></p>\n";
		}
		
		return templateService.generateWebPage(TITLE, content, roles);
	}
}
