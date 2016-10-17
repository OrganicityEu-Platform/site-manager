package fr.cea.organicity.manager.security;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class RoleManager {

	private final HttpClient client;
	private final ClaimsParser claimsParser;
	private final APIInvoker invoker;
	
	private final int CACHE_SIZE = 1000;
	private final int CACHE_EXPIRE_TIME_IN_SECONDS = 30;
	private final LoadingCache<String, List<Role>> cache;

	public RoleManager(HttpClient client, ClaimsParser claimsParser, APIInvoker invoker) {
		this.client = client;
		this.claimsParser = claimsParser;
		this.invoker = invoker;
		this.cache = createCache(CACHE_SIZE, CACHE_EXPIRE_TIME_IN_SECONDS);
	}

	private LoadingCache<String, List<Role>> createCache(int cacheSize, int expireTimeInSeconds) {
		return CacheBuilder.newBuilder().maximumSize(cacheSize).expireAfterWrite(expireTimeInSeconds, TimeUnit.SECONDS)
				.build(new CacheLoader<String, List<Role>>() {
					public List<Role> load(String userId) {
						return retrieveRoles(userId);
					}
				});
	}

	public List<Role> getRolesForSub(String userId) throws ExecutionException {
		return cache.get(userId);
	}

	public List<Role> getRolesForRequest(HttpServletRequest request) {
		try {
			String id_token = request.getParameter("id_token");
			OCClaims claims = claimsParser.getClaimsFromHeader(id_token);
			String sub = claims.getSub();
			return getRolesForSub(sub);
		} catch (Exception e) {
			return null;
		}
	}
	
	public void addRole(String userId, Role role) {
		WebTarget target = client.buildTarget(SecurityConstants.permWithUserUrl, "userId", userId, "roleName", role.getFullName());
		String url = target.getUri().toString();
		Builder builder = client.buildHeaders(target);
		Invocation invocation = builder.buildPost(Entity.json(role.toJsonObject().toString()));
		Response res = invoker.invoke(invocation, url);
		
		cache.invalidate(userId);

		if (res.getStatus() != 201)
			throw new RuntimeException("Status code = " + res.getStatus());
	}

	public void removeRole(String userId, Role role) {
		WebTarget target = client.buildTarget(SecurityConstants.permWithUserAndRoleUrl, "userId", userId, "roleName", role.getFullName());
		Builder builder = client.buildHeaders(target);
		Response res = builder.delete();
		cache.invalidate(userId);

		if (res.getStatus() != 200)
			throw new RuntimeException("Status code = " + res.getStatus());
	}

	private List<Role> retrieveRoles(String userId) {
		List<Role> roles = new ArrayList<>();

		WebTarget target = client.buildTarget(SecurityConstants.permWithUserUrl, "userId", userId);
		String url = target.getUri().toString();
		Builder builder = client.buildHeaders(target);
		Invocation invocation = builder.buildGet();
		Response res = invoker.invoke(invocation, url);
		
		JSONArray array = HttpClient.bodyToJsonArray(res);
		for (int i = 0; i < array.length(); i++) {
			JSONObject mapping = (JSONObject) array.get(i);
			try {
				roles.add(new Role(mapping));
			} catch (Exception e) {
				throw new RuntimeException("should never happend", e);
			}
		}
		return roles;
	}
}
