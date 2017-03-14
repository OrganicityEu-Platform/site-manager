package fr.cea.organicity.manager.config.environment;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.Manifest;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j;

@Log4j
@Component
public class ManifestSettings {

	private final String MANIFEST_PATH = "META-INF/MANIFEST.MF";
	
	private final Map<String, String> dictionary = new HashMap<>();
	
	public ManifestSettings() {
		try {
			for (Entry<Object, Object> entry : findEntries()) {
				Object key = entry.getKey();
				Object value = entry.getValue();
				dictionary.put(key.toString(), value.toString());
			}
		} catch (Exception e) {
			log.error("Initialization failed : " + e.getMessage());
		}
	}
	
	private Set<Entry<Object, Object>> findEntries() {
		URLClassLoader cl = (URLClassLoader) getClass().getClassLoader();
		URL url = cl.findResource(MANIFEST_PATH);
		if (url == null)
			throw new RuntimeException("Can't find " + MANIFEST_PATH + ": manifest environment variables will be ignored");

		Manifest manifest;
		try {
			manifest = new Manifest(url.openStream());
		} catch (IOException e) {
			throw new RuntimeException("Can't read " + MANIFEST_PATH + ": manifest environment variables will be ignored");
		}
		
		return manifest.getMainAttributes().entrySet();
	}

	public Map<String, String> getDictionary() {
		return dictionary;
	}
	
	public String getBuildTimestamp() {
		return dictionary.get("timestamp");
	}
}
