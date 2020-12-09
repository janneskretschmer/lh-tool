package de.lh.tool.service.entity.interfaces;

import de.lh.tool.domain.model.Slot;

public interface SlotService extends BasicEntityService<Slot, Long> {
	String getSlotNameWithStore(Slot slot);

}
