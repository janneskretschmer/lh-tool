package de.lh.tool.service.entity.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
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
import de.lh.tool.service.entity.interfaces.SlotService;
import de.lh.tool.service.entity.interfaces.TechnicalCrewService;
import de.lh.tool.service.entity.interfaces.UserRoleService;
import de.lh.tool.util.DateUtil;
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;

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
	@Autowired
	private TechnicalCrewService technicalCrewService;
	@Autowired
	private ItemItemService itemItemService;

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
	public List<ItemDto> findItemDtosByFilters(String freeText) {
		return convertToDtoList(getRepository().findByFilters(freeText).stream().filter(this::isViewAllowed)
				.collect(Collectors.toList()));
	}

	@Override
	@Transactional
	public ItemDto findItemDtoById(Long id) throws DefaultException {
		Item item = findItemByIdIfAllowed(id);
		return convertToDto(item);
	}

	@Override
	@Transactional
	public ItemDto createItemDto(ItemDto dto) throws DefaultException {
		if (dto.getId() != null) {
			throw ExceptionEnum.EX_ID_PROVIDED.createDefaultException();
		}
		Item item = getValidatedItem(dto);
		if (!isViewAllowed(item)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
		item = save(item);
		itemHistoryService.logCreated(item);

		return convertToDto(item);
	}

	@Override
	@Transactional
	public ItemDto updateItemDto(ItemDto dto, Long id) throws DefaultException {
		dto.setId(ObjectUtils.defaultIfNull(id, dto.getId()));
		ValidationUtil.checkIdsNonNull(dto.getId());
		Item itemToCompare = getValidatedItem(dto);

		Item old = findItemByIdIfAllowed(dto.getId());

		if (old.getBroken() ^ itemToCompare.getBroken()) {
			itemHistoryService.logNewBrokenState(itemToCompare);
			old.setBroken(itemToCompare.getBroken());
		}
		if (!Objects.equals(old.getSlot().getId(), itemToCompare.getSlot().getId())) {
			itemHistoryService.logNewSlot(itemToCompare, old.getSlot());
			old.setSlot(itemToCompare.getSlot());
		}
		if (!Objects.equals(old.getQuantity(), itemToCompare.getQuantity())) {
			itemHistoryService.logNewQuantity(itemToCompare, old.getQuantity());
			old.setQuantity(itemToCompare.getQuantity());
		}
		if (!old.equals(itemToCompare)) {
			itemHistoryService.logUpdated(itemToCompare);
		}

		ModelMapper modelMapper = getCompleteMapper();
		// if itemToCompare gets saved, dependend entities like ItemItemTags get deleted
		// (bc item.getTags==null)
		modelMapper.map(dto, old);
		return convertToDto(save(old));
	}

	private Item getValidatedItem(ItemDto dto) throws DefaultException {
		ValidationUtil.checkAllNonNull(ExceptionEnum.EX_ITEM_NO_NAME, dto.getName());
		ValidationUtil.checkAllNonNull(ExceptionEnum.EX_ITEM_NO_IDENTIFIER, dto.getIdentifier());
		if (getRepository().findByIdentifier(dto.getIdentifier()).map(Item::getId).map(itemId -> Optional
				.ofNullable(dto.getId()).map(itemId::equals).map(BooleanUtils::negate).orElse(Boolean.TRUE))
				.orElse(false)) {
			throw ExceptionEnum.EX_ITEM_IDENTIFIER_ALREADY_IN_USE.createDefaultException();
		}
		Item itemToCompare = convertToEntity(dto);
		ValidationUtil.checkAllNonNull(ExceptionEnum.EX_ITEM_NO_SLOT, itemToCompare.getSlot());
		ValidationUtil.checkAllNonNull(ExceptionEnum.EX_ITEM_NO_TECHNICAL_CREW, itemToCompare.getTechnicalCrew());
		return itemToCompare;
	}

	@Override
	@Transactional
	public ItemDto patchItemDto(ItemDto dto, Long id) throws DefaultException {
		dto.setId(ObjectUtils.defaultIfNull(id, dto.getId()));

		Item item = findItemByIdIfAllowed(dto.getId());

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

		boolean allowedToModifyQuantity = allowedToModify
				|| userRoleService.hasCurrentUserRight(UserRole.RIGHT_ITEMS_PATCH_QUANTITY);
		boolean modifiedQuantity = dto.getQuantity() != null && dto.getQuantity() != item.getQuantity();
		if (modifiedQuantity && allowedToModifyQuantity) {
			Double old = item.getQuantity();
			item.setQuantity(dto.getQuantity());
			itemHistoryService.logNewQuantity(item, old);
			modified = true;
		}
		dto.setQuantity(null);

		if (allowedToModify && dto.hasNonNullField()) {
			ModelMapper modelMapper = getNoSlotMapper();
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

	@Override
	@Transactional
	public void deleteItemById(Long id) throws DefaultException {
		Item item = findItemByIdIfAllowed(id);
		delete(item);
	}

	@Override
	@Transactional
	public List<ItemDto> findRelatedItemDtosByItemId(Long itemId) throws DefaultException {
		Item item = findItemByIdIfAllowed(itemId);
		return convertToDtoList(itemItemService.findRelatedItemsByItem(item));
	}

	private @NonNull Item findItemByIdIfAllowed(Long id) throws DefaultException {
		ValidationUtil.checkIdsNonNull(id);
		Item item = findById(id).orElseThrow(ExceptionEnum.EX_INVALID_ITEM_ID::createDefaultException);
		if (!isViewAllowed(item)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
		return item;
	}

	@Override
	public Item convertToEntity(ItemDto dto) {
		ModelMapper modelMapper = getCompleteMapper();
		return modelMapper.map(dto, Item.class);
	}

	private ModelMapper getCompleteMapper() {
		ModelMapper modelMapper = getNoSlotMapper();
		modelMapper.addMappings(new PropertyMap<ItemDto, Item>() {
			@Override
			protected void configure() {
				using(c -> Optional.ofNullable(((ItemDto) c.getSource()).getSlotId()).flatMap(slotService::findById)
						.orElse(null)).map(source).setSlot(null);
			}
		});
		return modelMapper;
	}

	private ModelMapper getNoSlotMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<ItemDto, Item>() {
			@Override
			protected void configure() {
				using(c -> Optional.ofNullable(((ItemDto) c.getSource()).getTechnicalCrewId())
						.flatMap(technicalCrewService::findById).orElse(null)).map(source).setTechnicalCrew(null);
			}
		});
		return modelMapper;
	}

}
