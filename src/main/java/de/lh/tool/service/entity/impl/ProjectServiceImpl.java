package de.lh.tool.service.entity.impl;

import java.util.Collection;
import java.util.Collections;

import javax.transaction.Transactional;

import org.apache.commons.lang3.BooleanUtils;
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
		return convertToDtoList(getOwnProjects());
	}

	@Override
	@Transactional
	public Collection<Project> getOwnProjects() {
		User currentUser = userService.getCurrentUser();
		if (currentUser != null) {
			return userRoleService.hasCurrentUserRight(UserRole.RIGHT_PROJECTS_CHANGE_FOREIGN) ? findAll()
					: currentUser.getProjects();
		}
		return Collections.emptyList();
	}

	@Override
	@Transactional
	public ProjectDto getProjectDtoById(Long id) throws DefaultException {
		Project project = findById(id).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID));
		checkIfViewable(project);
		return convertToDto(project);
	}

	@Override
	@Transactional
	public ProjectDto saveProjectDto(ProjectDto projectDto) throws DefaultException {
		if (projectDto.getId() != null) {
			throw ExceptionEnum.EX_ID_PROVIDED.createDefaultException();
		}

		if (getRepository().findByName(projectDto.getName()).isPresent()) {
			throw ExceptionEnum.EX_PROJECT_NAME_ALREADY_EXISTS.createDefaultException();
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
			throw ExceptionEnum.EX_NO_ID_PROVIDED.createDefaultException();
		}

		Project existing = findById(id).orElseThrow(ExceptionEnum.EX_INVALID_PROJECT_ID::createDefaultException);
		checkIfViewable(existing);
		Project project = convertToEntity(projectDto);

		// otherwise a project will loose all users after it gets modified
		if (project.getUsers() == null) {
			project.setUsers(existing.getUsers());
		}

		if (getRepository().findByName(projectDto.getName()).map(Project::getId).map(id::equals)
				.map(BooleanUtils::negate).orElse(false)) {
			throw ExceptionEnum.EX_PROJECT_NAME_ALREADY_EXISTS.createDefaultException();
		}

		project = save(project);
		return convertToDto(project);
	}

	@Override
	@Transactional
	public void deleteOwn(Long id) throws DefaultException {
		Project project = findById(id).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID));
		checkIfViewable(project);
		delete(project);
	}

	@Override
	public boolean isOwnProject(Project project) {
		return project != null && project.getUsers() != null
				&& project.getUsers().contains(userService.getCurrentUser());
	}

	@Override
	@Transactional
	public boolean isOwnProject(Long projectId) throws DefaultException {
		return isOwnProject(
				findById(projectId).orElseThrow(ExceptionEnum.EX_INVALID_PROJECT_ID::createDefaultException));
	}

	@Override
	@Transactional
	public void checkIfViewable(Long projectId) throws DefaultException {
		if (!isOwnProject(projectId) && !userRoleService.hasCurrentUserRight(UserRole.RIGHT_PROJECTS_CHANGE_FOREIGN)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
	}

	@Override
	@Transactional
	public void checkIfViewable(Project project) throws DefaultException {
		if (!isOwnProject(project) && !userRoleService.hasCurrentUserRight(UserRole.RIGHT_PROJECTS_CHANGE_FOREIGN)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
	}

}
