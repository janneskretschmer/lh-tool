package de.lh.tool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.lh.tool.domain.model.ItemTag;

public interface ItemTagRepository extends JpaRepository<ItemTag, Long> {

}
