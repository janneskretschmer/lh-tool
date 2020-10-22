package de.lh.tool.service.entity.interfaces;

import java.util.List;

import de.lh.tool.domain.dto.SlotDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.Slot;

public interface SlotService extends BasicEntityService<Slot, Long> {

	SlotDto getSlotDtoById(Long id) throws DefaultException;

	SlotDto createSlotDto(SlotDto dto) throws DefaultException;

	SlotDto updateSlotDto(SlotDto dto, Long id) throws DefaultException;

	List<SlotDto> getSlotDtosByFilters(String freeText, String name, String description, Long storeId);

	String getSlotNameWithStore(Slot slot);

	void deleteSlotById(Long slotId) throws DefaultException;

}
