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

import de.lh.tool.domain.dto.ItemDto;
import de.lh.tool.domain.dto.ItemHistoryDto;
import de.lh.tool.domain.dto.ItemNoteDto;
import de.lh.tool.domain.dto.TagDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.ItemHistoryService;
import de.lh.tool.service.entity.interfaces.ItemNoteService;
import de.lh.tool.service.entity.interfaces.ItemService;
import de.lh.tool.service.entity.interfaces.TagService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.ITEM_PREFIX)
public class ItemRestService {
	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemNoteService itemNoteService;
	@Autowired
	private ItemHistoryService itemHistoryService;
	@Autowired
	private TagService tagService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of items")
	@Secured(UserRole.RIGHT_ITEMS_GET)
	public Resources<ItemDto> get() throws DefaultException {

		List<ItemDto> dtoList = itemService.getItemDtos();

		return new Resources<>(dtoList, linkTo(methodOn(ItemRestService.class).get()).withSelfRel());
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Get a single item by id")
	@Secured(UserRole.RIGHT_ITEMS_GET_BY_ID)
	public Resource<ItemDto> getById(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		ItemDto dto = itemService.getItemDtoById(id);

		return new Resource<>(dto, linkTo(methodOn(ItemRestService.class).getById(id)).withSelfRel());
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Create a new item")
	@Secured(UserRole.RIGHT_ITEMS_POST)
	public Resource<ItemDto> create(@RequestBody(required = true) ItemDto dto) throws DefaultException {

		ItemDto itemDto = itemService.createItemDto(dto);

		return new Resource<>(itemDto, linkTo(methodOn(ItemRestService.class).create(itemDto)).withSelfRel());
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Update an item")
	@Secured(UserRole.RIGHT_ITEMS_PUT)
	public Resource<ItemDto> update(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) ItemDto dto) throws DefaultException {

		ItemDto itemDto = itemService.updateItemDto(dto, id);

		return new Resource<>(itemDto, linkTo(methodOn(ItemRestService.class).update(id, dto)).withSelfRel());
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_NOTES)
	@ApiOperation(value = "Get a list of notes for item")
	@Secured(UserRole.RIGHT_ITEMS_GET)
	public Resources<ItemNoteDto> getNotes(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long itemId)
			throws DefaultException {

		Collection<ItemNoteDto> dtoList = itemNoteService.getDtosByItemId(itemId);

		return new Resources<>(dtoList, linkTo(methodOn(ItemRestService.class).getNotes(itemId)).withSelfRel());
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_NOTES)
	@ApiOperation(value = "Create a new note for an item")
	@Secured(UserRole.RIGHT_ITEMS_POST)
	public Resource<ItemNoteDto> create(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long itemId,
			@RequestBody(required = true) ItemNoteDto dto) throws DefaultException {

		dto.setItemId(itemId);
		ItemNoteDto itemNoteDto = itemNoteService.createItemNoteDto(dto);

		return new Resource<>(itemNoteDto,
				linkTo(methodOn(ItemRestService.class).create(itemId, itemNoteDto)).withSelfRel());
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_TAGS)
	@ApiOperation(value = "Get a list of tags for item")
	@Secured(UserRole.RIGHT_ITEMS_GET)
	public Resources<TagDto> getTags(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long itemId)
			throws DefaultException {

		Collection<TagDto> dtoList = tagService.getTagDtosByItemId(itemId);

		return new Resources<>(dtoList, linkTo(methodOn(ItemRestService.class).getTags(itemId)).withSelfRel());
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_HISTORY)
	@ApiOperation(value = "Get history for item")
	@Secured(UserRole.RIGHT_ITEMS_GET)
	public Resources<ItemHistoryDto> getHistory(
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long itemId) throws DefaultException {

		Collection<ItemHistoryDto> dtoList = itemHistoryService.getDtosByItemId(itemId);

		return new Resources<>(dtoList, linkTo(methodOn(ItemRestService.class).getHistory(itemId)).withSelfRel());
	}

	/*
	 * TODO Add URLMapping for NoteId
	 * 
	 * @PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path =
	 * UrlMappings.ITEM_NOTES)
	 * 
	 * @ApiOperation(value = "Update a note")
	 * 
	 * @Secured(UserRole.RIGHT_ITEMS_PUT) public Resource<ItemNoteDto>
	 * update(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long
	 * id,
	 * 
	 * @RequestBody(required = true) ItemNoteDto dto) throws DefaultException {
	 * 
	 * ItemNoteDto itemNoteDto = itemNoteService.updateItemDto(dto, id);
	 * 
	 * return new Resource<>(itemNoteDto,
	 * linkTo(methodOn(ItemRestService.class).update(id,
	 * itemNoteDto)).withSelfRel()); }
	 */

}
