package de.lh.tool.service.entity.interfaces;

import java.util.Collection;

import de.lh.tool.domain.dto.ProjectDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.Project;

public interface ProjectService extends BasicEntityService<Project, Long> {
	Collection<ProjectDto> getProjectDtos();

	boolean isOwnProject(Project project);

	ProjectDto saveProjectDto(ProjectDto projectDto) throws DefaultException;

	ProjectDto updateProjectDto(Long id, ProjectDto projectDto) throws DefaultException;

	ProjectDto getProjectDtoById(Long id) throws DefaultException;

	void deleteOwn(Long id) throws DefaultException;

	Collection<Project> getOwnProjects();
}
