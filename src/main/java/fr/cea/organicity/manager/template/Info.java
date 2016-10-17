package fr.cea.organicity.manager.template;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import fr.cea.organicity.manager.otherservices.Experiment;
import fr.cea.organicity.manager.otherservices.ExperimentLister;
import fr.cea.organicity.manager.security.OCClaims;
import fr.cea.organicity.manager.security.Role;
import fr.cea.organicity.manager.security.SecurityConfig;

public class Info {

	private static final String TITLE = "My Account";
	
	private static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	
	public static String generateHTML(TemplateEngine templateService, SecurityConfig secuConfig, String id_token, String access_token, OCClaims claims, List<Role> roles, ExperimentLister experimentLister) throws ExecutionException, IOException {

		Map<String, String> dictionary = new HashMap<>();
		dictionary.put("user-name", claims.getName());
		dictionary.put("user-preferred-name", claims.getPreferred_username());
		dictionary.put("user-given-name", claims.getGiven_name());
		dictionary.put("user-family-name", claims.getFamily_name());
		dictionary.put("user-email", claims.getEmail());

		dictionary.put("roles-global", displayRoles("global", roles.stream().filter(Role::isRealmRole).collect(Collectors.toList())));
		dictionary.put("roles-local", displayRoles("local", roles.stream().filter(r -> r.isRealmRole() == false).collect(Collectors.toList())));
		if (roles.contains(secuConfig.ROLE_ADMIN_ROLE))
			dictionary.put("roles-update", "<br/>\n" + TemplateEngine.createNavigateLink("users/" + claims.getSub(), "Update your local roles"));
		else
			dictionary.put("roles-update", "");
		
		dictionary.put("experiments", displayExperiments(claims.getSub(), experimentLister));
		
		dictionary.put("token-sub", claims.getSub());
		dictionary.put("token-isa", df.format(claims.getIssuedAt()));
		dictionary.put("token-exp", df.format(claims.getExpiresAt()));
		dictionary.put("token-id", id_token);
		dictionary.put("token-access", access_token);
		
		String content = templateService.stringFromTemplate("/templates/user-info.html", dictionary);
		
		return templateService.generateWebPage(TITLE, content, roles);
	}

	private static String displayRoles(String name, List<Role> roles) {
		String content = "<strong>User has " + roles.size() + " " + name + " role";
		if (roles.size() != 1)
			content += "s";
		content += "</strong>";
		if (roles.size() != 0) {
			content += ": ";
			for (int i=0; i< roles.size(); i++) {
				if (i != 0)
					content += ", ";
				content += roles.get(i).getRoleName();
			}
		}
		content += "<br/>\n";

		return content;
	}
	
	private static String displayExperiments(String userSub, ExperimentLister experimentLister) {
		List<Experiment> experiments = experimentLister.getExperimentsByUser(userSub);
		String content = "<strong>User has " + experiments.size() + " expriment";
		if (experiments.size() != 1)
			content += "s";
		content += "</strong>";
		
		if (experiments.size() != 0) {
			content += ": ";
			for (int i=0; i< experiments.size(); i++) {
				if (i != 0)
					content += ", ";
				Experiment exp = experiments.get(i);
				content += TemplateEngine.createNavigateLink("/experiments/" + exp.getId(), exp.getName()); 
			}
		}

		return content;
	}
}
