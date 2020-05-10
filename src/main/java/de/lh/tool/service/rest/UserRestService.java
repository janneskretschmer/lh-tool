package de.lh.tool.service.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
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
import de.lh.tool.domain.dto.UserCreationDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.dto.UserRolesDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.User;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.UserService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.USER_PREFIX)
public class UserRestService {
	@Autowired
	private UserService userService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of all users")
	@Secured(UserRole.RIGHT_USERS_GET)
	public Resources<UserDto> get(
			@RequestParam(required = false, name = UrlMappings.PROJECT_ID_VARIABLE) Long projectId,
			@RequestParam(required = false, name = UrlMappings.ROLE_VARIABLE) String role) throws DefaultException {
		List<UserDto> dtos = userService.findDtosByProjectIdAndRoleIgnoreCase(projectId, role);
		return new Resources<>(dtos, linkTo(methodOn(UserRestService.class).get(projectId, role)).withSelfRel());
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Get a single user by id")
	@Secured(UserRole.RIGHT_USERS_GET_BY_ID)
	public Resource<UserDto> getById(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		UserDto dto = userService.findDtoById(id);

		return new Resource<>(dto, linkTo(methodOn(UserRestService.class).getById(id)).withSelfRel());
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.USER_CURRENT)
	@ApiOperation(value = "Get data of current users")
	public Resource<UserDto> getCurrent() throws DefaultException {
		User user = userService.getCurrentUser();
		if (user != null) {
			return new Resource<>(convertToDto(user));
		}
		throw new DefaultException(ExceptionEnum.EX_USERS_NOT_FOUND);
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@Secured(UserRole.RIGHT_USERS_CREATE)
	public Resource<UserDto> add(@RequestBody UserCreationDto userCreationDto) throws DefaultException {
		return new Resource<>(
				convertToDto(userService.createUser(new ModelMapper().map(userCreationDto, User.class))),
				linkTo(methodOn(UserRestService.class).add(userCreationDto)).withSelfRel(),
				linkTo(methodOn(UserRestService.class).changePassword(null)).withRel(UrlMappings.USER_PASSWORD));
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@Secured(UserRole.RIGHT_USERS_PUT)
	public Resource<UserDto> update(@PathVariable(name = UrlMappings.ID_VARIABLE, required = false) Long id,
			@RequestBody UserDto userDto) throws DefaultException {
		userDto.setId(id);
		return new Resource<>(convertToDto(userService.updateUser(new ModelMapper().map(userDto, User.class))),
				linkTo(methodOn(UserRestService.class).update(id, userDto)).withSelfRel());
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.USER_PASSWORD)
	public Resource<UserDto> changePassword(@RequestBody PasswordChangeDto passwordChangeDto) throws DefaultException {
		return new Resource<>(convertToDto(userService.changePassword(passwordChangeDto.getUserId(),
				passwordChangeDto.getToken(), passwordChangeDto.getOldPassword(), passwordChangeDto.getNewPassword(),
				passwordChangeDto.getConfirmPassword())));
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.USER_ROLES)
	@Secured(UserRole.RIGHT_USERS_CHANGE_ROLES)
	@Transactional
	public Resource<UserDto> changeRoles(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody UserRolesDto userRolesDto) throws DefaultException {
		User user = userService.findById(id)
				.orElseThrow(() -> new DefaultException(ExceptionEnum.EX_WRONG_ID_PROVIDED));
		if (userRolesDto != null) {
			if (userRolesDto.getRoles() != null) {
				if (user.getRoles() == null) {
					user.setRoles(Collections.emptyList());
				} else {
					user.getRoles().clear();
				}
				user.getRoles().addAll(userRolesDto.getRoles().stream().map(s -> new UserRole(null, user, s))
						.collect(Collectors.toList()));
			}
			userService.save(user);
		}
		return new Resource<>(convertToDto(user));
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@Secured(UserRole.RIGHT_USERS_DELETE)
	public ResponseEntity<Void> delete(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {
		userService.deleteUserById(id);
		return ResponseEntity.noContent().build();
	}

	private UserDto convertToDto(User user) {
		if (user == null) {
			return null;
		}
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<User, UserDto>() {
			@Override
			protected void configure() {
				using(ctx -> ctx.getSource() != null).map(user.getPasswordHash()).setActive(null);
			}
		});
		return modelMapper.map(user, UserDto.class);
	}
}
