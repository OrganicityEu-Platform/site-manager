package fr.cea.organicity.manager.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import fr.cea.organicity.manager.config.environment.ManifestSettings;
import fr.cea.organicity.manager.domain.OCError;
import fr.cea.organicity.manager.repositories.OCErrorRepository;
import fr.cea.organicity.manager.security.Identity;
import fr.cea.organicity.manager.security.SecurityConstants;
import fr.cea.organicity.manager.services.rolemanager.ClaimsExtractor;
import fr.cea.organicity.manager.services.security.CallbackUrlBuilderService;
import fr.cea.organicity.manager.services.userlister.UserLister;
import lombok.extern.log4j.Log4j;

@Log4j
class ThymeleafInterceptor extends HandlerInterceptorAdapter {

    private static final String DEFAULT_LAYOUT = "layouts/default";
    private static final String DEFAULT_VIEW_ATTRIBUTE_NAME = "view";

	private final CallbackUrlBuilderService urlBuilder;
	private final ManifestSettings manifestSettings;
	private final OCErrorRepository errorRepository;
	private final ClaimsExtractor claimsParser;
	private final UserLister userLister;
	private final SecurityConstants secuConstants;
	
	public ThymeleafInterceptor(CallbackUrlBuilderService urlBuilder, ManifestSettings manifestSettings, OCErrorRepository errorRepository, ClaimsExtractor claimsParser, UserLister userLister, SecurityConstants secuConstants) {
		this.urlBuilder = urlBuilder;
		this.manifestSettings = manifestSettings;
		this.errorRepository = errorRepository;
		this.claimsParser = claimsParser;
		this.userLister = userLister;
		this.secuConstants = secuConstants;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return super.preHandle(request, response, handler);
	}
	
	
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
         
    	Identity identity = getIdentity();
    	
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
        modelAndView.addObject("roles", identity != null ? identity.getRoles() : new ArrayList<>());
        modelAndView.addObject("userName", getAccountString(request));
        modelAndView.addObject("secuConstants", secuConstants);
        modelAndView.addObject("renewTokenUrl", urlBuilder.getUrl(request));
        modelAndView.addObject("buildtimestamp", manifestSettings.getBuildTimestamp());
        modelAndView.addObject("isDicoAdmin", isDictionaryAdmin(identity));
        
        // changing the view name
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
			String sub = claimsParser.getSubFromRequest(request);
			return userLister.getUserNameOrSub(sub);
		} catch (Exception e) {
			return "My account";
		}
	}
	
	private Identity getIdentity() {
		try {
			SecurityContext context = SecurityContextHolder.getContext();
			Authentication authentication = context.getAuthentication();
			return (Identity) authentication.getPrincipal();
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean isDictionaryAdmin(Identity identity) {
		if (identity == null)
			return false;
		else
			return identity.getRoles().contains(SecurityConstants.DICTIONARY_ADMIN_ROLE);
	}
}
