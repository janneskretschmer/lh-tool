package de.lh.tool.service.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;

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
	NeedService needService;

	@Autowired
	NeedUserService needUserService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of own needs")
	@Secured(UserRole.RIGHT_NEEDS_GET)
	public Resources<NeedDto> getOwn() throws DefaultException {

		Collection<NeedDto> dtoList = needService.getNeedDtos();

		return new Resources<>(dtoList, linkTo(methodOn(NeedRestService.class).getOwn()).withSelfRel());
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

		NeedDto needDto = needService.saveNeedDto(dto);

		return new Resource<>(needDto, linkTo(methodOn(NeedRestService.class).create(needDto)).withSelfRel());
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Update a need")
	@Secured(UserRole.RIGHT_NEEDS_PUT)
	public Resource<NeedDto> update(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) NeedDto dto) throws DefaultException {
		return new Resource<>(dto, linkTo(methodOn(NeedRestService.class).create(dto)).withSelfRel());
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
	@ApiOperation(value = "Get state between a relationship between a need and a user")
	@Secured(UserRole.RIGHT_NEEDS_USERS_GET)
	public Resource<NeedUserDto> getNeedUserState(
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@PathVariable(name = UrlMappings.USER_ID_VARIABLE, required = true) Long userId) throws DefaultException {

		NeedUserDto dto = needUserService.findDtoByNeedIdAndUserId(id, userId);

		return new Resource<>(dto, linkTo(methodOn(NeedRestService.class).getNeedUserState(id, userId)).withSelfRel());
	}
}
