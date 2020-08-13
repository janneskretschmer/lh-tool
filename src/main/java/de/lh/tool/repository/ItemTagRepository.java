package de.lh.tool.repository;

import java.util.Optional;

import de.lh.tool.domain.model.ItemTag;

public interface ItemTagRepository extends BasicEntityRepository<ItemTag, Long> {

	Optional<ItemTag> findByName(String name);

}
