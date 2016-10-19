package fr.cea.organicity.manager.otherservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.cea.organicity.manager.security.APIInvoker;
import fr.cea.organicity.manager.security.HttpClient;

public class ExperimentLister extends Lister<Experiment> {

	private final HttpClient client;
	private final APIInvoker invoker;
	private static final long TTL_IN_SECONDS = 10;
	
	// Experimenter portal API
	private static final String EXP_PORTAL_ROOT = "http://31.200.243.76:8081"; 
	private static final String EXP_PORTAL_ALL_EXPERIMENT_URL = EXP_PORTAL_ROOT + "/allexperiments";
	private static final String EXP_PORTAL_EXPERIMENT_URL = EXP_PORTAL_ROOT + "/experiments";
	
	// Discovery API 
	private static final String  DISCOVERY_ROOT = "http://api.discovery.organicity.eu/v0/assets/experiments/";
	
	private static final Logger log = LoggerFactory.getLogger(ExperimentLister.class);
	
	public ExperimentLister(HttpClient client, APIInvoker invoker) {
		super(Experiment::getId, TTL_IN_SECONDS);
		this.client = client;
		this.invoker = invoker;
	}

	@Override
	protected List<Experiment> retrieveElements() {
		
		List<Experiment> experiments = new ArrayList<>();
		Response res = null;
		try {
			res = invoker.defaultGet(client, EXP_PORTAL_ALL_EXPERIMENT_URL);
		} catch (Exception e) {
			log.error("[HTTP ERROR] on " + EXP_PORTAL_ALL_EXPERIMENT_URL + " : " + e.getMessage());
			return experiments;
		}
		
		JSONArray array = HttpClient.bodyToJsonObject(res).getJSONArray("experiments");
		
		for (int i = 0; i < array.length(); i++) {
			try {
				JSONObject jsonExperiment = (JSONObject) array.get(i);
				experiments.add(new Experiment(jsonExperiment));
			} catch (Exception e) {
				log.error("[HTTP ERROR] on " + EXP_PORTAL_ALL_EXPERIMENT_URL + " : malformed json object. " + e.getMessage());
				return experiments;
			}
		}
		
		return experiments;
	}

	public List<Experiment> getExperimentsByUser(String userId) {
		List<Experiment> experiments = new ArrayList<>();
		
		for (Experiment exp : getElements()) {
			if (exp.hasExperimenter(userId))
				experiments.add(exp);
		}
		
		return experiments;
	}
	
	public List<String> getDataSrcByExperiment(String experimentId) throws IOException {
		List<String> sources = new ArrayList<>();
		sources.addAll(getDataSrcByExperimentOnExperimenterPortal(experimentId));
		sources.addAll(getDataSrcByExperimentOnObservatory(experimentId));
		return sources;
	}
	
	public List<String> getDataSrcByExperimentOnExperimenterPortal(String experimentId) throws IOException {	
		List<String> sources = new ArrayList<>();
		
		String url = EXP_PORTAL_EXPERIMENT_URL + "/" + experimentId + "/datasources";
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
			log.error("Error while parsing urns in object " + body);
		}
		
		return sources;
	}
	
	public List<String> getDataSrcByExperimentOnObservatory(String experimentId) throws IOException {
		List<String> sources = new ArrayList<>();
		
		String url = DISCOVERY_ROOT + experimentId;
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
			log.error("Error while parsing urns in object " + body);
		}		
		return sources;
	}
}
