package de.lh.tool.service.entity.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.ProjectHelperTypeDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.ProjectHelperType;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.ProjectHelperTypeRepository;
import de.lh.tool.service.entity.interfaces.ProjectHelperTypeService;
import de.lh.tool.service.entity.interfaces.ProjectService;
import de.lh.tool.service.entity.interfaces.crud.ProjectHelperTypeCrudService;
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;

@Service
public class ProjectHelperTypeServiceImpl
		extends BasicEntityCrudServiceImpl<ProjectHelperTypeRepository, ProjectHelperType, ProjectHelperTypeDto, Long>
		implements ProjectHelperTypeService, ProjectHelperTypeCrudService {

	@Autowired
	private ProjectService projectService;

	@Override
	public List<ProjectHelperTypeDto> findDtosByProjectIdAndHelperTypeIdAndWeekday(Long projectId, Long helperTypeId,
			Integer weekday) throws DefaultException {
		checkFindRight();
		projectService.checkReadPermission(projectId);

		List<ProjectHelperType> projectHelperTypes = getRepository()
				.findByProjectIdAndNullableHelperTypeIdAndNullableWeekday(projectId, helperTypeId, weekday);
		// in this case result doesn't need to get filtered because the projectId gets
		// checked
		return convertToDtoList(projectHelperTypes);
	}

	@Override
	protected void checkValidity(@NonNull ProjectHelperType projectHelperType) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_PROJECT_ID, projectHelperType.getProject());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_HELPER_TYPE_ID, projectHelperType.getHelperType());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_WEEKDAY, projectHelperType.getWeekday());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_START_TIME, projectHelperType.getStartTime());
	}

	@Override
	public boolean hasReadPermission(@NonNull ProjectHelperType projectHelperType) {
		return Optional.ofNullable(projectHelperType.getProject()).map(projectService::hasReadPermission)
				.orElse(Boolean.FALSE);
	}

	@Override
	public boolean hasWritePermission(@NonNull ProjectHelperType projectHelperType) {
		return Optional.ofNullable(projectHelperType.getProject()).map(projectService::hasWritePermission)
				.orElse(Boolean.FALSE);
	}

	@Override
	public String getRightPrefix() {
		return UserRole.PROJECTS_HELPER_TYPES_PREFIX;
	}

}
