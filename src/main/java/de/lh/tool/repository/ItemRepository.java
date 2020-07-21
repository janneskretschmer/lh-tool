package de.lh.tool.repository;

import java.util.Optional;

import de.lh.tool.domain.model.Item;

public interface ItemRepository extends BasicEntityRepository<Item, Long> {

	public Optional<Item> findByIdentifier(String identifier);
}
