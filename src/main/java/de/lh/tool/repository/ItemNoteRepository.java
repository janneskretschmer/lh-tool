package de.lh.tool.repository;

import org.springframework.data.repository.CrudRepository;

import de.lh.tool.domain.model.ItemNote;

public interface ItemNoteRepository extends CrudRepository<ItemNote, Long> {
}
