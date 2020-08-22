package de.lh.tool.service.entity.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;

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
	public List<SlotDto> getSlotDtosByFilters(String freeText, String name, String description, Long storeId) {
		List<Slot> slots = getRepository()
				.findByNameAndDescriptionAndStoreIdAndFreeTextIgnoreCase(freeText, StringUtils.trimToNull(name),
						StringUtils.trimToNull(description), storeId)
				.stream().filter(slot -> storeService.isViewAllowed(slot.getStore())).collect(Collectors.toList());
		return convertToDtoList(slots);
	}

	@Override
	@Transactional
	public SlotDto getSlotDtoById(Long id) throws DefaultException {
		Slot slot = findSlotByIdIfAllowed(id);
		return convertToDto(slot);
	}

	private @NonNull Slot findSlotByIdIfAllowed(Long id) throws DefaultException {
		Slot slot = findById(id).orElseThrow(ExceptionEnum.EX_INVALID_ID::createDefaultException);
		boolean isAllowedToGetForeign = userRoleService.hasCurrentUserRight(UserRole.RIGHT_STORES_GET_FOREIGN_PROJECT);
		Boolean isAssignedToProject = Optional.of(slot).map(Slot::getStore).map(Store::getStoreProjects).map(sp -> {
			// user must be assigned to any project that uses the slot
			return sp.stream().map(StoreProject::getProject).map(Project::getUsers)
					.anyMatch(us -> us.stream().anyMatch(u -> userService.getCurrentUser().getId().equals(u.getId())));
		}).orElse(false);

		if (!isAllowedToGetForeign && !isAssignedToProject) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
		return slot;
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
		ValidationUtil.checkIdsNonNull(dto.getId());
		findSlotByIdIfAllowed(dto.getId());

		Slot slot = save(convertToEntity(dto));
		return convertToDto(slot);
	}

	@Override
	public String getSlotNameWithStore(Slot slot) {
		return new StringBuilder(slot.getStore().getName()).append(": ").append(slot.getName()).toString();
	}

	@Override
	@Transactional
	public void deleteSlotById(Long slotId) throws DefaultException {
		ValidationUtil.checkIdsNonNull(slotId);
		Slot slot = findSlotByIdIfAllowed(slotId);
		if (!slot.getItems().isEmpty()) {
			throw ExceptionEnum.EX_SLOT_NOT_EMPTY.createDefaultException();
		}
		delete(slot);
	}

}
