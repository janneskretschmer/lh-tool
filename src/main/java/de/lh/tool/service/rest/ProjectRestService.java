package de.lh.tool.service.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.lh.tool.domain.dto.ProjectDto;
import de.lh.tool.domain.dto.ProjectUserTypeDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.UserRole;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.PROJECT_PREFIX)
public class ProjectRestService {

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of own projects")
	@Secured(UserRole.RIGHT_PROJECTS_GET)
	public Resources<ProjectDto> getOwn() throws DefaultException {
		ProjectDto dto1 = new ProjectDto();
		dto1.setId(1l);
		dto1.setName("Altötting");
		dto1.setStartDate(1533081600l);
		dto1.setEndDate(1546732800l);
		ProjectDto dto2 = new ProjectDto();
		dto2.setId(2l);
		dto2.setName("Stuttgart");
		dto2.setStartDate(1556668800l);
		dto2.setEndDate(1588291200l);

		return new Resources<>(List.of(dto1, dto2), linkTo(methodOn(ProjectRestService.class).getOwn()).withSelfRel());
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Get a single project by id")
	@Secured(UserRole.RIGHT_PROJECTS_GET_BY_ID)
	public Resource<ProjectDto> getById(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		if (id == 1l) {
			ProjectDto dto1 = new ProjectDto();
			dto1.setId(1l);
			dto1.setName("Altötting");
			dto1.setStartDate(1533081600l);
			dto1.setEndDate(1546732800l);
			return new Resource<>(dto1, linkTo(methodOn(ProjectRestService.class).getById(id)).withSelfRel());
		}
		if (id == 2l) {
			ProjectDto dto2 = new ProjectDto();
			dto2.setId(2l);
			dto2.setName("Stuttgart");
			dto2.setStartDate(1556668800l);
			dto2.setEndDate(1588291200l);
			return new Resource<>(dto2, linkTo(methodOn(ProjectRestService.class).getById(id)).withSelfRel());
		}
		throw new DefaultException(ExceptionEnum.EX_PROJECT_NOT_FOUND);
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Create a new project")
	@Secured(UserRole.RIGHT_PROJECTS_POST)
	public Resource<ProjectDto> create(@RequestBody(required = true) ProjectDto dto) throws DefaultException {
		dto.setId(3l);
		return new Resource<>(dto, linkTo(methodOn(ProjectRestService.class).create(dto)).withSelfRel());
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Update a project")
	@Secured(UserRole.RIGHT_PROJECTS_PUT)
	public Resource<ProjectDto> update(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) ProjectDto dto) throws DefaultException {
		return new Resource<>(dto, linkTo(methodOn(ProjectRestService.class).create(dto)).withSelfRel());
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_USER_ID_EXTENSION)
	@ApiOperation(value = "Create a new relationship between project and user")
	@Secured(UserRole.RIGHT_PROJECTS_USERS_PUT)
	public Resource<ProjectUserTypeDto> addUser(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@PathVariable(name = UrlMappings.USER_ID_VARIABLE, required = true) Long userId,
			@RequestBody(required = true) ProjectUserTypeDto dto) throws DefaultException {
		return new Resource<>(dto, linkTo(methodOn(ProjectRestService.class).addUser(id, userId, dto)).withSelfRel());
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_USER_ID_EXTENSION)
	@ApiOperation(value = "Remove a new relationship between project and user")
	@Secured(UserRole.RIGHT_PROJECTS_USERS_DELETE)
	public Resource<Boolean> removeUser(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@PathVariable(name = UrlMappings.USER_ID_VARIABLE, required = true) Long userId) throws DefaultException {
		return new Resource<>(true, linkTo(methodOn(ProjectRestService.class).removeUser(id, userId)).withSelfRel());
	}
}
