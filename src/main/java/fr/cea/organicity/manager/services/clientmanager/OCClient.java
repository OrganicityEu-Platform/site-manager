package fr.cea.organicity.manager.services.clientmanager;

import lombok.Data;

@Data
public class OCClient {
	private String sub;
	private String secret;
	private String client_id;	
}
