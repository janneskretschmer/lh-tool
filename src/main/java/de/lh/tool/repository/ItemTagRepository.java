package de.lh.tool.repository;

import org.springframework.data.repository.CrudRepository;

import de.lh.tool.domain.model.ItemTag;

public interface ItemTagRepository extends CrudRepository<ItemTag, Long> {

}
