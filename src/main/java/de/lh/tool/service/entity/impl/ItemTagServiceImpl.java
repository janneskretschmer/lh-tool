package de.lh.tool.service.entity.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.ItemItemTagDto;
import de.lh.tool.domain.dto.ItemTagDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemItemTag;
import de.lh.tool.domain.model.ItemTag;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.ItemTagRepository;
import de.lh.tool.service.entity.interfaces.ItemItemTagService;
import de.lh.tool.service.entity.interfaces.ItemService;
import de.lh.tool.service.entity.interfaces.ItemTagService;
import de.lh.tool.service.entity.interfaces.crud.ItemItemTagCrudService;
import de.lh.tool.service.entity.interfaces.crud.ItemTagCrudService;
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;

@Service
public class ItemTagServiceImpl extends BasicEntityCrudServiceImpl<ItemTagRepository, ItemTag, ItemTagDto, Long>
		implements ItemTagService, ItemTagCrudService {

	@Autowired
	private ItemService itemService;

	// FUTURE get rid of this
	@Autowired
	private ItemItemTagCrudService itemItemTagCrudService;

	@Autowired
	private ItemItemTagService itemItemTagService;

	// FUTURE use ItemItemTagService
	@Override
	@Transactional
	public List<ItemTagDto> findDtosByItemId(Long itemId) throws DefaultException {
		checkFindRight();
		Item item = itemService.findByIdOrThrowInvalidIdException(itemId);
		itemService.checkReadPermission(item);
		return itemItemTagService.findByItem(item).stream().map(ItemItemTag::getItemTag).map(this::convertToDto)
				.collect(Collectors.toList());
	}

	// FUTURE use ItemItemTagService directly with dto
	@Override
	@Transactional
	public ItemTagDto createDtoForItem(@NonNull Long itemId, @NonNull ItemTagDto dto) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_ITEM_ID, itemId);

		itemService.checkWritePermission(itemId);
		ItemTag tag = convertToEntity(dto);
		checkValidity(tag);

		ItemTag savedTag = getRepository().findByName(tag.getName()).orElseGet(() -> save(tag));

		itemItemTagCrudService.createDto(ItemItemTagDto.builder().itemId(itemId).itemTagId(savedTag.getId()).build());

		return convertToDto(savedTag);
	}

	@Override
	protected void checkValidity(@NonNull ItemTag itemTag) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_NAME, itemTag.getName());
	}

	// FUTURE use ItemItemTagService directly
	@Override
	@Transactional
	public void deleteDtoByIdFromItem(Long itemId, Long itemTagId) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_ITEM_ID, itemId);
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_ITEM_TAG_ID, itemTagId);

		Item item = itemService.findByIdOrThrowInvalidIdException(itemId);

		itemService.checkWritePermission(item);

		ItemTag itemTag = findByIdOrThrowInvalidIdException(itemTagId);

		itemItemTagCrudService.deleteIfExists(item, itemTag);

		if (itemItemTagService.findByItemTag(itemTag).isEmpty()) {
			delete(itemTag);
		}
	}

	@Override
	public boolean hasReadPermission(@NonNull ItemTag entity) {
		return true;
	}

	@Override
	public boolean hasWritePermission(@NonNull ItemTag entity) {
		return true;
	}

	@Override
	public String getRightPrefix() {
		return UserRole.ITEM_TAGS_PREFIX;
	}

}
