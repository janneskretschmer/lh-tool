package de.lh.tool.service.entity.impl;

import java.util.Collection;

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
import de.lh.tool.service.entity.interfaces.UserRoleService;
import de.lh.tool.service.entity.interfaces.UserService;

@Service
public class ProjectServiceImpl extends BasicMappableEntityServiceImpl<ProjectRepository, Project, ProjectDto, Long>
		implements ProjectService {
	@Autowired
	private UserService userService;

	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private ProjectUserService projectUserService;

	@Override
	@Transactional
	public Collection<ProjectDto> getProjectDtos() {
		User currentUser = userService.getCurrentUser();
		if (currentUser != null) {
			return convertToDtoList(userRoleService.hasCurrentUserRight(UserRole.RIGHT_PROJECTS_GET_FOREIGN)
					? (Collection<Project>) findAll()
					: currentUser.getProjects());
		}
		return null;
	}

	@Override
	@Transactional
	public ProjectDto getProjectDtoById(Long id) throws DefaultException {
		Project project = findById(id).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID));
		if (isOwnProject(project) || userRoleService.hasCurrentUserRight(UserRole.RIGHT_PROJECTS_GET_FOREIGN)) {
			return convertToDto(project);
		}
		throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
	}

	@Override
	@Transactional
	public ProjectDto saveProjectDto(ProjectDto projectDto) throws DefaultException {
		if (projectDto.getId() != null) {
			throw new DefaultException(ExceptionEnum.EX_ID_PROVIDED);
		}
		Project project = convertToEntity(projectDto);
		project = save(project);
		User user = userService.getCurrentUser();
		projectUserService.save(new ProjectUser(project, user));
		return convertToDto(project);
	}

	@Override
	@Transactional
	public ProjectDto updateProjectDto(Long id, ProjectDto projectDto) throws DefaultException {
		projectDto.setId(id);
		if (projectDto.getId() == null) {
			throw new DefaultException(ExceptionEnum.EX_NO_ID_PROVIDED);
		}
		Project project = convertToEntity(projectDto);
		project = save(project);
		return convertToDto(project);
	}

	@Override
	public boolean isOwnProject(Project project) {
		return project.getUsers() == null || project.getUsers().contains(userService.getCurrentUser());
	}

}