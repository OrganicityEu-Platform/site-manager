package fr.cea.organicity.manager.services.rolemanager;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Role {
	private final RoleScope scope;
	private final String name;
	
	private static final String separator = ":";
	
	public Role(String qualifiedName) {
		int idx = qualifiedName.indexOf(separator);
		if (idx == -1)
			throw new RuntimeException("separator " + separator + " not found in " + qualifiedName);
		scope = RoleScope.valueOf(qualifiedName.substring(0, idx));
		name = qualifiedName.substring(idx + separator.length());
	}
	
	public String getQualifiedName() {
		return scope + separator + name;
	}
}
