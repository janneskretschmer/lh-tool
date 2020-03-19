package de.lh.tool.service.entity.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
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
import de.lh.tool.service.entity.interfaces.UserRoleService;

@Service
public class NeedServiceImpl extends BasicMappableEntityServiceImpl<NeedRepository, Need, NeedDto, Long>
		implements NeedService {

	@Autowired
	private UserRoleService userRoleService;

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
	public NeedDto getNeedDtoByProjectHelperTypeIdAndDate(Long projectHelperTypeId, LocalDate date)
			throws DefaultException {
		ProjectHelperType projectHelperType = projectHelperTypeService.findById(projectHelperTypeId)
				.filter(pht -> pht.getProject() != null)
				.orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID));

		Optional<Need> need = getRepository().findByProjectHelperType_IdAndDate(projectHelperTypeId, date);

		return convertToDto(
				need.orElse(Need.builder().projectHelperType(projectHelperType).date(date).quantity(0).build()));
	}

	@Override
	@Transactional
	public NeedDto getNeedDtoById(Long id) throws DefaultException {
		Need need = findById(id).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID));
		if (projectService.isOwnProject(need.getProjectHelperType().getProject())
				|| userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_GET_FOREIGN)) {
			return convertToDto(need);
		}
		throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
	}

	@Override
	@Transactional
	public NeedDto createNeedDto(NeedDto needDto) throws DefaultException {
		if (needDto.getId() != null) {
			throw new DefaultException(ExceptionEnum.EX_ID_PROVIDED);
		}
		Need need = convertToEntity(needDto);
		if (!projectService.isOwnProject(need.getProjectHelperType().getProject())
				&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_PROJECT)) {
			throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
		}
		need = save(need);
		return convertToDto(need);
	}

	@Override
	@Transactional
	public NeedDto updateNeedDto(NeedDto needDto, Long id) throws DefaultException {
		needDto.setId(ObjectUtils.defaultIfNull(id, needDto.getId()));
		if (needDto.getId() == null) {
			throw new DefaultException(ExceptionEnum.EX_NO_ID_PROVIDED);
		}
		Need need = convertToEntity(needDto);
		if (!projectService.isOwnProject(need.getProjectHelperType().getProject())
				&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_PROJECT)) {
			throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
		}
		need = save(need);
		return convertToDto(need);
	}

	@Override
	@Transactional
	public void deleteOwn(Long id) throws DefaultException {
		Need need = findById(id).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID));
		if (!projectService.isOwnProject(need.getProjectHelperType().getProject())
				&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_PROJECT)) {
			throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
		}
		delete(need);
	}

	public NeedDto convertToDto(Need need) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<Need, NeedDto>() {
			@Override
			protected void configure() {
				using(c -> Date.from(((Need) c.getSource()).getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()))
						.map(source).setDate(null);
			}
		});
		return modelMapper.map(need, NeedDto.class);
	}

	@Override
	public Need convertToEntity(NeedDto dto) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<NeedDto, Need>() {
			@Override
			protected void configure() {
				using(c -> ((NeedDto) c.getSource()).getProjectHelperTypeId() != null ? projectHelperTypeService
						.findById(((NeedDto) c.getSource()).getProjectHelperTypeId()).orElse(null) : null).map(source)
								.setProjectHelperType(null);
				using(c -> Instant.ofEpochMilli(((NeedDto) c.getSource()).getDate().getTime())
						.atZone(ZoneId.systemDefault()).toLocalDate()).map(source).setDate(null);
			}
		});
		return modelMapper.map(dto, Need.class);
	}
}
