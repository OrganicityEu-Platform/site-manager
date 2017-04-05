package fr.cea.organicity.manager.controllers.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.cea.organicity.manager.domain.OCApiCall;
import fr.cea.organicity.manager.domain.OCError;
import fr.cea.organicity.manager.domain.OCRequest;
import fr.cea.organicity.manager.repositories.OCApiCallRepository;
import fr.cea.organicity.manager.repositories.OCErrorRepository;
import fr.cea.organicity.manager.repositories.OCRequestRepository;
import fr.cea.organicity.manager.services.ThirdPartyResult;
import fr.cea.organicity.manager.services.experimentlister.ExperimentLister;
import fr.cea.organicity.manager.services.userlister.User;
import fr.cea.organicity.manager.services.userlister.UserLister;
import lombok.Data;

@Controller
@RequestMapping("/metrics")
public class MetricsControllerUI {
	
	@Autowired private OCRequestRepository accessRepository;
	@Autowired private UserLister userLister;
	@Autowired private ExperimentLister experimentLister; 
	@Autowired private OCApiCallRepository apiCallRepository;
	@Autowired private OCErrorRepository errorRepository;
		
	private final String title = "Metrics";
	
	@GetMapping
	@PreAuthorize("hasRole('APP:metrics')")
	public String metrics(Model model) {
		model.addAttribute("title", title);
		return "thmetrics";
	}
	
	@GetMapping("access/today")
	@PreAuthorize("hasRole('APP:metrics')")
	public String metricsAccessToday(Model model) {
		List<OCRequest> accessList = getAccessList(accessRepository, 1);
		model.addAttribute("title", title);
		model.addAttribute("subtitle", "Today's access records");
		model.addAttribute("perMethod", generateStats(accessList, "method", OCRequest::getMethod, null));
		model.addAttribute("perUser", generateStats(accessList, "user", OCRequest::getSub, userLister));		
		return "thaccessrecords";
	}
	
	@GetMapping("access/week")
	@PreAuthorize("hasRole('APP:metrics')")
	public String metricsAccessWeek(Model model) {
		List<OCRequest> accessList = getAccessList(accessRepository, 7);
		model.addAttribute("title", title);
		model.addAttribute("subtitle", "Past week access records");
		model.addAttribute("perMethod", generateStats(accessList, "method", OCRequest::getMethod, null));
		model.addAttribute("perUser", generateStats(accessList, "user", OCRequest::getSub, userLister));		
		return "thaccessrecords";
	}
	
	@GetMapping("access/log")
	@PreAuthorize("hasRole('APP:metrics')")
	public String metricsAccessLog(Model model) {
		model.addAttribute("title", title);
		model.addAttribute("logs", getLastLogs(100));		
		return "thlogrecords";
	}
	
	@GetMapping("api/status")
	@PreAuthorize("hasRole('APP:metrics')")
	public String metricsApiStatus(Model model) {
		model.addAttribute("title", title);

		model.addAttribute("userLastSuccess", getLastSuccessString(userLister.getUsers()));
		model.addAttribute("userLastCall", getLastCallString(userLister.getUsers()));
		
		model.addAttribute("expLastSuccess", getLastSuccessString(experimentLister.getExperiments()));
		model.addAttribute("expLastCall", getLastCallString(experimentLister.getExperiments()));
		
		return "thapistatus";
	}
	
	@GetMapping("api/longest")
	@PreAuthorize("hasRole('APP:metrics')")
	public String metricsApiLongest(Model model) {
		model.addAttribute("title", title);
		model.addAttribute("records", getApiLongestCalls(100, false));		
		return "thapilongest";
	}

	@GetMapping("api/failed")
	@PreAuthorize("hasRole('APP:metrics')")
	public String metricsAccessFailed(Model model) {
		model.addAttribute("title", title);
		model.addAttribute("records", getApiLongestCalls(100, true));		
		return "thapifailed";
	}
	
	@GetMapping("errors/today")
	@PreAuthorize("hasRole('APP:metrics')")
	public String metricsErrorsToday(Model model) {
		model.addAttribute("title", title);
		model.addAttribute("subtitle", "Today's errors");
		model.addAttribute("errors", getServerErrors(1));
		
		return "thservererrors";
	}

