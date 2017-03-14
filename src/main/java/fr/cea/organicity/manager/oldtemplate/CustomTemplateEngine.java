package fr.cea.organicity.manager.oldtemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j;

@Log4j
@Component
public class CustomTemplateEngine {

	public String stringFromTemplate(String path, Map<String, String> dictionary) throws IOException {
		StringBuilder sb = new StringBuilder();
		InputStream stream = this.getClass().getResourceAsStream(path);
		InputStreamReader isr = new InputStreamReader(stream);
		BufferedReader reader = new BufferedReader(isr);
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			for (int start = line.indexOf("{{", 0); start != -1; start = line.indexOf("{{", start)) {
				int end = line.indexOf("}}", start);
				if (end != -1) {
					String fulltoken = line.substring(start, end+2);
					String shorttoken = line.substring(start+2, end);
					String replaceStr = dictionary.get(shorttoken);
					if (replaceStr == null) {
						log.warn("No replacement string found for token " + fulltoken);
						start = end;
					} else {
						line = line.replace(fulltoken, replaceStr);
						start = start + replaceStr.length();
					}
				} else {
					start = -1;
				}
			}
			if (! line.trim().isEmpty())
				sb.append(line).append("\n");
		}
		return sb.toString();
	}
}
