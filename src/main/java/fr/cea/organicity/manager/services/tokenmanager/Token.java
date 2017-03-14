package fr.cea.organicity.manager.services.tokenmanager;

import lombok.Data;

@Data
public class Token {
	private String access_token;
	private long expires_in;
	private long refresh_expires_in;
	private String refresh_token;
	private String token_type;
	private String id_token;
}
