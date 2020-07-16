package de.lh.tool.service.entity.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
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
import de.lh.tool.service.entity.interfaces.ItemService;
import de.lh.tool.service.entity.interfaces.ProjectService;
import de.lh.tool.service.entity.interfaces.SlotService;
import de.lh.tool.service.entity.interfaces.UserRoleService;
import de.lh.tool.util.DateUtil;
import de.lh.tool.util.ValidationUtil;

@Service
public class ItemServiceImpl extends BasicMappableEntityServiceImpl<ItemRepository, Item, ItemDto, Long>
		implements ItemService {

	@Autowired
	private ItemHistoryService itemHistoryService;

	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private SlotService slotService;

	@Override
	public boolean isViewAllowed(Item item) {
		boolean ownProject = Optional.ofNullable(item).map(Item::getSlot).map(Slot::getStore)
				.map(Store::getStoreProjects).map(storeProjects -> storeProjects.stream().anyMatch(storeProject -> {
					boolean inRange = DateUtil.isDateWithinRange(LocalDate.now(), storeProject.getStart(),
							storeProject.getEnd());
					boolean permissionOnProject = projectService.isOwnProject(storeProject.getProject());
					return inRange && permissionOnProject;
				})).orElse(false);
		boolean hasRight = userRoleService.hasCurrentUserRight(UserRole.RIGHT_ITEMS_GET_FOREIGN_PROJECT);
		return ownProject || hasRight;
	}

	@Override
	@Transactional
	public List<ItemDto> getItemDtos() {
		return convertToDtoList(findAll().stream().filter(this::isViewAllowed).collect(Collectors.toList()));
	}

	@Override
	@Transactional
	public ItemDto getItemDtoById(Long id) throws DefaultException {
		Item item = findById(id).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID));
		if (!isViewAllowed(item)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
		return convertToDto(item);
	}

	@Override
	@Transactional
	public ItemDto createItemDto(ItemDto dto) throws DefaultException {
		if (dto.getId() != null) {
			throw new DefaultException(ExceptionEnum.EX_ID_PROVIDED);
		}
		Item item = save(convertToEntity(dto));
		itemHistoryService.logCreated(item);

		return convertToDto(item);
	}

	@Override
	@Transactional
	public ItemDto updateItemDto(ItemDto dto, Long id) throws DefaultException {
		dto.setId(ObjectUtils.defaultIfNull(id, dto.getId()));
		if (dto.getId() == null) {
			throw new DefaultException(ExceptionEnum.EX_NO_ID_PROVIDED);
		}
		Item item = convertToEntity(dto);
		Item old = findById(dto.getId()).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID));
		if (old.getBroken() ^ item.getBroken()) {
			itemHistoryService.logNewBrokenState(item);
		} else {
			itemHistoryService.logUpdated(item);
		}
		// TODO hack, bc otherwise tags get deleted after changing broken
		if (item.getTags() == null) {
			item.setTags(old.getTags());
		}
		return convertToDto(save(item));
	}

	@Override
	@Transactional
	public ItemDto patchItemDto(ItemDto dto, Long id) throws DefaultException {
		dto.setId(ObjectUtils.defaultIfNull(id, dto.getId()));
		ValidationUtil.checkIdNull(dto.getId());

		Item item = findById(dto.getId()).orElseThrow(ExceptionEnum.EX_INVALID_ITEM_ID::createDefaultException);
		if (!isViewAllowed(item)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}

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
		boolean modifiedSlot = dto.getSlotId() != null && dto.getSlotId() != item.getSlot().getId();

		if (modifiedSlot && allowedToModifySlot) {
			Slot slot = slotService.findById(dto.getSlotId())
					.orElseThrow(ExceptionEnum.EX_INVALID_ID::createDefaultException);
			Slot old = item.getSlot();
			item.setSlot(slot);
			itemHistoryService.logNewSlot(item, old);
			modified = true;
		}
		dto.setSlotId(null);

		if (allowedToModify && dto.hasNonNullField()) {
			ModelMapper modelMapper = new ModelMapper();
			modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
			modelMapper.map(dto, item);
			itemHistoryService.logUpdated(item);
			modified = true;
		}

		if (modified) {
			return convertToDto(save(item));
		}
		return convertToDto(item);
	}

}
