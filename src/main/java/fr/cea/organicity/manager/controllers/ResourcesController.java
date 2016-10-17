package fr.cea.organicity.manager.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.template.TemplateEngine;

@CrossOrigin(origins = "*")
@RestController
public class ResourcesController {

	@Autowired TemplateEngine templateSrv;

	@RequestMapping(value = "/swagger.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getSwaggerFile() throws IOException {
		return templateSrv.stringFromTemplate("/templates/swagger.json");
	}
}
