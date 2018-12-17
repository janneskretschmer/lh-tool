package de.lh.tool.domain.model;

import org.springframework.security.core.GrantedAuthority;

public enum Right implements GrantedAuthority {
	USERS_GET_ALL, USERS_CREATE;

	@Override
	public String getAuthority() {
		return name();
	}

}
