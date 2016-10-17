package fr.cea.organicity.manager.config.Environment;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@ConfigurationProperties(prefix="organicity.frontend")
public class FrontendSettings {

	@Valid @NotNull
	private boolean authenticationActivated;
	
	public boolean isAuthenticationActivated() {
		return authenticationActivated;
	}
	
	public Map<String, String> getDictionary() {
		Map<String, String> map = new HashMap<>();
		map.put("authenticationActivated", Boolean.toString(authenticationActivated));
		return map;
	}
}
