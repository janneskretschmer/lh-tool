package de.lh.tool.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.lh.tool.domain.model.Project;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {
	Optional<Project> findByName(String name);
}
