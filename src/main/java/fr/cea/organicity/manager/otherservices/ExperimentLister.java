package fr.cea.organicity.manager.otherservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.cea.organicity.manager.security.APIInvoker;
import fr.cea.organicity.manager.security.HttpClient;

public class ExperimentLister {

	private final HttpClient client;
	private final APIInvoker invoker;
	
	private final String experimenterPortalUrlAll;
	private final String experimenterPortalUrlOne;
	private final String discoveryServiceExperimentsUrl;
	
	private static final long TTL_IN_SECONDS = 10;
	
	private ThirdPartyResult<List<Experiment>> value = new ThirdPartyResult<List<Experiment>>(null, 0, false, 0, 0);
		
	private static final Logger log = LoggerFactory.getLogger(ExperimentLister.class);
	
	public ExperimentLister(HttpClient client, APIInvoker invoker, String experimenterPortalUrl, String discoveryServiceUrl) {
		this.client = client;
		this.invoker = invoker;
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
		Experiment experiment = curVal.getLastSuccessResult().stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
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
		
		List<Experiment> result = curVal.getLastSuccessResult();
		long result_ts = curVal.getLastSuccessTimestamp();
		
		long start = System.currentTimeMillis();
		
		// HTTP request
		List<Experiment> experiments = new ArrayList<>();
		Response res = null;
		try {
			res = invoker.defaultGet(client, experimenterPortalUrlAll);
		} catch (Exception e) {
			log.error("[HTTP ERROR] on " + experimenterPortalUrlAll + " : " + e.getMessage());
			long duration = System.currentTimeMillis() - start;
			return new ThirdPartyResult<List<Experiment>>(result, result_ts, false, duration, start);
		}
		
		// Json parsing
		try {
			JSONArray array = HttpClient.bodyToJsonObject(res).getJSONArray("experiments");
			for (int i = 0; i < array.length(); i++) {
				JSONObject jsonExperiment = (JSONObject) array.get(i);
				experiments.add(new Experiment(jsonExperiment));
			}
		} catch (Exception e) {
			log.error("[HTTP ERROR] on " + experimenterPortalUrlAll + " : malformed json object. " + e.getMessage());
			long duration = System.currentTimeMillis() - start;
			return new ThirdPartyResult<List<Experiment>>(result, result_ts, false, duration, start);
		}
		
		long duration = System.currentTimeMillis() - start;
		return new ThirdPartyResult<List<Experiment>>(experiments, start, true, duration, start);
	}
	
	public List<String> getDataSrcByExperiment(String experimentId) throws IOException {
		List<String> sources = new ArrayList<>();
		sources.addAll(getDataSrcByExperimentOnExperimenterPortal(experimentId));
		sources.addAll(getDataSrcByExperimentOnObservatory(experimentId));
		return sources;
	}
	
	public List<String> getDataSrcByExperimentOnExperimenterPortal(String experimentId) throws IOException {	
		List<String> sources = new ArrayList<>();
		
		String url = experimenterPortalUrlOne + "/" + experimentId + "/datasources";
		Response res = invoker.defaultGet(client, url);
		
		String body = res.readEntity(String.class);

		// TODO 2016-09-16 Experimentation Manager API --> Ask Luis D. when it will be updated
		try {
			JSONObject json = new JSONObject(body);
			JSONArray urns = json.getJSONArray("urns");
			for (int i=0; i< urns.length(); i++) {
				String urn = urns.getString(i);
				sources.add(urn);
			}
		} catch (Exception e) {
			log.error("Url " + url + " doesn't return a valid json object: \"" + body + "\"");
		}
		
		return sources;
	}
	
	public List<String> getDataSrcByExperimentOnObservatory(String experimentId) throws IOException {
		List<String> sources = new ArrayList<>();
		
		String url = discoveryServiceExperimentsUrl + experimentId;
		Response res = invoker.defaultGet(client, url);
		
		String body = res.readEntity(String.class);

		// TODO 2016-09-16 Discovery API --> Ask Silvia when it will be updated
		try {
			JSONArray aray = new JSONArray(body);
			for (int i=0; i< aray.length(); i++) {
				JSONObject object = aray.getJSONObject(i);
				sources.add(object.getString("id"));
			}
		} catch (Exception e) {
			log.error("Url " + url + " doesn't return a valid json object: \"" + body + "\"");
		}		
		return sources;
	}
}
