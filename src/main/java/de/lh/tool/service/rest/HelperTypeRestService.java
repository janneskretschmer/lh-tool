package de.lh.tool.service.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

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

import de.lh.tool.domain.dto.HelperTypeDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.HelperTypeService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.HELPER_TYPE_PREFIX)
public class HelperTypeRestService {

	@Autowired
	private HelperTypeService helperTypeService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of helper types by projectId and weekday")
	@Secured(UserRole.RIGHT_HELPER_TYPES_GET)
	public Resources<HelperTypeDto> getByProjectIdAndWeekday(
			@RequestParam(required = false, name = UrlMappings.PROJECT_ID_VARIABLE) Long projectId,
			@RequestParam(required = false, name = UrlMappings.WEEKDAY_VARIABLE) Integer weekday)
			throws DefaultException {

		List<HelperTypeDto> dtoList = helperTypeService.findDtosByProjectIdAndWeekday(projectId, weekday);

		return new Resources<>(dtoList,
				linkTo(methodOn(HelperTypeRestService.class).getByProjectIdAndWeekday(projectId, weekday))
						.withSelfRel());
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Get a single helper type by id")
	@Secured(UserRole.RIGHT_HELPER_TYPES_GET_BY_ID)
	public Resource<HelperTypeDto> getById(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		HelperTypeDto dto = helperTypeService.findDtoById(id);

		return new Resource<>(dto, linkTo(methodOn(HelperTypeRestService.class).getById(id)).withSelfRel());
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Create a new helper type")
	@Secured(UserRole.RIGHT_HELPER_TYPES_POST)
	public Resource<HelperTypeDto> create(@RequestBody(required = true) HelperTypeDto dto) throws DefaultException {

		HelperTypeDto helperTypeDto = helperTypeService.createDto(dto);

		return new Resource<>(helperTypeDto,
				linkTo(methodOn(HelperTypeRestService.class).update(helperTypeDto.getId(), helperTypeDto))
						.withRel(UrlMappings.ID_EXTENSION));
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Update a helper type")
	@Secured(UserRole.RIGHT_HELPER_TYPES_PUT)
	public Resource<HelperTypeDto> update(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) HelperTypeDto dto) throws DefaultException {

		HelperTypeDto helperTypeDto = helperTypeService.updateDto(dto, id);

		return new Resource<>(helperTypeDto,
				linkTo(methodOn(HelperTypeRestService.class).update(id, helperTypeDto)).withSelfRel());
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Delete a helper type")
	@Secured(UserRole.RIGHT_HELPER_TYPES_DELETE)
	public ResponseEntity<Void> delete(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		helperTypeService.deleteHelperTypeById(id);

		return ResponseEntity.noContent().build();
	}
}
