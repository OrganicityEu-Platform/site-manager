package fr.cea.organicity.manager.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.cea.organicity.manager.domain.OCError;
import fr.cea.organicity.manager.repositories.OCErrorRepository;
import fr.cea.organicity.manager.security.Role;

public class ErrorMetric {

	public static String generateHTML(TemplateEngine templateService, List<Role> roles, OCErrorRepository errorRepository, String title, int nbjours) throws IOException {
		
		List<OCError> errors = getErrorList(errorRepository, nbjours);
		
		// reverse order : latest first remove following line for natural id order
		errors.sort((e1, e2) -> ((int) (e2.getId() - e1.getId())));
		
		String content = "<h2>" + title + "</h2>\n";
		
		if (errors.isEmpty())
			content += "<p>No errors found during the last " + nbjours + " days.</p>";
		else
			content += generateTable(errors);
				
		return templateService.generateWebPage("Metrics", content, roles);
	}
	
	private static List<OCError> getErrorList(OCErrorRepository errorRepository, int nbjours) {
		
		List<OCError> errors = new ArrayList<>();
		Date now = new Date();
		long maxdiff = ((long) nbjours) * ((long) 24*60*60*1000); 
		
		for (OCError error : errorRepository.findAll()) {
			long nowMillis = now.getTime();
			long errMillis = error.getDate().getTime();
			if (nowMillis - errMillis < maxdiff)
				errors.add(error);
		}
		
		return errors;
	}
	
	private static String generateTable(List<OCError> errors) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<table>\n");
		sb.append("  <tr><th>id</th><th>date</th><th>status</th><th>path</th><th>message</th></tr>\n");

		for (OCError error : errors) {
			long id = error.getId();
			Date date = error.getDate();
			String statusError = "<abbr title=\"" + error.getError() + "\">" + error.getStatus() + "</abbr>";
			String path = error.getPath();
			String message = error.getMessage();
			sb.append("  <tr><td>" + id +"</td><td>" + date + "</td><td>" + statusError + "</td><td>" + path + "</td><td>" + message + "</td></tr>\n");
		}
		sb.append("</table>\n");
		
		return sb.toString();
	}
}
