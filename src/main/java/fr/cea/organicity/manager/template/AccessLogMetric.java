package fr.cea.organicity.manager.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.cea.organicity.manager.domain.OCRequest;
import fr.cea.organicity.manager.otherservices.UserLister;
import fr.cea.organicity.manager.repositories.OCRequestRepository;
import fr.cea.organicity.manager.security.Role;

public class AccessLogMetric {

	public static String generateHTML(TemplateEngine templateService, List<Role> roles, OCRequestRepository accessRepository, UserLister userLister, int nb) throws IOException {

		String title = "Latest access records";
		StringBuilder sb = new StringBuilder();
				
		UserUtil userUtil = new UserUtil(userLister);
		
		sb.append("<h2>" + title + "</h2>\n");

		sb.append("<table>\n");
		sb.append("  <tr><th>Date</th><th>status</th><th>duration</th><th>method</th><th>user</th><th>message</th></tr>\n");

		for (OCRequest access : getData(accessRepository, nb)) {
			Date date = access.getDate();
			String status = access.getStatus();
			String method = access.getMethod();
			String sub = access.getSub();
			String message = access.getMessage();
			long duration = access.getDuration();
			
			sb.append("  <tr><td>" + date + "</td><td>" + status + "</td><td>" + duration + "ms" + "</td><td>" + method + "</td><td>" + userUtil.getUserDisplayString(sub)
					+ "</td><td>" + message + "</td></tr>\n");			
		}
		
		sb.append("</table>\n");

		return templateService.generateWebPage("Metrics", sb.toString(), roles);
	}
	
	private static List<OCRequest> getData(OCRequestRepository accessRepository, int nb) {
		List<OCRequest> list = new ArrayList<>();
		accessRepository.findAll().forEach(list::add);

		if (list.size() > nb) {
			int fromIndex = list.size() - nb; // inclusive
			int toIndex = list.size(); // exclusive
			list = list.subList(fromIndex, toIndex);
		}
		
		// reverse order : latest first remove following line for natural id order
		list.sort((a1, a2) -> ((int) (a2.getId() - a1.getId())));
		
		return list;
	}
}
