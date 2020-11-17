package de.lh.tool.service.entity.interfaces.crud;

import java.time.LocalDate;

import de.lh.tool.domain.dto.NeedDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.Need;

public interface NeedCrudService extends BasicEntityCrudService<Need, NeedDto, Long> {

	NeedDto findDtoByProjectHelperTypeIdAndDate(Long projectHelperTypeId, LocalDate date) throws DefaultException;

}
