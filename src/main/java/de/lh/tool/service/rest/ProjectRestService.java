package de.lh.tool.service.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
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
import org.springframework.web.bind.annotation.RestController;

import de.lh.tool.domain.dto.ProjectDto;
import de.lh.tool.domain.dto.ProjectUserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Project;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.ProjectService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.PROJECT_PREFIX)
public class ProjectRestService {

	@Autowired
	private ProjectService projectService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of own projects")
	@Secured(UserRole.RIGHT_PROJECTS_GET)
	public Resources<ProjectDto> getOwn() throws DefaultException {
		Collection<Project> projects = projectService.getOwnProjects();

		return new Resources<>(projects.stream().map(this::convertToDto).collect(Collectors.toList()),
				linkTo(methodOn(ProjectRestService.class).getOwn()).withSelfRel());
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
			dto1.setStartDate(new Date(1533081600l));
			dto1.setEndDate(new Date(1546732800l));
			return new Resource<>(dto1, linkTo(methodOn(ProjectRestService.class).getById(id)).withSelfRel());
		}
		if (id == 2l) {
			ProjectDto dto2 = new ProjectDto();
			dto2.setId(2l);
			dto2.setName("Stuttgart");
			dto2.setStartDate(new Date(1556668800l));
			dto2.setEndDate(new Date(1588291200l));
			return new Resource<>(dto2, linkTo(methodOn(ProjectRestService.class).getById(id)).withSelfRel());
		}
		throw new DefaultException(ExceptionEnum.EX_PROJECT_NOT_FOUND);
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Create a new project")
	@Secured(UserRole.RIGHT_PROJECTS_POST)
	public Resource<ProjectDto> create(@RequestBody(required = true) ProjectDto dto) throws DefaultException {
		if (dto.getId() != null) {
			throw new DefaultException(ExceptionEnum.EX_ID_PROVIDED);
		}
		Project project = convertToEntity(dto);
		project = projectService.save(project);
		ProjectDto projectDto = convertToDto(project);
		return new Resource<>(projectDto,
				linkTo(methodOn(ProjectRestService.class).update(projectDto.getId(), projectDto))
						.withRel(UrlMappings.ID_EXTENSION));
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Update a project")
	@Secured(UserRole.RIGHT_PROJECTS_PUT)
	public Resource<ProjectDto> update(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) ProjectDto dto) throws DefaultException {
		dto.setId(id);
		if (dto.getId() == null) {
			throw new DefaultException(ExceptionEnum.EX_NO_ID_PROVIDED);
		}
		Project project = convertToEntity(dto);
		project = projectService.save(project);
		ProjectDto projectDto = convertToDto(project);
		return new Resource<>(projectDto,
				linkTo(methodOn(ProjectRestService.class).update(id, projectDto)).withSelfRel());
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Delete a project")
	@Secured(UserRole.RIGHT_PROJECTS_DELETE)
	public ResponseEntity<?> delete(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {
		projectService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_USER_ID_EXTENSION)
	@ApiOperation(value = "Create a new relationship between project and user")
	@Secured(UserRole.RIGHT_PROJECTS_USERS_PUT)
	public Resource<ProjectUserDto> addUser(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@PathVariable(name = UrlMappings.USER_ID_VARIABLE, required = true) Long userId,
			@RequestBody(required = true) ProjectUserDto dto) throws DefaultException {
		return new Resource<>(dto, linkTo(methodOn(ProjectRestService.class).addUser(id, userId, dto)).withSelfRel());
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_USER_ID_EXTENSION)
	@ApiOperation(value = "Remove a new relationship between project and user")
	@Secured(UserRole.RIGHT_PROJECTS_USERS_DELETE)
	public Resource<Boolean> removeUser(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@PathVariable(name = UrlMappings.USER_ID_VARIABLE, required = true) Long userId) throws DefaultException {
		return new Resource<>(true, linkTo(methodOn(ProjectRestService.class).removeUser(id, userId)).withSelfRel());
	}

	private Project convertToEntity(ProjectDto projectDto) {
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper.map(projectDto, Project.class);
	}

	private ProjectDto convertToDto(Project project) {
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper.map(project, ProjectDto.class);
	}
}
