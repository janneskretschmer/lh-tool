package de.lh.tool.service.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.lh.tool.domain.dto.StoreDto;
import de.lh.tool.domain.dto.StoreProjectDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.StoreProjectService;
import de.lh.tool.service.entity.interfaces.StoreService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.STORE_PREFIX)
public class StoreRestService {
	@Autowired
	private StoreService storeService;
	@Autowired
	private StoreProjectService storeProjectService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of own stores")
	@Secured(UserRole.RIGHT_STORES_GET)
	public Resources<StoreDto> getOwn() throws DefaultException {

		List<StoreDto> dtoList = storeService.getStoreDtos();

		return new Resources<>(dtoList, linkTo(methodOn(StoreRestService.class).getOwn()).withSelfRel());
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Get a single store by id")
	@Secured(UserRole.RIGHT_STORES_GET_BY_ID)
	public Resource<StoreDto> getById(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		StoreDto dto = storeService.getStoreDtoById(id);

		return new Resource<>(dto, linkTo(methodOn(StoreRestService.class).getById(id)).withSelfRel());
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Create a new store")
	@Secured(UserRole.RIGHT_STORES_POST)
	public Resource<StoreDto> create(@RequestBody(required = true) StoreDto dto) throws DefaultException {

		StoreDto storeDto = storeService.createStoreDto(dto);

		return new Resource<>(storeDto, linkTo(methodOn(StoreRestService.class).create(storeDto)).withSelfRel());
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Update a store")
	@Secured(UserRole.RIGHT_STORES_PUT)
	public Resource<StoreDto> update(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) StoreDto dto) throws DefaultException {

		StoreDto storeDto = storeService.updateStoreDto(dto, id);

		return new Resource<>(storeDto, linkTo(methodOn(StoreRestService.class).update(id, dto)).withSelfRel());
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.STORE_PROJECTS)
	@ApiOperation(value = "Get a list of projects for store")
	@Secured(UserRole.RIGHT_STORES_GET)
	public Resources<StoreProjectDto> getProjects(
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long storeId) throws DefaultException {

		Collection<StoreProjectDto> dtoList = storeProjectService.findDtosByStoreId(storeId);

		return new Resources<>(dtoList, linkTo(methodOn(StoreRestService.class).getProjects(storeId)).withSelfRel());
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.STORE_PROJECTS)
	@ApiOperation(value = "Set projects for store")
	@Secured(UserRole.RIGHT_STORES_PUT)
	public Resources<StoreProjectDto> connectToProject(
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long storeId,
			@RequestBody(required = true) List<StoreProjectDto> dtos) throws DefaultException {

		Collection<StoreProjectDto> storeProjectDtos = storeProjectService.bulkDeleteAndCreateByStoreId(storeId, dtos);

		return new Resources<>(storeProjectDtos,
				linkTo(methodOn(StoreRestService.class).connectToProject(storeId, dtos)).withSelfRel());
	}
}
