package de.lh.tool.service.entity.interfaces;

import java.util.List;

import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemItem;

public interface ItemItemService extends BasicEntityService<ItemItem, Long> {

	List<Item> findRelatedItemsByItemId(Long itemId) throws DefaultException;

}
