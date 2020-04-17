package de.lh.tool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.lh.tool.domain.model.ItemNote;

public interface ItemNoteRepository extends JpaRepository<ItemNote, Long> {
}
