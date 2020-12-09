package de.lh.tool.repository;

import java.util.List;
import java.util.Optional;

import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemItemTag;
import de.lh.tool.domain.model.ItemTag;

public interface ItemItemTagRepository extends BasicEntityRepository<ItemItemTag, Long> {

	List<ItemItemTag> findByItemTag(ItemTag itemTag);

	Optional<ItemItemTag> findByItemAndItemTag(Item item, ItemTag itemTag);

	List<ItemItemTag> findByItem(Item item);
}
