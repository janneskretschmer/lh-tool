package de.lh.tool.service.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Resource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.lh.tool.domain.dto.NeedDto;
import de.lh.tool.domain.dto.NeedUserState;
import de.lh.tool.domain.dto.NeedUserStateDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.HelperType;
import de.lh.tool.domain.model.UserRole;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.NEED_PREFIX)
public class NeedRestService {

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Get a single need by id")
	@Secured(UserRole.RIGHT_NEEDS_GET_BY_ID)
	public Resource<NeedDto> getById(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		if (id == 1l) {
			NeedDto dto1 = new NeedDto();
			dto1.setId(1l);
			dto1.setProjectId(1l);
			dto1.setDate(1533081600l);
			dto1.setQuantity(3);
			dto1.setHelperType(HelperType.CONSTRUCTION_WORKER);
			return new Resource<>(dto1, linkTo(methodOn(NeedRestService.class).getById(id)).withSelfRel());
		}
		if (id == 2l) {
			NeedDto dto2 = new NeedDto();
			dto2.setId(2l);
			dto2.setProjectId(2l);
			dto2.setDate(1556668800l);
			dto2.setQuantity(7);
			dto2.setHelperType(HelperType.KITCHEN_HELPER);
			return new Resource<>(dto2, linkTo(methodOn(NeedRestService.class).getById(id)).withSelfRel());
		}
		throw new DefaultException(ExceptionEnum.EX_NEED_NOT_FOUND);
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Create a new need")
	@Secured(UserRole.RIGHT_NEEDS_POST)
	public Resource<NeedDto> create(@RequestBody(required = true) NeedDto dto) throws DefaultException {
		dto.setId(3l);
		return new Resource<>(dto, linkTo(methodOn(NeedRestService.class).create(dto)).withSelfRel());
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Update a need")
	@Secured(UserRole.RIGHT_NEEDS_PUT)
	public Resource<NeedDto> update(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) NeedDto dto) throws DefaultException {
		return new Resource<>(dto, linkTo(methodOn(NeedRestService.class).create(dto)).withSelfRel());
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_USER_ID_EXTENSION)
	@ApiOperation(value = "Create change the state of the relationship between a need and a user")
	@Secured(UserRole.RIGHT_NEEDS_USERS_PUT)
	public Resource<NeedUserStateDto> changeNeedUserState(
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@PathVariable(name = UrlMappings.USER_ID_VARIABLE, required = true) Long userId,
			@RequestBody(required = true) NeedUserStateDto dto) throws DefaultException {
		return new Resource<>(dto,
				linkTo(methodOn(NeedRestService.class).changeNeedUserState(id, userId, dto)).withSelfRel(),
				linkTo(methodOn(NeedRestService.class).getNeedUserState(id, userId)).withRel("getState"));
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_USER_ID_EXTENSION)
	@ApiOperation(value = "Get state between a relationship between a need and a user")
	@Secured(UserRole.RIGHT_NEEDS_USERS_PUT)
	public Resource<NeedUserStateDto> getNeedUserState(
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@PathVariable(name = UrlMappings.USER_ID_VARIABLE, required = true) Long userId) throws DefaultException {
		NeedUserStateDto dto = new NeedUserStateDto();
		if (id == 1l) {
			if (userId == 1l) {
				dto.setState(NeedUserState.APPROVED);
			} else {
				dto.setState(NeedUserState.APPLIED);
			}
		} else {
			dto.setState(NeedUserState.DECLINED);
		}
		return new Resource<>(dto, linkTo(methodOn(NeedRestService.class).getNeedUserState(id, userId)).withSelfRel());
	}
}
