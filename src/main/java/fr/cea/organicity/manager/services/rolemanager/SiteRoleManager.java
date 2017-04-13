package fr.cea.organicity.manager.services.rolemanager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.javatuples.Pair;

import fr.cea.organicity.manager.domain.OCService;
import fr.cea.organicity.manager.domain.OCSite;
import fr.cea.organicity.manager.exceptions.local.LocalException;
import fr.cea.organicity.manager.exceptions.local.MethodNotAllowedLocalException;
import fr.cea.organicity.manager.exceptions.local.NotFoundLocalException;
import fr.cea.organicity.manager.repositories.OCServiceRepository;
import fr.cea.organicity.manager.repositories.OCSiteRepository;
import fr.cea.organicity.manager.services.userlister.User;
import fr.cea.organicity.manager.services.userlister.UserLister;

public class SiteRoleManager {

	private final OCSiteRepository siterepo;
	private final OCServiceRepository servicerepo;
	private final UserLister userLister;
	
	public SiteRoleManager(OCSiteRepository siterepo, OCServiceRepository servicerepo, UserLister userLister) {
		this.siterepo = siterepo;
		this.userLister = userLister;
		this.servicerepo = servicerepo;
	}

	
	/* ======================= */
	/* GENERAL ROLE MANAGEMENT */
	/* ======================= */
	
	public List<Role> getRolesForSub(String userId) {
		return getSiteNamesManaged(userId).stream().map(this::getSiteManagerRole).collect(Collectors.toList());
	}

	public void addRole(String userId, Role role) throws LocalException {
		String sitename = getSiteManagerName(role);
		OCSite site = getSiteByName(sitename);
 		User user = getUserBySub(userId); // Checks if the user exists
 		site.getManagers().add(user.getId());
 		siterepo.save(site);
	}
	
	public void removeRole(String userId, Role role) throws LocalException {
		String sitename = getSiteManagerName(role);
		OCSite site = getSiteByName(sitename);
 		User user = getUserBySub(userId); // Checks if the user exists
 		site.getManagers().remove(user.getId());
 		siterepo.save(site);
	}

 	private OCSite getSiteByName(String sitename) throws NotFoundLocalException {
 		String urn = OCSite.computeUrn(sitename);
 		OCSite site = siterepo.findOne(urn);
 		if (site == null)
 			throw new NotFoundLocalException(urn, OCSite.class);
 		return site;
 	}
	
 	private OCService getServiceByName(String sitename, String servicename) throws NotFoundLocalException {
 		String urn = OCService.computeUrn(sitename, servicename);
 		OCService service = servicerepo.findOne(urn);
 		if (service == null)
 			throw new NotFoundLocalException(urn, OCService.class);
 		return service;
 	}
 	
 	private User getUserBySub(String sub) throws NotFoundLocalException {
 		User user = userLister.getUser(sub).getLastSuccessResult();
 		if (user == null)
 			throw new NotFoundLocalException(sub, User.class);
 		return user;
 	}
 	
	
	/* ======================== */
	/* SITE MANAGERS MANAGEMENT */
	/* ======================== */
	
	public List<Role> getSiteManagerRoles() {
		List<Role> list = new ArrayList<>();		
		List<String> siteNames = siterepo.findAll().stream().map(OCSite::getName).collect(Collectors.toList());
		siteNames.sort(Comparator.naturalOrder());
		for (String siteName : siteNames) {
			list.add(getSiteManagerRole(siteName));
		}
		return list;
	}
 	
	public boolean isSiteManager(String userId, String sitename) throws NotFoundLocalException {
		for (Pair<String, String> manager : getSiteManagers(sitename)) {
			if (manager.getValue0().equals(userId))
				return true;
		}
		return false;
	}
	
 	public List<Pair<String, String>> getSiteManagers(String sitename) throws NotFoundLocalException {
 		List<Pair<String, String>> list = new ArrayList<>();
 		for (String sub : getSiteByName(sitename).getManagers()) {
 			String name = userLister.getUserNameOrSub(sub);
 			list.add(new Pair<String, String>(sub, name));
 		}
 		return list;
 	}

	public boolean isServiceManager(String userId, String sitename, String servicename) throws NotFoundLocalException {
		for (Pair<String, String> manager : getServiceManagers(sitename, servicename)) {
			if (manager.getValue0().equals(userId))
				return true;
		}
		return false;
	}
	
 	public List<Pair<String, String>> getServiceManagers(String sitename, String servicename) throws NotFoundLocalException {
 		List<Pair<String, String>> list = new ArrayList<>();
 		for (String sub : getServiceByName(sitename, servicename).getManagers()) {
 			String name = userLister.getUserNameOrSub(sub);
 			list.add(new Pair<String, String>(sub, name));
 		}
 		return list;
 	}
	
	public Object getNonSiteManagers(String sitename) throws NotFoundLocalException {
 		Set<String> managers = getSiteByName(sitename).getManagers();
 		return computePairs(managers);
	}
	
 	public List<Pair<String, String>> getNonServiceManagers(String sitename, String servicename) throws NotFoundLocalException {
 		Set<String> managers = getServiceByName(sitename, servicename).getManagers();
 		return computePairs(managers);
 	}

 	private List<Pair<String, String>> computePairs(Set<String> managers) {
 		List<Pair<String, String>> list = new ArrayList<>();
 		for (User user : userLister.getUsers().getLastSuccessResult()) {
 			String sub = user.getId();
 			
 			if (! managers.contains(sub)) {
 				String name = userLister.getUserNameOrSub(user.getId());
 				list.add(new Pair<String, String>(sub, name)); 				
 			}
 		}
 		
 		list.sort(Comparator.comparing(Pair::getValue1, String::compareToIgnoreCase));
 		return list;
 	}
 	
	private Role getSiteManagerRole(String siteName) {
		return new Role(RoleScope.LOCAL, "site-" + siteName + "-manager");
	}
	
	private String getSiteManagerName(Role role) throws LocalException {
		RoleScope scope = role.getScope();
		String name = role.getName();
		
		if (scope != RoleScope.LOCAL && ! name.startsWith("site-") && ! name.endsWith("-manager"))
			throw new MethodNotAllowedLocalException(null);
		
		return name.substring("site-".length(), name.length() - "-manager".length());
	}	
	
 	private List<String> getSiteNamesManaged(String sub) {
 		List<String> list = new ArrayList<>();
 		for (OCSite site : siterepo.findAll()) {
 			if (site.getManagers().contains(sub))
 				list.add(site.getName());
 		}
 		return list;
 	}
}
 