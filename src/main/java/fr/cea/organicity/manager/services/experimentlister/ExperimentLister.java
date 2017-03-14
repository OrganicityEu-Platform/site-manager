package fr.cea.organicity.manager.services.experimentlister;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import fr.cea.organicity.manager.services.ThirdPartyResult;

public class ExperimentLister {

	private final RestTemplate restTemplate;
	private final String experimenterPortalUrlAll;
	private final String experimenterPortalUrlOne;
	private final String discoveryServiceExperimentsUrl;
	
	private static final long TTL_IN_SECONDS = 10;
	
	private ThirdPartyResult<List<Experiment>> value = new ThirdPartyResult<List<Experiment>>(null, 0, false, 0, 0);
		
	private static final Logger log = LoggerFactory.getLogger(ExperimentLister.class);
	
	public ExperimentLister(RestTemplate restTemplate, String experimenterPortalUrl, String discoveryServiceUrl) {
		this.restTemplate = restTemplate;
		this.experimenterPortalUrlAll = experimenterPortalUrl + "/allexperiments";
		this.experimenterPortalUrlOne = experimenterPortalUrl + "/experiments";
		this.discoveryServiceExperimentsUrl = discoveryServiceUrl + "/assets/experiments/";		
	}
	
	public ThirdPartyResult<List<Experiment>> getExperiments() {
		if (needupdate()) {
			value = retrieveExperiments();
		}
		return value;
	}
	
	public ThirdPartyResult<List<Experiment>> getCachedValue() {
		return value;
	}

	public ThirdPartyResult<Experiment> getExperiment(String id) {
		// by this way, no need to synchronize because ThirdPartyResult is immutable
		ThirdPartyResult<List<Experiment>> curVal = getExperiments(); 
				
		// never succeed :-(
		if ( ! curVal.hasAlreadySucceed()) {
			return new ThirdPartyResult<Experiment>(null, 0, false, curVal.getLastCallduration(), curVal.getLastCallTimestamp());
		} 
				
		// Already succeed :-)
		Experiment experiment = curVal.getLastSuccessResult().stream().filter(e -> e.getExperimentId().equals(id)).findFirst().orElse(null);
		return new ThirdPartyResult<Experiment>(experiment, curVal.getLastSuccessTimestamp(), curVal.isLastCallSucess(), curVal.getLastCallduration(), curVal.getLastCallTimestamp());
	}
	
	public ThirdPartyResult<List<Experiment>> getExperimentsByUser(String userId) {
		// by this way, no need to synchronize because ThirdPartyResult is immutable
		ThirdPartyResult<List<Experiment>> curVal = getExperiments();
		
		// never succeed :-(
		if ( ! curVal.hasAlreadySucceed()) {
			return new ThirdPartyResult<List<Experiment>>(null, 0, false, curVal.getLastCallduration(), curVal.getLastCallTimestamp());
		} 
						
		// Already succeed :-)
		List<Experiment> experiments = curVal.getLastSuccessResult().stream().filter(exp -> exp.hasExperimenter(userId)).collect(Collectors.toList());
		return new ThirdPartyResult<List<Experiment>>(experiments, curVal.getLastSuccessTimestamp(), curVal.isLastCallSucess(), curVal.getLastCallduration(), curVal.getLastCallTimestamp());
	}
	
	private boolean needupdate() {
		// by this way, no need to synchronize because ThirdPartyResult is immutable
		ThirdPartyResult<List<Experiment>> curVal = value; 
		
		if (! curVal.hasAlreadySucceed())
			return true;
		
		long cur = System.currentTimeMillis();
		if (cur - curVal.getLastCallTimestamp() > TTL_IN_SECONDS * 1000)
			return true;
		
		return false;
	}
	
	private ThirdPartyResult<List<Experiment>> retrieveExperiments() {
		// by this way, no need to synchronize because ThirdPartyResult is immutable
		ThirdPartyResult<List<Experiment>> curVal = value;
		List<Experiment> lastSuccessResult = curVal.getLastSuccessResult();
		long lastCallTimestamp = System.currentTimeMillis();
		long lastCallDuration;
		boolean lastCallSuccess;
		
		ParameterizedTypeReference<Experiments> typeRef = new ParameterizedTypeReference<Experiments>() {};
		try {
			ResponseEntity<Experiments> response = restTemplate.exchange(experimenterPortalUrlAll, HttpMethod.GET, null, typeRef);
			lastSuccessResult = response.getBody().getExperiments();
			lastCallSuccess = true;
		} catch (Exception e) {
			lastCallSuccess = false;
			log.error("[HTTP ERROR] on " + experimenterPortalUrlAll + " : " + e.getMessage());
		}

		lastCallDuration = System.currentTimeMillis() - lastCallTimestamp;
		
		long lastSucessTimestamp;
		if (lastCallSuccess)
			lastSucessTimestamp = lastCallTimestamp;
		else
			lastSucessTimestamp = curVal.getLastSuccessTimestamp();
		
		return new ThirdPartyResult<List<Experiment>>(lastSuccessResult, lastSucessTimestamp, lastCallSuccess, lastCallDuration, lastCallTimestamp);
	}
	
	public List<String> getDataSrcByExperiment(String experimentId) throws IOException {
		List<String> sources = new ArrayList<>();
		
		// TODO update this asking Luisco
		// Get on experimenter portal
		// NOT POSSIBLE NOW SINCE IT IS NOT POSSIBLE TO ASK FOR THE INFO OF THE EXPERIMENTS
		// BELONGING TO ANOTHER USER
		// sources.addAll(getDataSrcByExperimentOnExperimenterPortal(experimentId));
		
		// Get on UDO
		for (UDOExperiment exp : getDataSrcByExperimentOnObservatory(experimentId))
			sources.add(exp.getId());
		
		return sources;
	}
	
	// API : http://experimenters.organicity.eu:8081/docs/#/
	private List<String> getDataSrcByExperimentOnExperimenterPortal(String experimentId) throws IOException {	
		List<String> sources = new ArrayList<>();
				
		ThirdPartyResult<Experiment> experiment = getExperiment(experimentId);
		if (experiment.hasAlreadySucceed()) {
			
			String url = experimenterPortalUrlOne + "/" + experimentId + "/datasources";
			
			try {
				// TODO this line haven't been tested after refactoring  
				String body = restTemplate.getForObject(url, String.class);
				JSONObject json = new JSONObject(body);
				JSONArray urns = json.getJSONArray("urns");
				for (int i=0; i< urns.length(); i++) {
					String urn = urns.getString(i);
					sources.add(urn);
				}
			} catch (Exception e) {
				log.error("Error while calling " + url);
			}
		} else {
			log.error("Can't find experiment with id " + experimentId);
		}
		
		return sources;
	}
	
	private List<UDOExperiment> getDataSrcByExperimentOnObservatory(String experimentId) throws IOException {
		List<UDOExperiment> experiments;
		
		try {
			String url = discoveryServiceExperimentsUrl + experimentId;
			ParameterizedTypeReference<List<UDOExperiment>> typeRef = new ParameterizedTypeReference<List<UDOExperiment>>() {};
			ResponseEntity<List<UDOExperiment>> response = restTemplate.exchange(url, HttpMethod.GET, null, typeRef);
			experiments = response.getBody();
		} catch (Exception e) {
			log.error("[HTTP ERROR] on getDataSrcByExperimentOnObservatory : " + e.getMessage());
			experiments = new ArrayList<>();;
		}
		
		return experiments;
	}
}
