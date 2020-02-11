package de.lh.tool.repository;

import org.springframework.data.repository.CrudRepository;

import de.lh.tool.domain.model.Item;

public interface ItemRepository extends CrudRepository<Item, Long> {

}
