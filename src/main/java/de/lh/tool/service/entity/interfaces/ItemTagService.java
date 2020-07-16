package de.lh.tool.service.entity.interfaces;

import java.util.List;
import java.util.Optional;

import de.lh.tool.domain.dto.ItemTagDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.ItemTag;

public interface ItemTagService extends MappableEntityService<ItemTag, ItemTagDto, Long> {

	List<ItemTagDto> getItemTagDtosByItemId(Long itemId) throws DefaultException;

	Optional<ItemTag> findByName(String name);

	ItemTagDto createItemTagForItem(Long itemId, ItemTagDto dto) throws DefaultException;

	void deleteItemTagFromItem(Long itemId, Long itemTagId) throws DefaultException;

}
