package de.lh.tool.service.entity.interfaces;

import java.util.List;

import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemItemTag;
import de.lh.tool.domain.model.ItemTag;
import lombok.NonNull;

public interface ItemItemTagService extends BasicEntityService<ItemItemTag, Long> {

	ItemItemTag createIfNotExists(Item item, ItemTag itemTag) throws DefaultException;

	void deleteIfExists(@NonNull Item item, @NonNull ItemTag itemTag) throws DefaultException;

	List<ItemItemTag> findByItemTag(ItemTag itemTag);

}
