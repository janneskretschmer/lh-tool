package de.lh.tool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.lh.tool.domain.model.ItemHistory;

public interface ItemHistoryRepository extends JpaRepository<ItemHistory, Long> {
}
