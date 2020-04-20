package de.lh.tool.service.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

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

import de.lh.tool.domain.dto.ProjectDto;
import de.lh.tool.domain.dto.ProjectHelperTypeDto;
import de.lh.tool.domain.dto.ProjectUserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.ProjectHelperTypeService;
import de.lh.tool.service.entity.interfaces.ProjectService;
import de.lh.tool.service.entity.interfaces.ProjectUserService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.PROJECT_PREFIX)
public class ProjectRestService {

	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProjectUserService projectUserService;
	@Autowired
	private ProjectHelperTypeService projectHelperTypeService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of own projects")
	@Secured(UserRole.RIGHT_PROJECTS_GET)
	public Resources<ProjectDto> getOwn() throws DefaultException {

		Collection<ProjectDto> dtoList = projectService.getProjectDtos();

		return new Resources<>(dtoList, linkTo(methodOn(ProjectRestService.class).getOwn()).withSelfRel());
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Get a single project by id")
	@Secured(UserRole.RIGHT_PROJECTS_GET_BY_ID)
	public Resource<ProjectDto> getById(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		ProjectDto dto = projectService.getProjectDtoById(id);

		return new Resource<>(dto, linkTo(methodOn(ProjectRestService.class).getById(id)).withSelfRel());
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Create a new project")
	@Secured(UserRole.RIGHT_PROJECTS_POST)
	public Resource<ProjectDto> create(@RequestBody(required = true) ProjectDto dto) throws DefaultException {

		ProjectDto projectDto = projectService.saveProjectDto(dto);

		return new Resource<>(projectDto,
				linkTo(methodOn(ProjectRestService.class).update(projectDto.getId(), projectDto))
						.withRel(UrlMappings.ID_EXTENSION));
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Update a project")
	@Secured(UserRole.RIGHT_PROJECTS_PUT)
	public Resource<ProjectDto> update(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) ProjectDto dto) throws DefaultException {

		ProjectDto projectDto = projectService.updateProjectDto(id, dto);

		return new Resource<>(projectDto,
				linkTo(methodOn(ProjectRestService.class).update(id, projectDto)).withSelfRel());
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Delete a project")
	@Secured(UserRole.RIGHT_PROJECTS_DELETE)
	public ResponseEntity<Void> delete(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		projectService.deleteOwn(id);

		return ResponseEntity.noContent().build();
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_USER_ID_EXTENSION)
	@ApiOperation(value = "Create a new relationship between project and user")
	@Secured(UserRole.RIGHT_PROJECTS_USERS_POST)
	@Transactional
	public Resource<ProjectUserDto> addUser(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@PathVariable(name = UrlMappings.USER_ID_VARIABLE, required = true) Long userId) throws DefaultException {

		ProjectUserDto dto = projectUserService.save(id, userId);

		return new Resource<>(dto, linkTo(methodOn(ProjectRestService.class).addUser(id, userId)).withSelfRel());
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_USER_ID_EXTENSION)
	@ApiOperation(value = "Remove a new relationship between project and user")
	@Secured(UserRole.RIGHT_PROJECTS_USERS_DELETE)
	@Transactional
	public ResponseEntity<Void> removeUser(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@PathVariable(name = UrlMappings.USER_ID_VARIABLE, required = true) Long userId) throws DefaultException {

		projectUserService.deleteByProjectAndUser(id, userId);

		return ResponseEntity.noContent().build();
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.PROJECT_HELPER_TYPES)
	@ApiOperation(value = "Get a list of relationships between project and helpertypes")
	@Secured(UserRole.RIGHT_PROJECTS_GET)
	public Resources<ProjectHelperTypeDto> getProjectHelperTypes(
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long projectId,
			@PathVariable(name = UrlMappings.HELPER_TYPE_ID_VARIABLE) Long helperTypeId,
			@RequestParam(required = true, name = UrlMappings.WEEKDAY_VARIABLE) Integer weekday)
			throws DefaultException {

		List<ProjectHelperTypeDto> dtoList = projectHelperTypeService
				.findDtosByProjectIdAndHelperTypeIdAndWeekday(projectId, helperTypeId, weekday);

		return new Resources<>(dtoList,
				linkTo(methodOn(ProjectRestService.class).getProjectHelperTypes(projectId, helperTypeId, weekday))
						.withSelfRel());
	}
}
