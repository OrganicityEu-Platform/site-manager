package fr.cea.organicity.manager.template;

import fr.cea.organicity.manager.otherservices.User;
import fr.cea.organicity.manager.otherservices.UserLister;

public class UserUtil {

	
	private final UserLister userLister;


	public UserUtil(UserLister userLister) {
		this.userLister = userLister;
	}
	
	public String getUserDisplayString(String userId) {
		User user = userLister.getElement(userId);
		if (user == null)
			return userId;
		else
			return user.getName(); 
	}
}
