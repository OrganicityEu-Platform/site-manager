package fr.cea.organicity.manager.security;

import java.security.PublicKey;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.json.JSONObject;


public class TokenManager {

	private final PublicKey pk;
	private final Credentials credentials;	
	private final Client client;	
	private final APIInvoker invoker;
	
	private String authToken = null;
	
	/** The token must be valid now and during this period in seconds to be considered up to date */ 
	private final long SECURITY_PERIOD = 30;
	
	public TokenManager(PublicKey pk, Credentials credentials, Client client, APIInvoker invoker) {
		this.pk = pk;
		this.credentials = credentials;
		this.client = client;
		this.invoker = invoker;
	}
	
	public String getAuthToken(boolean forceRenew) {
		if (forceRenew || !isUpToDateToken())
			authToken = renewAuthToken(client, credentials);
		return authToken;
	}
	
	public String getAuthToken() {
		return getAuthToken(false);
	}
	
	private boolean isUpToDateToken() {
		
		if (authToken == null)
			return false;
		
		// Extract claims
		OCClaims claim = null;
		try {
			claim = new ClaimsParser(pk).getClaimsFromIdToken(authToken);
		} catch (Exception e) {
			// token expired
			return false;
		}

		if (claim.getExpiresAt() != null && claim.stillValidDuringInSeconds() < SECURITY_PERIOD)
			return false;

		return true;
	}
	
	private String renewAuthToken(Client client, Credentials credentials) {
		String url = SecurityConstants.connectUrl;
		String auth = credentials.getAuthString();		
		Invocation invocation = client.target(url).request().header("Authorization", "Basic " + auth)
				.buildPost(Entity.form(new Form("grant_type", "client_credentials")));	
		Response res = invoker.invoke(invocation, url);

		if (res.hasEntity()) {
			String body = res.readEntity(String.class);

			if (res.getStatus() == 200) {
				JSONObject reply = new JSONObject(body);
				String token = reply.getString("access_token");
				if (token == null || token.isEmpty())
					throw new RuntimeException("null or empty token returned by server");
				return token;
			} else {
				throw new RuntimeException("Error HTTP" + res.getStatus() + " while getting token");				
			}
		} else {
			throw new RuntimeException("No body in response while getting token");
		}
	}
}
