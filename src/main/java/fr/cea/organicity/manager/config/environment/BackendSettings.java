package fr.cea.organicity.manager.config.environment;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@ConfigurationProperties(prefix = "organicity.backend")
public class BackendSettings {

	@Valid
	@NotNull
	private String certificate;

	@Valid
	@NotNull
	private String clientId;

	@Valid
	@NotNull
	private String clientPassword;

	@Valid
	private boolean secured;
	
	public String getCertificate() {
		return certificate.trim();
	}

	public String getClientId() {
		return clientId.trim();
	}

	public String getClientPassword() {
		return clientPassword.trim();
	}
	
	public Map<String, String> getDictionary() {
		Map<String, String> map = new HashMap<>();
		map.put("certificate", getCertificate());
		map.put("clientId", getClientId());
		map.put("clientPassword", getClientPassword());
		map.put("secured", Boolean.toString(isSecured()));
		return map;
	}
}
