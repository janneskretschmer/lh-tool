package de.lh.tool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.lh.tool.domain.model.Slot;

public interface SlotRepository extends JpaRepository<Slot, Long> {

}
