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

import de.lh.tool.domain.dto.HelperTypeDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.crud.HelperTypeCrudService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.HELPER_TYPE_PREFIX)
public class HelperTypeRestService {

	@Autowired
	private HelperTypeCrudService helperTypeService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of helper types by projectId and weekday")
	@Secured(UserRole.RIGHT_HELPER_TYPES_GET)
	public List<HelperTypeDto> getByProjectIdAndWeekday(
			@RequestParam(required = false, name = UrlMappings.PROJECT_ID_VARIABLE) Long projectId,
			@RequestParam(required = false, name = UrlMappings.WEEKDAY_VARIABLE) Integer weekday)
			throws DefaultException {

		List<HelperTypeDto> dtoList = helperTypeService.findDtosByProjectIdAndWeekday(projectId, weekday);

		return dtoList;
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Get a single helper type by id")
	@Secured(UserRole.RIGHT_HELPER_TYPES_GET)
	public HelperTypeDto getById(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		HelperTypeDto dto = helperTypeService.findDtoById(id);

		return dto;
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Create a new helper type")
	@Secured(UserRole.RIGHT_HELPER_TYPES_POST)
	public HelperTypeDto create(@RequestBody(required = true) HelperTypeDto dto) throws DefaultException {

		HelperTypeDto helperTypeDto = helperTypeService.createDto(dto);

		return helperTypeDto;
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Update a helper type")
	@Secured(UserRole.RIGHT_HELPER_TYPES_PUT)
	public HelperTypeDto update(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) HelperTypeDto dto) throws DefaultException {

		HelperTypeDto helperTypeDto = helperTypeService.updateDto(dto, id);

		return helperTypeDto;
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Delete a helper type")
	@Secured(UserRole.RIGHT_HELPER_TYPES_DELETE)
	public ResponseEntity<Void> delete(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		helperTypeService.deleteDtoById(id);

		return ResponseEntity.noContent().build();
	}
}
