package de.lh.tool.service.entity.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.ItemItemDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemItem;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.ItemItemRepository;
import de.lh.tool.service.entity.interfaces.ItemItemService;
import de.lh.tool.service.entity.interfaces.ItemService;
import de.lh.tool.service.entity.interfaces.crud.ItemItemCrudService;
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;

@Service
public class ItemItemServiceImpl extends BasicEntityCrudServiceImpl<ItemItemRepository, ItemItem, ItemItemDto, Long>
		implements ItemItemService, ItemItemCrudService {

	@Autowired
	private ItemService itemService;

	@Override
	@Transactional
	public List<Item> findRelatedItemsByItemId(Long itemId) throws DefaultException {
		checkFindRight();
		Item item = itemService.findByIdOrThrowInvalidIdException(itemId);
		itemService.checkReadPermission(item);
		return filterFindResult(getRepository().findByItem(item)).stream()
				.map(itemItem -> itemItem.getItem1().getId().equals(item.getId()) ? itemItem.getItem2()
						: itemItem.getItem1())
				.collect(Collectors.toList());
	}

	@Override
	protected void checkValidity(@NonNull ItemItem itemItem) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_ITEM_ID, itemItem.getItem1());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_ITEM_ID, itemItem.getItem2());
		if (itemItem.getItem1().getId().equals(itemItem.getItem2().getId())) {
			throw ExceptionEnum.EX_ITEM_SELF_RELATION.createDefaultException();
		}
		ValidationUtil.checkSameIdIfExists(ExceptionEnum.EX_ITEM_RELATION_ALREADY_EXISTS,
				getRepository().findByItems(itemItem.getItem1(), itemItem.getItem2()), itemItem);
	}

	// FUTURE: use deleteDtoById in REST-service
	@Override
	@Transactional
	public void deleteItemItem(Long item1Id, Long item2Id) throws DefaultException {
		ItemItem itemItem = convertToEntity(ItemItemDto.builder().item1Id(item1Id).item2Id(item2Id).build());
		Optional<Long> optId = getRepository().findByItems(itemItem.getItem1(), itemItem.getItem2())
				.map(ItemItem::getId);
		if (optId.isPresent()) {
			deleteDtoById(optId.get());
		}
	}

	@Override
	public boolean hasReadPermission(@NonNull ItemItem itemItem) {
		return itemService.hasReadPermission(itemItem.getItem1()) && itemService.hasReadPermission(itemItem.getItem2());
	}

	@Override
	public boolean hasWritePermission(@NonNull ItemItem itemItem) {
		return hasReadPermission(itemItem);
	}

	@Override
	public String getRightPrefix() {
		return UserRole.ITEMS_PREFIX;
	}

}
