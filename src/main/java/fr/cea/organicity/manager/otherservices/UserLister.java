package fr.cea.organicity.manager.otherservices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import fr.cea.organicity.manager.security.APIInvoker;
import fr.cea.organicity.manager.security.HttpClient;
import fr.cea.organicity.manager.security.SecurityConstants;

public class UserLister {

	private final HttpClient client;
	private final APIInvoker invoker;
	private static final long AUTOREFRESH_DELAY_MILLIS = 60000;
	private static final int maxUsersPerRequest = 50;
	private List<User> users = new ArrayList<>();
		
	private static final Logger log = LoggerFactory.getLogger(UserLister.class);
	
	public UserLister(HttpClient client, APIInvoker invoker) {
		this.client = client;
		this.invoker = invoker;
	}

	@Scheduled(fixedRate = AUTOREFRESH_DELAY_MILLIS)
	public void updateInfo() {
		log.debug("[AUTOREFRESH] triggerd for users");
		long start = System.currentTimeMillis();
		users = retrieveElements();
		long duration = System.currentTimeMillis() - start;
		log.debug("[AUTOREFRESH] ended for users after " + duration + "ms");
	}
	
	public List<User> getElements() {
		return users;
	}
	
	public User getElement(String id) {
		return users.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
	}
	
	protected List<User> retrieveElements() {
		List<User> users = new ArrayList<>();
		int offset = 0;
		int count = maxUsersPerRequest;
		
		while (true) {
			List<User> cur = retrieveUsers(offset, count);
			if (cur.isEmpty())
				return users;
			users.addAll(cur);
			offset += count;
		}
	}
	
	private List<User> retrieveUsers(int offset, int count) {
		List<User> users = new ArrayList<>();
		
		String url = SecurityConstants.usersUrl + "?offset=" + offset + "&count=" + count;
		Response res = invoker.defaultGet(client, url, false);
		
		JSONArray array = HttpClient.bodyToJsonArray(res);
		for (int i = 0; i < array.length(); i++) {
			JSONObject mapping = (JSONObject) array.get(i);
			try {
				String id = mapping.getString("id");
				String name = mapping.getString("name");
				users.add(new User(id, name));
			} catch (Exception e) {
				throw new RuntimeException("should never happend", e);
			}
		}
		
		return users;
	}
}
