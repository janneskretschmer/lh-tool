package de.lh.tool.service.entity.impl;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
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
import de.lh.tool.service.entity.interfaces.UserRoleService;

@Service
public class ProjectHelperTypeServiceImpl extends
		BasicMappableEntityServiceImpl<ProjectHelperTypeRepository, ProjectHelperType, ProjectHelperTypeDto, Long>
		implements ProjectHelperTypeService {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private UserRoleService userService;

	@Override
	@Transactional
	public ProjectHelperTypeDto findDtoById(Long id) throws DefaultException {
		// TODO add implementation
		// Tag tag = findById(id).orElseThrow(() -> new
		// DefaultException(ExceptionEnum.EX_INVALID_ID));

		return convertToDto(null);
	}

	@Override
	@Transactional
	public ProjectHelperTypeDto createDto(ProjectHelperTypeDto dto) throws DefaultException {
		if (dto.getId() != null) {
			throw new DefaultException(ExceptionEnum.EX_ID_PROVIDED);
		}
		// TODO check if project is own or user has right to change others
		ProjectHelperType projectHelperType = save(convertToEntity(dto));
		return convertToDto(projectHelperType);
	}

	@Override
	@Transactional
	public ProjectHelperTypeDto updateDto(ProjectHelperTypeDto dto, Long id) throws DefaultException {
		// TODO add implementation
//		dto.setId(ObjectUtils.defaultIfNull(id, dto.getId()));
//		if (dto.getId() == null) {
//			throw new DefaultException(ExceptionEnum.EX_NO_ID_PROVIDED);
//		}
//		Tag tag = save(convertToEntity(dto));
//		return convertToDto(tag);
		return null;
	}

	@Override
	public List<ProjectHelperTypeDto> findDtosByProjectIdAndHelperTypeIdAndWeekday(Long projectId, Long helperTypeId,
			Integer weekday) throws DefaultException {
		if (projectService.isOwnProject(projectId)
				|| userService.hasCurrentUserRight(UserRole.RIGHT_PROJECTS_GET_FOREIGN))
			return convertToDtoList(
					getRepository().findByProject_IdAndHelperType_IdAndWeekday(projectId, helperTypeId, weekday));
		else {
			throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
		}
	}

	@Override
	public ProjectHelperType convertToEntity(ProjectHelperTypeDto dto) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<ProjectHelperTypeDto, ProjectHelperType>() {
			@Override
			protected void configure() {
				using(c -> Optional.ofNullable(((ProjectHelperTypeDto) c.getSource()))
						.map(ProjectHelperTypeDto::getProjectId)
						.flatMap(projectId -> projectService.findById(projectId)).orElse(null)).map(source)
								.setProject(null);
				using(c -> Optional.ofNullable(((ProjectHelperTypeDto) c.getSource()))
						.map(ProjectHelperTypeDto::getHelperTypeId)
						.flatMap(helperTypeId -> projectService.findById(helperTypeId)).orElse(null)).map(source)
								.setHelperType(null);
			}
		});
		return modelMapper.map(dto, ProjectHelperType.class);
	}

}
