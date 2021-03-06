package fr.cea.organicity.manager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired private JwtAuthenticationTokenFilter tokenfilter;
	@Autowired private JwtAuthenticationEntryPoint unauthorizedHandler;

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {

		// @formatter:off
		httpSecurity
				// we don't need CSRF because our token is invulnerable
				.csrf().disable()

				// unauthorized
				.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

				.formLogin().disable()
				
				// don't create session
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

				.authorizeRequests()
					.antMatchers(HttpMethod.GET, "/", "/callback", "/organicity_logo.png", "/favicon.ico", "/apple-touch-icon-precomposed.png", "/**/*.css", "/**/*.js", "/h2-console/**")
					.permitAll()
				
				.anyRequest()
					.authenticated();
		// @formatter:on

		// by default, frame option is activated everywhere. To use H2 console, we need to allow frame on same origin 
		httpSecurity.headers().frameOptions().sameOrigin();
		
		// HTTP filters
		httpSecurity.addFilterBefore(tokenfilter, UsernamePasswordAuthenticationFilter.class);

		// disable page caching
		httpSecurity.headers().cacheControl();
	}
}
