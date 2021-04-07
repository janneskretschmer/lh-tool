package de.lh.tool.service.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
	public List<ItemDto> getByFilters(
			@RequestParam(required = false, name = UrlMappings.FREE_TEXT_VARIABLE) String freeText)
			throws DefaultException {

		return itemService.findDtosByFilters(freeText);
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Get a single item by id")
	@Secured(UserRole.RIGHT_ITEMS_GET)
	public ItemDto getById(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id)
			throws DefaultException {

		return itemService.findDtoById(id);
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.NO_EXTENSION)
	@ApiOperation(value = "Create a new item")
	@Secured(UserRole.RIGHT_ITEMS_POST)
	public ItemDto create(@RequestBody(required = true) ItemDto dto) throws DefaultException {

		return itemService.createDto(dto);
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Update an item")
	@Secured(UserRole.RIGHT_ITEMS_PUT)
	public ItemDto update(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) ItemDto dto) throws DefaultException {

		return itemService.updateDto(dto, id);
	}

	@PatchMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ID_EXTENSION)
	@ApiOperation(value = "Update an item (only non null values)")
	@Secured(UserRole.RIGHT_ITEMS_PATCH)
	public ItemDto patch(@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) ItemDto dto) throws DefaultException {

		return itemService.patchDto(dto, id);
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
	public List<ItemNoteDto> getNotes(@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId)
			throws DefaultException {

		return itemNoteService.findDtosByItemId(itemId);
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_NOTES)
	@ApiOperation(value = "Create a new note for an item")
	@Secured(UserRole.RIGHT_ITEMS_NOTES_POST)
	public ItemNoteDto createNote(@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId,
			@RequestBody(required = true) ItemNoteDto dto) throws DefaultException {

		return itemNoteService.createDto(dto);
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
	public UserDto getNoteUser(@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId,
			@PathVariable(name = UrlMappings.NOTE_ID_VARIABLE, required = true) Long noteId) throws DefaultException {

		return itemNoteService.findUserNameDto(itemId, noteId);
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_TAGS)
	@ApiOperation(value = "Get a list of tags for item")
	@Secured(UserRole.RIGHT_ITEMS_GET)
	public List<ItemTagDto> getTags(@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId)
			throws DefaultException {

		return itemTagService.findDtosByItemId(itemId);
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_TAGS)
	@ApiOperation(value = "Add tag to item and create it if it doesn't exist")
	@Secured(UserRole.RIGHT_ITEM_TAGS_POST)
	public ItemTagDto createTag(@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId,
			@RequestBody(required = true) ItemTagDto itemTagDto) throws DefaultException {

		return itemTagService.createDtoForItem(itemId, itemTagDto);
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
	public List<ItemHistoryDto> getHistory(
			@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId) throws DefaultException {

		return itemHistoryService.findDtosByItemId(itemId);
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_HISTORY_USER)
	@ApiOperation(value = "Get creator (first and last name) of the event. Necessary bc /users/{id} exposes more data and might be forbidden for the user.")
	@Secured(UserRole.RIGHT_ITEMS_GET)
	public UserDto getHistoryUser(@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId,
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id) throws DefaultException {

		return itemHistoryService.findUserNameDto(itemId, id);
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_IMAGE)
	@ApiOperation(value = "Add image to item")
	@Secured(UserRole.RIGHT_ITEMS_POST)
	public ItemImageDto addItemImage(@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId,
			@RequestBody(required = true) ItemImageDto dto) throws DefaultException {

		return itemImageService.createDto(dto, itemId);
	}

	@PutMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_IMAGE_ID)
	@ApiOperation(value = "Update image of item")
	@Secured(UserRole.RIGHT_ITEMS_PUT)
	public ItemImageDto updateItemImage(@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId,
			@PathVariable(name = UrlMappings.ID_VARIABLE, required = true) Long id,
			@RequestBody(required = true) ItemImageDto dto) throws DefaultException {

		return itemImageService.updateDto(dto, id, itemId);
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_IMAGE)
	@ApiOperation(value = "Get image of item")
	@Secured(UserRole.RIGHT_ITEMS_GET)
	public ItemImageDto getItemImage(@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId)
			throws DefaultException {

		return itemImageService.findDtoByItemId(itemId);
	}

	@GetMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_ITEMS)
	@ApiOperation(value = "Get related items of item")
	@Secured(UserRole.RIGHT_ITEMS_GET)
	public List<ItemDto> getReltatedItems(
			@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId) throws DefaultException {

		return itemService.findRelatedItemDtosByItemId(itemId);
	}

	@PostMapping(produces = UrlMappings.MEDIA_TYPE_JSON, path = UrlMappings.ITEM_ITEMS)
	@ApiOperation(value = "Add related item to item")
	@Secured(UserRole.RIGHT_ITEMS_POST)
	public ItemItemDto addItemReltation(@PathVariable(name = UrlMappings.ITEM_ID_VARIABLE, required = true) Long itemId,
			@RequestBody(required = true) ItemItemDto dto) throws DefaultException {

		return itemItemService.createDto(dto);
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
