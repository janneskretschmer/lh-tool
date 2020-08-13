package de.lh.tool.service.entity.interfaces;

import de.lh.tool.domain.dto.ItemImageDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.ItemImage;

public interface ItemImageService extends MappableEntityService<ItemImage, ItemImageDto, Long> {
	ItemImageDto findDtoByItemId(Long itemId) throws DefaultException;

	ItemImageDto createDto(Long itemId, ItemImageDto dto) throws DefaultException;

	ItemImageDto updateDto(Long itemId, Long id, ItemImageDto dto) throws DefaultException;
}
