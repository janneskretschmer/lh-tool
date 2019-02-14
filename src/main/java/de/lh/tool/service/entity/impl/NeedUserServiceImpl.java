package de.lh.tool.service.entity.impl;

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
import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.NeedUserRepository;
import de.lh.tool.service.entity.interfaces.NeedService;
import de.lh.tool.service.entity.interfaces.NeedUserService;
import de.lh.tool.service.entity.interfaces.ProjectService;
import de.lh.tool.service.entity.interfaces.UserRoleService;
import de.lh.tool.service.entity.interfaces.UserService;

@Service
public class NeedUserServiceImpl extends BasicMappableEntityServiceImpl<NeedUserRepository, NeedUser, NeedUserDto, Long>
		implements NeedUserService {

	@Autowired
	NeedService needService;

	@Autowired
	UserService userService;

	@Autowired
	ProjectService projectService;

	@Autowired
	UserRoleService userRoleService;

	// necessary rights:
	// +-apply-+ . +-approve-+
	// v . . . v . v . . . . v
	// NONE . APPLIED . APPROVED
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
			delete(needUser);
			dto.setId(null);
			return dto;
		case APPLIED:
			if (NeedUserState.NONE.equals(needUser.getState())
					&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_APPLY)) {
				throw new DefaultException(ExceptionEnum.EX_NEED_USER_INVALID_STATE);
			} else if (NeedUserState.APPROVED.equals(needUser.getState())
					&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_APPROVE)) {
				throw new DefaultException(ExceptionEnum.EX_NEED_USER_INVALID_STATE);
			}
			break;
		case APPROVED:
			if (!userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_APPROVE)) {
				throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
			}
			break;
		}
		needUser.setState(dto.getState());
		return saveNeedUser(needUser);

	}

	private NeedUserDto saveNeedUser(NeedUser needUser) throws DefaultException {
		if ((!projectService.isOwnProject(needUser.getNeed().getProject())
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
		if (!projectService.isOwnProject(needUser.getNeed().getProject())
				&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_PROJECT)) {
			throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
		}
		return convertToDto(needUser);
	}

	private NeedUser findByNeedIdAndUserId(Long needId, Long userId) throws DefaultException {
		Need need = needService.findById(needId).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID));
		User user = userService.findById(userId)
				.orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_USER_ID));
		NeedUser needUser = getRepository().findByNeedAndUser(need, user)
				.orElse(NeedUser.builder().need(need).user(user).state(NeedUserState.NONE).build());
		return needUser;
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
