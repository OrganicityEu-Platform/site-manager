package fr.cea.organicity.manager.security;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;


public class HttpClient {

	private Client client;
	private TokenManager tokenManager;

	public HttpClient(Client client, TokenManager tokenManager) {
		this.client = client;
		this.tokenManager = tokenManager;
	}
	
	public WebTarget buildTarget(String urlTemplate, String... keyVal) {
		WebTarget target = client.target(urlTemplate);
		for (int i = 0; i < keyVal.length; i += 2) {
			String key = keyVal[i];
			String value = keyVal[i + 1];
			target = target.resolveTemplate(key, value);
		}
		return target;
	}

	public Builder buildHeaders(WebTarget target) throws IOException {
		return target.request().header("Authorization", "Bearer " + tokenManager.getAuthToken());
	}
	
	public static JSONArray bodyToJsonArray(Response res) {
		if (res.hasEntity()) {
			String body = res.readEntity(String.class);

			if (res.getStatus() == 200) {
				return new JSONArray(body);
			} else {
				throw new RuntimeException("Error HTTP" + res.getStatus() + " while getting roles");
			}
		} else {
			throw new RuntimeException("No body in response while getting roles");
		}
	}
	
	public static JSONObject bodyToJsonObject(Response res) {
		if (res.hasEntity()) {
			String body = res.readEntity(String.class);

			if (res.getStatus() == 200) {
				return new JSONObject(body);
			} else {
				throw new RuntimeException("Error HTTP" + res.getStatus() + " while getting roles");
			}
		} else {
			throw new RuntimeException("No body in response while getting roles");
		}
	}	
}
