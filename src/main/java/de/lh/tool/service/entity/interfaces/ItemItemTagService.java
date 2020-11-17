package de.lh.tool.service.entity.interfaces;

import java.util.List;

import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemItemTag;
import de.lh.tool.domain.model.ItemTag;

public interface ItemItemTagService extends BasicEntityService<ItemItemTag, Long> {

	List<ItemItemTag> findByItem(Item item);

	List<ItemItemTag> findByItemTag(ItemTag itemTag);

}
