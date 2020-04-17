package de.lh.tool.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.lh.tool.domain.model.Project;
import de.lh.tool.domain.model.ProjectUser;
import de.lh.tool.domain.model.User;

@Repository
public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
	Optional<ProjectUser> findByProjectAndUser(Project project, User user);
}
