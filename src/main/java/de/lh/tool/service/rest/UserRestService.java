package de.lh.tool.service.rest;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.lh.tool.bean.dto.UserDto;
import de.lh.tool.bean.model.User;
import de.lh.tool.service.entity.interfaces.UserService;
import de.lh.tool.service.rest.exception.RestException;
import de.lh.tool.service.rest.exception.RestExceptionEnum;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.USER_PREFIX)
public class UserRestService {
	@Autowired
	private UserService userService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of all users")

	public Resources<UserDto> getAll() throws RestException {
		Iterable<User> users = userService.findAll();
		if (users != null) {
			return new Resources<>(
					StreamSupport.stream(users.spliterator(), true).map(this::convertToDto)
							.collect(Collectors.toList()),
					ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(UserRestService.class).getAll())
							.withSelfRel());
		}
		throw new RestException(RestExceptionEnum.EX_USERS_NOT_FOUND);
	}

	private UserDto convertToDto(User user) {
		return new ModelMapper().map(user, UserDto.class);
	}
}
