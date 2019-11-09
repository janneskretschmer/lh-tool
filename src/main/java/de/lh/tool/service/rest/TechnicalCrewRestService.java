package de.lh.tool.service.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.lh.tool.domain.dto.TechnicalCrewDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.TechnicalCrewService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.TECHNICAL_CREW_PREFIX)
public class TechnicalCrewRestService {
	@Autowired
	private TechnicalCrewService technicalCrewService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of own technical crews")
	@Secured(UserRole.RIGHT_TECHNICAL_CREWS_GET)
	public Resources<TechnicalCrewDto> getOwn() throws DefaultException {

		List<TechnicalCrewDto> dtoList = technicalCrewService.getTechnicalCrewDtos();

		return new Resources<>(dtoList, linkTo(methodOn(TechnicalCrewRestService.class).getOwn()).withSelfRel());
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Get a single technical crew by id")
	@Secured(UserRole.RIGHT_TECHNICAL_CREWS_GET_BY_ID)
	public Resource<TechnicalCrewDto> getById(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		TechnicalCrewDto dto = technicalCrewService.getTechnicalCrewDtoById(id);

		return new Resource<>(dto, linkTo(methodOn(TechnicalCrewRestService.class).getById(id)).withSelfRel());
	}
}
