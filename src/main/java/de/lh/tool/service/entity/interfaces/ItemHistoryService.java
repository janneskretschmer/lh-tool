package de.lh.tool.service.entity.interfaces;

import java.util.Collection;

import de.lh.tool.domain.dto.ItemHistoryDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemHistory;
import de.lh.tool.domain.model.Slot;

public interface ItemHistoryService extends BasicEntityService<ItemHistory, Long> {
	Collection<ItemHistoryDto> getDtosByItemId(Long itemId) throws DefaultException;

	void logNewBrokenState(Item item);

	void logUpdated(Item item);

	UserDto getUserNameDto(Long itemId, Long id) throws DefaultException;

	void logCreated(Item item);

	void logNewSlot(Item item, Slot old);
}
