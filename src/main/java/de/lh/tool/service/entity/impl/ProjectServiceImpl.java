package de.lh.tool.service.entity.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.ProjectDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Project;
import de.lh.tool.domain.model.ProjectUser;
import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.ProjectRepository;
import de.lh.tool.service.entity.interfaces.ProjectService;
import de.lh.tool.service.entity.interfaces.ProjectUserService;
import de.lh.tool.service.entity.interfaces.UserService;
import de.lh.tool.service.entity.interfaces.crud.ProjectCrudService;
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;

@Service
public class ProjectServiceImpl extends BasicEntityCrudServiceImpl<ProjectRepository, Project, ProjectDto, Long>
		implements ProjectService, ProjectCrudService {

	@Autowired
	private UserService userService;
	@Autowired
	private ProjectUserService projectUserService;

	@Override
	protected void postCreate(@NonNull Project project) {
		User user = userService.getCurrentUser();
		projectUserService.save(new ProjectUser(project, user));
	}

	@Override
	protected void checkValidity(@NonNull Project project) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_NAME, project.getName());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_START_DATE, project.getStartDate());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_END_DATE, project.getEndDate());
		ValidationUtil.checkSameIdIfExists(ExceptionEnum.EX_PROJECT_NAME_ALREADY_EXISTS,
				getRepository().findByName(project.getName()), project);
	}

	@Override
	@Transactional
	public boolean hasReadPermission(@NonNull Project project) {
		return userRoleService.hasCurrentUserRight(UserRole.RIGHT_PROJECTS_CHANGE_FOREIGN)
				|| projectUserService.findByProjectAndUser(project, userService.getCurrentUser()).isPresent();
	}

	@Override
	@Transactional
	public boolean hasWritePermission(@NonNull Project project) {
		return (project.getId() == null && userRoleService.hasCurrentUserRight(UserRole.RIGHT_PROJECTS_POST))
				|| hasReadPermission(project);
	}

	@Override
	public String getRightPrefix() {
		return UserRole.PROJECTS_PREFIX;
	}

	@Override
	protected ExceptionEnum getInvalidIdException() {
		return ExceptionEnum.EX_INVALID_PROJECT_ID;
	}

}
