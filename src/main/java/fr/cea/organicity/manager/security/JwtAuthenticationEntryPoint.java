package fr.cea.organicity.manager.security;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

	private static final long serialVersionUID = 1268510945883424791L;

	/**
	 * Method called on authentication failure.
	 */
	@Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
		String path = request.getServletPath();
		
		if (path.startsWith("/v1"))
			response.sendError(401, "Have you tried to refresh your token?");
		else
			response.sendRedirect("/?path=" + path);
    }
}