package de.lh.tool.service.entity.impl;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.ItemTagDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemItemTag;
import de.lh.tool.domain.model.ItemTag;
import de.lh.tool.repository.ItemTagRepository;
import de.lh.tool.service.entity.interfaces.ItemItemTagService;
import de.lh.tool.service.entity.interfaces.ItemService;
import de.lh.tool.service.entity.interfaces.ItemTagService;
import de.lh.tool.util.ValidationUtil;

@Service
public class ItemTagServiceImpl extends BasicMappableEntityServiceImpl<ItemTagRepository, ItemTag, ItemTagDto, Long>
		implements ItemTagService {

	@Autowired
	private ItemService itemService;

	@Autowired
	private ItemItemTagService itemItemTagService;

	@Override
	@Transactional
	public List<ItemTagDto> getItemTagDtosByItemId(Long itemId) throws DefaultException {
		return convertToDtoList(itemService.findById(itemId)
				.orElseThrow(ExceptionEnum.EX_INVALID_ID::createDefaultException).getTags());
	}

	@Override
	@Transactional
	public Optional<ItemTag> findByName(String name) {
		return getRepository().findByName(name);
	}

	@Override
	@Transactional
	public ItemTagDto createItemTagForItem(Long itemId, ItemTagDto dto) throws DefaultException {
		ValidationUtil.checkIdsNonNull(itemId);
		Item item = itemService.findById(itemId).orElseThrow(ExceptionEnum.EX_INVALID_ID::createDefaultException);
		if (!itemService.isViewAllowed(item)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}

		String tagName = Optional.ofNullable(dto).map(ItemTagDto::getName)
				.orElseThrow(ExceptionEnum.EX_ITEM_NO_TAG::createDefaultException);

		ItemTag tag = findByName(tagName).orElseGet(() -> save(ItemTag.builder().name(tagName).build()));

		itemItemTagService.save(ItemItemTag.builder().item(item).itemTag(tag).build());

		return convertToDto(tag);
	}

	@Override
	@Transactional
	public void deleteItemTagFromItem(Long itemId, Long itemTagId) throws DefaultException {
		ValidationUtil.checkIdsNonNull(itemId, itemTagId);

		Item item = itemService.findById(itemId).orElseThrow(ExceptionEnum.EX_INVALID_ITEM_ID::createDefaultException);

		if (!itemService.isViewAllowed(item)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
		ItemTag itemTag = findById(itemTagId).orElseThrow(ExceptionEnum.EX_INVALID_ID::createDefaultException);

		itemItemTagService.deleteIfExists(item, itemTag);

		if (itemItemTagService.findByItemTag(itemTag).size() <= 0) {
			delete(itemTag);
		}
	}

}
