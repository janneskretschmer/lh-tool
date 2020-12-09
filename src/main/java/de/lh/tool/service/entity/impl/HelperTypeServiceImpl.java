package de.lh.tool.service.entity.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.HelperTypeDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.HelperType;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.HelperTypeRepository;
import de.lh.tool.service.entity.interfaces.HelperTypeService;
import de.lh.tool.service.entity.interfaces.ProjectService;
import de.lh.tool.service.entity.interfaces.crud.HelperTypeCrudService;
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;

@Service
public class HelperTypeServiceImpl
		extends BasicEntityCrudServiceImpl<HelperTypeRepository, HelperType, HelperTypeDto, Long>
		implements HelperTypeService, HelperTypeCrudService {

	@Autowired
	private ProjectService projectService;

	@Override
	@Transactional
	protected void checkValidity(@NonNull HelperType helperType) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_NAME, helperType.getName());
		ValidationUtil.checkSameIdIfExists(ExceptionEnum.EX_HELPER_TYPE_ALREADY_EXISTS,
				getRepository().findByName(helperType.getName()), helperType);
	}

	@Override
	@Transactional
	public List<HelperTypeDto> findDtosByProjectIdAndWeekday(Long projectId, Integer weekday) throws DefaultException {
		checkFindRight();

		List<HelperType> helperTypes;
		if (projectId == null && weekday == null) {
			helperTypes = findAll();
		} else {
			if (projectId != null) {
				projectService.checkReadPermission(projectId);
			} else {
				throw ExceptionEnum.EX_HELPER_TYPE_WEEKDAY_WITHOUT_PROJECT.createDefaultException();
			}
			helperTypes = getRepository().findByProjectIdAndWeekday(projectId, weekday);
		}

		return convertToDtoList(filterFindResult(helperTypes));

	}

	@Override
	public String getRightPrefix() {
		return UserRole.HELPER_TYPES_PREFIX;
	}

	@Override
	public boolean hasReadPermission(@NonNull HelperType entity) {
		return true;
	}

	@Override
	public boolean hasWritePermission(@NonNull HelperType entity) {
		return true;
	}

	@Override
	protected ExceptionEnum getInvalidIdException() {
		return ExceptionEnum.EX_INVALID_HELPER_TYPE_ID;
	}

}
