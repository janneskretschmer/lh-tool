package de.lh.tool.service.entity.impl;

import java.util.List;
import java.util.Optional;

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
import de.lh.tool.service.entity.interfaces.UserService;
import de.lh.tool.service.entity.interfaces.crud.ProjectUserCrudService;
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;

@Service
public class ProjectUserServiceImpl
		extends BasicEntityCrudServiceImpl<ProjectUserRepository, ProjectUser, ProjectUserDto, Long>
		implements ProjectUserService, ProjectUserCrudService {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private UserService userService;

	@Override
	public Optional<ProjectUser> findByProjectAndUser(Project project, User user) {
		return getRepository().findByProjectAndUser(project, user);
	}

	@Override
	public List<ProjectUser> findByUserId(Long userId) {
		return getRepository().findByUserId(userId);
	}

	@Override
	@Transactional
	public ProjectUserDto createDto(@NonNull Long projectId, @NonNull Long userId) throws DefaultException {
		return createDto(ProjectUserDto.builder().projectId(projectId).userId(userId).build());
	}

	@Override
	protected void checkValidity(@NonNull ProjectUser projectUser) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_PROJECT_ID, projectUser.getProject());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_USER_ID, projectUser.getUser());
	}

	@Override
	@Transactional
	public void deleteByProjectAndUser(@NonNull Long projectId, @NonNull Long userId) throws DefaultException {
		ProjectUserDto dto = ProjectUserDto.builder().projectId(projectId).userId(userId).build();
		ProjectUser projectUser = convertToEntity(dto);
		Optional<Long> optId = getRepository().findByProjectAndUser(projectUser.getProject(), projectUser.getUser())
				.map(ProjectUser::getId);
		if (optId.isPresent()) {
			deleteDtoById(optId.get());
		}
	}

	@Override
	@Transactional
	public List<ProjectUserDto> findDtosByUserId(Long userId) throws DefaultException {
		checkFindRight();
		User user = userService.findByIdOrThrowInvalidIdException(userId);
		userService.checkReadPermission(user);

		List<ProjectUser> projectUsers = getRepository().findByUser(user);
		return convertToDtoList(projectUsers);

	}

	@Override
	public boolean hasReadPermission(@NonNull ProjectUser projectUser) {
		return userService.hasReadPermission(projectUser.getUser());
	}

	@Override
	public boolean hasWritePermission(@NonNull ProjectUser projectUser) {
		return userRoleService.hasCurrentUserRight(UserRole.RIGHT_PROJECTS_USERS_CHANGE_FOREIGN)
				|| (projectService.hasWritePermission(projectUser.getProject())
						&& (userRoleService.hasCurrentUserRightToGrantAllRoles(projectUser.getUser())
								|| userService.hasWritePermission(projectUser.getUser())));
	}

	@Override
	public String getRightPrefix() {
		return UserRole.PROJECTS_USERS_PREFIX;
	}

}
