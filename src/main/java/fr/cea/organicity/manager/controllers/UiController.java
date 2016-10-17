package fr.cea.organicity.manager.controllers;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.domain.OCAppType;
import fr.cea.organicity.manager.domain.OCAssetType;
import fr.cea.organicity.manager.domain.OCAttributeType;
import fr.cea.organicity.manager.domain.OCDataType;
import fr.cea.organicity.manager.domain.OCService;
import fr.cea.organicity.manager.domain.OCSite;
import fr.cea.organicity.manager.domain.OCTool;
import fr.cea.organicity.manager.domain.OCUnit;
import fr.cea.organicity.manager.domain.OCUnregisteredAssetType;
import fr.cea.organicity.manager.otherservices.Experiment;
import fr.cea.organicity.manager.otherservices.ExperimentLister;
import fr.cea.organicity.manager.otherservices.User;
import fr.cea.organicity.manager.otherservices.UserLister;
import fr.cea.organicity.manager.repositories.OCRequestRepository;
import fr.cea.organicity.manager.repositories.OCApiCallRepository;
import fr.cea.organicity.manager.repositories.OCAppTypeRepository;
import fr.cea.organicity.manager.repositories.OCAssetTypeRepository;
import fr.cea.organicity.manager.repositories.OCAttributeTypeRepository;
import fr.cea.organicity.manager.repositories.OCDataTypeRepository;
import fr.cea.organicity.manager.repositories.OCErrorRepository;
import fr.cea.organicity.manager.repositories.OCServiceRepository;
import fr.cea.organicity.manager.repositories.OCSiteRepository;
import fr.cea.organicity.manager.repositories.OCToolRepository;
import fr.cea.organicity.manager.repositories.OCUnitRepository;
import fr.cea.organicity.manager.repositories.OCUnregisteredAssetTypeRepository;
import fr.cea.organicity.manager.security.ClaimsParser;
import fr.cea.organicity.manager.security.OCClaims;
import fr.cea.organicity.manager.security.Role;
import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.RoleManager;
import fr.cea.organicity.manager.security.SecurityConfig;
import fr.cea.organicity.manager.security.SecurityConstants;
import fr.cea.organicity.manager.template.AccessLogMetric;
import fr.cea.organicity.manager.template.AccessTabMetric;
import fr.cea.organicity.manager.template.ApiCallMetric;
import fr.cea.organicity.manager.template.DebugTemplate;
import fr.cea.organicity.manager.template.Dictionaries;
import fr.cea.organicity.manager.template.ErrorMetric;
import fr.cea.organicity.manager.template.ErrorTemplate;
import fr.cea.organicity.manager.template.Experiments;
import fr.cea.organicity.manager.template.Home;
import fr.cea.organicity.manager.template.Info;
import fr.cea.organicity.manager.template.Sites;
import fr.cea.organicity.manager.template.TemplateEngine;
import fr.cea.organicity.manager.template.Users;
import fr.cea.organicity.manager.template.WebPageTemplate;

@RestController
public class UiController {

	@Autowired private TemplateEngine templateService;
	@Autowired private ClaimsParser claimsParser;
	@Autowired private RoleManager roleManager;
	@Autowired private UserLister userLister;
	@Autowired private SecurityConfig secuConfig;
	
	@Autowired private OCSiteRepository siterepository;
	@Autowired private OCServiceRepository serviceRepository;
	@Autowired private OCAssetTypeRepository assetTypeRepository;
	@Autowired private OCUnregisteredAssetTypeRepository unregisteredassetTypeRepository;
	@Autowired private OCAttributeTypeRepository attributeTypeRepository;
	@Autowired private OCUnitRepository unitRepository;
	@Autowired private OCDataTypeRepository datatypeRepository;
	@Autowired private OCToolRepository toolRepository;
	@Autowired private OCAppTypeRepository appTypeRepository;
	@Autowired private OCErrorRepository errorRepository;
	@Autowired private OCApiCallRepository apiCallRepository;
	@Autowired private OCRequestRepository accessRepository;
	@Autowired private ExperimentLister experimentLister;
	
