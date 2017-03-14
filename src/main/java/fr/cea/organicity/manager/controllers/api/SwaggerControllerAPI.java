package fr.cea.organicity.manager.controllers.api;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import fr.cea.organicity.manager.config.environment.EnvService;

@Controller
@CrossOrigin(origins = "*")
public class SwaggerControllerAPI {

	@Autowired EnvService envService;

	@GetMapping(value = "/swagger.json", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getSwaggerFile(Model model) throws IOException {
		model.addAttribute("host", envService.getServerSettings().getHost());
		model.addAttribute("exposedport", envService.getServerSettings().getExposedPort());
		model.addAttribute("protocol", envService.getServerSettings().getProtocol());
		model.addAttribute("clientId", envService.getBackendSettings().getClientId());

		return "thswagger";
	}
}
