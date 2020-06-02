package de.lh.tool.service.entity.impl;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
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

@Service
public class UserRoleServiceImpl extends BasicMappableEntityServiceImpl<UserRoleRepository, UserRole, UserRoleDto, Long>
		implements UserRoleService {

	private static final String GRANT_RIGHT_PREFIX = "ROLE_RIGHT_USERS_GRANT_";

	@Autowired
	private UserService userService;

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
		return hasCurrentUserRight(GRANT_RIGHT_PREFIX + role);
	}

	@Override
	@Transactional
	public List<UserRoleDto> findDtosByUserId(Long userId) throws DefaultException {
		if (userId == null) {
			throw ExceptionEnum.EX_NO_ID_PROVIDED.createDefaultException();
		}
		User user = userService.findById(userId).orElseThrow(ExceptionEnum.EX_INVALID_USER_ID::createDefaultException);
		userService.checkIfEditIsAllowed(user, true);
		return convertToDtoList(user.getRoles());
	}

	@Override
	@Transactional
	public UserRoleDto createUserRoleDto(UserRoleDto dto) throws DefaultException {
		if (dto.getId() != null) {
			throw ExceptionEnum.EX_ID_PROVIDED.createDefaultException();
		}
		UserRole userRole = convertToEntity(dto);
		if (!hasCurrentUserRightToGrantRole(userRole.getRole())) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
		if (userRole.getUser() == null) {
			throw ExceptionEnum.EX_INVALID_USER_ID.createDefaultException();
		}
		userService.checkIfEditIsAllowed(userRole.getUser(), true);
		if (hasUserRight(userRole.getUser(), userRole.getRole())) {
			throw ExceptionEnum.EX_USER_ROLE_ALREADY_EXISTS.createDefaultException();
		}
		return convertToDto(save(userRole));
	}

	@Override
	@Transactional
	public void deleteUserRoleById(Long id) throws DefaultException {
		if (id == null) {
			throw ExceptionEnum.EX_NO_ID_PROVIDED.createDefaultException();
		}
		UserRole userRole = findById(id).orElseThrow(ExceptionEnum.EX_INVALID_ID::createDefaultException);
		if (!hasCurrentUserRightToGrantRole(userRole.getRole())) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
		userService.checkIfEditIsAllowed(userRole.getUser(), true);
		userRole.getUser().getRoles().remove(userRole);
		delete(userRole);
	}

	@Override
	public UserRole convertToEntity(UserRoleDto dto) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<UserRoleDto, UserRole>() {
			@Override
			protected void configure() {
				using(c -> Optional.ofNullable((UserRoleDto) c.getSource()).map(UserRoleDto::getUserId)
						.flatMap(userService::findById).orElse(null)).map(source).setUser(null);
			}
		});
		return modelMapper.map(dto, UserRole.class);
	}
}
