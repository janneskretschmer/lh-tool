package de.lh.tool.service.entity.interfaces;

import java.util.List;

import de.lh.tool.domain.dto.ItemTagDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.ItemTag;

public interface ItemTagService extends BasicEntityService<ItemTag, Long> {

	ItemTagDto getItemTagDtoById(Long id) throws DefaultException;

	ItemTagDto createItemTagDto(ItemTagDto dto) throws DefaultException;

	ItemTagDto updateItemTagDto(ItemTagDto dto, Long id) throws DefaultException;

	List<ItemTagDto> getItemTagDtosByItemId(Long itemId) throws DefaultException;

}
