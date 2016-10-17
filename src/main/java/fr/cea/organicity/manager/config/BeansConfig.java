package fr.cea.organicity.manager.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.cea.organicity.manager.config.Environment.EnvService;
import fr.cea.organicity.manager.otherservices.ExperimentLister;
import fr.cea.organicity.manager.otherservices.UserLister;
import fr.cea.organicity.manager.repositories.OCApiCallRepository;
import fr.cea.organicity.manager.security.APIInvoker;
import fr.cea.organicity.manager.security.ClaimsParser;
import fr.cea.organicity.manager.security.Credentials;
import fr.cea.organicity.manager.security.HttpClient;
import fr.cea.organicity.manager.security.RoleManager;
import fr.cea.organicity.manager.security.TokenManager;

@Configuration
public class BeansConfig {

	@Autowired EnvService env;
	@Autowired OCApiCallRepository apiCallRepository;
	
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
	Client getClient() {
		return ClientBuilder.newClient();
	}

	@Bean
	APIInvoker getApiInvoker() {
		return new APIInvoker(apiCallRepository);
	}
	
	@Bean
	TokenManager getTokenManager(PublicKey pk, Credentials credentials, Client client, APIInvoker invoker) {
		return new TokenManager(pk, credentials, client, invoker);	
	}
		
	@Bean
	HttpClient getHttpClient(Client client, TokenManager tokenManager) {
		return new HttpClient(client, tokenManager);
	}	
		
	@Bean
	RoleManager getRoleManager(HttpClient httpClient, ClaimsParser claimsParser, APIInvoker invoker) {
		return new RoleManager(httpClient, claimsParser, invoker);
	}
	
	@Bean
	UserLister getUserLister(HttpClient httpClient, APIInvoker invoker) {
		return new UserLister(httpClient, invoker);
	}
	
	@Bean
	ExperimentLister getExperimenterLister(HttpClient httpClient, APIInvoker invoker) {
		return new ExperimentLister(httpClient, invoker);
	}
}
