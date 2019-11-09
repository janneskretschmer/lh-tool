package de.lh.tool.service.entity.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.TechnicalCrewDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.TechnicalCrew;
import de.lh.tool.repository.TechnicalCrewRepository;
import de.lh.tool.service.entity.interfaces.TechnicalCrewService;

@Service
public class TechnicalCrewServiceImpl
		extends BasicMappableEntityServiceImpl<TechnicalCrewRepository, TechnicalCrew, TechnicalCrewDto, Long>
		implements TechnicalCrewService {

	@Override
	public List<TechnicalCrewDto> getTechnicalCrewDtos() throws DefaultException {
		return convertToDtoList(findAll());
	}

	@Override
	@Transactional
	public TechnicalCrewDto getTechnicalCrewDtoById(Long id) throws DefaultException {
		return convertToDto(findById(id).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID)));
	}

}
