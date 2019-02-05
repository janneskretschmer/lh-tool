package de.lh.tool.service.entity.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.UserRoleRepository;
import de.lh.tool.service.entity.interfaces.UserRoleService;
import de.lh.tool.service.entity.interfaces.UserService;

@Service
public class UserRoleServiceImpl extends BasicEntityServiceImpl<UserRoleRepository, UserRole, Long>
		implements UserRoleService {

	@Autowired
	private UserService userService;

	@Override
	public boolean hasCurrentUserRight(String right) {
		User user = userService.getCurrentUser();
		return hasUserRight(user, right);
	}

	@Override
	public boolean hasUserRight(User user, String right) {
		if (right != null && user != null && user.getAuthorities() != null) {
			return user.getAuthorities().stream().anyMatch(a -> right.equals(a.getAuthority()));
		}
		return false;
	}
}
