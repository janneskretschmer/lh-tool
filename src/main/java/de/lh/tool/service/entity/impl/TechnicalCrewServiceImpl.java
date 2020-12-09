package de.lh.tool.service.entity.impl;

import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.TechnicalCrewDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.TechnicalCrew;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.TechnicalCrewRepository;
import de.lh.tool.service.entity.interfaces.TechnicalCrewService;
import de.lh.tool.service.entity.interfaces.crud.TechnicalCrewCrudService;
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;

@Service
public class TechnicalCrewServiceImpl
		extends BasicEntityCrudServiceImpl<TechnicalCrewRepository, TechnicalCrew, TechnicalCrewDto, Long>
		implements TechnicalCrewService, TechnicalCrewCrudService {

	@Override
	protected ExceptionEnum getInvalidIdException() {
		return ExceptionEnum.EX_INVALID_TECHNICAL_CREW_ID;
	}

	@Override
	protected void checkValidity(@NonNull TechnicalCrew technicalCrew) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_NAME, technicalCrew.getName());
	}

	@Override
	public boolean hasReadPermission(@NonNull TechnicalCrew technicalCrew) {
		return true;
	}

	@Override
	public boolean hasWritePermission(@NonNull TechnicalCrew technicalCrew) {
		return true;
	}

	@Override
	public String getRightPrefix() {
		return UserRole.TECHNICAL_CREWS_PREFIX;
	}

}
