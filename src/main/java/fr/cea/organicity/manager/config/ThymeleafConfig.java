package fr.cea.organicity.manager.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.cea.organicity.manager.config.environment.ManifestSettings;
import fr.cea.organicity.manager.repositories.OCErrorRepository;
import fr.cea.organicity.manager.security.SecurityConfig;
import fr.cea.organicity.manager.services.rolemanager.ClaimsParser;
import fr.cea.organicity.manager.services.rolemanager.RoleManager;
import fr.cea.organicity.manager.services.userlister.UserLister;


/**
 * Here, we are extending WebMvcConfigurerAdapter to keep Spring's auto configuration.
 * In the documentation, a lot of examples are extending WebMvcConfigurationSupport but it
 * disables Spring's auto configuration.
 */
@Configuration
public class ThymeleafConfig extends WebMvcConfigurerAdapter {
    
	@Autowired private RoleManager roleManager;
	@Autowired private SecurityConfig secuConfig;
	@Autowired private ManifestSettings manifestsettings;
	@Autowired private OCErrorRepository errorRepository;
	@Autowired private ClaimsParser claimsParser;
	@Autowired private UserLister userLister;
	
	@Autowired private SpringTemplateEngine templateEngine;
	@Autowired private ApplicationContext applicationContext;
	
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ThymeleafInterceptor(roleManager, secuConfig, manifestsettings, errorRepository, claimsParser, userLister));
    }
	
	@PostConstruct
    public void init() {
		//Set<ITemplateResolver> resolvers = templateEngine.getTemplateResolvers();
        templateEngine.addTemplateResolver(getJsonTemplateResolver(applicationContext));
    }
	
	private static ITemplateResolver getJsonTemplateResolver(ApplicationContext applicationContext) {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setCacheable(true); // true = NO LIVE RELOAD (but quicker)
		resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".json");
        resolver.setTemplateMode(TemplateMode.TEXT);
        resolver.setApplicationContext(applicationContext);
        //resolver.setOrder(templateEngine.getTemplateResolvers().size());        
        return resolver;
	}
}
