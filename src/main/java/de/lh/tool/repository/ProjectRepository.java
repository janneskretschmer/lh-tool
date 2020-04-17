package de.lh.tool.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.lh.tool.domain.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
	Optional<Project> findByName(String name);
}
