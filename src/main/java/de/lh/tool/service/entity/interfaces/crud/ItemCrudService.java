package de.lh.tool.service.entity.interfaces.crud;

import java.util.List;

import de.lh.tool.domain.dto.ItemDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.Item;
import lombok.NonNull;

public interface ItemCrudService extends BasicEntityCrudService<Item, ItemDto, Long> {

	ItemDto patchDto(@NonNull ItemDto dto, @NonNull Long id) throws DefaultException;

	List<ItemDto> findDtosByFilters(String freeText) throws DefaultException;

	List<ItemDto> findRelatedItemDtosByItemId(@NonNull Long itemId) throws DefaultException;

}
