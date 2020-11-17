package de.lh.tool.service.entity.impl;

import java.time.LocalDate;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.NeedDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Need;
import de.lh.tool.domain.model.ProjectHelperType;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.NeedRepository;
import de.lh.tool.service.entity.interfaces.NeedService;
import de.lh.tool.service.entity.interfaces.ProjectHelperTypeService;
import de.lh.tool.service.entity.interfaces.ProjectService;
import de.lh.tool.service.entity.interfaces.crud.NeedCrudService;
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;

@Service
public class NeedServiceImpl extends BasicEntityCrudServiceImpl<NeedRepository, Need, NeedDto, Long>
		implements NeedService, NeedCrudService {

	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProjectHelperTypeService projectHelperTypeService;

	/**
	 * get all possible needs of the current user's projects
	 * 
	 * @param date delta in days from today (may be negative)
	 * 
	 */
	@Override
	@Transactional
	public NeedDto findDtoByProjectHelperTypeIdAndDate(@NonNull Long projectHelperTypeId, @NonNull LocalDate date)
			throws DefaultException {
		ProjectHelperType projectHelperType = projectHelperTypeService
				.findByIdOrThrowInvalidIdException(projectHelperTypeId);

		Need need = getRepository().findByProjectHelperType_IdAndDate(projectHelperTypeId, date)
				.orElse(Need.builder().projectHelperType(projectHelperType).date(date).quantity(0).build());
		checkFindPermission(need);

		return convertToDto(need);
	}

	@Override
	protected void checkValidity(@NonNull Need need) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_DATE, need.getDate());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_QUANTITY, need.getQuantity());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_PROJECT_HELPER_TYPE_ID, need.getProjectHelperType());
		ValidationUtil.checkSameIdIfExists(ExceptionEnum.EX_NEED_ALREADY_EXISTS,
				getRepository().findByProjectHelperType_IdAndDate(need.getProjectHelperType().getId(), need.getDate()),
				need);
	}

	@Override
	public boolean hasReadPermission(@NonNull Need need) {
		return userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_PROJECT)
				|| Optional.ofNullable(need.getProjectHelperType()).map(ProjectHelperType::getProject)
						.map(projectService::hasReadPermission).orElse(Boolean.FALSE);
	}

	@Override
	public boolean hasWritePermission(@NonNull Need need) {
		return hasReadPermission(need);
	}

	@Override
	public String getRightPrefix() {
		return UserRole.NEEDS_PREFIX;
	}
}
