package de.lh.tool.service.entity.interfaces;

import java.util.List;

import de.lh.tool.domain.dto.NeedDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.Need;

public interface NeedService extends BasicEntityService<Need, Long> {

	NeedDto createNeedDto(NeedDto needDto) throws DefaultException;

	void deleteOwn(Long id) throws DefaultException;

	NeedDto getNeedDtoById(Long id) throws DefaultException;

	NeedDto updateNeedDto(NeedDto needDto, Long id) throws DefaultException;

	List<NeedDto> getNeedDtos(Integer startDiff, Integer endDiff) throws DefaultException;

}
