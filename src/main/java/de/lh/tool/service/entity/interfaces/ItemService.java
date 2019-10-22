package de.lh.tool.service.entity.interfaces;

import java.util.List;

import de.lh.tool.domain.dto.ItemDto;
import de.lh.tool.domain.dto.StoreDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.Item;

public interface ItemService extends BasicEntityService<Item, Long> {

	List<ItemDto> getItemDtos();

	ItemDto getItemDtoById(Long id) throws DefaultException;

	ItemDto createItemDto(StoreDto dto) throws DefaultException;

	ItemDto updateItemDto(StoreDto dto, Long id) throws DefaultException;

}
