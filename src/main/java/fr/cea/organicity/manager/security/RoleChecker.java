package fr.cea.organicity.manager.security;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import com.google.common.base.Strings;

import fr.cea.organicity.manager.domain.OCRequest;
import fr.cea.organicity.manager.exceptions.token.InvalidTokenException;
import fr.cea.organicity.manager.exceptions.token.RoleComputationTokenException;
import fr.cea.organicity.manager.exceptions.token.TokenException;
import fr.cea.organicity.manager.exceptions.token.UserNotAuthorizedTokenException;
import fr.cea.organicity.manager.repositories.OCRequestRepository;
import fr.cea.organicity.manager.services.rolemanager.ClaimsParser;
import fr.cea.organicity.manager.services.rolemanager.OCClaims;
import fr.cea.organicity.manager.services.rolemanager.Role;
import fr.cea.organicity.manager.services.rolemanager.RoleManager;

@Aspect
@Component
public class RoleChecker {

	@Autowired private ClaimsParser claimsParser;
	@Autowired private RoleManager roleManager;
	@Autowired private OCRequestRepository requestRepository;
	@Autowired private SecurityConfig secuConfig;

	private static final Logger log = LoggerFactory.getLogger(RoleChecker.class);

	@Around("execution(* *(..)) && @annotation(guard) && args(request,..)")
	public Object aroundWeb(ProceedingJoinPoint point, RoleGuard guard, HttpServletRequest request) throws Throwable {

		long startTime = System.currentTimeMillis();
		String id_token = CookieTokenExtractor.getCookieID(request);
		OCClaims claims = getClaims(id_token);

		try {
			checkAuthorization(point, claims, guard);
		} catch (InvalidTokenException e) {
			logAccess("DENIED", point, e.getSub(), e.getMessage(), System.currentTimeMillis() - startTime);
			return "thinvalidtoken";
		} catch (RoleComputationTokenException e) {
			logAccess("INTERNAL", point, e.getSub(), e.getMessage(), System.currentTimeMillis() - startTime);
			return "thinternalerror";
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
		
		try {
			if (secuConfig.isBackendSecured()) {
				checkAuthorization(point, claims, guard);
			}
		} catch (TokenException e) {
			logAccess("DENIED", point, e.getSub(), e.getMessage(), System.currentTimeMillis() - startTime);
			throw e;
		} 
		
		logAccess("AUTHORISED", point, claims != null ? claims.getSub() : null, null, System.currentTimeMillis() - startTime);
		
		return point.proceed();
	}

	private void checkAuthorization(ProceedingJoinPoint point, OCClaims claims, RoleGuard guard) throws TokenException {

		String sub = checkClaimsAndGetSub(claims);

		if (guard != null && !Strings.isNullOrEmpty(guard.roleName())) {
			Role requiredRole = computeRequiredRole(sub, point, guard);
			List<Role> roles = roleManager.getRolesForSub(claims.getSub());
			if (!roles.contains(requiredRole)) {
				new UserNotAuthorizedTokenException(claims.getSub(), "User does not have role " + requiredRole);
			}
		}
	}

	private Role computeRequiredRole(String sub, ProceedingJoinPoint point, RoleGuard guard) throws RoleComputationTokenException {
		try {
			return new Role(getRoleName(point, guard));
		} catch (Exception e) {
			throw new RoleComputationTokenException(sub);
		}
	}

	private String checkClaimsAndGetSub(OCClaims claims) throws InvalidTokenException {
		if (claims == null) {
			throw new InvalidTokenException(null, "claims is null");
		} else {
			return claims.getSub();
		}
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
			log.debug("[ACCESS " + status + "] " + duration + "ms for access check " + point.getSignature().toShortString() + " sub="
					+ sub);
		else
			log.debug("[ACCESS " + status + "] " + duration + "ms for access check " + point.getSignature().toShortString() + " sub="
					+ sub + " message=" + message);

		// DB storage
		requestRepository.save(request);
	}
}
