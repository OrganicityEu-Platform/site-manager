package fr.cea.organicity.manager.security;

import java.util.Calendar;
import java.util.Date;

import io.jsonwebtoken.Claims;
import lombok.Data;

@Data
public class OCClaims {

	 private final String sub;
	 private final String name;
	 private final String preferred_username;
	 private final String given_name;
	 private final String family_name;
	 private final String email;
	 private final Date issuedAt;
	 private final Date expiresAt;
	
	public OCClaims(Claims claims) throws Exception {
		sub = claims.getSubject();
		name = (String) claims.get("name");
		preferred_username = (String) claims.get("preferred_username");
		given_name = (String) claims.get("given_name");
		family_name = (String) claims.get("family_name");
		email = (String) claims.get("email");
		issuedAt = claims.getIssuedAt();
		expiresAt = claims.getExpiration();
	}
	
	public long stillValidDuringInSeconds() {
		long curentInSec = Calendar.getInstance().getTimeInMillis() / (long) 1000;
		long expInsec = expiresAt.getTime() / (long) 1000;
		return expInsec - curentInSec;
	}
}
