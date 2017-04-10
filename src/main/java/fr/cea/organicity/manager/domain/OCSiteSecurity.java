package fr.cea.organicity.manager.domain;

import java.util.Set;

import fr.cea.organicity.manager.services.clientmanager.OCClient;
import lombok.Data;

@Data
public class OCSiteSecurity {
	private final OCClient client;
	private final Set<String> managers;
}
