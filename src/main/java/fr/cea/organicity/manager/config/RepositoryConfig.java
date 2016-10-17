package fr.cea.organicity.manager.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAutoConfiguration
@EntityScan(basePackages = {"fr.cea.organicity.manager.domain"})
@EnableJpaRepositories(basePackages = {"fr.cea.organicity.manager.repositories"})
@EnableTransactionManagement
public class RepositoryConfig {

}
