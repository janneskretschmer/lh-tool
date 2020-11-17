package de.lh.tool.service.entity.interfaces;

import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemHistory;
import de.lh.tool.domain.model.Slot;

public interface ItemHistoryService extends BasicEntityService<ItemHistory, Long> {
	void logNewBrokenState(Item item);

	void logUpdated(Item item);

	void logCreated(Item item);

	void logNewSlot(Item item, Slot old);

	void logNewQuantity(Item item, Double old);
}
