package de.lh.tool.service.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Resource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.lh.tool.domain.dto.CongregationDto;
import de.lh.tool.domain.dto.CongregationUserTypeDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.UserRole;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.CONGREGATION_PREFIX)
public class CongregationRestService {

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Create a new congregation")
	@Secured(UserRole.RIGHT_CONGREGATIONS_POST)
	public Resource<CongregationDto> create(@RequestBody(required = true) CongregationDto dto) throws DefaultException {
		dto.setId(1l);
		return new Resource<>(dto, linkTo(methodOn(CongregationRestService.class).create(dto)).withSelfRel());
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_USER_ID_EXTENSION)
	@ApiOperation(value = "Create a new relationship between congregation and user")
	@Secured(UserRole.RIGHT_CONGREGATIONS_USERS_PUT)
	public Resource<CongregationUserTypeDto> addUser(
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@PathVariable(name = UrlMappings.USER_ID_VARIABLE, required = true) Long userId,
			@RequestBody(required = true) CongregationUserTypeDto dto) throws DefaultException {
		return new Resource<>(dto,
				linkTo(methodOn(CongregationRestService.class).addUser(id, userId, dto)).withSelfRel());
	}
}
