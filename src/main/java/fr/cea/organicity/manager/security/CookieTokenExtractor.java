package fr.cea.organicity.manager.security;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

@Service
public class CookieTokenExtractor {

	private final static String COOKIE_KEY = "octoken";
	private final static String ID = "id_token";
	private final static String ACCESS = "access_token";
	
	
	public static Map<String, String> getCookieContent(HttpServletRequest request) {
		String value = getCookieValue(request);
		if (value != null)
			return getCookieContent(value);
		else
			return new HashMap<>();
	}
	
	public static String getCookieID(HttpServletRequest request) {
		return getCookieContent(request).get(ID);
	}
	
	public static String getCookieAccess(HttpServletRequest request) {
		return getCookieContent(request).get(ACCESS);
	}
	
	private static String getCookieValue(HttpServletRequest request) {
		for (Cookie c : request.getCookies()) {
			if (c.getName().equals(COOKIE_KEY)) {
				return c.getValue();
			}
		}
		return null;
	}
	
	private static Map<String, String> getCookieContent(String cookieValue) {
		if (Strings.isNullOrEmpty(cookieValue))
			throw new IllegalArgumentException("Cookie value is null or empty.");
		
		Map<String, String> map = new HashMap<>();
		String[] elements = cookieValue.split("&");
		for (String element: elements) {
			String[] parts = element.split("=");
			if (parts.length != 2) {
				throw new IllegalArgumentException("Part of the cookie content is invalid : " + element);
			}
			map.put(parts[0].trim(), parts[1].trim());
		}
		
		return map;
	}
}
