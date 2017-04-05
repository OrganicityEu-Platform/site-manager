package fr.cea.organicity.manager.services.security;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

import fr.cea.organicity.manager.config.environment.EnvService;

@Service
public class CallbackUrlBuilderService {
	
	private final String clientName;
	private final String protocol;
	private final String host;
	private final int port;
		
	private static final Logger log = LoggerFactory.getLogger(CallbackUrlBuilderService.class);
	
	@Autowired
	public CallbackUrlBuilderService(EnvService env) {
		clientName = env.getBackendSettings().getClientId();
		protocol = env.getServerSettings().getProtocol();
		host = env.getServerSettings().getHost();
		port = env.getServerSettings().getExposedPort();
	}
		
	public String getUrl(HttpServletRequest request) {
		
		String path = null;
		if (request != null) {
			Map<String, String> query = parseQueryString(request.getQueryString());
			if (query.containsKey("path"))
				path = query.get("path");
			else
				request.getRequestURI();
		}
		
		if (path != null)
			return getUrl("/callback?path=" + path);
		else
			return getUrl("/callback?path=/");
	}
	
	public String getUrl(String path) {
		
		String loginUrl = "https://accounts.organicity.eu/realms/organicity/protocol/openid-connect/auth";
		String responseType = "id_token+token";
		String redirectUri=null;
		try {
			String url = null;
			if (protocol.equals("http") && port == 80)
				url = protocol + "://" + host;	
			else if (protocol.equals("https") && port == 443)
				url = protocol + "://" + host;
			else
				url = protocol + "://" + host + ":" + port;

			if (! Strings.isNullOrEmpty(path)) {
				if (path.startsWith("/"))
					url += path;
				else
					url += "/" + path;
			}
			redirectUri = URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			log.error("url encoding failed", e);
		}
		String url = loginUrl + "?response_type=" + responseType + "&client_id=" + clientName + "&redirect_uri=" + redirectUri;
		return url;
	}
	
	private static Map<String, String> parseQueryString(String qs) {
		HashMap<String, String> map = new HashMap<>();		
		if (!Strings.isNullOrEmpty(qs)) {
			for (String token : qs.split("&")) {
				int idx = token.indexOf("=");
				if (idx != -1) {
					String key = token.substring(0, idx);
					String value = token.substring(idx+1);
					map.put(key, value);
				}
			}
		}
		return map;
	}	
}
