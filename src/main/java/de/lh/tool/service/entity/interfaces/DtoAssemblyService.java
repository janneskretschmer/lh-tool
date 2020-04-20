package de.lh.tool.service.entity.interfaces;

import java.time.LocalDate;
import java.util.Map;

import de.lh.tool.domain.dto.assembled.AssembledHelperTypeWrapperDto;
import de.lh.tool.domain.exception.DefaultException;

public interface DtoAssemblyService {

	Map<String, AssembledHelperTypeWrapperDto> findHelperTypesWithNeedsAndUsersBetweenDates(Long projectId,
			LocalDate start, LocalDate end) throws DefaultException;

}
