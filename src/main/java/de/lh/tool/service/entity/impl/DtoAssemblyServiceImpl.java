package de.lh.tool.service.entity.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.HelperTypeDto;
import de.lh.tool.domain.dto.NeedDto;
import de.lh.tool.domain.dto.NeedUserDto;
import de.lh.tool.domain.dto.ProjectHelperTypeDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.dto.assembled.AssembledHelperTypeDto;
import de.lh.tool.domain.dto.assembled.AssembledHelperTypeWrapperDto;
import de.lh.tool.domain.dto.assembled.AssembledNeedDto;
import de.lh.tool.domain.dto.assembled.AssembledNeedUserDto;
import de.lh.tool.domain.dto.assembled.AssembledProjectHelperTypeDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.service.entity.interfaces.DtoAssemblyService;
import de.lh.tool.service.entity.interfaces.HelperTypeService;
import de.lh.tool.service.entity.interfaces.NeedService;
import de.lh.tool.service.entity.interfaces.NeedUserService;
import de.lh.tool.service.entity.interfaces.ProjectHelperTypeService;
import de.lh.tool.service.entity.interfaces.UserService;

@Service
public class DtoAssemblyServiceImpl implements DtoAssemblyService {
	@Autowired
	HelperTypeService helperTypeService;
	@Autowired
	ProjectHelperTypeService projectHelperTypeService;
	@Autowired
	NeedService needService;
	@Autowired
	NeedUserService needUserService;
	@Autowired
	UserService userService;

	private ModelMapper modelMapper = new ModelMapper();

	@Transactional
	@Override
	public Map<String, AssembledHelperTypeWrapperDto> findHelperTypesWithNeedsAndUsersBetweenDates(Long projectId,
			LocalDate start, LocalDate end) throws DefaultException {
		HashMap<String, AssembledHelperTypeWrapperDto> data = new HashMap<>();
		LocalDate date = start;
		while (date.isBefore(end) || date.isEqual(end)) {
			final LocalDate finalDate = date;
			data.put(date.toString(),
					new AssembledHelperTypeWrapperDto(helperTypeService
							.findDtosByProjectIdAndWeekday(projectId, finalDate.getDayOfWeek().getValue()).stream()
							.map(ht -> assembleHelperType(projectId, ht, finalDate)).collect(Collectors.toList())));

			date = date.plusDays(1);
		}

		return data;
	}

	private AssembledHelperTypeDto assembleHelperType(Long projectId, HelperTypeDto helperType, LocalDate date) {
		AssembledHelperTypeDto assembled = modelMapper.map(helperType, AssembledHelperTypeDto.class);
		try {
			assembled.setShifts(projectHelperTypeService
					.findDtosByProjectIdAndHelperTypeIdAndWeekday(projectId, helperType.getId(),
							date.getDayOfWeek().getValue())
					.stream().map(pht -> assembleProjectHelperType(pht, date)).collect(Collectors.toList()));
		} catch (DefaultException e) {
			throw new RuntimeException(e);
		}
		return assembled;
	}

	private AssembledProjectHelperTypeDto assembleProjectHelperType(ProjectHelperTypeDto projectHelperType,
			LocalDate date) {
		AssembledProjectHelperTypeDto assembled = modelMapper.map(projectHelperType,
				AssembledProjectHelperTypeDto.class);
		try {
			assembled.setNeed(
					assembleNeed(needService.getNeedDtoByProjectHelperTypeIdAndDate(projectHelperType.getId(), date)));
		} catch (DefaultException e) {
			throw new RuntimeException(e);
		}
		return assembled;
	}

	private AssembledNeedDto assembleNeed(NeedDto need) {
		AssembledNeedDto assembled = modelMapper.map(need, AssembledNeedDto.class);
		try {
			if (need.getId() != null) {
				assembled.setState(needUserService
						.findDtoByNeedIdAndUserId(need.getId(), userService.getCurrentUser().getId()).getState());
				assembled.setUsers(needUserService.findDtosByNeedId(need.getId()).stream().map(this::assembleNeedUser)
						.collect(Collectors.toList()));
			}
		} catch (DefaultException e) {
			throw new RuntimeException(e);
		}
		return assembled;
	}

	private AssembledNeedUserDto assembleNeedUser(NeedUserDto needUser) {
		AssembledNeedUserDto assembled = modelMapper.map(needUser, AssembledNeedUserDto.class);
		assembled.setUser(modelMapper.map(
				userService.findById(needUser.getUserId())
						.orElseThrow(() -> (new RuntimeException(new DefaultException(ExceptionEnum.EX_INVALID_ID)))),
				UserDto.class));
		return assembled;
	}
}
