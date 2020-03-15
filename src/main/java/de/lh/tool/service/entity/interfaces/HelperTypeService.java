package de.lh.tool.service.entity.interfaces;

import java.util.List;

import de.lh.tool.domain.dto.HelperTypeDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.HelperType;

public interface HelperTypeService extends MappableEntityService<HelperType, HelperTypeDto, Long> {

	HelperTypeDto createDto(HelperTypeDto dto) throws DefaultException;

	HelperTypeDto updateDto(HelperTypeDto dto, Long id) throws DefaultException;

	List<HelperTypeDto> findDtosByProjectIdAndWeekday(Long projectId, Integer weekday) throws DefaultException;

}
