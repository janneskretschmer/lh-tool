package de.lh.tool.service.entity.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.model.Project;
import de.lh.tool.domain.model.User;
import de.lh.tool.repository.ProjectRepository;
import de.lh.tool.service.entity.interfaces.ProjectService;
import de.lh.tool.service.entity.interfaces.UserService;

@Service
public class ProjectServiceImpl extends BasicEntityServiceImpl<ProjectRepository, Project, Long>
		implements ProjectService {
	@Autowired
	private UserService userService;

	@Override
	public Collection<Project> getOwnProjects() {
		User currentUser = userService.getCurrentUser();
		if (currentUser != null) {
			return currentUser.getProjects();
		}
		return null;
	}

}
