package de.lh.tool.service.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
	private RoleService roleService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of all roles that the user can grant")
	@Secured(UserRole.RIGHT_USERS_ROLES_GET)
	public List<RoleDto> get() throws DefaultException {
		return roleService.getGrantableRoleDtos();
	}

}
