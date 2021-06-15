package de.lh.tool.service.rest;

import java.util.List;

import javax.transaction.Transactional;

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

import de.lh.tool.domain.dto.ProjectDto;
import de.lh.tool.domain.dto.ProjectHelperTypeDto;
import de.lh.tool.domain.dto.ProjectUserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.crud.ProjectCrudService;
import de.lh.tool.service.entity.interfaces.crud.ProjectHelperTypeCrudService;
import de.lh.tool.service.entity.interfaces.crud.ProjectUserCrudService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.PROJECT_PREFIX)
public class ProjectRestService {

	@Autowired
	private ProjectCrudService projectService;
	@Autowired
	private ProjectUserCrudService projectUserService;
	@Autowired
	private ProjectHelperTypeCrudService projectHelperTypeService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of own projects")
	@Secured(UserRole.RIGHT_PROJECTS_GET)
	public List<ProjectDto> getOwn() throws DefaultException {

		return projectService.findDtos();
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Get a single project by id")
	@Secured(UserRole.RIGHT_PROJECTS_GET)
	public ProjectDto getById(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		return projectService.findDtoById(id);
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Create a new project")
	@Secured(UserRole.RIGHT_PROJECTS_POST)
	public ProjectDto create(@RequestBody(required = true) ProjectDto dto) throws DefaultException {

		return projectService.createDto(dto);
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Update a project")
	@Secured(UserRole.RIGHT_PROJECTS_PUT)
	public ProjectDto update(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) ProjectDto dto) throws DefaultException {

		return projectService.updateDto(dto, id);
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Delete a project")
	@Secured(UserRole.RIGHT_PROJECTS_DELETE)
	public ResponseEntity<Void> delete(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		projectService.deleteDtoById(id);

		return ResponseEntity.noContent().build();
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_USER_ID_EXTENSION)
	@ApiOperation(value = "Create a new relationship between project and user")
	@Secured(UserRole.RIGHT_PROJECTS_USERS_POST)
	@Transactional
	public ProjectUserDto addUser(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@PathVariable(name = UrlMappings.USER_ID_VARIABLE, required = true) Long userId) throws DefaultException {

		return projectUserService.createDto(id, userId);
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
	@Secured(UserRole.RIGHT_PROJECTS_HELPER_TYPES_GET)
	public List<ProjectHelperTypeDto> getProjectHelperTypes(
			@PathVariable(name = UrlMappings.PROJECT_ID_VARIABLE, required = true) Long projectId,
			@RequestParam(required = false, name = UrlMappings.HELPER_TYPE_ID_VARIABLE) Long helperTypeId,
			@RequestParam(required = false, name = UrlMappings.WEEKDAY_VARIABLE) Integer weekday)
			throws DefaultException {

		return projectHelperTypeService.findDtosByProjectIdAndHelperTypeIdAndWeekday(projectId, helperTypeId, weekday);
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.PROJECT_HELPER_TYPES)
	@ApiOperation(value = "Create a new relationship between project and helpertype")
	@Secured(UserRole.RIGHT_PROJECTS_HELPER_TYPES_POST)
	public ProjectHelperTypeDto createPojectHelperType(
			@PathVariable(name = UrlMappings.PROJECT_ID_VARIABLE, required = true) Long id,
			@RequestBody ProjectHelperTypeDto dto) throws DefaultException {

		return projectHelperTypeService.createDto(dto);
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.PROJECT_HELPER_TYPES_ID)
	@ApiOperation(value = "Update a new relationship between project and helpertype")
	@Secured(UserRole.RIGHT_PROJECTS_HELPER_TYPES_PUT)
	public ProjectHelperTypeDto updatePojectHelperType(
			@PathVariable(name = UrlMappings.PROJECT_ID_VARIABLE, required = true) Long projectId,
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody ProjectHelperTypeDto dto) throws DefaultException {

		dto.setProjectId(projectId);
		return projectHelperTypeService.updateDto(dto, id);
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.PROJECT_HELPER_TYPES_ID)
	@ApiOperation(value = "Delete relationship between project and helpertype")
	@Secured(UserRole.RIGHT_PROJECTS_HELPER_TYPES_DELETE)
	public ResponseEntity<Void> deletePojectHelperType(
			@PathVariable(name = UrlMappings.PROJECT_ID_VARIABLE, required = true) Long projectId,
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id) throws DefaultException {

		projectHelperTypeService.deleteDtoById(id);

		return ResponseEntity.noContent().build();
	}

}
