package de.lh.tool.service.entity.interfaces.crud;

import java.util.List;

import de.lh.tool.domain.dto.ProjectUserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.ProjectUser;

public interface ProjectUserCrudService extends BasicEntityCrudService<ProjectUser, ProjectUserDto, Long> {

	ProjectUserDto createDto(Long projectId, Long userId) throws DefaultException;

	void deleteByProjectAndUser(Long projectId, Long userId) throws DefaultException;

	List<ProjectUserDto> findDtosByUserId(Long userId) throws DefaultException;

}
