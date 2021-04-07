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

import de.lh.tool.domain.dto.SlotDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.crud.SlotCrudService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.SLOT_PREFIX)
public class SlotRestService {
	@Autowired
	private SlotCrudService slotService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of own slots")
	@Secured(UserRole.RIGHT_SLOTS_GET)
	public List<SlotDto> getByFilters(
			@RequestParam(name = UrlMappings.FREE_TEXT_VARIABLE, required = false) String freeText,
			@RequestParam(name = UrlMappings.NAME_VARIABLE, required = false) String name,
			@RequestParam(name = UrlMappings.DESCRIPTION_VARIABLE, required = false) String description,
			@RequestParam(name = UrlMappings.STORE_ID_VARIABLE, required = false) Long storeId)
			throws DefaultException {

		return slotService.findDtosByFilters(freeText, name, description, storeId);
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Get a single slot by id")
	@Secured(UserRole.RIGHT_SLOTS_GET)
	public SlotDto getById(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		return slotService.findDtoById(id);
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Create a new slot")
	@Secured(UserRole.RIGHT_SLOTS_POST)
	public SlotDto create(@RequestBody(required = true) SlotDto dto) throws DefaultException {

		return slotService.createDto(dto);
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Update a slot")
	@Secured(UserRole.RIGHT_SLOTS_PUT)
	public SlotDto update(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) SlotDto dto) throws DefaultException {

		return slotService.updateDto(dto, id);
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Delete a slot")
	@Secured(UserRole.RIGHT_SLOTS_DELETE)
	public ResponseEntity<Void> delete(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		slotService.deleteDtoById(id);

		return ResponseEntity.noContent().build();
	}
}
