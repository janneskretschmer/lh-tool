package de.lh.tool.service.rest;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

import de.lh.tool.domain.dto.NeedDto;
import de.lh.tool.domain.dto.NeedUserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.crud.NeedCrudService;
import de.lh.tool.service.entity.interfaces.crud.NeedUserCrudService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.NEED_PREFIX)
public class NeedRestService {

	@Autowired
	private NeedCrudService needService;

	@Autowired
	private NeedUserCrudService needUserService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of own needs")
	@Secured(UserRole.RIGHT_NEEDS_GET)
	public NeedDto getOwnByProjectHelperTypeIdAndDate(
			@RequestParam(required = true, name = UrlMappings.PROJECT_HELPER_TYPE_ID_VARIABLE) Long projectHelperTypeId,
			@RequestParam(required = true, name = UrlMappings.DATE_VARIABLE) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
			throws DefaultException {

		return needService.findDtoByProjectHelperTypeIdAndDate(projectHelperTypeId, date);
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Get a single need by id")
	@Secured(UserRole.RIGHT_NEEDS_GET)
	public NeedDto getById(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		return needService.findDtoById(id);
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Create a new need")
	@Secured(UserRole.RIGHT_NEEDS_POST)
	public NeedDto create(@RequestBody(required = true) NeedDto dto) throws DefaultException {

		return needService.createDto(dto);
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Update a need")
	@Secured(UserRole.RIGHT_NEEDS_PUT)
	public NeedDto update(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) NeedDto dto) throws DefaultException {

		return needService.updateDto(dto, id);
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Delete a need")
	@Secured(UserRole.RIGHT_NEEDS_DELETE)
	public ResponseEntity<Void> delete(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		needService.deleteDtoById(id);

		return ResponseEntity.noContent().build();
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_USER_ID_EXTENSION)
	@ApiOperation(value = "Create change the state of the relationship between a need and a user")
	@Secured(UserRole.RIGHT_NEEDS_USERS_PUT)
	public NeedUserDto changeNeedUserState(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@PathVariable(name = UrlMappings.USER_ID_VARIABLE, required = true) Long userId,
			@RequestBody(required = true) NeedUserDto dto) throws DefaultException {

		return needUserService.saveOrUpdateDto(id, userId, dto);
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_USER_ID_EXTENSION)
	@ApiOperation(value = "Get state for a relationship between a need and a user")
	@Secured(UserRole.RIGHT_NEEDS_USERS_GET)
	public NeedUserDto getNeedUserState(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@PathVariable(name = UrlMappings.USER_ID_VARIABLE, required = true) Long userId) throws DefaultException {

		return needUserService.findDtoByNeedIdAndUserId(id, userId);
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_USER_EXTENSION)
	@ApiOperation(value = "Get list of all users for need")
	@Secured(UserRole.RIGHT_NEEDS_USERS_GET)
	public List<NeedUserDto> getNeedUsers(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		return needUserService.findDtosByNeedId(id);
	}

}
