package de.lh.tool.service.entity.impl;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.ProjectHelperTypeDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.ProjectHelperType;
import de.lh.tool.repository.ProjectHelperTypeRepository;
import de.lh.tool.service.entity.interfaces.HelperTypeService;
import de.lh.tool.service.entity.interfaces.ProjectHelperTypeService;
import de.lh.tool.service.entity.interfaces.ProjectService;

@Service
public class ProjectHelperTypeServiceImpl extends
		BasicMappableEntityServiceImpl<ProjectHelperTypeRepository, ProjectHelperType, ProjectHelperTypeDto, Long>
		implements ProjectHelperTypeService {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private HelperTypeService helperTypeService;

	@Override
	@Transactional
	public ProjectHelperTypeDto createDto(ProjectHelperTypeDto dto) throws DefaultException {
		if (dto.getId() != null) {
			throw ExceptionEnum.EX_ID_PROVIDED.createDefaultException();
		}
		projectService.checkIfViewable(dto.getProjectId());
		if (!helperTypeService.existsById(dto.getHelperTypeId())) {
			throw ExceptionEnum.EX_INVALID_HELPER_TYPE_ID.createDefaultException();
		}
		ProjectHelperType projectHelperType = save(convertToEntity(dto));
		return convertToDto(projectHelperType);
	}

	@Override
	@Transactional
	public ProjectHelperTypeDto updateDto(ProjectHelperTypeDto dto, Long id) throws DefaultException {
		dto.setId(Optional.ofNullable(ObjectUtils.defaultIfNull(id, dto.getId()))
				.orElseThrow(ExceptionEnum.EX_NO_ID_PROVIDED::createDefaultException));
		projectService.checkIfViewable(dto.getProjectId());
		if (!existsById(dto.getId())) {
			throw ExceptionEnum.EX_INVALID_ID.createDefaultException();
		}
		if (!helperTypeService.existsById(dto.getHelperTypeId())) {
			throw ExceptionEnum.EX_INVALID_HELPER_TYPE_ID.createDefaultException();
		}
		ProjectHelperType projectHelperType = save(convertToEntity(dto));
		return convertToDto(projectHelperType);
	}

	@Override
	public List<ProjectHelperTypeDto> findDtosByProjectIdAndHelperTypeIdAndWeekday(Long projectId, Long helperTypeId,
			Integer weekday) throws DefaultException {
		projectService.checkIfViewable(projectId);
		return convertToDtoList(getRepository().findByProjectIdAndNullableHelperTypeIdAndNullableWeekday(projectId,
				helperTypeId, weekday));
	}

	@Override
	@Transactional
	public void delete(Long id) throws DefaultException {
		if (id == null) {
			throw ExceptionEnum.EX_NO_ID_PROVIDED.createDefaultException();
		}
		ProjectHelperType projectHelperType = findById(id)
				.orElseThrow(ExceptionEnum.EX_INVALID_ID::createDefaultException);
		projectService.checkIfViewable(projectHelperType.getProject());

		deleteById(id);
	}

	@Override
	public ProjectHelperType convertToEntity(ProjectHelperTypeDto dto) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<ProjectHelperTypeDto, ProjectHelperType>() {
			@Override
			protected void configure() {
				using(c -> castToDto(c).map(ProjectHelperTypeDto::getProjectId)
						.flatMap(projectId -> projectService.findById(projectId)).orElse(null)).map(source)
								.setProject(null);

				using(c -> castToDto(c).map(ProjectHelperTypeDto::getHelperTypeId)
						.flatMap(helperTypeId -> helperTypeService.findById(helperTypeId)).orElse(null)).map(source)
								.setHelperType(null);

				using(c -> castToDto(c).map(ProjectHelperTypeDto::getStartTime).map(LocalTime::parse).orElse(null))
						.map(source).setStartTime(null);

				using(c -> castToDto(c).map(ProjectHelperTypeDto::getEndTime).map(LocalTime::parse).orElse(null))
						.map(source).setEndTime(null);
			}

			private Optional<ProjectHelperTypeDto> castToDto(MappingContext<Object, Object> c) {
				return Optional.ofNullable(((ProjectHelperTypeDto) c.getSource()));
			}
		});
		return modelMapper.map(dto, ProjectHelperType.class);
	}

}
