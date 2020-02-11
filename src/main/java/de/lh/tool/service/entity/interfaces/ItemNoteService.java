package de.lh.tool.service.entity.interfaces;

import java.util.Collection;

import de.lh.tool.domain.dto.ItemNoteDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.ItemNote;

public interface ItemNoteService extends BasicEntityService<ItemNote, Long> {
	Collection<ItemNoteDto> getDtosByItemId(Long itemId) throws DefaultException;

	ItemNoteDto createItemNoteDto(ItemNoteDto dto) throws DefaultException;

	ItemNoteDto updateItemNoteDto(ItemNoteDto dto, Long id) throws DefaultException;
}
