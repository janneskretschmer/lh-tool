package de.lh.tool.service.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.lh.tool.domain.dto.SlotDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.SlotService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.SLOT_PREFIX)
public class SlotRestService {
	@Autowired
	private SlotService slotService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of own slots")
	@Secured(UserRole.RIGHT_SLOTS_GET)
	public Resources<SlotDto> getByFilters(
			@RequestParam(name = UrlMappings.FREE_TEXT_VARIABLE, required = false) String freeText,
			@RequestParam(name = UrlMappings.NAME_VARIABLE, required = false) String name,
			@RequestParam(name = UrlMappings.DESCRIPTION_VARIABLE, required = false) String description,
			@RequestParam(name = UrlMappings.STORE_ID_VARIABLE, required = false) Long storeId)
			throws DefaultException {

		List<SlotDto> dtoList = slotService.getSlotDtosByFilters(freeText, name, description, storeId);

		return new Resources<>(dtoList,
				linkTo(methodOn(SlotRestService.class).getByFilters(freeText, name, description, storeId))
						.withSelfRel());
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Get a single slot by id")
	@Secured(UserRole.RIGHT_SLOTS_GET_BY_ID)
	public Resource<SlotDto> getById(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		SlotDto dto = slotService.getSlotDtoById(id);

		return new Resource<>(dto, linkTo(methodOn(SlotRestService.class).getById(id)).withSelfRel());
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Create a new slot")
	@Secured(UserRole.RIGHT_SLOTS_POST)
	public Resource<SlotDto> create(@RequestBody(required = true) SlotDto dto) throws DefaultException {

		SlotDto slotDto = slotService.createSlotDto(dto);

		return new Resource<>(slotDto, linkTo(methodOn(SlotRestService.class).create(slotDto)).withSelfRel());
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Update a slot")
	@Secured(UserRole.RIGHT_SLOTS_PUT)
	public Resource<SlotDto> update(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) SlotDto dto) throws DefaultException {

		SlotDto slotDto = slotService.updateSlotDto(dto, id);

		return new Resource<>(slotDto, linkTo(methodOn(SlotRestService.class).update(id, dto)).withSelfRel());
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Delete a slot")
	@Secured(UserRole.RIGHT_SLOTS_DELETE)
	public ResponseEntity<Void> delete(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		slotService.deleteSlotById(id);

		return ResponseEntity.noContent().build();
	}
}
