package de.lh.tool.service.entity.interfaces.crud;

import java.util.List;

import de.lh.tool.domain.dto.ItemTagDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.ItemTag;

public interface ItemTagCrudService extends BasicEntityCrudService<ItemTag, ItemTagDto, Long> {

	List<ItemTagDto> findDtosByItemId(Long itemId) throws DefaultException;

	ItemTagDto createDtoForItem(Long itemId, ItemTagDto dto) throws DefaultException;

	void deleteDtoByIdFromItem(Long itemId, Long itemTagId) throws DefaultException;

}
