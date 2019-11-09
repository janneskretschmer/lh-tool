package de.lh.tool.repository;

import org.springframework.data.repository.CrudRepository;

import de.lh.tool.domain.model.Tag;

public interface TagRepository extends CrudRepository<Tag, Long> {

}
