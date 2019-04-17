package de.lh.tool.service.entity.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.NeedDto;
import de.lh.tool.domain.dto.NeedUserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.HelperType;
import de.lh.tool.domain.model.Need;
import de.lh.tool.domain.model.NeedUser;
import de.lh.tool.domain.model.NeedUserState;
import de.lh.tool.domain.model.Project;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.NeedRepository;
import de.lh.tool.service.entity.interfaces.NeedService;
import de.lh.tool.service.entity.interfaces.NeedUserService;
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
	private NeedUserService needUserService;

	public NeedDto convertToDto(Need need) {
		if (need.getId() == null) {
			return super.convertToDto(need);
		}
		try {
			List<NeedUserDto> needUsers = needUserService.findDtosByNeedId(need.getId()).stream()
					.filter(nu -> nu.getState() != NeedUserState.NONE).collect(Collectors.toList());
			int appliedCount = (int) needUsers.stream().filter(nu -> nu.getState() == NeedUserState.APPLIED).count();
			int approvedCount = (int) needUsers.stream().filter(nu -> nu.getState() == NeedUserState.APPROVED).count();
			NeedDto needDto = super.convertToDto(need);
			if (userRoleService.hasCurrentUserRight(UserRole.RIGHT_NEEDS_APPROVE)) {
				needDto.setUsers(needUsers);
			}
			needDto.setAppliedCount(appliedCount);
			needDto.setApprovedCount(approvedCount);
			return needDto;
		} catch (DefaultException e) {
			return super.convertToDto(need);
		}
	}

	/**
	 * get all possible needs of the current user's projects
	 * 
	 * @param startDiff delta in days from today (may be negative)
	 * @param endDiff   delta in days from today (may be negative)
	 * 
	 */
	@Override
	@Transactional
	public List<NeedDto> getNeedDtos(Integer startDiff, Integer endDiff) throws DefaultException {
		List<NeedDto> needDtos = new ArrayList<>();
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		Date start = DateUtils.addDays(today, ObjectUtils.defaultIfNull(startDiff, 0));
		Date end = DateUtils.addDays(today, ObjectUtils.defaultIfNull(endDiff, 14));
		for (Project project : projectService.getOwnProjects()) {
			// TODO write test
			if (project.getEndDate().before(start) || project.getStartDate().after(end)) {
				continue;
			}
			Map<Date, Map<HelperType, Need>> index = createNeedIndex(
					getRepository().findByProject_IdAndDateBetween(project.getId(), start, end));
			Date date = DateUtils.truncate(project.getStartDate(), Calendar.DATE);
			if (date.before(start)) {
				date = start;
			}
			while (!date.after(project.getEndDate()) && !date.after(end)) {
				for (HelperType helperType : HelperType.values()) {
					Need need = Optional.ofNullable(index.get(date)).map(m -> m.get(helperType)).orElse(
							Need.builder().project(project).helperType(helperType).date(date).quantity(0).build());
					needDtos.add(convertToDto(need));
				}
				date = DateUtils.addDays(date, 1);
			}
		}

		return needDtos;
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
