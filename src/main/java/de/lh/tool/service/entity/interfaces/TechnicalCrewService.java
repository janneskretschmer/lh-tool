package de.lh.tool.service.entity.interfaces;

import java.util.List;

import de.lh.tool.domain.dto.TechnicalCrewDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.TechnicalCrew;

public interface TechnicalCrewService extends BasicEntityService<TechnicalCrew, Long> {

	List<TechnicalCrewDto> getTechnicalCrewDtos() throws DefaultException;

	TechnicalCrewDto getTechnicalCrewDtoById(Long id) throws DefaultException;

}
