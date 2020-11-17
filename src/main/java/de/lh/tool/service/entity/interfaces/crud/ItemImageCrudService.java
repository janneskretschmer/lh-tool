package de.lh.tool.service.entity.interfaces.crud;

import de.lh.tool.domain.dto.ItemImageDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.ItemImage;

public interface ItemImageCrudService extends BasicEntityCrudService<ItemImage, ItemImageDto, Long> {
	ItemImageDto findDtoByItemId(Long itemId) throws DefaultException;

	ItemImageDto createDto(ItemImageDto dto, Long itemId) throws DefaultException;

	ItemImageDto updateDto(ItemImageDto dto, Long id, Long itemId) throws DefaultException;
}
