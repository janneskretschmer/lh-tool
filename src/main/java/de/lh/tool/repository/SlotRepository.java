package de.lh.tool.repository;

import org.springframework.data.repository.CrudRepository;

import de.lh.tool.domain.model.Slot;

public interface SlotRepository extends CrudRepository<Slot, Long> {

}
