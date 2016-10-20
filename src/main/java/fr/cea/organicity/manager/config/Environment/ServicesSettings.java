package fr.cea.organicity.manager.config.Environment;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@ConfigurationProperties(prefix = "organicity.services")
public class ServicesSettings {

	@NotNull
	private String experimenterPortalUrl;
	
	@NotNull
	private String discoveryServiceUrl;
	
	public Map<String, String> getDictionary() {
		Map<String, String> map = new HashMap<>();
		map.put("experimenterPortalUrl", experimenterPortalUrl);
		map.put("discoveryServiceUrl", discoveryServiceUrl);
		return map;
	}
}
