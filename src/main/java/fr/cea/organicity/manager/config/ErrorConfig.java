package fr.cea.organicity.manager.config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.View;

import fr.cea.organicity.manager.domain.OCError;
import fr.cea.organicity.manager.repositories.OCErrorRepository;
import fr.cea.organicity.manager.security.Role;
import fr.cea.organicity.manager.security.RoleManager;
import fr.cea.organicity.manager.template.ErrorTemplate;
import fr.cea.organicity.manager.template.TemplateEngine;

@Configuration
public class ErrorConfig {

	private static final Logger log = LoggerFactory.getLogger(ErrorConfig.class);
	
	@Bean(name = "error")
	public View defaultErrorView(TemplateEngine templateEngine, RoleManager roleManager, OCErrorRepository errorRepository) {
		return new View() {
			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
				if (response.getContentType() == null) {
					response.setContentType(getContentType());
				}
				List<Role> roles = roleManager.getRolesForRequest(request);
				
				String content = ErrorTemplate.generateHTML(templateEngine, roles, model, request, response);
				response.getWriter().append(content);
				try {
					OCError error = buildError(model);
					error = errorRepository.save(error);
					log.error("An error occured and has been reported with id " + error.getId());
				} catch (Exception e) {
					log.error("================================");
					log.error("Error reporting failed !!!!!!!!.");
					log.error("================================");
					log.error("Here is the content displayed to the user");
					log.error(content);
					log.error("================================");
					e.printStackTrace(System.err);
					log.error("================================");
				}
			}
			
			private OCError buildError(Map<String, ?> model) {			
				String timestamp = String.valueOf(model.get("timestamp"));
				Date date = null;
				try {
					SimpleDateFormat inFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
					date = inFormat.parse(timestamp);
				} catch (Exception e) {
					System.err.println("Wrong date format while parsing timestamp " + timestamp + " Curent date used instead.");
					date = new Date(System.currentTimeMillis());
				}
				
				String status = String.valueOf(model.get("status"));
				String error = String.valueOf(model.get("error"));
				String message = String.valueOf(model.get("message"));
				String path = String.valueOf(model.get("path"));
				String modelStr = model.toString();
				
				return new OCError(date, status, error, message, path, modelStr);				
			}
			
			@Override
			public String getContentType() {
				return "text/html";
			}
		};		
	}
}
