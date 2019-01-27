package de.lh.tool.service.entity.interfaces;

import java.util.Collection;

import de.lh.tool.domain.model.Project;

public interface ProjectService extends BasicEntityService<Project, Long> {
	Collection<Project> getOwnProjects();
}
