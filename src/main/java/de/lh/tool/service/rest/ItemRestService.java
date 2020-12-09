package de.lh.tool.service.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.lh.tool.domain.dto.ItemDto;
import de.lh.tool.domain.dto.ItemHistoryDto;
import de.lh.tool.domain.dto.ItemImageDto;
import de.lh.tool.domain.dto.ItemItemDto;
import de.lh.tool.domain.dto.ItemNoteDto;
import de.lh.tool.domain.dto.ItemTagDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.service.entity.interfaces.crud.ItemCrudService;
import de.lh.tool.service.entity.interfaces.crud.ItemHistoryCrudService;
import de.lh.tool.service.entity.interfaces.crud.ItemImageCrudService;
import de.lh.tool.service.entity.interfaces.crud.ItemItemCrudService;
import de.lh.tool.service.entity.interfaces.crud.ItemNoteCrudService;
import de.lh.tool.service.entity.interfaces.crud.ItemTagCrudService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(UrlMappings.ITEM_PREFIX)
public class ItemRestService {
	@Autowired
	private ItemCrudService itemService;
	@Autowired
	private ItemNoteCrudService itemNoteService;
	@Autowired
	private ItemHistoryCrudService itemHistoryService;
	@Autowired
	private ItemTagCrudService itemTagService;
	@Autowired
	private ItemImageCrudService itemImageService;
	@Autowired
	private ItemItemCrudService itemItemService;

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Get a list of items")
	@Secured(UserRole.RIGHT_ITEMS_GET)
	public Resources<ItemDto> getByFilters(
			@RequestParam(required = false, name = UrlMappings.FREE_TEXT_VARIABLE) String freeText)
			throws DefaultException {

		List<ItemDto> dtoList = itemService.findDtosByFilters(freeText);

		return new Resources<>(dtoList, linkTo(methodOn(ItemRestService.class).getByFilters(freeText)).withSelfRel());
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Get a single item by id")
	@Secured(UserRole.RIGHT_ITEMS_GET)
	public Resource<ItemDto> getById(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		ItemDto dto = itemService.findDtoById(id);

		return new Resource<>(dto, linkTo(methodOn(ItemRestService.class).getById(id)).withSelfRel());
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Create a new item")
	@Secured(UserRole.RIGHT_ITEMS_POST)
	public Resource<ItemDto> create(@RequestBody(required = true) ItemDto dto) throws DefaultException {

		ItemDto itemDto = itemService.createDto(dto);

		return new Resource<>(itemDto, linkTo(methodOn(ItemRestService.class).create(itemDto)).withSelfRel());
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Update an item")
	@Secured(UserRole.RIGHT_ITEMS_PUT)
	public Resource<ItemDto> update(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) ItemDto dto) throws DefaultException {

		ItemDto itemDto = itemService.updateDto(dto, id);

		return new Resource<>(itemDto, linkTo(methodOn(ItemRestService.class).update(id, dto)).withSelfRel());
	}

	@PatchMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Update an item (only non null values)")
	@Secured(UserRole.RIGHT_ITEMS_PATCH)
	public Resource<ItemDto> patch(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) ItemDto dto) throws DefaultException {

		ItemDto itemDto = itemService.patchDto(dto, id);

		return new Resource<>(itemDto, linkTo(methodOn(ItemRestService.class).patch(id, dto)).withSelfRel());
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Delete item")
	@Secured(UserRole.RIGHT_ITEMS_DELETE)
	public ResponseEntity<Void> delete(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		itemService.deleteDtoById(id);

		return ResponseEntity.noContent().build();
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_NOTES)
	@ApiOperation(value = "Get a list of notes for item")
	@Secured(UserRole.RIGHT_ITEMS_NOTES_GET)
	public Resources<ItemNoteDto> getNotes(
			@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId) throws DefaultException {

		List<ItemNoteDto> dtoList = itemNoteService.findDtosByItemId(itemId);

		return new Resources<>(dtoList, linkTo(methodOn(ItemRestService.class).getNotes(itemId)).withSelfRel());
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_NOTES)
	@ApiOperation(value = "Create a new note for an item")
	@Secured(UserRole.RIGHT_ITEMS_NOTES_POST)
	public Resource<ItemNoteDto> createNote(
			@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId,
			@RequestBody(required = true) ItemNoteDto dto) throws DefaultException {

		ItemNoteDto itemNoteDto = itemNoteService.createDto(dto);

		return new Resource<>(itemNoteDto,
				linkTo(methodOn(ItemRestService.class).createNote(itemId, itemNoteDto)).withSelfRel());
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_NOTES_ID)
	@ApiOperation(value = "Delete note from item")
	@Secured(UserRole.RIGHT_ITEMS_NOTES_POST)
	public ResponseEntity<Void> deleteNote(
			@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId,
			@PathVariable(name = UrlMappings.NOTE_ID_VARIABLE, required = true) Long id) throws DefaultException {

		itemNoteService.deleteDtoById(id);

		return ResponseEntity.noContent().build();
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_NOTES_USER)
	@ApiOperation(value = "Get creator (first and last name) of the note. Necessary bc /users/{id} exposes more data and might be forbidden for the user.")
	@Secured(UserRole.RIGHT_ITEMS_NOTES_GET)
	public Resource<UserDto> getNoteUser(
			@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId,
			@PathVariable(name = UrlMappings.NOTE_ID_VARIABLE, required = true) Long noteId) throws DefaultException {

		UserDto userDto = itemNoteService.findUserNameDto(itemId, noteId);

		return new Resource<>(userDto,
				linkTo(methodOn(ItemRestService.class).getNoteUser(itemId, noteId)).withSelfRel());
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_TAGS)
	@ApiOperation(value = "Get a list of tags for item")
	@Secured(UserRole.RIGHT_ITEMS_GET)
	public Resources<ItemTagDto> getTags(
			@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId) throws DefaultException {

		Collection<ItemTagDto> dtoList = itemTagService.findDtosByItemId(itemId);

		return new Resources<>(dtoList, linkTo(methodOn(ItemRestService.class).getTags(itemId)).withSelfRel());
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_TAGS)
	@ApiOperation(value = "Add tag to item and create it if it doesn't exist")
	@Secured(UserRole.RIGHT_ITEM_TAGS_POST)
	public Resource<ItemTagDto> createTag(
			@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId,
			@RequestBody(required = true) ItemTagDto itemTagDto) throws DefaultException {

		ItemTagDto savedItemTag = itemTagService.createDtoForItem(itemId, itemTagDto);

		return new Resource<ItemTagDto>(savedItemTag,
				linkTo(methodOn(ItemRestService.class).createTag(itemId, itemTagDto)).withSelfRel());
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_TAGS_ID)
	@ApiOperation(value = "Remove tag from item and delete tag if it is not referenced anymore")
	@Secured(UserRole.RIGHT_ITEM_TAGS_DELETE)
	public ResponseEntity<Void> removeTags(
			@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId,
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id) throws DefaultException {

		itemTagService.deleteDtoByIdFromItem(itemId, id);

		return ResponseEntity.noContent().build();
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_HISTORY)
	@ApiOperation(value = "Get history for item")
	@Secured(UserRole.RIGHT_ITEMS_GET)
	public Resources<ItemHistoryDto> getHistory(
			@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId) throws DefaultException {

		Collection<ItemHistoryDto> dtoList = itemHistoryService.findDtosByItemId(itemId);

		return new Resources<>(dtoList, linkTo(methodOn(ItemRestService.class).getHistory(itemId)).withSelfRel());
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_HISTORY_USER)
	@ApiOperation(value = "Get creator (first and last name) of the event. Necessary bc /users/{id} exposes more data and might be forbidden for the user.")
	@Secured(UserRole.RIGHT_ITEMS_GET)
	public Resource<UserDto> getHistoryUser(
			@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId,
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id) throws DefaultException {

		UserDto userDto = itemHistoryService.findUserNameDto(itemId, id);

		return new Resource<>(userDto,
				linkTo(methodOn(ItemRestService.class).getHistoryUser(itemId, id)).withSelfRel());
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_IMAGE)
	@ApiOperation(value = "Add image to item")
	@Secured(UserRole.RIGHT_ITEMS_POST)
	public Resource<ItemImageDto> addItemImage(
			@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId,
			@RequestBody(required = true) ItemImageDto dto) throws DefaultException {

		ItemImageDto saved = itemImageService.createDto(dto, itemId);

		return new Resource<>(saved, linkTo(methodOn(ItemRestService.class).addItemImage(itemId, dto)).withSelfRel());
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_IMAGE_ID)
	@ApiOperation(value = "Update image of item")
	@Secured(UserRole.RIGHT_ITEMS_PUT)
	public Resource<ItemImageDto> updateItemImage(
			@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId,
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) ItemImageDto dto) throws DefaultException {

		ItemImageDto saved = itemImageService.updateDto(dto, id, itemId);

		return new Resource<>(saved,
				linkTo(methodOn(ItemRestService.class).updateItemImage(itemId, id, dto)).withSelfRel());
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_IMAGE)
	@ApiOperation(value = "Get image of item")
	@Secured(UserRole.RIGHT_ITEMS_GET)
	public Resource<ItemImageDto> getItemImage(
			@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId) throws DefaultException {

		ItemImageDto dto = itemImageService.findDtoByItemId(itemId);
		return new Resource<>(dto, linkTo(methodOn(ItemRestService.class).getItemImage(itemId)).withSelfRel());
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_ITEMS)
	@ApiOperation(value = "Get related items of item")
	@Secured(UserRole.RIGHT_ITEMS_GET)
	public Resources<ItemDto> getReltatedItems(
			@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId) throws DefaultException {

		List<ItemDto> items = itemService.findRelatedItemDtosByItemId(itemId);
		return new Resources<>(items, linkTo(methodOn(ItemRestService.class).getReltatedItems(itemId)).withSelfRel());
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_ITEMS)
	@ApiOperation(value = "Add related item to item")
	@Secured(UserRole.RIGHT_ITEMS_POST)
	public Resource<ItemItemDto> addItemReltation(
			@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId,
			@RequestBody(required = true) ItemItemDto dto) throws DefaultException {

		ItemItemDto savedRelation = itemItemService.createDto(dto);
		return new Resource<>(savedRelation,
				linkTo(methodOn(ItemRestService.class).addItemReltation(itemId, dto)).withSelfRel());
	}

	@DeleteMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_ITEMS_ID)
	@ApiOperation(value = "Delete relation between items")
	@Secured(UserRole.RIGHT_ITEMS_POST)
	public ResponseEntity<Void> deleteItemReltation(
			@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long item1Id,
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long item2Id) throws DefaultException {

		itemItemService.deleteItemItem(item1Id, item2Id);
		return ResponseEntity.noContent().build();
	}

}
