package de.lh.tool.service.entity.impl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.transaction.Transactional;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.SlotDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Project;
import de.lh.tool.domain.model.Slot;
import de.lh.tool.domain.model.Store;
import de.lh.tool.domain.model.StoreProject;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.SlotRepository;
import de.lh.tool.service.entity.interfaces.SlotService;
import de.lh.tool.service.entity.interfaces.StoreService;
import de.lh.tool.service.entity.interfaces.UserRoleService;
import de.lh.tool.service.entity.interfaces.UserService;

@Service
public class SlotServiceImpl extends BasicMappableEntityServiceImpl<SlotRepository, Slot, SlotDto, Long>
		implements SlotService {
	@Autowired
	private UserService userService;

	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private StoreService storeService;

	@Override
	@Transactional
	public List<SlotDto> getSlotDtosByStore(Long storeId) {
		List<Slot> slots = StreamSupport.stream(storeService.getOwnStores().spliterator(), false)
				.filter(s -> storeId == null || storeId.equals(s.getId())).map(Store::getSlots)
				.flatMap(Collection::stream).collect(Collectors.toList());
		return convertToDtoList(slots);
	}

	@Override
	@Transactional
	public SlotDto getSlotDtoById(Long id) throws DefaultException {
		Slot slot = findById(id).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID));
		if (!Optional.of(slot).map(Slot::getStore).map(Store::getStoreProjects).map(sp -> {
			if (sp.isEmpty()) {
				return userRoleService.hasCurrentUserRight(UserRole.RIGHT_STORES_GET_FOREIGN_PROJECT);
			}
			// user must be assigned to any project that uses the slot
			return sp.stream().map(StoreProject::getProject).map(Project::getUsers)
					.map(us -> us.stream().anyMatch(u -> userService.getCurrentUser().getId().equals(u.getId())))
					.anyMatch(ok -> {
						return ok || userRoleService.hasCurrentUserRight(UserRole.RIGHT_STORES_GET_FOREIGN_PROJECT);
					});
		}).orElse(userRoleService.hasCurrentUserRight(UserRole.RIGHT_STORES_GET_FOREIGN_PROJECT))) {
			throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
		}
		return convertToDto(slot);
	}

	@Override
	@Transactional
	public SlotDto createSlotDto(SlotDto dto) throws DefaultException {
		if (dto.getId() != null) {
			throw new DefaultException(ExceptionEnum.EX_ID_PROVIDED);
		}
		Slot slot = save(convertToEntity(dto));
		return convertToDto(slot);
	}

	@Override
	@Transactional
	public SlotDto updateSlotDto(SlotDto dto, Long id) throws DefaultException {
		dto.setId(ObjectUtils.defaultIfNull(id, dto.getId()));
		if (dto.getId() == null) {
			throw new DefaultException(ExceptionEnum.EX_NO_ID_PROVIDED);
		}
		Slot slot = save(convertToEntity(dto));
		return convertToDto(slot);
	}

}
