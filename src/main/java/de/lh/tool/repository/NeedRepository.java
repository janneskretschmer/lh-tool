package de.lh.tool.repository;

import java.util.Date;

import org.springframework.data.repository.CrudRepository;

import de.lh.tool.domain.model.Need;

public interface NeedRepository extends CrudRepository<Need, Long> {
	public Iterable<Need> findByProject_IdAndDateBetween(Long id, Date start, Date end);
}
