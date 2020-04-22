package de.lh.tool.service.entity.impl;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang3.ObjectUtils;
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
import de.lh.tool.service.entity.interfaces.UserRoleService;

@Service
public class HelperTypeServiceImpl
		extends BasicMappableEntityServiceImpl<HelperTypeRepository, HelperType, HelperTypeDto, Long>
		implements HelperTypeService {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private UserRoleService userRoleService;

	@Override
	@Transactional
	public HelperTypeDto createDto(HelperTypeDto dto) throws DefaultException {
		if (dto.getId() != null) {
			throw ExceptionEnum.EX_ID_PROVIDED.createDefaultException();
		}
		if (getRepository().existsByName(dto.getName())) {
			throw ExceptionEnum.EX_HELPER_TYPE_ALREADY_EXISTS.createDefaultException();
		}
		HelperType HelperType = save(convertToEntity(dto));
		return convertToDto(HelperType);
	}

	@Override
	@Transactional
	public HelperTypeDto updateDto(HelperTypeDto dto, Long id) throws DefaultException {
		dto.setId(ObjectUtils.defaultIfNull(id, dto.getId()));
		if (dto.getId() == null) {
			throw ExceptionEnum.EX_NO_ID_PROVIDED.createDefaultException();
		}
		if (!existsById(dto.getId())) {
			throw ExceptionEnum.EX_INVALID_ID.createDefaultException();
		}
		if (getRepository().existsByName(dto.getName())) {
			throw ExceptionEnum.EX_HELPER_TYPE_ALREADY_EXISTS.createDefaultException();
		}
		HelperType HelperType = save(convertToEntity(dto));
		return convertToDto(HelperType);
	}

	@Override
	@Transactional
	public List<HelperTypeDto> findDtosByProjectIdAndWeekday(Long projectId, Integer weekday) throws DefaultException {
		if (!ObjectUtils.anyNotNull(projectId, weekday)) {
			return findAllDtos();
		}

		boolean ownProject = projectService
				.isOwnProject(Optional.ofNullable(projectId).flatMap(projectService::findById)
						.orElseThrow(ExceptionEnum.EX_HELPER_TYPE_WEEKDAY_WITHOUT_PROJECT::createDefaultException));
		if (!ownProject && !userRoleService.hasCurrentUserRight(UserRole.RIGHT_PROJECTS_CHANGE_FOREIGN)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
		return convertToDtoList(getRepository().findByProjectIdAndWeekday(projectId, weekday));
	}

	@Override
	@Transactional
	public void deleteHelperTypeById(Long id) throws DefaultException {
		if (!existsById(id)) {
			throw ExceptionEnum.EX_INVALID_ID.createDefaultException();
		}
		super.deleteById(id);
	}

}
