package de.lh.tool.service.entity.interfaces.crud;

import de.lh.tool.domain.dto.ItemItemTagDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemItemTag;
import de.lh.tool.domain.model.ItemTag;
import lombok.NonNull;

public interface ItemItemTagCrudService extends BasicEntityCrudService<ItemItemTag, ItemItemTagDto, Long> {

	void deleteIfExists(@NonNull Item item, @NonNull ItemTag itemTag) throws DefaultException;

}
