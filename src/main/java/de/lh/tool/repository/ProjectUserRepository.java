package de.lh.tool.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import de.lh.tool.domain.model.Project;
import de.lh.tool.domain.model.ProjectUser;
import de.lh.tool.domain.model.User;

@Repository
public interface ProjectUserRepository extends BasicEntityRepository<ProjectUser, Long> {
	Optional<ProjectUser> findByProjectAndUser(Project project, User user);

	List<ProjectUser> findByUser(User user);

	List<ProjectUser> findByUserId(Long userId);
}
