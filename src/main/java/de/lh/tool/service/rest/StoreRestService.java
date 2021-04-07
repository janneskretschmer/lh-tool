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
import org.springframework.web.bind.annotation.RestController;

import de.lh.tool.domain.dto.StoreDto;
import de.lh.tool.domain.dto.StoreProjectDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.crud.StoreCrudService;
import de.lh.tool.service.entity.interfaces.crud.StoreProjectCrudService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.STORE_PREFIX)
public class StoreRestService {
	@Autowired
	private StoreCrudService storeService;
	@Autowired
	private StoreProjectCrudService storeProjectService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of own stores")
	@Secured(UserRole.RIGHT_STORES_GET)
	public List<StoreDto> getOwn() throws DefaultException {

		return storeService.findDtos();
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Get a single store by id")
	@Secured(UserRole.RIGHT_STORES_GET)
	public StoreDto getById(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		return storeService.findDtoById(id);
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Create a new store")
	@Secured(UserRole.RIGHT_STORES_POST)
	public StoreDto create(@RequestBody(required = true) StoreDto dto) throws DefaultException {

		return storeService.createDto(dto);
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Update a store")
	@Secured(UserRole.RIGHT_STORES_PUT)
	public StoreDto update(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) StoreDto dto) throws DefaultException {

		return storeService.updateDto(dto, id);
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Delete a store")
	@Secured(UserRole.RIGHT_STORES_DELETE)
	public ResponseEntity<Void> delete(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		storeService.deleteDtoById(id);

		return ResponseEntity.noContent().build();
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.STORE_PROJECTS)
	@ApiOperation(value = "Get a list of projects for store")
	@Secured(UserRole.RIGHT_STORES_GET)
	public List<StoreProjectDto> getProjects(
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long storeId) throws DefaultException {

		return storeProjectService.findDtosByStoreId(storeId);
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.STORE_PROJECTS)
	@ApiOperation(value = "Set projects for store")
	@Secured(UserRole.RIGHT_STORES_PUT)
	public List<StoreProjectDto> connectToProject(
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long storeId,
			@RequestBody(required = true) List<StoreProjectDto> dtos) throws DefaultException {

		return storeProjectService.bulkDeleteAndCreateByStoreId(storeId, dtos);
	}
}
