package de.lh.tool.service.entity.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.ItemDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.Slot;
import de.lh.tool.domain.model.Store;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.ItemRepository;
import de.lh.tool.service.entity.interfaces.ItemHistoryService;
import de.lh.tool.service.entity.interfaces.ItemItemService;
import de.lh.tool.service.entity.interfaces.ItemService;
import de.lh.tool.service.entity.interfaces.ProjectService;
import de.lh.tool.service.entity.interfaces.crud.ItemCrudService;
import de.lh.tool.util.DateUtil;
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;

@Service
public class ItemServiceImpl extends BasicEntityCrudServiceImpl<ItemRepository, Item, ItemDto, Long>
		implements ItemService, ItemCrudService {

	@Autowired
	private ItemHistoryService itemHistoryService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ItemItemService itemItemService;

	@Override
	@Transactional
	public List<ItemDto> findDtosByFilters(String freeText) throws DefaultException {
		checkFindRight();
		return convertToDtoList(filterFindResult(getRepository().findByFilters(freeText)));
	}

	@Override
	protected void postCreate(@NonNull Item item) {
		itemHistoryService.logCreated(item);
	}

	@Override
	protected void checkValidity(@NonNull Item item) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_BROKEN, item.getBroken());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_CONSUMABLE, item.getConsumable());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_HAS_BARCODE, item.getHasBarcode());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_IDENTIFIER, item.getIdentifier());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_NAME, item.getName());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_OUTSIDE_QUALIFIED, item.getOutsideQualified());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_QUANTITY, item.getQuantity());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_SLOT_ID, item.getSlot());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_TECHNICAL_CREW_ID, item.getTechnicalCrew());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_UNIT, item.getUnit());
		ValidationUtil.checkSameIdIfExists(ExceptionEnum.EX_ITEM_IDENTIFIER_ALREADY_IN_USE,
				getRepository().findByIdentifier(item.getIdentifier()), item);
	}

	@Override
	protected void postUpdate(@NonNull Item oldItem, @NonNull Item newItem) {
		if (oldItem.getBroken() ^ newItem.getBroken()) {
			itemHistoryService.logNewBrokenState(newItem);
			oldItem.setBroken(newItem.getBroken());
		}
		if (!Objects.equals(oldItem.getSlot().getId(), newItem.getSlot().getId())) {
			itemHistoryService.logNewSlot(newItem, oldItem.getSlot());
			oldItem.setSlot(newItem.getSlot());
		}
		if (!Objects.equals(oldItem.getQuantity(), newItem.getQuantity())) {
			itemHistoryService.logNewQuantity(newItem, oldItem.getQuantity());
			oldItem.setQuantity(newItem.getQuantity());
		}
		if (!oldItem.equals(newItem)) {
			itemHistoryService.logUpdated(newItem);
		}
	}

	@Override
	@Transactional
	public ItemDto patchDto(@NonNull ItemDto dto, @NonNull Long id) throws DefaultException {
		dto.setId(id);
		Item item = findById(id).orElseThrow(getInvalidIdException()::createDefaultException);
		checkWritePermission(item);

		boolean allowedToModify = userRoleService.hasCurrentUserRight(UserRole.RIGHT_ITEMS_PUT);
		boolean allowedToModifyBroken = allowedToModify
				|| userRoleService.hasCurrentUserRight(UserRole.RIGHT_ITEMS_PATCH_BROKEN);
		boolean modifiedBroken = dto.getBroken() != null && dto.getBroken() ^ item.getBroken();

		boolean modified = false;
		if (modifiedBroken && allowedToModifyBroken) {
			item.setBroken(dto.getBroken());
			itemHistoryService.logNewBrokenState(item);
			modified = true;
		}
		dto.setBroken(null);

		boolean allowedToModifySlot = allowedToModify
				|| userRoleService.hasCurrentUserRight(UserRole.RIGHT_ITEMS_PATCH_SLOT);
		boolean modifiedSlot = dto.getSlotId() != null && !dto.getSlotId().equals(item.getSlot().getId());

		if (modifiedSlot && allowedToModifySlot) {
			Slot slot = convertToEntity(dto).getSlot();
			Slot old = item.getSlot();
			item.setSlot(slot);
			itemHistoryService.logNewSlot(item, old);
			modified = true;
		}
		dto.setSlotId(null);

		boolean allowedToModifyQuantity = allowedToModify
				|| userRoleService.hasCurrentUserRight(UserRole.RIGHT_ITEMS_PATCH_QUANTITY);
		boolean modifiedQuantity = dto.getQuantity() != null && !dto.getQuantity().equals(item.getQuantity());
		if (modifiedQuantity && allowedToModifyQuantity) {
			Double old = item.getQuantity();
			item.setQuantity(dto.getQuantity());
			itemHistoryService.logNewQuantity(item, old);
			modified = true;
		}
		dto.setQuantity(null);

		if (allowedToModify && dto.hasNonNullField()) {
			modelMapper.map(dto, item);
			itemHistoryService.logUpdated(item);
			modified = true;
		}

		if (modified) {
			checkValidity(item);
			return convertToDto(save(item));
		}
		return convertToDto(item);
	}

	// FUTURE uses itemItemService directly and return list of ItemItems
	@Override
	@Transactional
	public List<ItemDto> findRelatedItemDtosByItemId(@NonNull Long itemId) throws DefaultException {
		// permissions get checked in itemItemService
		return convertToDtoList(itemItemService.findRelatedItemsByItemId(itemId));
	}

	@Override
	public boolean hasReadPermission(@NonNull Item item) {
		boolean hasPermission = userRoleService.hasCurrentUserRight(UserRole.RIGHT_ITEMS_GET_FOREIGN_PROJECT);

		hasPermission = hasPermission || Optional.ofNullable(item).map(Item::getSlot).map(Slot::getStore)
				.map(Store::getStoreProjects).map(storeProjects -> storeProjects.stream().anyMatch(storeProject -> {

					boolean inRange = DateUtil.isDateWithinRange(LocalDate.now(), storeProject.getStart(),
							storeProject.getEnd());

					boolean permissionOnProject = projectService.hasReadPermission(storeProject.getProject());

					return inRange && permissionOnProject;
				})).orElse(false);

		return hasPermission;
	}

	@Override
	public boolean hasWritePermission(@NonNull Item item) {
		return hasReadPermission(item);
	}

	@Override
	public String getRightPrefix() {
		return UserRole.ITEMS_PREFIX;
	}

	@Override
	protected ExceptionEnum getInvalidIdException() {
		return ExceptionEnum.EX_INVALID_ITEM_ID;
	}

}
