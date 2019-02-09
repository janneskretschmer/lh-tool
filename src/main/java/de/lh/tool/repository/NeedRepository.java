package de.lh.tool.repository;

import org.springframework.data.repository.CrudRepository;

import de.lh.tool.domain.model.Need;

public interface NeedRepository extends CrudRepository<Need, Long> {

}
