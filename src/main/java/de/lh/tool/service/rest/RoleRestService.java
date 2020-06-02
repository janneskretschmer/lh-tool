package de.lh.tool.service.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.lh.tool.domain.dto.RoleDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.RoleService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.ROLES_PREFIX)
public class RoleRestService {

	@Autowired
	RoleService roleService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of all roles that the user can grant")
	@Secured(UserRole.RIGHT_USERS_CHANGE_ROLES)
	public Resources<RoleDto> get() throws DefaultException {
		List<RoleDto> dtos = roleService.getGrantableRoleDtos();
		return new Resources<>(dtos, linkTo(methodOn(RoleRestService.class).get()).withSelfRel());
	}

}
