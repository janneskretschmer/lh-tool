package de.lh.tool.service.entity.impl;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
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
	// +-apply-+ . +-approve-+-approve-+
	// v . . . v . v . . . . v . . . . v
	// NONE . APPLIED . APPROVED . REJECTED
	// ^ | . . . . . . . . ^ |
	// | +------approve----+ |
	// +--------apply--------+
	// setting to NONE = delete
	@Override
	@Transactional
	public NeedUserDto saveOrUpdateDto(Long needId, Long userId, NeedUserDto dto) throws DefaultException {
		NeedUser needUser = findByNeedIdAndUserId(needId, userId);
		switch (dto.getState()) {
		case NONE:
			if (!userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_APPLY)) {
				throw new DefaultException(ExceptionEnum.EX_NEED_USER_INVALID_STATE);
			}
			if (NeedUserState.APPROVED.equals(needUser.getState())) {
				needUser.setState(NeedUserState.NONE);
				userService
						.findByProjectIdAndRoleIgnoreCase(getProjectWithIdByNeedUser(needUser).getId(),
								UserRole.ROLE_LOCAL_COORDINATOR)
						.stream().forEach(u -> mailService.sendNeedUserStateChangedMailToCoordinator(needUser, u));
			}
			delete(needUser);
			dto.setId(null);
			return dto;
		case APPLIED:
			if ((NeedUserState.NONE.equals(needUser.getState())
					&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_APPLY))
					|| (NeedUserState.APPROVED.equals(needUser.getState())
							&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_APPROVE))) {
				throw new DefaultException(ExceptionEnum.EX_NEED_USER_INVALID_STATE);
			}
			if (NeedUserState.APPROVED.equals(needUser.getState())) {
				needUser.setState(NeedUserState.APPLIED);
				mailService.sendNeedUserStateChangedMailToUser(needUser);
			}
			break;
		case APPROVED:
			if (!userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_APPROVE)) {
				throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
			}
			if (NeedUserState.APPLIED.equals(needUser.getState())
					|| NeedUserState.REJECTED.equals(needUser.getState())) {
				needUser.setState(NeedUserState.APPROVED);
				mailService.sendNeedUserStateChangedMailToUser(needUser);
			}
			break;
		case REJECTED:
			if (!userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_APPROVE)) {
				throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
			}
			if (NeedUserState.APPLIED.equals(needUser.getState())
					|| NeedUserState.APPROVED.equals(needUser.getState())) {
				needUser.setState(NeedUserState.REJECTED);
				mailService.sendNeedUserStateChangedMailToUser(needUser);
			}
			break;
		}
		// new state is already set above in some cases
		needUser.setState(dto.getState());
		return saveNeedUser(needUser);

	}

	private @NonNull Project getProjectWithIdByNeedUser(NeedUser needUser) throws DefaultException {
		Optional<@NonNull Project> project = Optional.ofNullable(needUser).map(NeedUser::getNeed)
				.map(Need::getProjectHelperType).map(ProjectHelperType::getProject).filter(p -> p.getId() != null);
		return project.orElseThrow(() -> new DefaultException(ExceptionEnum.EX_WRONG_ID_PROVIDED));
	}

	private NeedUserDto saveNeedUser(NeedUser needUser) throws DefaultException {
		if ((!projectService.isOwnProject(getProjectWithIdByNeedUser(needUser))
				&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_PROJECT))
				|| (needUser.getUser() != userService.getCurrentUser()
						&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_USER))) {
			throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
		}
		return convertToDto(save(needUser));
	}

	@Override
	@Transactional
	public NeedUserDto findDtoByNeedIdAndUserId(Long needId, Long userId) throws DefaultException {
		NeedUser needUser = findByNeedIdAndUserId(needId, userId);
		if (!projectService.isOwnProject(getProjectWithIdByNeedUser(needUser))
				&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_PROJECT)) {
			throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
		}
		return convertToDto(needUser);
	}

	private NeedUser findByNeedIdAndUserId(Long needId, Long userId) throws DefaultException {
		Need need = needService.findById(needId).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID));
		User user = userService.findById(userId)
				.orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_USER_ID));
		return getRepository().findByNeedAndUser(need, user)
				.orElse(NeedUser.builder().need(need).user(user).state(NeedUserState.NONE).build());
	}

	@Override
	@Transactional
	public List<NeedUserDto> findDtosByNeedId(Long needId) throws DefaultException {
		Need need = needService.findById(needId).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID));
		List<NeedUser> needUserList = newArrayList(
				getRepository().findByNeedOrderByUser_LastNameAscUser_FirstNameAsc(need));
		if (!(needUserList.size() > 0 && projectService.isOwnProject(getProjectWithIdByNeedUser(needUserList.get(0))))
				&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_PROJECT)) {
			throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
		}
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
