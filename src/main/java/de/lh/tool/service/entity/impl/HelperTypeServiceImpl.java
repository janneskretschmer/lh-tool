package de.lh.tool.service.entity.impl;

import java.util.List;

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
			throw new DefaultException(ExceptionEnum.EX_ID_PROVIDED);
		}
		HelperType HelperType = save(convertToEntity(dto));
		return convertToDto(HelperType);
	}

	@Override
	@Transactional
	public HelperTypeDto updateDto(HelperTypeDto dto, Long id) throws DefaultException {
		dto.setId(ObjectUtils.defaultIfNull(id, dto.getId()));
		if (dto.getId() == null) {
			throw new DefaultException(ExceptionEnum.EX_NO_ID_PROVIDED);
		}
		HelperType HelperType = save(convertToEntity(dto));
		return convertToDto(HelperType);
	}

	@Override
	@Transactional
	public List<HelperTypeDto> findDtosByProjectIdAndWeekday(Long projectId, Integer weekday) throws DefaultException {
		if (!projectService.isOwnProject(projectId)
				&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_PROJECTS_CHANGE_FOREIGN)) {
			throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
		}
		return convertToDtoList(getRepository().findByProjectIdAndWeekday(projectId, weekday));
	}

}
