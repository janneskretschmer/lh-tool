package de.lh.tool.service.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

import de.lh.tool.domain.dto.NeedDto;
import de.lh.tool.domain.dto.NeedUserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.NeedService;
import de.lh.tool.service.entity.interfaces.NeedUserService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.NEED_PREFIX)
public class NeedRestService {

	@Autowired
	private NeedService needService;

	@Autowired
	private NeedUserService needUserService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of own needs")
	@Secured(UserRole.RIGHT_NEEDS_GET)
	public Resource<NeedDto> getOwnByProjectHelperTypeIdAndDate(
			@RequestParam(required = true, name = UrlMappings.PROJECT_HELPER_TYPE_ID_VARIABLE) Long projectHelperTypeId,
			@RequestParam(required = true, name = UrlMappings.DATE_VARIABLE) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
			throws DefaultException {

		NeedDto dto = needService.getNeedDtoByProjectHelperTypeIdAndDate(projectHelperTypeId, date);

		return new Resource<>(dto,
				linkTo(methodOn(NeedRestService.class).getOwnByProjectHelperTypeIdAndDate(projectHelperTypeId, date))
						.withSelfRel());
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Get a single need by id")
	@Secured(UserRole.RIGHT_NEEDS_GET_BY_ID)
	public Resource<NeedDto> getById(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		NeedDto dto = needService.getNeedDtoById(id);

		return new Resource<>(dto, linkTo(methodOn(NeedRestService.class).getById(id)).withSelfRel());
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Create a new need")
	@Secured(UserRole.RIGHT_NEEDS_POST)
	public Resource<NeedDto> create(@RequestBody(required = true) NeedDto dto) throws DefaultException {

		NeedDto needDto = needService.createNeedDto(dto);

		return new Resource<>(needDto, linkTo(methodOn(NeedRestService.class).create(needDto)).withSelfRel());
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Update a need")
	@Secured(UserRole.RIGHT_NEEDS_PUT)
	public Resource<NeedDto> update(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) NeedDto dto) throws DefaultException {

		NeedDto needDto = needService.updateNeedDto(dto, id);

		return new Resource<>(needDto, linkTo(methodOn(NeedRestService.class).update(id, dto)).withSelfRel());
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Delete a need")
	@Secured(UserRole.RIGHT_NEEDS_DELETE)
	public ResponseEntity<Void> delete(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		needService.deleteOwn(id);

		return ResponseEntity.noContent().build();
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_USER_ID_EXTENSION)
	@ApiOperation(value = "Create change the state of the relationship between a need and a user")
	@Secured(UserRole.RIGHT_NEEDS_USERS_PUT)
	public Resource<NeedUserDto> changeNeedUserState(
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@PathVariable(name = UrlMappings.USER_ID_VARIABLE, required = true) Long userId,
			@RequestBody(required = true) NeedUserDto dto) throws DefaultException {

		NeedUserDto needUserDto = needUserService.saveOrUpdateDto(id, userId, dto);

		return new Resource<>(needUserDto,
				linkTo(methodOn(NeedRestService.class).changeNeedUserState(id, userId, dto)).withSelfRel(),
				linkTo(methodOn(NeedRestService.class).getNeedUserState(id, userId)).withRel("getState"));
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_USER_ID_EXTENSION)
	@ApiOperation(value = "Get state for a relationship between a need and a user")
	@Secured(UserRole.RIGHT_NEEDS_USERS_GET)
	public Resource<NeedUserDto> getNeedUserState(
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@PathVariable(name = UrlMappings.USER_ID_VARIABLE, required = true) Long userId) throws DefaultException {

		NeedUserDto dto = needUserService.findDtoByNeedIdAndUserId(id, userId);

		return new Resource<>(dto, linkTo(methodOn(NeedRestService.class).getNeedUserState(id, userId)).withSelfRel());
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_USER_EXTENSION)
	@ApiOperation(value = "Get list of all users for need")
	@Secured(UserRole.RIGHT_NEEDS_USERS_GET)
	public Resources<NeedUserDto> getNeedUsers(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		List<NeedUserDto> dtoList = needUserService.findDtosByNeedId(id);

		return new Resources<>(dtoList, linkTo(methodOn(NeedRestService.class).getNeedUsers(id)).withSelfRel());
	}

}
