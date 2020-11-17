package de.lh.tool.service.entity.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.UserRoleDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.UserRoleRepository;
import de.lh.tool.service.entity.interfaces.UserRoleService;
import de.lh.tool.service.entity.interfaces.UserService;
import de.lh.tool.service.entity.interfaces.crud.UserRoleCrudService;
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;

@Service
public class UserRoleServiceImpl extends BasicEntityCrudServiceImpl<UserRoleRepository, UserRole, UserRoleDto, Long>
		implements UserRoleService, UserRoleCrudService {

	@Autowired
	private UserService userService;

	@Override
	@Transactional
	public List<UserRoleDto> findDtosByUserId(@NonNull Long userId) throws DefaultException {
		checkFindRight();
		User user = userService.findByIdOrThrowInvalidIdException(userId);
		userService.checkReadPermission(user);
		return convertToDtoList(user.getRoles());
	}

	@Override
	public boolean hasCurrentUserRight(String right) {
		User user = userService.getCurrentUser();
		return hasUserRight(user, right);
	}

	@Override
	public void checkCurrentUserRight(String right) throws DefaultException {
		if (!hasCurrentUserRight(right)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
	}

	@Override
	public boolean hasUserRight(User user, String right) {
		if (right != null && user != null && user.getAuthorities() != null) {
			return user.getAuthorities().stream().anyMatch(a -> right.equals(a.getAuthority()));
		}
		return false;
	}

	@Override
	public boolean hasCurrentUserRightToGrantRole(String role) {
		return hasCurrentUserRight(StringUtils.join(UserRole.USERS_GRANT_ROLE_PREFIX, "_", role));
	}

	@Override
	@Transactional
	public boolean hasCurrentUserRightToGrantAllRoles(@NonNull User user) {
		return getRepository().findByUserId(user.getId()).stream().map(UserRole::getRole)
				.allMatch(this::hasCurrentUserRightToGrantRole);
	}

	@Override
	protected void checkValidity(@NonNull UserRole userRole) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_ROLE, userRole.getRole());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_USER_ID, userRole.getUser());
		if (hasUserRight(userRole.getUser(), userRole.getRole())) {
			throw ExceptionEnum.EX_USER_ROLE_ALREADY_EXISTS.createDefaultException();
		}
	}

	@Override
	protected void postDelete(@NonNull UserRole userRole) {
		userRole.getUser().getRoles().remove(userRole);
	}

	@Override
	public boolean hasReadPermission(@NonNull UserRole userRole) {
		return userService.hasReadPermission(userRole.getUser());
	}

	@Override
	public boolean hasWritePermission(@NonNull UserRole userRole) {
		return userService.hasWritePermission(userRole.getUser()) && hasCurrentUserRightToGrantRole(userRole.getRole());
	}

	@Override
	public String getRightPrefix() {
		return UserRole.USERS_ROLES_PREFIX;
	}
}
