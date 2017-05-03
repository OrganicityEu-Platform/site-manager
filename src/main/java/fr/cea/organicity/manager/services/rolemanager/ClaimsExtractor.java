package fr.cea.organicity.manager.services.rolemanager;

import java.io.FileNotFoundException;
import java.security.PublicKey;
import java.security.cert.CertificateException;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Strings;

import fr.cea.organicity.manager.services.security.CookieTokenExtractorService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.log4j.Log4j;

@Log4j
public class ClaimsExtractor {

	private final PublicKey pk;

	public ClaimsExtractor(PublicKey pk) throws FileNotFoundException, CertificateException {
		this.pk = pk;
	}

	public Claims getClaimsFromIdToken(String idToken) throws RuntimeException {
		try {
			Claims claims = Jwts.parser().setSigningKey(pk).parseClaimsJws(idToken).getBody();
			if (claims == null)
				throw new RuntimeException("claims is null");
			return claims;
		} catch (ExpiredJwtException e) {
			throw new RuntimeException("Token expired. Please reconnect");
		} catch (MalformedJwtException e) {
			throw new RuntimeException("Malformed token. Please reconnect");
		} catch (Exception e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}

	public String getSubFromRequest(HttpServletRequest request) {
		return getClaimsFromRequest(request).getSubject();
	}

	public Claims getClaimsFromRequest(HttpServletRequest request) {
		String token = getTokenFromRequest(request);
		return getClaimsFromToken(token);
	}
	
	private static String getTokenFromRequest(HttpServletRequest request) {

		// Trying to get the token from the cookie
		try {
			String accessToken = CookieTokenExtractorService.getCookieAccess(request);
			if (accessToken != null) {
				log.trace("Request recieved with cookie access token " + accessToken);
				return accessToken;
			}
		} catch (Exception e) {
			// no cookie ; do nothing
		}

		// Trying to get the token from the header
		String idHeader = request.getHeader("Authorization");
		if (idHeader != null) {
			log.trace("Request recieved with authorisation header " + idHeader);
			return idHeader;
		}

		log.debug("No valid header in request");
		
		// no id available
		return null;
	}

	public Claims getClaimsFromToken(String authHeader) {
		if (Strings.isNullOrEmpty(authHeader))
			throw new IllegalArgumentException("idtoken value is null or empty.");

		authHeader = authHeader.trim();
		if (authHeader.startsWith("Bearer") || authHeader.startsWith("bearer"))
			authHeader = authHeader.substring("Bearer".length());
		authHeader = authHeader.trim();

		return getClaimsFromIdToken(authHeader);
	}
}