	@GetMapping("errors/week")
	@PreAuthorize("hasRole('APP:metrics')")
	public String metricsErrorsWeek(Model model) {
		model.addAttribute("title", title);
		model.addAttribute("subtitle", "Week's errors");
		model.addAttribute("errors", getServerErrors(7));
		
		return "thservererrors";
	}
	
	
	/* ==================================== */
	/* FUNCTIONS FOR ACCESS RECORDS METRICS */
	/* ==================================== */
	
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
	
	private static Map<String, AccessAnalysis> generateStats(List<OCRequest> requestList, String keyName, Function<OCRequest, String> getKeyFunc, UserLister userLister) {
		
		Map<String, AccessAnalysis> dataWithId = generateData(requestList, getKeyFunc);
		Map<String, AccessAnalysis> dataWithName = new HashMap<>();

		for (String key : dataWithId.keySet()) {
			AccessAnalysis value = dataWithId.get(key);
			String name = key;
			if (userLister != null) {
				User user = userLister.getUser(key).getLastSuccessResult();
				if (user != null)
					name = user.getName();
			}
			dataWithName.put(name, value);
		}		
		return dataWithName;
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
		
		@SuppressWarnings("unused") // used in thymeleaf template
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
	
	
	/* ========================== */
	/* FUNCTIONS FOR LATESTS LOGS */
	/* ========================== */
	
	private List<OCRequest> getLastLogs(int nb) {
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
	
	
	/* ======================== */
	/* FUNCTIONS FOR API STATUS */
	/* ======================== */
	
	private static String getLastSuccessString(ThirdPartyResult<?> thirdParty) {
		if (thirdParty.getLastSuccessTimestamp() == 0) {
			return "no success";
		} else {
			long ts = thirdParty.getLastSuccessTimestamp();
			return generateAgo(ts);
		}
	}
	
	private static String getLastCallString(ThirdPartyResult<?> thirdParty) {
		if (thirdParty.getLastCallTimestamp() == 0) {
			return "no calls already performed";
		} else {
			String lastCall = thirdParty.isLastCallSucess() ? "SUCCESS" : "FAILURE";
			lastCall += " - " + thirdParty.getLastCallduration() + "ms";
			lastCall += " - " + generateAgo(thirdParty.getLastCallTimestamp());
			return lastCall;
		}
	}
		
	private static String generateAgo(long timestamp) {
		long cur = System.currentTimeMillis();
		long s = (cur - timestamp) / 1000;
		return s + "s ago (" + new Date(timestamp).toString() + ")";
	}
	
	
	/* ======================= */
	/* FUNCTIONS FOR API CALLS */
	/* ======================= */
	
	private List<OCApiCall> getApiLongestCalls(int nb, boolean onlyfailed) {
		
		List<OCApiCall> list = apiCallRepository.findAll(); 
		
		if (onlyfailed)
			list.removeIf(call -> call.isSuccess());

		// Get only the nb latest registered records
		if (list.size() > nb) {
			int fromIndex = list.size() - nb; // inclusive
			int toIndex = list.size(); // exclusive
			list = list.subList(fromIndex, toIndex);
		}
		
		// Sort longest first
		list.sort((a1, a2) -> ( (int) (a2.getDuration() - a1.getDuration())));
		
		return list;
	}

	
	/* ========================== */
	/* FUNCTIONS FOR SERVER ERROR */
	/* ========================== */
	
	private List<OCError> getServerErrors(int nbDays) {
		List<OCError> errors = new ArrayList<>();
		Date now = new Date();
		long maxdiff = ((long) nbDays) * ((long) 24*60*60*1000); 
		
		for (OCError error : errorRepository.findAll()) {
			long nowMillis = now.getTime();
			long errMillis = error.getDate().getTime();
			if (nowMillis - errMillis < maxdiff)
				errors.add(error);
		}
		
		// reverse order : latest first remove following line for natural id order
		errors.sort((e1, e2) -> ((int) (e2.getId() - e1.getId())));
		
		return errors;
	}	
}
