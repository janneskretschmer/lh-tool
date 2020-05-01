package de.lh.tool.service.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.Resource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.lh.tool.domain.dto.assembled.AssembledHelperTypeWrapperDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.DtoAssemblyService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.ASSEMBLED_PREFIX)
public class AssemblyRestService {

	@Autowired
	private DtoAssemblyService dtoAssemblyService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ASSEMBLED_NEED_FOR_CALENDAR)
	@ApiOperation(value = "Get data for needs with users of several dates")
	@Secured(UserRole.RIGHT_NEEDS_USERS_GET)
	public Resource<Map<String, AssembledHelperTypeWrapperDto>> getAssembledNeedsForCalendarBetweenDates(
			@RequestParam(required = true, name = UrlMappings.PROJECT_ID_VARIABLE) Long projectId,
			@RequestParam(required = true, name = UrlMappings.START_DATE_VARIABLE) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
			@RequestParam(required = true, name = UrlMappings.END_DATE_VARIABLE) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end)
			throws DefaultException {

		return new Resource<>(dtoAssemblyService.findHelperTypesWithNeedsAndUsersBetweenDates(projectId, start, end),
				linkTo(methodOn(AssemblyRestService.class).getAssembledNeedsForCalendarBetweenDates(projectId, start,
						end)).withSelfRel());
	}

}
