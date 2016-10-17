package fr.cea.organicity.manager.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import fr.cea.organicity.manager.config.Environment.EnvService;
import fr.cea.organicity.manager.security.Role;
import fr.cea.organicity.manager.security.SecurityConfig;


@Service
public class TemplateEngine {

	@Autowired EnvService env;
	@Autowired SecurityConfig secuConfig;
	
	private static final Logger log = LoggerFactory.getLogger(TemplateEngine.class);
	
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
	
	public String stringFromTemplate(String path) throws IOException {
		return stringFromTemplate(path, env.mergeDictionaries());
	}
	
	public String getDictionnaryContentAsList() {
		StringBuilder sb = new StringBuilder();

		Map<String, Map<String, String>> dictionaries = env.getDictionaries();		
		for (String dicoName : dictionaries.keySet()) {
			sb.append("<strong>" + dicoName + "</strong>\n");
			sb.append("<ul>\n");
			Map<String, String> dicoContent = dictionaries.get(dicoName);
			for (String key : dicoContent.keySet()) {
				String value;
				if (key.toLowerCase().contains("password"))
					value = "&lt;&lt;password not displayed&gt;&gt;";
				else
					value = dicoContent.get(key); 
				sb.append(createKeyValueLi(key, value));
			}
			sb.append("</ul>\n");			
		}

		return sb.toString();
	}
	
	protected String generateWebPage(String title, String content, List<Role> roles) throws IOException {
		Map<String, String> dictionary = new HashMap<>();
		
		// title
		dictionary.put("title", title);
		
		// navigation
		String navigation = createNavigateLink("/", "Home");
		if (roles != null) {
			navigation += " | " + createNavigateLink("/info", "My account");
			navigation += " | " + createNavigateLink("/users", "Permissions");
			navigation += " | " + createNavigateLink("/sites", "Sites");
			if (roles.contains(secuConfig.EXPERIMENT_USER_ROLE))
				navigation += " | " + createNavigateLink("/experiments", "Experiments");
			if (roles.contains(secuConfig.METRICS_USER_ROLE))
				navigation += " | " + createNavigateLink("/metrics", "Metrics");
			if (roles.contains(secuConfig.DICTIONARY_USER_ROLE))
				navigation += " | " + createNavigateLink("/dictionaries", "Dictionaries");
			
			navigation += " || <a href=\"/\"><strong>logout</strong></a>";
			
			if (roles.contains(secuConfig.DEVELOPER_ROLE))
				navigation += " | " + createNavigateLink("/links", "<strong>DEV LINKS</strong>");
		}
		dictionary.put("navigation", navigation);
		
		// content
		String str="";
		if (content != null) {
			String[] lines = content.split("\n");
			for (int i=0; i<lines.length; i++) {
				if (i != 0) str += "		";
				str += lines[i];
				if (i != lines.length - 1) str += "\n";
			}
		}
		dictionary.put("content", str);
		
		return stringFromTemplate("/templates/index.html", dictionary);			
	}
	
	protected static String createNavigateLink(String url, String text) {
		return "<a href=\"#\" onclick=\"navigateTo('" + url + "');\">" + text + "</a>";
	}
	
	protected static String createPlainLink(String text) {
		return createPlainLink(text, text);
	}
	
	protected static String createPlainLink(String url, String text) {
		return "<a href=\"" + url + "\">" + text + "</a>";
	}
	
	protected static String createKeyValue(String key, Object value) {
		if (value != null)
			return "<strong>" + key + "</strong>: " + value.toString() + "<br/>\n";
		else
			return "<strong>" + key + "</strong>: " + "null" + "<br/>\n";
	}
	
	protected static String createKeyValueLi(String key, String value) {
		return "<li><strong>" + key + "</strong>: " + value + "</li>\n";
	}
	
	protected static String createListSize(Iterable<?> iterable, String listName) {
		int size = Iterables.size(iterable);
		if (size == 0)
			return "<p>No " + listName + " available</p>";
		if (size == 1)
			return "<p>One " + listName + " available</p>";
		return "<p>" + size + " " + listName + "s available</p>";
	}
	
	protected static String generateMessageBannerIfNeeded(String message) {
		if (Strings.isNullOrEmpty(message))
			return "";
		return "<div class=\"message\">" + message + "</div>\n";
	}
	
	protected static String generateFormInput(String id, String label) {
		return generateFormInput(id, label, null);
	}
	
	protected static String generateFormInput(String id, String label, String value) {
		String content = "  <div class=\"line\">\n";
		content += "    <label for=\"" + id + "\">" + label + "</label>\n";
		content += "    <input id=\"" + id + "\" value=\"" + Strings.nullToEmpty(value) + "\">\n";
		content += "  </div>\n";
		return content;
	}
	
	protected static <T extends Object> String generateFormSelect(String label, String id, Iterable<T> elements, Function<T, String> getValueFn, Function<T, String> getDisplayFn, String endofline) {
		String content = "  <div class=\"line\">\n";
		content += "    <label for=\""+ id +"\">" + label + "</label>\n";
		content += "    <select id=\""+ id +"\">\n";
		for (T element : elements) {
			content += "      <option value=\"" + getValueFn.apply(element) + "\">"+ getDisplayFn.apply(element) + "</option>\n";
		}
		content += "    </select>\n";
		if (endofline != null)
			content += endofline;
		content += "  </div>\n";
		return content;
	}

	protected static <T extends Object> String generateUsedBy(Collection<T> collection, String usedByTypeDisplayName, String dictionaryPath, Function<T, String> nameFn, Function<T, String> urnFn) {
		StringBuilder sb = new StringBuilder();
		sb.append("Used by " + collection.size() + " " + usedByTypeDisplayName + (collection.size() == 1 ? "":"s"));
		if (collection.size() != 0) {
			sb.append(": ");
			boolean begin=true;
			for (T unit: collection) {
				if (begin)
					begin = false;
				else
					sb.append(", ");
				sb.append(TemplateEngine.createNavigateLink("/dictionaries/" + dictionaryPath + "/" + nameFn.apply(unit), urnFn.apply(unit)));
			}
		}

		sb.append("\n");
		
		return sb.toString();
	}

	protected static String generateSubmitBtn(String methodName, String label, String extraParameter, String... ids) {
		String content = "  <a href=\"#\" class=\"btn validate\" onclick=\"" + methodName + "(";
		if (extraParameter != null)
			content += extraParameter + ",";
		
		boolean start = true;
		for (String id : ids) {
			if (start)
				start=false;						
			else
				content += ",";
			content += "document.getElementById('" + id + "').value";
		}
		content += ");\">" + label + "</a>\n";

		return content;
	}
		
	protected static String generateDeleteBtn(String path, String itemName, String itemDisplayType) {
		return "  <a href=\"#\" class=\"btn danger\" onclick=\"navigateTo('" + path +"/" + itemName + "/" + "delete');\">DELETE " + itemName + " " + itemDisplayType + "</a>\n";
	}
}
