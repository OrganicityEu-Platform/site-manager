package fr.cea.organicity.manager.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import fr.cea.organicity.manager.services.rolemanager.Role;
import lombok.Data;

@Data
public class Identity implements UserDetails {

	private static final long serialVersionUID = -1241372561210700107L;

	private final String sub;
	private final String name;
	private final String idToken;
	private final String accessToken;
	private final List<Role> roles;
	private final List<String> roleNames;
	private final Collection<? extends GrantedAuthority> authorities;
	
	public boolean check() {
		System.out.println("test");
		return true;
	}
	
	@Override
	public String getUsername() {
		return sub;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public boolean hasRole(Role adminRole) {
		return roles.contains(adminRole);
	}
}
