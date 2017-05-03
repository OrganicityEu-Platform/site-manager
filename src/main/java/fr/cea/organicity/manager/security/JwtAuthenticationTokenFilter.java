package fr.cea.organicity.manager.security;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import fr.cea.organicity.manager.domain.OCRequest;
import fr.cea.organicity.manager.repositories.OCRequestRepository;
import fr.cea.organicity.manager.services.rolemanager.ClaimsExtractor;
import fr.cea.organicity.manager.services.rolemanager.Role;
import fr.cea.organicity.manager.services.rolemanager.RoleManager;
import fr.cea.organicity.manager.services.security.CookieTokenExtractorService;
import fr.cea.organicity.manager.services.userlister.UserLister;
import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j;

@Log4j
@Component
class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

	@Autowired
	private RoleManager rolemanager;
	@Autowired
	private UserLister userLister;
	@Autowired
	private ClaimsExtractor claimsextractor;
	@Autowired
	private OCRequestRepository requestRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getServletPath();
		long start = System.currentTimeMillis();
		String sub;

		try {
			Identity identity = getIdentity(request);
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(identity, null,
					identity.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			logger.debug("HTTP REQUEST " + path + " with user " + identity.getName());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			sub = identity.getSub();
		} catch (Exception e) {
			logger.debug("HTTP REQUEST " + path + " with user anonymous");
			sub = "anonymous";
		}

		filterChain.doFilter(request, response);

		if (logPath(path)) {
			String status = computeStatus(response.getStatus());
			String message = "";
			long duration = System.currentTimeMillis() - start;

			logAccess(status, path, sub, message, duration);
		}
	}

	private Identity getIdentity(HttpServletRequest request) throws Exception {

		Claims claims = claimsextractor.getClaimsFromRequest(request);
		String sub = claims.getSubject();
		String name = userLister.getUserNameOrSub(sub);
		
		String idToken = null;
		try {
			idToken = CookieTokenExtractorService.getCookieID(request);	
		} catch (Exception e) {
			// do nothing
		}
		
		String accessToken = null;
		try {
			accessToken = CookieTokenExtractorService.getCookieAccess(request);
		} catch (Exception e) {
			// do nothing
		}
		
		List<Role> roles = rolemanager.getRolesForSub(sub);
		List<String> roleNames = roles.stream().map(Role::getQualifiedName).map(r -> "ROLE_" + r).collect(Collectors.toList());
		Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(roleNames.toArray(new String[0]));
		
		return new Identity(sub, name, idToken, accessToken, roles, roleNames, authorities);
	}

	private boolean logPath(String path) {
		switch (path) {
		case "/organicity_logo.png":
			return false;
		case "/js/app.js":
			return false;
		case "/stylesheet.css":
			return false;
		case "/callback":
			return false;
		default:
			return true;
		}
	}

	private String computeStatus(int code) {
		if (code <= 399)
			return "AUTHORISED";
		if (code <= 499)
			return "DENIED";
		return "ERROR";
	}

	private void logAccess(String status, String path, String sub, String message, long duration) {

		OCRequest request = new OCRequest();
		request.setDate(new Date());
		request.setStatus(status);
		request.setMethod(path);
		request.setSub(sub);
		request.setMessage(message);
		request.setDuration(duration);

		if (message == null)
			log.debug("[ACCESS " + status + "] " + duration + "ms for access check " + path + " sub=" + sub);
		else
			log.debug("[ACCESS " + status + "] " + duration + "ms for access check " + path + " sub=" + sub
					+ " message=" + message);

		// DB storage
		requestRepository.save(request);
	}
}
