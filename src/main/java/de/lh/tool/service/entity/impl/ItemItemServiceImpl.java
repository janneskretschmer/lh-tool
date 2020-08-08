package de.lh.tool.service.entity.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.ItemItemDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemItem;
import de.lh.tool.repository.ItemItemRepository;
import de.lh.tool.service.entity.interfaces.ItemItemService;
import de.lh.tool.service.entity.interfaces.ItemService;
import de.lh.tool.util.ValidationUtil;

@Service
public class ItemItemServiceImpl extends BasicMappableEntityServiceImpl<ItemItemRepository, ItemItem, ItemItemDto, Long>
		implements ItemItemService {

	@Autowired
	ItemService itemService;

	@Override
	@Transactional
	public List<Item> findRelatedItemsByItem(Item item) throws DefaultException {
		return getRepository().findByItem(item).stream()
				.map(itemItem -> itemItem.getItem1().getId().equals(item.getId()) ? itemItem.getItem2()
						: itemItem.getItem1())
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public ItemItemDto createDto(Long itemId, ItemItemDto dto) throws DefaultException {
		if (dto.getId() != null) {
			throw ExceptionEnum.EX_ID_PROVIDED.createDefaultException();
		}
		ValidationUtil.checkIdsNonNull(dto.getItem1Id(), dto.getItem2Id());
		if (dto.getItem1Id().equals(dto.getItem2Id())) {
			throw ExceptionEnum.EX_ITEM_SELF_RELATION.createDefaultException();
		}
		Item item1 = itemService.findById(dto.getItem1Id())
				.orElseThrow(ExceptionEnum.EX_INVALID_ITEM_ID::createDefaultException);
		Item item2 = itemService.findById(dto.getItem2Id())
				.orElseThrow(ExceptionEnum.EX_INVALID_ITEM_ID::createDefaultException);
		if (!itemService.isViewAllowed(item1) || !itemService.isViewAllowed(item2)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
		if (getRepository().findByItems(item1, item2).isPresent()) {
			throw ExceptionEnum.EX_ITEM_RELATION_ALREADY_EXISTS.createDefaultException();
		}

		ItemItem savedItemItem = save(ItemItem.builder().item1(item1).item2(item2).build());
		return convertToDto(savedItemItem);
	}

	@Override
	@Transactional
	public void deleteItemItem(Long item1Id, Long item2Id) throws DefaultException {
		ValidationUtil.checkIdsNonNull(item1Id, item2Id);
		Item item1 = itemService.findById(item1Id)
				.orElseThrow(ExceptionEnum.EX_INVALID_ITEM_ID::createDefaultException);
		Item item2 = itemService.findById(item2Id)
				.orElseThrow(ExceptionEnum.EX_INVALID_ITEM_ID::createDefaultException);
		if (!itemService.isViewAllowed(item1) || !itemService.isViewAllowed(item2)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
		getRepository().findByItems(item1, item2).ifPresent(this::delete);
	}

}
