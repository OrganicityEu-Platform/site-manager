package fr.cea.organicity.manager.services.rolemanager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Pair;

import fr.cea.organicity.manager.domain.OCSite;
import fr.cea.organicity.manager.exceptions.local.LocalException;
import fr.cea.organicity.manager.exceptions.local.MethodNotAllowedLocalException;
import fr.cea.organicity.manager.exceptions.local.NotFoundLocalException;
import fr.cea.organicity.manager.repositories.OCSiteRepository;
import fr.cea.organicity.manager.services.userlister.User;
import fr.cea.organicity.manager.services.userlister.UserLister;

public class SiteRoleManager {

	private final OCSiteRepository siterepo;
	private final UserLister userLister;
	
	public SiteRoleManager(OCSiteRepository siterepo, UserLister userLister) {
		this.siterepo = siterepo;
		this.userLister = userLister;
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
 	
 	public List<Pair<String, String>> getSiteManagers(String sitename) throws NotFoundLocalException {
 		List<Pair<String, String>> list = new ArrayList<>();
 		for (String sub : getSiteByName(sitename).getManagers()) {
 			String name = userLister.getUserNameOrSub(sub);
 			list.add(new Pair<String, String>(sub, name));
 		}
 		return list;
 	}
	
	public boolean isSiteManager(String userId, String sitename) throws NotFoundLocalException {
		return getSiteManagers(sitename).contains(userId);
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
 