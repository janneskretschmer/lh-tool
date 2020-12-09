package de.lh.tool.service.entity.interfaces;

import java.util.List;
import java.util.Optional;

import de.lh.tool.domain.model.Project;
import de.lh.tool.domain.model.ProjectUser;
import de.lh.tool.domain.model.User;

public interface ProjectUserService extends BasicEntityService<ProjectUser, Long> {

	Optional<ProjectUser> findByProjectAndUser(Project project, User user);

	List<ProjectUser> findByUserId(Long userId);

}
