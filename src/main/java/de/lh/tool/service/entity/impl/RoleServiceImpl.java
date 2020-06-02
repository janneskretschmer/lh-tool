package de.lh.tool.service.entity.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.RoleDto;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.RoleService;
import de.lh.tool.service.entity.interfaces.UserRoleService;
import de.lh.tool.service.entity.interfaces.UserService;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	UserService userService;

	@Autowired
	UserRoleService userRoleService;

	@Override
	public List<RoleDto> getGrantableRoleDtos() {
		return UserRole.getRoles().stream().filter(userRoleService::hasCurrentUserRightToGrantRole).map(RoleDto::new)
				.collect(Collectors.toList());
	}

}
