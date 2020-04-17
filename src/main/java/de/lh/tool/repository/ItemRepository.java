package de.lh.tool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.lh.tool.domain.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
