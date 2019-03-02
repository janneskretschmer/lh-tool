package de.lh.tool.service.entity.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.NeedDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.HelperType;
import de.lh.tool.domain.model.Need;
import de.lh.tool.domain.model.Project;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.NeedRepository;
import de.lh.tool.service.entity.interfaces.NeedService;
import de.lh.tool.service.entity.interfaces.ProjectService;
import de.lh.tool.service.entity.interfaces.UserRoleService;

@Service
public class NeedServiceImpl extends BasicMappableEntityServiceImpl<NeedRepository, Need, NeedDto, Long>
		implements NeedService {
	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private ProjectService projectService;

	/**
	 * get all possible needs of the current user's projects
	 */
	@Override
	@Transactional
	public List<NeedDto> getNeedDtos() {
		List<Need> needs = new ArrayList<>();
		for (Project project : projectService.getOwnProjects()) {
			Map<Date, Map<HelperType, Need>> index = createNeedIndex(getRepository().findByProject_Id(project.getId()));
			Date date = DateUtils.truncate(project.getStartDate(), Calendar.DATE);
			while (!date.after(project.getEndDate())) {
				for (HelperType helperType : HelperType.values()) {
					needs.add(Optional.ofNullable(index.get(date)).map(m -> m.get(helperType)).orElse(
							Need.builder().project(project).helperType(helperType).date(date).quantity(0).build()));
				}
				date = DateUtils.addDays(date, 1);
			}
		}

		return convertToDtoList(needs);
	}

	public Map<Date, Map<HelperType, Need>> createNeedIndex(Iterable<Need> needs) {
		Map<Date, Map<HelperType, Need>> index = new HashMap<>();
		for (Need need : needs) {
			need.setDate(DateUtils.truncate(need.getDate(), Calendar.DATE));
			Map<HelperType, Need> tmp = ObjectUtils.defaultIfNull(index.get(need.getDate()), new HashMap<>());
			tmp.put(need.getHelperType(), need);
			index.put(need.getDate(), tmp);
		}
		return index;
	}

	@Override
	@Transactional
	public NeedDto getNeedDtoById(Long id) throws DefaultException {
		Need need = findById(id).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID));
		if (projectService.isOwnProject(need.getProject())
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
		if (!projectService.isOwnProject(need.getProject())
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
		if (!projectService.isOwnProject(need.getProject())
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
		if (!projectService.isOwnProject(need.getProject())
				&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_CHANGE_FOREIGN_PROJECT)) {
			throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
		}
		delete(need);
	}

	@Override
	public Need convertToEntity(NeedDto dto) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<NeedDto, Need>() {
			@Override
			protected void configure() {
				using(c -> ((NeedDto) c.getSource()).getProjectId() != null
						? projectService.findById(((NeedDto) c.getSource()).getProjectId()).orElse(null)
						: null).map(source).setProject(null);
			}
		});
		return modelMapper.map(dto, Need.class);
	}
}
