package de.lh.tool.service.entity.interfaces.crud;

import java.util.List;

import de.lh.tool.domain.dto.SlotDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.Slot;

public interface SlotCrudService extends BasicEntityCrudService<Slot, SlotDto, Long> {

	List<SlotDto> findDtosByFilters(String freeText, String name, String description, Long storeId)
			throws DefaultException;

}
