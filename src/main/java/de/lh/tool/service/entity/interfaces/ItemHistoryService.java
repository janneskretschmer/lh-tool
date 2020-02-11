package de.lh.tool.service.entity.interfaces;

import java.util.Collection;

import de.lh.tool.domain.dto.ItemHistoryDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemHistory;

public interface ItemHistoryService extends BasicEntityService<ItemHistory, Long> {
	Collection<ItemHistoryDto> getDtosByItemId(Long itemId) throws DefaultException;

	void logNewBrokenState(Item item);

	void logUpdated(Item item);
}
