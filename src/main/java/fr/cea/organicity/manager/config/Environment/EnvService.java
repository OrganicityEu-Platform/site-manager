package fr.cea.organicity.manager.config.Environment;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Data;

@Service
@Data
public class EnvService {
	
	@Autowired BackendSettings backendSettings;
	@Autowired FrontendSettings frontendSettings;
	@Autowired ServerSettings serverSettings;
	@Autowired ManifestSettings manifestSettings;
	@Autowired ServicesSettings servicesSettings;
	
	public Map<String, Map<String, String>> getDictionaries() {
		Map<String, Map<String, String>> map = new HashMap<>();
		map.put("backend", backendSettings.getDictionary());
		map.put("frontend", frontendSettings.getDictionary());
		map.put("server", serverSettings.getDictionary());
		map.put("manifest", manifestSettings.getDictionary());
		map.put("services", servicesSettings.getDictionary());
		return map;
	}
	
	public Map<String, String> mergeDictionaries() {
		Map<String, String> map = new HashMap<>();
		for (String key : getDictionaries().keySet())
			map.putAll(getDictionaries().get(key));
		return map;
	}
}
