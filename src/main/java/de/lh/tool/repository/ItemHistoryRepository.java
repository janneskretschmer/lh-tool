package de.lh.tool.repository;

import org.springframework.data.repository.CrudRepository;

import de.lh.tool.domain.model.ItemHistory;

public interface ItemHistoryRepository extends CrudRepository<ItemHistory, Long> {
}
