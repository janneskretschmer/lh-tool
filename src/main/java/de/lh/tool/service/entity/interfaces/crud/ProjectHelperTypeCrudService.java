package de.lh.tool.service.entity.interfaces.crud;

import java.util.List;

import de.lh.tool.domain.dto.ProjectHelperTypeDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.ProjectHelperType;

public interface ProjectHelperTypeCrudService
		extends BasicEntityCrudService<ProjectHelperType, ProjectHelperTypeDto, Long> {

	List<ProjectHelperTypeDto> findDtosByProjectIdAndHelperTypeIdAndWeekday(Long projectId, Long helperTypeId,
			Integer weekday) throws DefaultException;

}
