package de.lh.tool.service.entity.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.NeedUserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.DefaultRuntimeException;
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
import de.lh.tool.service.entity.interfaces.UserRoleService;
import de.lh.tool.service.entity.interfaces.UserService;
import lombok.NonNull;

@Service
public class NeedUserServiceImpl extends BasicMappableEntityServiceImpl<NeedUserRepository, NeedUser, NeedUserDto, Long>
		implements NeedUserService {

	@Autowired
	private NeedService needService;

	@Autowired
	private UserService userService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private MailService mailService;

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

		boolean noPermissionOnProject = !projectService.isOwnProject(getProject(needUser))
				&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_PROJECT);
		boolean noPermissionOnUser = needUser.getUser() != userService.getCurrentUser()
				&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_USER);
		if (noPermissionOnProject || noPermissionOnUser) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}

		@NonNull
		NeedUserState oldState = needUser.getState();
		NeedUserState newState = Optional.ofNullable(dto).map(NeedUserDto::getState)
				.orElseThrow(ExceptionEnum.EX_NEED_USER_INVALID_STATE::createDefaultException);

		if (oldState == newState) {
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
			userService.findByProjectIdAndRoleIgnoreCase(getProject(needUser).getId(), UserRole.ROLE_LOCAL_COORDINATOR)
					.stream().forEach(u -> mailService.sendNeedUserStateChangedMailToCoordinator(needUser, u));
			delete(needUser);
			dto.setId(null);
			return dto;
		} else {

			throw ExceptionEnum.EX_NEED_USER_INVALID_STATE.createDefaultException();
		}

		// if mail has to be sent, new state is already set above
		needUser.setState(newState);
		return convertToDto(save(needUser));

	}

	private @NonNull Project getProject(NeedUser needUser) {
		Optional<@NonNull Project> project = Optional.ofNullable(needUser).map(NeedUser::getNeed)
				.map(Need::getProjectHelperType).map(ProjectHelperType::getProject).filter(p -> p.getId() != null);
		return project.orElseThrow(() -> new DefaultRuntimeException(ExceptionEnum.EX_INVALID_PROJECT_ID));
	}

	private boolean isViewAllowed(NeedUser needUser) {
		boolean self = userService.getCurrentUser().getId().equals(needUser.getUser().getId());
		boolean permissionOnProject = projectService.isOwnProject(getProject(needUser))
				|| userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_PROJECT);
		boolean allowedToViewForeign = userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_GET_FOREIGN_USER);
		return self || (permissionOnProject && allowedToViewForeign);
	}

	@Override
	@Transactional
	public NeedUserDto findDtoByNeedIdAndUserId(Long needId, Long userId) throws DefaultException {
		NeedUser needUser = findByNeedIdAndUserId(needId, userId);
		if (!isViewAllowed(needUser)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
		return convertToDto(needUser);
	}

	private NeedUser findByNeedIdAndUserId(Long needId, Long userId) throws DefaultException {
		Need need = needService.findById(needId).orElseThrow(ExceptionEnum.EX_INVALID_ID::createDefaultException);
		User user = userService.findById(userId).orElseThrow(ExceptionEnum.EX_INVALID_USER_ID::createDefaultException);
		return getRepository().findByNeedAndUser(need, user)
				.orElse(NeedUser.builder().need(need).user(user).state(NeedUserState.NONE).build());
	}

	@Override
	@Transactional
	public List<NeedUserDto> findDtosByNeedId(Long needId) throws DefaultException {
		Need need = needService.findById(needId).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID));

		boolean ownProject = projectService.isOwnProject(need.getProjectHelperType().getProject());
		if (!ownProject && !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_PROJECT)) {
			throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
		}

		List<NeedUser> needUserList = getRepository().findByNeedOrderByUser_LastNameAscUser_FirstNameAsc(need);
		needUserList = needUserList.stream().filter(this::isViewAllowed).collect(Collectors.toList());

		return convertToDtoList(needUserList);
	}

	@Override
	public void deleteByNeedAndUser(Long needId, Long userId) throws DefaultException {
		delete(findByNeedIdAndUserId(needId, userId));

	}

	@Override
	public NeedUser convertToEntity(NeedUserDto dto) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<NeedUserDto, NeedUser>() {
			@Override
			protected void configure() {
				using(c -> ((NeedUserDto) c.getSource()).getNeedId() != null
						? needService.findById(((NeedUserDto) c.getSource()).getNeedId()).orElse(null)
						: null).map(source).setNeed(null);
				using(c -> ((NeedUserDto) c.getSource()).getUserId() != null
						? userService.findById(((NeedUserDto) c.getSource()).getUserId()).orElse(null)
						: null).map(source).setUser(null);
			}
		});
		return modelMapper.map(dto, NeedUser.class);
	}

}
