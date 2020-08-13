package de.lh.tool.service.entity.interfaces;

import java.util.List;

import de.lh.tool.domain.dto.ItemItemDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemItem;

public interface ItemItemService extends MappableEntityService<ItemItem, ItemItemDto, Long> {

	List<Item> findRelatedItemsByItem(Item item) throws DefaultException;

	ItemItemDto createDto(Long itemId, ItemItemDto dto) throws DefaultException;

	void deleteItemItem(Long item1Id, Long item2Id) throws DefaultException;

}
