package de.lh.tool.service.entity.interfaces.crud;

import java.util.List;

import de.lh.tool.domain.dto.ItemNoteDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.ItemNote;

public interface ItemNoteCrudService extends BasicEntityCrudService<ItemNote, ItemNoteDto, Long> {
	UserDto findUserNameDto(Long itemId, Long noteId) throws DefaultException;

	List<ItemNoteDto> findDtosByItemId(Long itemId) throws DefaultException;
}
