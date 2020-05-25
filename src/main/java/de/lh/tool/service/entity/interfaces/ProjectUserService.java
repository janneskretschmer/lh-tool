package de.lh.tool.service.entity.interfaces;

import java.util.List;

import de.lh.tool.domain.dto.ProjectUserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.ProjectUser;

public interface ProjectUserService extends BasicEntityService<ProjectUser, Long> {

	ProjectUserDto save(Long projectId, Long userId) throws DefaultException;

	void deleteByProjectAndUser(Long projectId, Long userId) throws DefaultException;

	List<ProjectUserDto> findDtosByUserId(Long userId) throws DefaultException;

}
