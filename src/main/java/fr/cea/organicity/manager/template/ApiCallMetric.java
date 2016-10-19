package fr.cea.organicity.manager.template;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.cea.organicity.manager.domain.OCApiCall;
import fr.cea.organicity.manager.repositories.OCApiCallRepository;
import fr.cea.organicity.manager.security.Role;

public class ApiCallMetric {

	public static String generateHTML(TemplateEngine templateService, List<Role> roles, String title, OCApiCallRepository apiCallRepository, int nb, long threshold, boolean isSuccess) throws IOException {

		StringBuilder sb = new StringBuilder();
						
		sb.append("<h2>" + title + "</h2>\n");

		sb.append("<table>\n");
		sb.append("  <tr><th>Date</th><th>url</th><th>duration</th>\n");

		for (OCApiCall call : getData(apiCallRepository, nb, threshold, isSuccess)) {
			Date date = call.getDate();
			String url = call.getUrl();
			long duration = call.getDuration();
			
			sb.append("  <tr><td>" + date + "</td><td>" + url + "</td><td>" + duration + "ms" + "</td></tr>\n");			
		}
		
		sb.append("</table>\n");

		return templateService.generateWebPage("Metrics", sb.toString(), roles);
	}
	
	private static List<OCApiCall> getData(OCApiCallRepository apiCallRepository, int nb, long threshold, boolean isSuccess) {
		
		Stream<OCApiCall> stream = apiCallRepository.findAll().stream().filter(call -> call.isSuccess() == isSuccess);
		if (isSuccess)
			stream = stream.filter(call -> call.getDuration() > threshold);
		List<OCApiCall> list = stream.collect(Collectors.toList());
		
		if (list.size() > nb) {
			int fromIndex = list.size() - nb; // inclusive
			int toIndex = list.size(); // exclusive
			list = list.subList(fromIndex, toIndex);
		}
		
		// reverse order : latest first remove following line for natural id order
		list.sort((a1, a2) -> ( (int) (a2.getDuration() - a1.getDuration())));
		
		return list;
	}
}
