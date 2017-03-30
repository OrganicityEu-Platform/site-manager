package fr.cea.organicity.manager.config;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import fr.cea.organicity.manager.config.environment.ManifestSettings;
import fr.cea.organicity.manager.domain.OCError;
import fr.cea.organicity.manager.repositories.OCErrorRepository;
import fr.cea.organicity.manager.security.SecurityConfig;
import fr.cea.organicity.manager.services.rolemanager.ClaimsParser;
import fr.cea.organicity.manager.services.rolemanager.OCClaims;
import fr.cea.organicity.manager.services.rolemanager.RoleManager;
import fr.cea.organicity.manager.services.userlister.UserLister;
import lombok.extern.log4j.Log4j;

@Log4j
class ThymeleafInterceptor extends HandlerInterceptorAdapter {

    private static final String DEFAULT_LAYOUT = "layouts/default";
    private static final String DEFAULT_VIEW_ATTRIBUTE_NAME = "view";

	private final RoleManager roleManager;
	private final SecurityConfig secuConfig;
	private final ManifestSettings manifestSettings;
	private final OCErrorRepository errorRepository;
	private final ClaimsParser claimsParser;
	private final UserLister userLister;
	
	public ThymeleafInterceptor(RoleManager roleManager, SecurityConfig secuConfig, ManifestSettings manifestSettings, OCErrorRepository errorRepository, ClaimsParser claimsParser, UserLister userLister) {
		this.roleManager = roleManager;
		this.secuConfig = secuConfig;
		this.manifestSettings = manifestSettings;
		this.errorRepository = errorRepository;
		this.claimsParser = claimsParser;
		this.userLister = userLister;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return super.preHandle(request, response, handler);
	}
	
	
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        
    	if (modelAndView == null || !modelAndView.hasView()) {
            return;
        }

        String originalViewName = modelAndView.getViewName();
        if (isRedirectOrForward(originalViewName)) {
            return;
        }
        
        // filter txt templates and API calls 
        if (!originalViewName.equals("error") && !originalViewName.startsWith("th"))
        	return;
        
        // managing error
        if (originalViewName.equals("error")) {
        	try {
        		updateModelForError(modelAndView);
        		OCError error = buildError(modelAndView);
        		error = errorRepository.save(error);
        		log.error("An error occured and has been reported with id " + error.getId());
        	} catch (Exception e) {
        		log.error("ERROR WHILE SAVING ERROR", e);
			}
        } 
        
        // Variables injection
        modelAndView.addObject("roles", roleManager.getRolesForRequest(request));
        modelAndView.addObject("userName", getAccountString(request));
        modelAndView.addObject("secuConfig", secuConfig);
        modelAndView.addObject("renewTokenUrl", secuConfig.getUrl(request));
        modelAndView.addObject("buildtimestamp", manifestSettings.getBuildTimestamp());
        
        modelAndView.addObject("sq", "'");
        modelAndView.addObject("dq", "\"");
        
        // changing the vew name
        modelAndView.setViewName(DEFAULT_LAYOUT);
        modelAndView.addObject(DEFAULT_VIEW_ATTRIBUTE_NAME, originalViewName);
    }

	private void updateModelForError(ModelAndView modelAndView) {
    	modelAndView.addObject("title", "Error");
	}
    
	private OCError buildError(ModelAndView modelAndView) {
		Map<String, Object> model = modelAndView.getModel();
		
		Date timestamp = (Date) model.get("timestamp");
		Integer status = (Integer) model.get("status");
		String statusStr = status == null ? null : status.toString();
		String error = (String) model.get("error");
		String message = (String) model.get("message");
		String path = (String) model.get("path");
		String modelStr = model.toString();
		
		return new OCError(timestamp, statusStr, error, message, path, modelStr);	
	}

	private boolean isRedirectOrForward(String viewName) {
        return viewName.startsWith("redirect:") || viewName.startsWith("forward:");
    }
	
	
	private String getAccountString(HttpServletRequest request) {
		try {
			OCClaims claims = claimsParser.getClaimsFromRequest(request);
			String sub = claims.getSub();
			return userLister.getUserNameOrSub(sub);
		} catch (Exception e) {
			return "My account";
		}
	}
}
