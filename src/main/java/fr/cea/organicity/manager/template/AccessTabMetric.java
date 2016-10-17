package fr.cea.organicity.manager.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import fr.cea.organicity.manager.domain.OCRequest;
import fr.cea.organicity.manager.otherservices.UserLister;
import fr.cea.organicity.manager.repositories.OCRequestRepository;
import fr.cea.organicity.manager.security.Role;
import lombok.Data;

public class AccessTabMetric {

	public static String generateHTML(TemplateEngine templateService, List<Role> roles, OCRequestRepository accessRepository, UserLister userLister, String sectionTitle, String pageTitle, int nbjours) throws IOException {
		
		List<OCRequest> accessList = getAccessList(accessRepository, nbjours);
		
		String content = "<h2>" + pageTitle + "</h2>\n";
		
		content += "<p><strong>Per method analysis</strong></p>"; 
		content += generateTab(accessList, "method", OCRequest::getMethod, null);
		
		content += "<p><strong>Per user analysis</strong></p>";
		content += generateTab(accessList, "user", OCRequest::getSub, new UserUtil(userLister));
				
		return templateService.generateWebPage(sectionTitle, content, roles);
	}

	private static List<OCRequest> getAccessList(OCRequestRepository accessRepository, int nbjours) {
		List<OCRequest> accessList = new ArrayList<>();
		Date now = new Date();
		long maxdiff = ((long) nbjours) * ((long) 24*60*60*1000); 
		
		for (OCRequest access : accessRepository.findAll()) {
			long nowMillis = now.getTime();
			long errMillis = access.getDate().getTime();
			if (nowMillis - errMillis < maxdiff)
				accessList.add(access);
		}
		
		return accessList;
	}
	
	private static String generateTab(List<OCRequest> requestList, String keyName, Function<OCRequest, String> getKeyFunc, UserUtil userutil) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<table>\n");
		sb.append("  <tr><th>" + keyName + "</th><th>success</th><th>failure</th><th>total</th></tr>\n");

		Map<String, AccessAnalysis> data = generateData(requestList, getKeyFunc);
		for (String key : data.keySet()) {
			AccessAnalysis value = data.get(key);
			if (userutil != null)
				key = userutil.getUserDisplayString(key);			
			sb.append("  <tr><td>" + key + "</td><td>" + value.getSuccess() + "</td><td>" + value.getFailure() + "</td><td>" + value.getTotal() + "</td></td>\n");
		}
		
		sb.append("</table>\n");
		
		return sb.toString();
	}
	
	private static Map<String, AccessAnalysis> generateData(List<OCRequest> requestList, Function<OCRequest, String> getKeyFunc) {
		
		Map<String, AccessAnalysis> map = new HashMap<>();
		for (OCRequest request : requestList) {
			String key = getKeyFunc.apply(request);
			AccessAnalysis value = map.get(key);
			if (value == null) {
				value = new AccessAnalysis();
				map.put(key, value);
			}
			if (request.getStatus().equals("AUTHORISED"))
				value.incSuccess();
			else
				value.incFailure();
		}
		return map;
	}
	
	@Data
	private static class AccessAnalysis {
		private int success = 0;
		private int failure = 0;
		
		public int getTotal() {
			return success + failure;
		}
		
		public void incSuccess() {
			success++;
		}
		
		public void incFailure() {
			failure++;
		}
	}
}
