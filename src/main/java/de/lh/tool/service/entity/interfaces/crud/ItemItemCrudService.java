package de.lh.tool.service.entity.interfaces.crud;

import de.lh.tool.domain.dto.ItemItemDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.ItemItem;

public interface ItemItemCrudService extends BasicEntityCrudService<ItemItem, ItemItemDto, Long> {

	void deleteItemItem(Long item1Id, Long item2Id) throws DefaultException;

}
