package fr.cea.organicity.manager.security;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import com.google.common.base.Strings;

import fr.cea.organicity.manager.domain.OCRequest;
import fr.cea.organicity.manager.repositories.OCRequestRepository;
import fr.cea.organicity.manager.template.TemplateEngine;
import fr.cea.organicity.manager.template.WebPageTemplate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Aspect
@Component
public class RoleChecker {

	@Autowired private ClaimsParser claimsParser;
	@Autowired private RoleManager roleManager;
	@Autowired private TemplateEngine templateService;
	@Autowired private OCRequestRepository requestRepository;
	@Autowired private SecurityConfig secuConfig;

	private static final Logger log = LoggerFactory.getLogger(RoleChecker.class);

	@Around("execution(* *(..)) && @annotation(guard) && args(request,..)")
	public Object aroundWeb(ProceedingJoinPoint point, RoleGuard guard, HttpServletRequest request) throws Throwable {

		long startTime = System.currentTimeMillis();
		String id_token = request.getParameter("id_token");
		OCClaims claims = getClaims(id_token);

		try {
			validateRequest(point, claims, guard);
		} catch (HtmlMessageException e) {
			List<Role> roles = roleManager.getRolesForRequest(request);
			logAccess("DENIED", point, e.getSub(), e.getMessage(), System.currentTimeMillis() - startTime);
			return WebPageTemplate.generateUnauthorizedHTML(templateService, roles, e.getHtml());
		}

		// compute result
		if (claims == null)
			logAccess("AUTHORISED", point, "<anonymous>", System.currentTimeMillis() - startTime);
		else
			logAccess("AUTHORISED", point, claims.getSub(), System.currentTimeMillis() - startTime);

		return point.proceed();
	}

	@Around("execution(* *(..)) && @annotation(guard) && args(auth,..)")
	public Object aroundAPI(ProceedingJoinPoint point, RoleGuard guard, String auth) throws Throwable {

		long startTime = System.currentTimeMillis();
		OCClaims claims = getClaims(auth);

		if (secuConfig.isBackendSecured()) {
			try {
				validateRequest(point, claims, guard);
			} catch (HtmlMessageException e) {
				logAccess("DENIED", point, e.getSub(), e.getMessage(), System.currentTimeMillis() - startTime);
				JSONObject json = new JSONObject();
				json.put("error", "UNAUTHORIZED");
				json.put("message", e.getMessage());
				return new ResponseEntity<String>(json.toString(), HttpStatus.UNAUTHORIZED);
			}

			// compute result
			if (claims == null)
				logAccess("AUTHORISED", point, "<anonymous>", System.currentTimeMillis() - startTime);
			else
				logAccess("AUTHORISED", point, claims.getSub(), System.currentTimeMillis() - startTime);
		}

		return point.proceed();
	}

	private void validateRequest(ProceedingJoinPoint point, OCClaims claims, RoleGuard guard)
			throws HtmlMessageException, IOException {

		if (claims == null) {
			String message = "INVALID_TOKEN";

			Map<String, String> dictionary = new HashMap<>();
			HttpServletRequest request = getRequest(point.getArgs());
			String url = secuConfig.getUrl(request);
			dictionary.put("url", url);

			String html = templateService.stringFromTemplate("/templates/invalidToken.html", dictionary);

			throw new HtmlMessageException(null, message, html);
		}

		// check role
		if (guard != null && !Strings.isNullOrEmpty(guard.roleName())) {

			Role requiredRole;
			try {
				requiredRole = new Role(getRoleName(point, guard));
			} catch (Exception e) {
				String message = "Can't compute role to be checked";
				throw new HtmlMessageException(claims.getSub(), message, message);
			}

			// check if user has role
			List<Role> roles;
			try {
				roles = roleManager.getRolesForSub(claims.getSub());
			} catch (ExecutionException e) {
				String message = "Can't get user roles";
				throw new HtmlMessageException(claims.getSub(), message, message);
			}
			if (!roles.contains(requiredRole)) {
				String message = "User doesn't have role " + requiredRole.getFullName();
				throw new HtmlMessageException(claims.getSub(), message, message);
			}
		}
	}

	private HttpServletRequest getRequest(Object[] args) {
		for (Object arg : args)
			if (arg instanceof HttpServletRequest)
				return (HttpServletRequest) arg;
		return null;
	}

	private String getRoleName(ProceedingJoinPoint point, RoleGuard guard) {

		String name = guard.roleName();

		if (name.contains("{")) {
			Method method = getMethod(point);
			for (int i = 0; i < method.getParameters().length; i++) {
				Parameter param = method.getParameters()[i];
				PathVariable annotation = param.getAnnotation(PathVariable.class);
				if (annotation != null) {
					String key = annotation.value();
					Object value = point.getArgs()[i];
					name = name.replace("{" + key + "}", value.toString());
				}
			}
		}
		name = name.replace(SecurityConstants.clientNameKey, secuConfig.clientName);

		return name;
	}

	private Method getMethod(ProceedingJoinPoint point) {
		MethodSignature signature = (MethodSignature) point.getSignature();
		Class<?> type = signature.getDeclaringType();
		String name = signature.getName();
		Class<?>[] params = signature.getParameterTypes();
		try {
			return type.getMethod(name, params);
		} catch (Exception e) {
			throw new RuntimeException("Internal error while looking for method", e);
		}
	}

	private OCClaims getClaims(String id_token) {
		try {
			return claimsParser.getClaimsFromHeader(id_token);
		} catch (Exception e) {
			return null;
		}
	}

	private void logAccess(String status, ProceedingJoinPoint point, String sub, long duration) {
		logAccess(status, point, sub, null, duration);
	}

	private void logAccess(String status, ProceedingJoinPoint point, String sub, String message, long duration) {

		OCRequest request = new OCRequest();
		request.setDate(new Date());
		request.setStatus(status);
		request.setMethod(point.getSignature().toShortString());
		request.setSub(sub);
		request.setMessage(message);
		request.setDuration(duration);

		if (message == null)
			log.debug("[ACCESS " + status + "] " + duration + "ms " + point.getSignature().toShortString() + " sub="
					+ sub);
		else
			log.debug("[ACCESS " + status + "] " + duration + "ms " + point.getSignature().toShortString() + " sub="
					+ sub + " message=" + message);

		// DB storage
		requestRepository.save(request);
	}

	@Data
	@EqualsAndHashCode(callSuper = false)
	private class HtmlMessageException extends Exception {
		private static final long serialVersionUID = -7928067741742885882L;
		private final String sub;
		private final String message;
		private final String html;

		public HtmlMessageException(String sub, String message, String html) {
			this.sub = sub;
			this.message = message;
			this.html = html;
		}
	}
}
