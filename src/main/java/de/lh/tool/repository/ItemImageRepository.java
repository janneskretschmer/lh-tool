package de.lh.tool.repository;

import java.util.Optional;

import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemImage;

public interface ItemImageRepository extends BasicEntityRepository<ItemImage, Long> {

	Optional<ItemImage> findByItem(Item item);
}
