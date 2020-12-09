package de.lh.tool.service.entity.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.NeedUserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Need;
import de.lh.tool.domain.model.NeedUser;
import de.lh.tool.domain.model.NeedUserState;
import de.lh.tool.domain.model.Project;
import de.lh.tool.domain.model.ProjectHelperType;
import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.NeedUserRepository;
import de.lh.tool.service.entity.interfaces.MailService;
import de.lh.tool.service.entity.interfaces.NeedService;
import de.lh.tool.service.entity.interfaces.NeedUserService;
import de.lh.tool.service.entity.interfaces.ProjectService;
import de.lh.tool.service.entity.interfaces.UserService;
import de.lh.tool.service.entity.interfaces.crud.NeedUserCrudService;
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;

@Service
public class NeedUserServiceImpl extends BasicEntityCrudServiceImpl<NeedUserRepository, NeedUser, NeedUserDto, Long>
		implements NeedUserService, NeedUserCrudService {

	@Autowired
	private NeedService needService;

	@Autowired
	private UserService userService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private MailService mailService;

	// FUTURE: use standard methods
	//
	// necessary rights:
	// . . . . . . +----------------8--+
	// 1-apply-2 . 3-approve4+5approve-6
	// v . . . v . v . . . . v . . . . v
	// NONE . APPLIED . APPROVED . REJECTED
	// ^ . . . . . . . . . . |
	// +---7----apply--------+
	// setting to NONE = delete
	@Override
	@Transactional
	public NeedUserDto saveOrUpdateDto(Long needId, Long userId, NeedUserDto dto) throws DefaultException {
		NeedUser needUser = findByNeedIdAndUserId(needId, userId);

		Project project = getProject(needUser);
		boolean noPermissionOnProject = !(project != null && projectService.hasReadPermission(project))
				&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_PROJECT);
		boolean noPermissionOnUser = !userService.isCurrentUser(needUser.getUser())
				&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_USER);
		if (noPermissionOnProject || noPermissionOnUser) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}

		@NonNull
		NeedUserState oldState = needUser.getState();
		NeedUserState newState = Optional.ofNullable(dto).map(NeedUserDto::getState)
				.orElseThrow(ExceptionEnum.EX_NEED_USER_INVALID_STATE::createDefaultException);

		if (newState.equals(oldState)) {
			return convertToDto(needUser);
		}

		if (oldState == NeedUserState.APPLIED && newState == NeedUserState.NONE) {
			// 1: APPLIED -> NONE
			userRoleService.checkCurrentUserRight(UserRole.RIGHT_NEEDS_APPLY);
			delete(needUser);
			dto.setId(null);
			return dto;

		} else if (oldState == NeedUserState.NONE && newState == NeedUserState.APPLIED) {
			// 2: NONE -> APPLIED
			userRoleService.checkCurrentUserRight(UserRole.RIGHT_NEEDS_APPLY);

		} else if ((oldState == NeedUserState.APPROVED && newState == NeedUserState.APPLIED)
				// 3: APPROVED -> APPLIED
				|| (oldState == NeedUserState.APPLIED && newState == NeedUserState.APPROVED)
				// 4: APPLIED -> APPROVED
				|| (oldState == NeedUserState.REJECTED && newState == NeedUserState.APPROVED)
				// 5: REJECTED -> APPROVED
				|| (oldState == NeedUserState.APPROVED && newState == NeedUserState.REJECTED)
				// 6: APPROVED -> REJECTED
				|| (oldState == NeedUserState.APPLIED && newState == NeedUserState.REJECTED
				// 8: APPLIED -> REJECTED
				)) {

			userRoleService.checkCurrentUserRight(UserRole.RIGHT_NEEDS_APPROVE);
			needUser.setState(newState);
			mailService.sendNeedUserStateChangedMailToUser(needUser);

		} else if (oldState == NeedUserState.APPROVED && newState == NeedUserState.NONE) {
			// 7: APPROVED -> NONE
			userRoleService.checkCurrentUserRight(UserRole.RIGHT_NEEDS_APPLY);
			needUser.setState(NeedUserState.NONE);
			Optional.ofNullable(project).map(Project::getId)
					.ifPresent(projectId -> userService
							.findByProjectIdAndRoleIgnoreCase(projectId, UserRole.ROLE_LOCAL_COORDINATOR).stream()
							.forEach(user -> mailService.sendNeedUserStateChangedMailToCoordinator(needUser, user)));
			delete(needUser);
			dto.setId(null);
			return dto;
		} else {

			throw ExceptionEnum.EX_NEED_USER_INVALID_STATE.createDefaultException();
		}

		// if mail has to be sent, new state is already set above
		needUser.setState(newState);

		checkValidity(needUser);

		return convertToDto(save(needUser));

	}

	@Override
	protected void checkValidity(@NonNull NeedUser needUser) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_NEED_ID, needUser.getNeed());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_USER_ID, needUser.getUser());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_STATE, needUser.getState());
	}

	private Project getProject(NeedUser needUser) {
		Optional<Project> project = Optional.ofNullable(needUser).map(NeedUser::getNeed).map(Need::getProjectHelperType)
				.map(ProjectHelperType::getProject);
		return project.orElse(null);
	}

	@Override
	@Transactional
	public NeedUserDto findDtoByNeedIdAndUserId(Long needId, Long userId) throws DefaultException {
		NeedUser needUser = findByNeedIdAndUserId(needId, userId);
		checkFindPermission(needUser);
		return convertToDto(needUser);
	}

	private NeedUser findByNeedIdAndUserId(Long needId, Long userId) throws DefaultException {
		Need need = needService.findByIdOrThrowInvalidIdException(needId);
		User user = userService.findByIdOrThrowInvalidIdException(userId);
		return getRepository().findByNeedAndUser(need, user)
				.orElse(NeedUser.builder().need(need).user(user).state(NeedUserState.NONE).build());
	}

	@Override
	@Transactional
	public List<NeedUserDto> findDtosByNeedId(Long needId) throws DefaultException {
		checkFindRight();
		Need need = needService.findByIdOrThrowInvalidIdException(needId);

		if (!userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_PROJECT)) {
			projectService.checkReadPermission(need.getProjectHelperType().getProject());
		}

		List<NeedUser> needUserList = getRepository().findByNeedOrderByUser_LastNameAscUser_FirstNameAsc(need);
		needUserList = needUserList.stream().map(this::anonymizeOrFilterNeedUserIfNecessary).filter(Objects::nonNull)
				.collect(Collectors.toList());

		return convertToDtoList(needUserList);
	}

	private NeedUser anonymizeOrFilterNeedUserIfNecessary(NeedUser needUser) {
		if (hasReadPermission(needUser)) {
			return needUser;
		}
		// necessary for getting correct approved count if user isn't allowed to see
		// whole user list
		if (userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_GET_ANONYMIZED_USER_LIST)) {
			return new NeedUser(needUser.getId(), needUser.getNeed(), new User(), needUser.getState());
		}
		return null;
	}

	@Override
	public boolean hasReadPermission(@NonNull NeedUser needUser) {
		boolean permissionOnProject = Optional.ofNullable(getProject(needUser)).map(projectService::hasReadPermission)
				.orElse(Boolean.FALSE)
				|| userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_PROJECT);
		boolean permissionOnUser = userService.isCurrentUser(needUser.getUser())
				|| userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_GET_FOREIGN_USER);
		return permissionOnProject && permissionOnUser;
	}

	@Override
	public boolean hasWritePermission(@NonNull NeedUser needUser) {
		boolean permissionOnProject = Optional.ofNullable(getProject(needUser)).map(projectService::hasReadPermission)
				.orElse(Boolean.FALSE)
				|| userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_PROJECT);
		boolean permissionOnUser = userService.isCurrentUser(needUser.getUser())
				|| userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_USER);
		return permissionOnProject && permissionOnUser;
	}

	@Override
	public String getRightPrefix() {
		return UserRole.NEEDS_USERS_PREFIX;
	}

}
