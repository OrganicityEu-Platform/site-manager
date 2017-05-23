package fr.cea.organicity.manager.config.environment;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Component
@Data
@Validated
@ConfigurationProperties(prefix="organicity.server")
public class ServerSettings {

	@Valid @NotNull
	private String protocol;
	
	@Valid @NotNull
	private String host;
	
	@Valid @NotNull
	private int localport;
	
	@Valid @NotNull
	private int exposedport;
	
	public String getProtocol() {
		return protocol.trim();
	}
	
	public String getHost() {
		return host.trim();
	}

	public int getLocalPort() {
		return localport;
	}
	
	public int getExposedPort() {
		return exposedport;
	}
	
	
	public Map<String, String> getDictionary() {
		Map<String, String> map = new HashMap<>();
		map.put("protocol", getProtocol());
		map.put("host", getHost());
		map.put("localport", Integer.toString(getLocalPort()));
		map.put("exposedport", Integer.toString(getExposedPort()));
		return map;
	}
}
