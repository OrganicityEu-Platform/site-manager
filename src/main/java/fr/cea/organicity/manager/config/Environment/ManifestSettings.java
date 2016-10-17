package fr.cea.organicity.manager.config.Environment;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.springframework.stereotype.Component;

@Component
public class ManifestSettings {

	private final String MANIFEST_PATH = "META-INF/MANIFEST.MF";
	
	private final Map<String, String> dictionary = new HashMap<>();
	
	public ManifestSettings() throws IOException {
		for (Entry<Object, Object> entry : findEntries()) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			dictionary.put(key.toString(), value.toString());
		}
	}
	
	private Set<Entry<Object, Object>> findEntries() throws IOException {
		URLClassLoader cl = (URLClassLoader) getClass().getClassLoader();
		URL url = cl.findResource(MANIFEST_PATH);
		Manifest manifest = new Manifest(url.openStream());
		Attributes attr = manifest.getMainAttributes();
		return attr.entrySet();
	}

	public Map<String, String> getDictionary() {
		return dictionary;
	}
}
