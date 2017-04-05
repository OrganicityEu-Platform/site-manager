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
import fr.cea.organicity.manager.security.SecurityConstants;
import fr.cea.organicity.manager.services.rolemanager.ClaimsExtractor;
import fr.cea.organicity.manager.services.security.CallbackUrlBuilderService;
import fr.cea.organicity.manager.services.userlister.UserLister;


/**
 * Here, we are extending WebMvcConfigurerAdapter to keep Spring's auto configuration.
 * In the documentation, a lot of examples are extending WebMvcConfigurationSupport but it
 * disables Spring's auto configuration.
 */
@Configuration
public class ThymeleafConfig extends WebMvcConfigurerAdapter {
    
	@Autowired private CallbackUrlBuilderService urlBuilder;
	@Autowired private ManifestSettings manifestsettings;
	@Autowired private OCErrorRepository errorRepository;
	@Autowired private ClaimsExtractor claimsExtractor;
	@Autowired private UserLister userLister;
	
	@Autowired private SpringTemplateEngine templateEngine;
	@Autowired private ApplicationContext applicationContext;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ThymeleafInterceptor(urlBuilder, manifestsettings, errorRepository, claimsExtractor, userLister, new SecurityConstants()));
    }
	
	@PostConstruct
    public void init() {
		//Set<ITemplateResolver> resolvers = templateEngine.getTemplateResolvers();
		templateEngine.addTemplateResolver(getTextTemplateResolver(applicationContext, "txt"));
    }
	
	private ITemplateResolver getTextTemplateResolver(ApplicationContext applicationContext, String extension) {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setCacheable(true); // true = NO LIVE RELOAD (but quicker)
		resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix("." + extension);
        resolver.setTemplateMode(TemplateMode.TEXT);
        resolver.setApplicationContext(applicationContext);
        //resolver.setOrder(templateEngine.getTemplateResolvers().size());        
        return resolver;
	}
}
