package fr.cea.organicity.manager.security;

import java.io.FileNotFoundException;
import java.security.PublicKey;
import java.security.cert.CertificateException;

import com.google.common.base.Strings;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;

public class ClaimsParser {
	
	private final PublicKey pk;
	
	public ClaimsParser(PublicKey pk) throws FileNotFoundException, CertificateException {
		this.pk = pk;
	}

	public OCClaims getClaimsFromIdToken(String idToken) throws RuntimeException {
		try {
			Claims claims = Jwts.parser().setSigningKey(pk).parseClaimsJws(idToken).getBody();
			if (claims == null)
				throw new RuntimeException("claims is null");
			return new OCClaims(claims);
		} catch (ExpiredJwtException e) {
			throw new RuntimeException("Token expired. Please reconnect");
		} catch (MalformedJwtException e) {
			throw new RuntimeException("Malformed token. Please reconnect");
		} catch (Exception e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}
	
	public OCClaims getClaimsFromHeader(String authHeader) {
		if (Strings.isNullOrEmpty(authHeader))
			throw new IllegalArgumentException("Authorization header is null or empty.");
		authHeader = authHeader.trim();
		
		if (authHeader.startsWith("Bearer") || authHeader.startsWith("bearer"))
			authHeader = authHeader.substring("Bearer".length());
		authHeader = authHeader.trim();
		
		return getClaimsFromIdToken(authHeader);
	}
}