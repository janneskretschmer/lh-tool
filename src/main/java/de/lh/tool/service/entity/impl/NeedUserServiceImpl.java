package de.lh.tool.service.entity.impl;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.NeedUserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.NeedUser;
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

	@Override
	public NeedUserDto save(Long needId, Long userId, NeedUserDto dto) throws DefaultException {
		if (needId != null) {
			dto.setNeedId(needId);
		}
		if (userId != null) {
			dto.setUserId(userId);
		}

		NeedUser needUser = convertToEntity(dto);

		if (needUser == null
				|| (!projectService.isOwnProject(needUser.getNeed().getProject())
						&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN))
				|| (needUser.getUser() != userService.getCurrentUser()
						&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_USER))) {
			throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
		}
		return convertToDto(save(needUser));
	}

	@Override
	public void deleteByNeedAndUser(Long needId, Long userId) throws DefaultException {
		// TODO Auto-generated method stub

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
