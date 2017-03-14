package fr.cea.organicity.manager.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import fr.cea.organicity.manager.config.environment.EnvService;
import fr.cea.organicity.manager.repositories.OCApiCallRepository;
import fr.cea.organicity.manager.services.experimentlister.ExperimentLister;
import fr.cea.organicity.manager.services.rolemanager.ClaimsParser;
import fr.cea.organicity.manager.services.rolemanager.RoleManager;
import fr.cea.organicity.manager.services.tagdomain.TagDomainService;
import fr.cea.organicity.manager.services.tokenmanager.Credentials;
import fr.cea.organicity.manager.services.tokenmanager.TokenManager;
import fr.cea.organicity.manager.services.userlister.UserLister;

@Configuration
public class BeansConfig {

	@Autowired EnvService env;
	@Autowired OCApiCallRepository callRepository;
	
	@Bean
	PublicKey getPublicKey() throws FileNotFoundException, CertificateException {
		String path = env.getBackendSettings().getCertificate();
		InputStream is = new FileInputStream(new File(path));
		CertificateFactory f = CertificateFactory.getInstance("X.509");
		X509Certificate certificate = (X509Certificate) f.generateCertificate(is);
		return certificate.getPublicKey();
	}
	
	@Bean
	ClaimsParser claimsParser(PublicKey pk) throws FileNotFoundException, CertificateException {
		return new ClaimsParser(pk);
	}

	@Bean
	Credentials getCredentials() {
		String id = env.getBackendSettings().getClientId();
		String password = env.getBackendSettings().getClientPassword();
		return new Credentials(id, password);
	}
	
	@Bean
	TokenManager getTokenManager(PublicKey pk, Credentials credentials) {
		return new TokenManager(pk, credentials);	
	}
	
	@Bean
	RestTemplateInterceptor getRestTemplateInterceptor(TokenManager tokenManager) {
		return new RestTemplateInterceptor(tokenManager, callRepository);
	}
	
	@Bean({"auth"})
	RestTemplate getRestTemplate(List<ClientHttpRequestInterceptor> interceptors) {
		RestTemplate template = new RestTemplate();
		template.setInterceptors(interceptors);
		return template;
	}
	
	@Bean
	RoleManager getRoleManager(ClaimsParser claimsParser, RestTemplate restTemplate) {
		return new RoleManager(claimsParser, restTemplate);
	}
	
	@Bean
	UserLister getUserLister(RestTemplate restTemplate) {
		return new UserLister(restTemplate);
	}
	
	@Bean
	TagDomainService getTagDomainService(RestTemplate restTemplate) {
		return new TagDomainService(restTemplate);
	}
	
	@Bean
	ExperimentLister getExperimenterLister(RestTemplate restTemplate) {
		String experimenterPortalUrl = env.getServicesSettings().getExperimenterPortalUrl();
		String discoveryServiceUrl = env.getServicesSettings().getDiscoveryServiceUrl();
		return new ExperimentLister(restTemplate, experimenterPortalUrl, discoveryServiceUrl);
	}
}
