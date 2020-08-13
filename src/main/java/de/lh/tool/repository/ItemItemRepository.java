package de.lh.tool.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemItem;

public interface ItemItemRepository extends BasicEntityRepository<ItemItem, Long> {

	@Query("SELECT ii FROM ItemItem ii WHERE ii.item1 = :item OR ii.item2 = :item")
	List<ItemItem> findByItem(@Param("item") Item item);

	@Query("SELECT ii FROM ItemItem ii WHERE (ii.item1 = :item1 AND ii.item2 = :item2) OR (ii.item1 = :item2 AND ii.item2 = :item1)")
	Optional<ItemItem> findByItems(@Param("item1") Item item1, @Param("item2") Item item2);

}
