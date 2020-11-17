package de.lh.tool.service.entity.impl;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import de.lh.tool.domain.dto.ItemHistoryDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.HistoryType;
import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemHistory;
import de.lh.tool.domain.model.Slot;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.ItemHistoryRepository;
import de.lh.tool.service.entity.interfaces.ItemHistoryService;
import de.lh.tool.service.entity.interfaces.ItemService;
import de.lh.tool.service.entity.interfaces.SlotService;
import de.lh.tool.service.entity.interfaces.UserService;
import de.lh.tool.service.entity.interfaces.crud.ItemHistoryCrudService;
import de.lh.tool.util.ValidationUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Service
public class ItemHistoryServiceImpl
		extends BasicEntityCrudServiceImpl<ItemHistoryRepository, ItemHistory, ItemHistoryDto, Long>
		implements ItemHistoryService, ItemHistoryCrudService {

	@Autowired
	private ItemService itemService;
	@Autowired
	private UserService userService;
	@Autowired
	private SlotService slotService;

	@Override
	@Transactional
	public List<ItemHistoryDto> findDtosByItemId(@NonNull Long itemId) throws DefaultException {
		checkFindRight();
		Item item = itemService.findByIdOrThrowInvalidIdException(itemId);
		itemService.checkReadPermission(item);

		List<ItemHistory> history = item.getHistory().stream()
				.sorted(Comparator.comparing(ItemHistory::getTimestamp).reversed()).collect(Collectors.toList());
		return convertToDtoList(history);
	}

	@Override
	@Transactional
	public UserDto findUserNameDto(@NonNull Long itemId, @NonNull Long id) throws DefaultException {
		ItemHistory event = findByIdOrThrowInvalidIdException(id);

		checkFindPermission(event);

		UserDto userDto = Optional.of(event).map(ItemHistory::getUser)
				// don't expose personal data except name
				.map(user -> UserDto.builder().id(user.getId()).firstName(user.getFirstName())
						.lastName(user.getLastName()).build())
				.orElse(new UserDto());
		return userDto;
	}

	@Override
	protected void checkValidity(@NonNull ItemHistory entity) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_ITEM_ID, entity.getItem());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_TIMESTAMP, entity.getTimestamp());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_TYPE, entity.getType());
	}

	@Override
	@Transactional
	public void logNewBrokenState(Item item) {
		saveHistoryEntry(item, item.getBroken() ? HistoryType.BROKEN : HistoryType.FIXED);
	}

	@Data
	@AllArgsConstructor
	private class FromToData {
		private String from;
		private String to;
	}

	@Override
	@Transactional
	public void logNewSlot(Item item, Slot old) {
		String data = new Gson().toJson(new FromToData(slotService.getSlotNameWithStore(old),
				slotService.getSlotNameWithStore(item.getSlot())));
		saveHistoryEntry(item, data, HistoryType.MOVED);
	}

	@Override
	@Transactional
	public void logNewQuantity(Item item, Double old) {
		DecimalFormat decimalFormat = new DecimalFormat("##.###");
		String data = new Gson()
				.toJson(new FromToData(decimalFormat.format(old), decimalFormat.format(item.getQuantity())));
		saveHistoryEntry(item, data, HistoryType.QUANTITY_CHANGED);
	}

	@Override
	@Transactional
	public void logCreated(Item item) {
		saveHistoryEntry(item, HistoryType.CREATED);
	}

	@Override
	@Transactional
	public void logUpdated(Item item) {
		saveHistoryEntry(item, HistoryType.UPDATED);
	}

	private void saveHistoryEntry(Item item, HistoryType type) {
		saveHistoryEntry(item, null, type);
	}

	private void saveHistoryEntry(Item item, String data, HistoryType type) {
		save(ItemHistory.builder().item(item).type(type).data(data).user(userService.getCurrentUser())
				.timestamp(LocalDateTime.now()).build());
	}

	@Override
	public String getRightPrefix() {
		return UserRole.ITEMS_PREFIX;
	}

	@Override
	public boolean hasReadPermission(@NonNull ItemHistory itemHistory) {
		return itemService.hasReadPermission(itemHistory.getItem());
	}

	@Override
	public boolean hasWritePermission(@NonNull ItemHistory itemHistory) {
		return hasReadPermission(itemHistory);
	}

}
