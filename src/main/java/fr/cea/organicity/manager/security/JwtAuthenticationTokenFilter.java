package fr.cea.organicity.manager.security;

import java.io.IOException;
import java.util.Collection;
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

import fr.cea.organicity.manager.services.rolemanager.ClaimsExtractor;
import fr.cea.organicity.manager.services.rolemanager.Role;
import fr.cea.organicity.manager.services.rolemanager.RoleManager;
import fr.cea.organicity.manager.services.security.CookieTokenExtractorService;
import fr.cea.organicity.manager.services.userlister.UserLister;

@Component
class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
	
	@Autowired private RoleManager rolemanager;
	@Autowired private UserLister userLister;
	@Autowired private ClaimsExtractor claimsextractor;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			Identity identity = getIdentity(request);
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(identity, null, identity.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			logger.debug("HTTP REQUEST " + request.getServletPath() + " with user " + identity.getName());
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (Exception e) {
			logger.debug("HTTP REQUEST " + request.getServletPath() + " with user anonymous");
		}

		filterChain.doFilter(request, response);
	}
	
	private Identity getIdentity(HttpServletRequest request) throws Exception {
		
		String sub = claimsextractor.getSubFromRequest(request);
		String name = userLister.getUserNameOrSub(sub);
		String idToken = CookieTokenExtractorService.getCookieID(request); 
		String accessToken = CookieTokenExtractorService.getCookieAccess(request);
		List<Role> roles = rolemanager.getRolesForSub(sub);
		List<String> roleNames = roles.stream().map(Role::getQualifiedName).map(r -> "ROLE_" + r).collect(Collectors.toList());
		Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(roleNames.toArray(new String[0]));

		return new Identity(sub, name, idToken, accessToken, roles, roleNames, authorities); 	
	}
	
}
