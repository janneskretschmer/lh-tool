package de.lh.tool.service.entity.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.ProjectUserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Project;
import de.lh.tool.domain.model.ProjectUser;
import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.ProjectUserRepository;
import de.lh.tool.service.entity.interfaces.ProjectService;
import de.lh.tool.service.entity.interfaces.ProjectUserService;
import de.lh.tool.service.entity.interfaces.UserRoleService;
import de.lh.tool.service.entity.interfaces.UserService;

@Service
public class ProjectUserServiceImpl
		extends BasicMappableEntityServiceImpl<ProjectUserRepository, ProjectUser, ProjectUserDto, Long>
		implements ProjectUserService {

	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private UserService userService;

	@Override
	@Transactional
	public ProjectUserDto save(Long projectId, Long userId) throws DefaultException {
		Project project = projectService.findById(projectId)
				.orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID));
		User user = userService.findById(userId)
				.orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_USER_ID));
		if (!projectService.isOwnProject(project)
				&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_PROJECTS_USERS_CHANGE_FOREIGN)) {
			throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
		}
		return convertToDto(save(new ProjectUser(project, user)));
	}

	@Override
	@Transactional
	public void deleteByProjectAndUser(Long projectId, Long userId) throws DefaultException {
		Project project = projectService.findById(projectId)
				.orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID));
		User user = userService.findById(userId)
				.orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_USER_ID));
		if (!projectService.isOwnProject(project)
				&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_PROJECTS_USERS_CHANGE_FOREIGN)) {
			throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
		}
		getRepository().findByProjectAndUser(project, user).ifPresent(this::delete);
	}

}
