package de.lh.tool.service.entity.interfaces;

import java.util.List;

import de.lh.tool.domain.dto.ProjectHelperTypeDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.ProjectHelperType;

public interface ProjectHelperTypeService extends BasicEntityService<ProjectHelperType, Long> {

	ProjectHelperTypeDto findDtoById(Long id) throws DefaultException;

	ProjectHelperTypeDto createDto(ProjectHelperTypeDto dto) throws DefaultException;

	ProjectHelperTypeDto updateDto(ProjectHelperTypeDto dto, Long id) throws DefaultException;

	List<ProjectHelperTypeDto> findDtosByProjectIdAndHelperTypeIdAndWeekday(Long projectId, Long helperTypeId,
			Integer weekday) throws DefaultException;

}
