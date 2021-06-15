package de.lh.tool.service.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.lh.tool.domain.dto.PasswordChangeDto;
import de.lh.tool.domain.dto.ProjectUserDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.dto.UserRoleDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.crud.ProjectUserCrudService;
import de.lh.tool.service.entity.interfaces.crud.UserCrudService;
import de.lh.tool.service.entity.interfaces.crud.UserRoleCrudService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.USER_PREFIX)
public class UserRestService {
	@Autowired
	private UserCrudService userService;

	@Autowired
	private UserRoleCrudService userRoleService;

	@Autowired
	private ProjectUserCrudService projectUserService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of all users")
	@Secured(UserRole.RIGHT_USERS_GET)
	public List<UserDto> get(@RequestParam(required = false, name = UrlMappings.PROJECT_ID_VARIABLE) Long projectId,
			@RequestParam(required = false, name = UrlMappings.ROLE_VARIABLE) String role,
			@RequestParam(required = false, name = UrlMappings.FREE_TEXT_VARIABLE) String freeText)
			throws DefaultException {

		return userService.findDtosByProjectIdAndRoleIgnoreCase(projectId, role, freeText);
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Get a single user by id")
	@Secured(UserRole.RIGHT_USERS_GET)
	public UserDto getById(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		return userService.findDtoById(id);
	}

	// FUTURE convert to dto in service layer
	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.USER_CURRENT)
	@ApiOperation(value = "Get data of current users")
	public UserDto getCurrent() throws DefaultException {

		return userService.findCurrentUserDto();
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@Secured(UserRole.RIGHT_USERS_POST)
	public UserDto add(@RequestBody UserDto userDto) throws DefaultException {

		return userService.createDto(userDto);
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@Secured(UserRole.RIGHT_USERS_PUT)
	public UserDto update(@PathVariable(name = UrlMappings.ID_VARIABLE, required = false) Long id,
			@RequestBody UserDto userDto) throws DefaultException {

		return userService.updateDto(userDto, id);
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.USER_PASSWORD)
	public UserDto changePassword(@RequestBody PasswordChangeDto passwordChangeDto) throws DefaultException {

		return userService.changePassword(passwordChangeDto.getUserId(), passwordChangeDto.getToken(),
				passwordChangeDto.getOldPassword(), passwordChangeDto.getNewPassword(),
				passwordChangeDto.getConfirmPassword());
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@Secured(UserRole.RIGHT_USERS_DELETE)
	public ResponseEntity<Void> delete(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		userService.deleteDtoById(id);

		return ResponseEntity.noContent().build();
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.USER_ROLES)
	@Secured(UserRole.RIGHT_USERS_ROLES_GET)
	public List<UserRoleDto> getUserRoles(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long userId)
			throws DefaultException {

		return userRoleService.findDtosByUserId(userId);
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.USER_ROLES)
	@Secured(UserRole.RIGHT_USERS_ROLES_POST)
	public UserRoleDto createUserRole(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long userId,
			@RequestBody(required = true) UserRoleDto userRoleDto) throws DefaultException {

		return userRoleService.createDto(userRoleDto);
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.USER_ROLES_ID)
	@Secured(UserRole.RIGHT_USERS_ROLES_DELETE)
	public ResponseEntity<Void> deleteUserRole(
			@PathVariable(name = UrlMappings.USER_ID_VARIABLE, required = true) Long userId,
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long userRoleId) throws DefaultException {

		userRoleService.deleteDtoById(userRoleId);

		return ResponseEntity.noContent().build();
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.USER_PROJECTS)
	@Secured(UserRole.RIGHT_PROJECTS_USERS_GET)
	public List<ProjectUserDto> getUserProjects(
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long userId) throws DefaultException {

		return projectUserService.findDtosByUserId(userId);
	}
}
