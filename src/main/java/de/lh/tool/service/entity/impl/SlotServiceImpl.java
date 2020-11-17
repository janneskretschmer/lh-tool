package de.lh.tool.service.entity.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.SlotDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Slot;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.SlotRepository;
import de.lh.tool.service.entity.interfaces.SlotService;
import de.lh.tool.service.entity.interfaces.StoreService;
import de.lh.tool.service.entity.interfaces.crud.SlotCrudService;
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;

@Service
public class SlotServiceImpl extends BasicEntityCrudServiceImpl<SlotRepository, Slot, SlotDto, Long>
		implements SlotService, SlotCrudService {

	@Autowired
	private StoreService storeService;

	@Override
	protected ExceptionEnum getInvalidIdException() {
		return ExceptionEnum.EX_INVALID_SLOT_ID;
	}

	@Override
	@Transactional
	public List<SlotDto> findDtosByFilters(String freeText, String name, String description, Long storeId)
			throws DefaultException {
		checkFindRight();
		List<Slot> slots = getRepository().findByNameAndDescriptionAndStoreIdAndFreeTextIgnoreCase(freeText,
				StringUtils.trimToNull(name), StringUtils.trimToNull(description), storeId);
		return convertToDtoList(filterFindResult(slots));
	}

	@Override
	public String getSlotNameWithStore(Slot slot) {
		return new StringBuilder(slot.getStore().getName()).append(": ").append(slot.getName()).toString();
	}

	@Override
	protected void checkValidity(@NonNull Slot slot) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_NAME, slot.getName());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_OUTSIDE, slot.getOutside());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_STORE_ID, slot.getStore());
	}

	@Override
	protected void checkDeletable(@NonNull Slot slot) throws DefaultException {
		if (!slot.getItems().isEmpty()) {
			throw ExceptionEnum.EX_SLOT_NOT_EMPTY.createDefaultException();
		}
	}

	@Override
	public boolean hasReadPermission(@NonNull Slot slot) {
		boolean hasPermission = userRoleService.hasCurrentUserRight(UserRole.RIGHT_STORES_GET_FOREIGN_PROJECT);
		hasPermission = hasPermission || storeService.hasReadPermission(slot.getStore());
		return hasPermission;
	}

	@Override
	public boolean hasWritePermission(@NonNull Slot slot) {
		return hasReadPermission(slot);
	}

	@Override
	public String getRightPrefix() {
		return UserRole.SLOTS_PREFIX;
	}

}