	@RequestMapping("/")
	public String getRoot(HttpServletRequest request) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		return Home.generateHTML(templateService, roles, secuConfig.getUrl(request));
	}
	
	@RequestMapping("/callback")
	public String getCallback(HttpServletRequest request) throws IOException {
		String title = "Redirrect";
		String message = "You will be redirrected to the page you wanted to visit. Please wait...";
		List<Role> roles = roleManager.getRolesForRequest(request);
		return WebPageTemplate.basicTemplate(templateService, roles, title, message);
	}
	
	@RequestMapping("/info")
	@RoleGuard
	public String info(HttpServletRequest request) throws ExecutionException, IOException {
		String id_token = request.getParameter("id_token");
		String access_token = request.getParameter("access_token");
		OCClaims claims;
		try {
			claims = claimsParser.getClaimsFromHeader(id_token);
		} catch (Exception e) {
			return WebPageTemplate.generateNotAvailable(templateService, null, e.getMessage());
		}
		List<Role> roles = roleManager.getRolesForSub(claims.getSub());
		
		return Info.generateHTML(templateService, secuConfig, id_token, access_token, claims, roles, experimentLister);
	}

	@RequestMapping("/users")
	@RoleGuard(roleName=SecurityConstants.USER_VIEWER)
	public String users(HttpServletRequest request) throws IOException {
		List<User> users = userLister.getElements();
		List<Role> roles = roleManager.getRolesForRequest(request);
		return Users.generateUserList(templateService, roles, users);
	}

	@RequestMapping("/users/{userId}")
	@RoleGuard(roleName=SecurityConstants.ROLE_ADMIN)
	public String user(HttpServletRequest request, @PathVariable("userId") String userId) throws ExecutionException, IOException {
		List<Role> standardRoles = secuConfig.getLocalRoles_TEMPORARY();
		List<Role> userRoles = roleManager.getRolesForSub(userId);
		String userName = userLister.getElement(userId).getName();
		return Users.generateUserPermissionDetails(templateService, standardRoles, userName, userId, userRoles, null);
	}
	
	@RequestMapping("/users/{userId}/addrole/{roleName}")
	@RoleGuard(roleName=SecurityConstants.ROLE_ADMIN)
	public String userAddRole(HttpServletRequest request, @PathVariable("userId") String userId, @PathVariable("roleName") String roleName) throws ExecutionException, IOException {
		List<Role> standardRoles = secuConfig.getLocalRoles_TEMPORARY();
		String userName = userLister.getElement(userId).getName();
		
		String message = null;
		try {
			roleManager.addRole(userId, new Role(roleName));
		} catch (Exception e) {
			message = "ERROR: " + e.getMessage();
		}
		
		List<Role> userRoles = roleManager.getRolesForSub(userId);
		
		return Users.generateUserPermissionDetails(templateService, standardRoles, userName, userId, userRoles, message);
	}
	
	@RequestMapping("/users/{userId}/removerole/{roleName}")
	@RoleGuard(roleName=SecurityConstants.ROLE_ADMIN)
	public String userRemoveRole(HttpServletRequest request, @PathVariable("userId") String userId, @PathVariable("roleName") String roleName) throws ExecutionException, IOException {

		List<Role> standardRoles = secuConfig.getLocalRoles_TEMPORARY();
		String userName = userLister.getElement(userId).getName();
		
		String message = null;
		try {
			roleManager.removeRole(userId, new Role(roleName));
		} catch (Exception e) {
			message = "ERROR: " + e.getMessage();
		}
		
		List<Role> userRoles = roleManager.getRolesForSub(userId);
		
		return Users.generateUserPermissionDetails(templateService, standardRoles, userName, userId, userRoles, message);
	}
	
	@RequestMapping("/sites")
	@RoleGuard
	public String sites(HttpServletRequest request) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		return Sites.generateSiteList(templateService, roles, getSiterepository());
	}

	@RequestMapping("/sites/{siteName}")
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-user")
	public String siteGET(HttpServletRequest request, @PathVariable("siteName") String siteName) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		Role adminRole = new Role(SecurityConstants.clientNameKey + ":site-" + siteName + "-admin");
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		if (site == null)
			return ErrorTemplate.generateHTML(templateService, roles, "site " + siteName + " not found.");
		else
			return Sites.generateSiteDetails(templateService, roles, null, hasRole(request, adminRole), site);
	}

	@RequestMapping(value="/sites/{siteName}", method = RequestMethod.POST)
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-admin")
	public String sitePOST(HttpServletRequest request, @PathVariable("siteName") String siteName) throws IOException {	
		List<Role> roles = roleManager.getRolesForRequest(request);
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		if (site == null)
			return ErrorTemplate.generateHTML(templateService, roles, "site " + siteName + " not found.");
		else {
			// basic info update
			if (request.getParameter("email") != null) {
				site.setEmail(request.getParameter("email"));
				site.setRelated(request.getParameter("related"));
				
				String message = null;
				try {
					site = siterepository.saveAndFlush(site);
					message = "Site " + site.getName() + " updated";	
				} catch (Exception e) {
					site = siterepository.getOne(OCSite.computeUrn(siteName));
					message = "Site " + site.getName() + " NOT updated";
				}
				
				return Sites.generateSiteDetails(templateService, roles, message, true, site);
			}

			// new service 
			else {
				OCService service = new OCService();
				service.setName(request.getParameter("name"));
				service.setDescription(request.getParameter("description"));
				service.setRelated(request.getParameter("related"));
				service.setSite(site);			
				
				String message = null;
				try {
					service = serviceRepository.saveAndFlush(service);
					site.getServices().add(service);
					message = "Service " + service.getName() + " created";	
				} catch (Exception e) {
					message = "Service " + service.getName() + " NOT created";
				}
				
				return Sites.generateSiteDetails(templateService, roles, message, true, site);
			}	
		}
	}
	
	@RequestMapping("/sites/{siteName}/{serviceName}")
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-user")
	public String serviceGET(HttpServletRequest request, @PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		Role adminRole = new Role(SecurityConstants.clientNameKey + ":site-" + siteName + "-admin");
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		if (site == null)
			return ErrorTemplate.generateHTML(templateService, roles, "site " + siteName + " not found.");
		else {
			OCService service = site.getService(serviceName);
			if (service == null)
				return ErrorTemplate.generateHTML(templateService, roles, "service " + serviceName + " not found in site " + siteName);
			return Sites.generateServiceDetails(templateService, roles, null, hasRole(request, adminRole), site, service);
		}
	}
	
	@RequestMapping(value="/sites/{siteName}/{serviceName}", method = RequestMethod.POST)
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-admin")
	public String servicePOST(HttpServletRequest request, @PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName) throws IOException {	
		List<Role> roles = roleManager.getRolesForRequest(request);
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		if (site == null)
			return ErrorTemplate.generateHTML(templateService, roles, "site " + siteName + " not found.");
		else {
			OCService service = site.getService(serviceName);
			if (service == null)
				return ErrorTemplate.generateHTML(templateService, roles, "service " + serviceName + " not found in site " + siteName);

			service.setDescription(request.getParameter("description"));
			service.setRelated(request.getParameter("related"));

			String message = null;
			try {
				site = siterepository.saveAndFlush(site);
				message = "Site " + site.getName() + " updated";	
			} catch (Exception e) {
				message = "Site " + site.getName() + " NOT updated";
			}
			
			return Sites.generateServiceDetails(templateService, roles, message, true, site, service);
		}	
	}
		
	@RequestMapping("/sites/{siteName}/{serviceName}/delete")
	@RoleGuard(roleName=SecurityConstants.clientNameKey + ":site-{siteName}-admin")
	public String serviceDELETE(HttpServletRequest request, @PathVariable("siteName") String siteName, @PathVariable("serviceName") String serviceName) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		OCSite site = siterepository.findOne(OCSite.computeUrn(siteName));
		if (site == null)
			return ErrorTemplate.generateHTML(templateService, roles, "site " + siteName + " not found.");
		else {
			OCService service = site.getService(serviceName);
			if (service == null)
				return ErrorTemplate.generateHTML(templateService, roles, "service " + serviceName + " not found in site " + siteName);

			site.getServices().remove(service);
			siterepository.saveAndFlush(site);
			
			String message = null;
			try {
				site = siterepository.saveAndFlush(site);
				message = "Service " + service.getName() + " deleted";	
			} catch (Exception e) {
				message = "Service " + service.getName() + " NOT deleted";
			}
			
			return Sites.generateSiteDetails(templateService, roles, message, true, site);
		}	
	}
	
	@RequestMapping("/experiments")
	@RoleGuard(roleName=SecurityConstants.EXPERIMENT_USER)
	public String experiments(HttpServletRequest request) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		List<Experiment> experiments = experimentLister.getElements();
		return Experiments.generateHTML(templateService, roles, experiments);
	}
	
	@RequestMapping("/experiments/{experimentId}")
	@RoleGuard(roleName=SecurityConstants.EXPERIMENT_USER)
	public ResponseEntity<String> experiment(HttpServletRequest request, @PathVariable("experimentId") String experimentId) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		Experiment experiment = experimentLister.getElement(experimentId);
		List<String> sources = experimentLister.getDataSrcByExperiment(experimentId);
		String content = Experiments.generateHTML(templateService, userLister, roles, experiment, sources);
		
		if (content == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		else
			return new ResponseEntity<>(content, HttpStatus.ACCEPTED);
	}
	
	
	@RequestMapping("/metrics")
	@RoleGuard(roleName=SecurityConstants.METRICS_USER)
	public String metrics(HttpServletRequest request) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		return WebPageTemplate.generateHTML(templateService, roles, "Metrics", "/templates/metrics.html");
	}

	@RequestMapping("/metrics/access/today")
	@RoleGuard(roleName=SecurityConstants.METRICS_USER)
	public String metricsAccessToday(HttpServletRequest request) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		return AccessTabMetric.generateHTML(templateService, roles, accessRepository, userLister, "Metrics", "Today's access", 1);
	}
	
	@RequestMapping("/metrics/access/week")
	@RoleGuard(roleName=SecurityConstants.METRICS_USER)
	public String metricsAccessWeek(HttpServletRequest request) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		return AccessTabMetric.generateHTML(templateService, roles, accessRepository, userLister, "Metrics", "Week's access", 7);
	}
	
	@RequestMapping("/metrics/access/log")
	@RoleGuard(roleName=SecurityConstants.METRICS_USER)
	public String metricsAccessLog(HttpServletRequest request) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		return AccessLogMetric.generateHTML(templateService, roles, accessRepository, userLister, 100);
	}
	
	@RequestMapping("/metrics/access/call")
	@RoleGuard(roleName=SecurityConstants.METRICS_USER)
	public String metricsAccessCall(HttpServletRequest request) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		return ApiCallMetric.generateHTML(templateService, roles, apiCallRepository, 100, 1);
	}
	
	@RequestMapping("/metrics/errors/today")
	@RoleGuard(roleName=SecurityConstants.METRICS_USER)
	public String metricsErrorsToday(HttpServletRequest request) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		return ErrorMetric.generateHTML(templateService, roles, errorRepository, "Today's errors", 1);
	}
	
	@RequestMapping("/metrics/errors/week")
	@RoleGuard(roleName=SecurityConstants.METRICS_USER)
	public String metricsErrorsWeek(HttpServletRequest request) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		return ErrorMetric.generateHTML(templateService, roles, errorRepository, "Week's errors", 7);
	}
	
	@RequestMapping("/dictionaries")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String dictionaries(HttpServletRequest request) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		return Dictionaries.generateDictionaries(templateService, roles);
	}
	
	@RequestMapping("/dictionaries/assettypes")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String assettypesGET(HttpServletRequest request) throws IOException {	
		List<Role> roles = roleManager.getRolesForRequest(request);
		return Dictionaries.generateAssettypes(templateService, roles, null, isDictionaryAdmin(request), getAssetTypeRepository(), getUnregisteredAssetTypeRepository());
	}
		
	@RequestMapping(value="/dictionaries/assettypes", method = RequestMethod.POST)
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public String assettypesPOST(HttpServletRequest request) throws IOException {	
		List<Role> roles = roleManager.getRolesForRequest(request);
		OCAssetType assetType = new OCAssetType();
		assetType.setName(request.getParameter("name"));
		assetType.setDescription(request.getParameter("description"));
		assetType.setRelated(request.getParameter("related"));

		String message = null;
		try {
			assetType = assetTypeRepository.saveAndFlush(assetType);
			try {
				unregisteredassetTypeRepository.delete(OCUnregisteredAssetType.computeUrn(request.getParameter("name")));
			} catch (Exception e) {	
				// do nothing
			}
			message = "AssetType " + assetType.getName() + " created";	
		} catch (Exception e) {
			message = "AssetType " + assetType.getName() + " NOT created";
		}
		
		return Dictionaries.generateAssettypes(templateService, roles, message, isDictionaryAdmin(request), getAssetTypeRepository(), getUnregisteredAssetTypeRepository());
	}	
	
	@RequestMapping("/dictionaries/assettypes/{assettypeName}/delete")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public String assettypeDELETE(HttpServletRequest request, @PathVariable("assettypeName") String assettypeName) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		OCAssetType assetType = assetTypeRepository.findOne(OCAssetType.computeUrn(assettypeName));
		if (assetType == null)
			return ErrorTemplate.generateHTML(templateService, roles, "Asset Type with name " + assettypeName + " not found.");
		else {
			String message = null;
			try {
				assetTypeRepository.delete(assetType);
				message = "AssetType " + assetType.getName() + " deleted";	
			} catch (Exception e) {
				message = "AssetType " + assetType.getName() + " NOT deleted";
			}
			return Dictionaries.generateAssettypes(templateService, roles, message, isDictionaryAdmin(request), getAssetTypeRepository(), getUnregisteredAssetTypeRepository());
		}
	}
	
	@RequestMapping("/dictionaries/assettypes/{assettypeName}")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String assetTypeDetailsGET(HttpServletRequest request, @PathVariable("assettypeName") String assettypeName) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		OCAssetType assetType = assetTypeRepository.findOne(OCAssetType.computeUrn(assettypeName));
		if (assetType == null)
			return ErrorTemplate.generateHTML(templateService, roles, "assettype with name " + assettypeName + " not found.");
		else
			return Dictionaries.generateAssettypeDetail(templateService, roles, null, isDictionaryAdmin(request), assetType, getAttributeTypeRepository());
	}	
	
	@RequestMapping(value="/dictionaries/assettypes/{assettypeName}", method = RequestMethod.POST)
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public String assetTypeDetailsPOST(HttpServletRequest request, @PathVariable("assettypeName") String assettypeName) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		OCAssetType assetType = assetTypeRepository.findOne(OCAssetType.computeUrn(assettypeName));
		if (assetType == null)
			return ErrorTemplate.generateHTML(templateService, roles, "assettype with name " + assettypeName + " not found.");
		else {
			String message = "Update performed";
			String description = request.getParameter("description");
			if (description != null)
				assetType.setDescription(description);
			String related = request.getParameter("related");
			if (related != null)
				assetType.setRelated(related);
			String attributeName = request.getParameter("attribute");
			if (attributeName != null) {
				Collection<OCAttributeType> attributes = assetType.getAttributes();
				OCAttributeType attribute = attributeTypeRepository.findOne(attributeName);
				if (attribute == null) {
					message = "Can't find attribute to be added";
				} else {
					attributes.add(attribute);
				}
			}
			assetType = assetTypeRepository.save(assetType);
			
			return Dictionaries.generateAssettypeDetail(templateService, roles, message, isDictionaryAdmin(request), assetType, getAttributeTypeRepository());
		}
	}
	
	@RequestMapping("/dictionaries/assettypes/{assettypeName}/removeattribute/{attributeName}")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public String assetTypeAttributeREMOVE(HttpServletRequest request, @PathVariable("assettypeName") String assettypeName, @PathVariable("attributeName") String attributeName) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		OCAssetType assetType = assetTypeRepository.findOne(OCAssetType.computeUrn(assettypeName));
		if (assetType == null)
			return ErrorTemplate.generateHTML(templateService, roles, "assettype with name " + assettypeName + " not found.");
		else {
			OCAttributeType attribute = attributeTypeRepository.findOne(attributeName);

			String message = "Can't remove attribute with name " + attributeName + ": not found";
			if (attribute != null && assetType.getAttributes().remove(attribute)) {
				assetType = assetTypeRepository.save(assetType);
				message = "Attribute Type " + attribute.getUrn() + " removed from the list";
			}
			return Dictionaries.generateAssettypeDetail(templateService, roles, message, isDictionaryAdmin(request), assetType, getAttributeTypeRepository());
		}
	}
	
	@RequestMapping("/dictionaries/attributetypes")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String attributeTypesGET(HttpServletRequest request) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		return Dictionaries.generateAttributetypes(templateService, roles, null, isDictionaryAdmin(request), getAttributeTypeRepository());
	}	
	
	@RequestMapping(value="/dictionaries/attributetypes", method = RequestMethod.POST)
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public String attributeTypesPOST(HttpServletRequest request) throws IOException {	
		List<Role> roles = roleManager.getRolesForRequest(request);
		OCAttributeType attrType = new OCAttributeType();
		attrType.setName(request.getParameter("name"));
		attrType.setDescription(request.getParameter("description"));
		attrType.setRelated(request.getParameter("related"));

		String message = null;
		try {
			attrType = attributeTypeRepository.saveAndFlush(attrType);
			message = "AttributeType " + attrType.getName() + " created";	
		} catch (Exception e) {
			message = "AttributeType " + attrType.getName() + " NOT created";
		}
		
		return Dictionaries.generateAttributetypes(templateService, roles, message, isDictionaryAdmin(request), getAttributeTypeRepository());
	}	

	@RequestMapping("/dictionaries/attributetypes/{attributetypeName}/delete")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public String attributeTypeDELETE(HttpServletRequest request, @PathVariable("attributetypeName") String attributetypeName) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		OCAttributeType attributeType = attributeTypeRepository.findOne(OCAttributeType.computeUrn(attributetypeName));
		if (attributeType == null)
			return ErrorTemplate.generateHTML(templateService, roles, "attributeType with name " + attributetypeName + " not found.");
		else {
			Collection<OCAssetType> assets = attributeType.getAssets();
			if (assets.isEmpty()) {
				attributeTypeRepository.delete(attributeType);
				String message = "Attribute Type " + attributetypeName + " deleted";
				return Dictionaries.generateAttributetypes(templateService, roles, message, isDictionaryAdmin(request), getAttributeTypeRepository());
			} else {
				String message = "Can't delete attribute type with name " + attributetypeName + ": it's used by at least one asset type";
				return Dictionaries.generateAttributetypes(templateService, roles, message, isDictionaryAdmin(request), getAttributeTypeRepository());
			}
		}
	}
	
	@RequestMapping("/dictionaries/attributetypes/{attributetypeName}")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String attributeTypeDetailsGET(HttpServletRequest request, @PathVariable("attributetypeName") String attributetypeName) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		OCAttributeType attributeType = attributeTypeRepository.findOne(OCAttributeType.computeUrn(attributetypeName));
		if (attributeType == null)
			return ErrorTemplate.generateHTML(templateService, roles, "attributeType with name " + attributetypeName + " not found.");
		else
			return Dictionaries.generateAttributetypeDetail(templateService, roles, null, isDictionaryAdmin(request), attributeType, getUnitRepository());
	}	
	
	
	@RequestMapping(value="/dictionaries/attributetypes/{attributetypeName}", method = RequestMethod.POST)
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public String attributeTypeDetailsPOST(HttpServletRequest request, @PathVariable("attributetypeName") String attributetypeName) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		OCAttributeType attributeType = attributeTypeRepository.findOne(OCAttributeType.computeUrn(attributetypeName));
		if (attributeType == null)
			return ErrorTemplate.generateHTML(templateService, roles, "attributeType with name " + attributetypeName + " not found.");
		else {
			String message = "Update performed";
			String description = request.getParameter("description");
			if (description != null)
				attributeType.setDescription(description);
			String related = request.getParameter("related");
			if (related != null)
				attributeType.setRelated(related);
			String unitName = request.getParameter("unit");
			if (unitName != null) {
				Collection<OCUnit> units = attributeType.getUnits();
				OCUnit unit = unitRepository.findOne(unitName);
				if (unit == null) {
					message = "Can't find unit to be added";
				} else {
					units.add(unit);
				}
			}			
			attributeType = attributeTypeRepository.save(attributeType);
			
			return Dictionaries.generateAttributetypeDetail(templateService, roles, message, isDictionaryAdmin(request), attributeType, getUnitRepository());
		}
	}
		
	@RequestMapping("/dictionaries/attributetypes/{attributetypeName}/removeunit/{unitName}")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public String attributeTypeUnitREMOVE(HttpServletRequest request, @PathVariable("attributetypeName") String attributetypeName, @PathVariable("unitName") String unitName) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		OCAttributeType attributeType = attributeTypeRepository.findOne(OCAttributeType.computeUrn(attributetypeName));
		if (attributeType == null)
			return ErrorTemplate.generateHTML(templateService, roles, "attributeType with name " + attributetypeName + " not found.");
		else {
			OCUnit unit = unitRepository.findOne(unitName);
			
			String message = "Can't remove unit with name " + unitName + ": not found";
			if (unit != null && attributeType.getUnits().remove(unit)) {
				attributeType = attributeTypeRepository.save(attributeType);
				message = "Unit " + unit.getUrn() + " removed from the list";
			}
			
			return Dictionaries.generateAttributetypeDetail(templateService, roles, message, isDictionaryAdmin(request), attributeType, getUnitRepository());
		}
	}
	
	@RequestMapping("/dictionaries/units")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String unitsGET(HttpServletRequest request) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		return Dictionaries.generateUnits(templateService, roles, null, isDictionaryAdmin(request), getUnitRepository(), getDatatypeRepository());
	}	
	
	@RequestMapping(value="/dictionaries/units", method = RequestMethod.POST)
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public String unitPOST(HttpServletRequest request) throws IOException {	
		List<Role> roles = roleManager.getRolesForRequest(request);
		OCUnit unit = new OCUnit();
		unit.setName(request.getParameter("name"));
		unit.setDatatype(datatypeRepository.findOne(request.getParameter("datatype")));
		unit.setDescription(request.getParameter("description"));
		unit.setRelated(request.getParameter("related"));
		
		String message = null;
		try {
			unit = unitRepository.saveAndFlush(unit);
			message = "Unit " + unit.getName() + " created";	
		} catch (Exception e) {
			message = "Unit " + unit.getName() + " NOT created";
		}
		
		return Dictionaries.generateUnits(templateService, roles, message, isDictionaryAdmin(request), getUnitRepository(), getDatatypeRepository());
	}	
	
	@RequestMapping("/dictionaries/units/{unitName}/delete")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public String unitDELETE(HttpServletRequest request, @PathVariable("unitName") String unitName) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		OCUnit unit = unitRepository.findOne(OCUnit.computeUrn(unitName));
		if (unit == null)
			return ErrorTemplate.generateHTML(templateService, roles, "unit with name " + unitName + " not found.");
		else {
			Collection<OCAttributeType> attributes = unit.getAttributes();
			if (attributes.isEmpty()) {
				unitRepository.delete(unit.getUrn());
				String message = "unit with name " + unitName + " deleted";
				return Dictionaries.generateUnits(templateService, roles, message, isDictionaryAdmin(request), getUnitRepository(), getDatatypeRepository());
			} else {
				String message = "Can't delete unit with name " + unitName + ": it's used by at least one attributes";
				return Dictionaries.generateUnits(templateService, roles, message, isDictionaryAdmin(request), getUnitRepository(), getDatatypeRepository());
			}
		}
	}
	
	@RequestMapping(value="/dictionaries/units/{unitName}", method = RequestMethod.GET)
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String unitDetailsGET(HttpServletRequest request, @PathVariable("unitName") String unitName) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		OCUnit unit = unitRepository.findOne(OCUnit.computeUrn(unitName));
		if (unit == null)
			return ErrorTemplate.generateHTML(templateService, roles, "unit with name " + unitName + " not found.");
		else
			return Dictionaries.generateUnitDetail(templateService, roles, null, isDictionaryAdmin(request), unit);
	}	
	
	@RequestMapping(value="/dictionaries/units/{unitName}", method = RequestMethod.POST)
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public String unitDetailsPOST(HttpServletRequest request, @PathVariable("unitName") String unitName) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		OCUnit unit = unitRepository.findOne(OCUnit.computeUrn(unitName));
		if (unit == null)
			return ErrorTemplate.generateHTML(templateService, roles, "unit with name " + unitName + " not found.");
		else {
			unit.setDescription(request.getParameter("description"));
			unit.setRelated(request.getParameter("related"));
			unit = unitRepository.save(unit);
			String message = "Update performed";
			return Dictionaries.generateUnitDetail(templateService, roles, message, isDictionaryAdmin(request), unit);
		}
	}
	
	@RequestMapping(value="/dictionaries/datatypes", method = RequestMethod.GET)
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String datatypesGET(HttpServletRequest request) throws IOException {	
		List<Role> roles = roleManager.getRolesForRequest(request);
		return Dictionaries.generateDatatypes(templateService, roles, isDictionaryAdmin(request), null, getDatatypeRepository());
	}	
	
	@RequestMapping(value="/dictionaries/datatypes", method = RequestMethod.POST)
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public String datatypesPOST(HttpServletRequest request) throws IOException {	
		List<Role> roles = roleManager.getRolesForRequest(request);
		OCDataType dataType = new OCDataType();
		dataType.setName(request.getParameter("name"));
		
		String message = null;
		try {
			dataType = datatypeRepository.saveAndFlush(dataType);
			message = "DataType " + dataType.getName() + " created";	
		} catch (Exception e) {
			message = "DataType " + dataType.getName() + " NOT created";
		}
		
		return Dictionaries.generateDatatypes(templateService, roles, isDictionaryAdmin(request), message, getDatatypeRepository());
	}	

	@RequestMapping("/dictionaries/datatypes/{datatypeName}/delete")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public String datatypeDELETE(HttpServletRequest request, @PathVariable("datatypeName") String datatypeName) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		OCDataType datatype = datatypeRepository.findOne(OCDataType.computeUrn(datatypeName));
		if (datatype == null)
			return ErrorTemplate.generateHTML(templateService, roles, "datatype with name " + datatypeName + " not found.");
		else {
			Collection<OCUnit> units = datatype.getUnits();
			if (units.isEmpty()) {
				datatypeRepository.delete(datatype);
				String message = "Datatype " + datatypeName + " deleted";
				return Dictionaries.generateDatatypes(templateService, roles, isDictionaryAdmin(request), message, getDatatypeRepository());
			} else {
				String message = "Can't delete datatype " + datatypeName + ": it's used by at least one unit";
				return Dictionaries.generateDatatypes(templateService, roles, isDictionaryAdmin(request), message, getDatatypeRepository());
			}
		}
	}

	@RequestMapping("/dictionaries/datatypes/{datatypeName}")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String datatypeDetailsGET(HttpServletRequest request, @PathVariable("datatypeName") String datatypeName) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		OCDataType datatype = datatypeRepository.findOne(OCDataType.computeUrn(datatypeName));
		if (datatype == null)
			return ErrorTemplate.generateHTML(templateService, roles, "datatype with name " + datatypeName + " not found.");
		else
			return Dictionaries.generateDatatypeDetail(templateService, roles, isDictionaryAdmin(request), datatype);
	}	
	
	@RequestMapping("/dictionaries/tools")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String tools(HttpServletRequest request) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		return Dictionaries.generateTools(templateService, roles, getToolRepository());
	}
	
	@RequestMapping("/dictionaries/applicationtypes")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String applicationTypes(HttpServletRequest request) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		return Dictionaries.generateAppTypes(templateService, roles, getAppTypeRepository());
	}
	
	@RequestMapping("/links")
	@RoleGuard(roleName=SecurityConstants.DEVELOPER)
	public String links(HttpServletRequest request) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		return WebPageTemplate.generateHTML(templateService, roles, "Dev links", "/templates/links.html");
	}
	
	@RequestMapping("/debug")
	@RoleGuard(roleName=SecurityConstants.DEVELOPER)
	public String debug(HttpServletRequest request) throws IOException {
		List<Role> roles = roleManager.getRolesForRequest(request);
		return DebugTemplate.generateHTML(templateService, roles);
	}
		
	private boolean hasRole(HttpServletRequest request, Role role) {
		List<Role> roles = roleManager.getRolesForRequest(request);
		return (roles != null && roles.contains(role));
	}
	
	private boolean isDictionaryAdmin(HttpServletRequest request) {
		Role role = new Role(SecurityConstants.DICTIONARY_ADMIN);
		return hasRole(request, role);
	}
	
	private List<OCSite> getSiterepository() {
		return getRepositoryContent(siterepository, (o1, o2) -> o1.getUrn().compareTo(o2.getUrn())); 	
	}
	
	private List<OCAssetType> getAssetTypeRepository() {
		return getRepositoryContent(assetTypeRepository, (o1, o2) -> o1.getUrn().compareTo(o2.getUrn()));
	}
	
	private List<OCUnregisteredAssetType> getUnregisteredAssetTypeRepository() {
		return getRepositoryContent(unregisteredassetTypeRepository, (o1, o2) -> o1.getUrn().compareTo((o2).getUrn()));
	}
	
	private List<OCAttributeType> getAttributeTypeRepository() {
		return getRepositoryContent(attributeTypeRepository, (o1, o2) -> o1.getUrn().compareTo(o2.getUrn()));
	}
	
	private List<OCUnit> getUnitRepository() {
		return getRepositoryContent(unitRepository, (o1, o2) -> o1.getUrn().compareTo(o2.getUrn()));
	}
	
	private List<OCDataType> getDatatypeRepository() {
		return getRepositoryContent(datatypeRepository, (o1, o2) -> o1.getUrn().compareTo(o2.getUrn()));
	}
	
	private List<OCTool> getToolRepository() {
		return getRepositoryContent(toolRepository, (o1, o2) -> o1.getUrn().compareTo(o2.getUrn()));
	}
	
	private List<OCAppType> getAppTypeRepository() {
		return getRepositoryContent(appTypeRepository, (o1, o2) -> o1.getUrn().compareTo(o2.getUrn()));
	}
		
	private static <T extends Object> List<T> getRepositoryContent(CrudRepository<T, String> repository, Comparator<? super T> comparator) {
		 return StreamSupport.stream(repository.findAll().spliterator(), false).sorted(comparator).collect(Collectors.toList());
	}
}
