package de.lh.tool.service.entity.impl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
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
import de.lh.tool.repository.ItemHistoryRepository;
import de.lh.tool.service.entity.interfaces.ItemHistoryService;
import de.lh.tool.service.entity.interfaces.ItemService;
import de.lh.tool.service.entity.interfaces.SlotService;
import de.lh.tool.service.entity.interfaces.UserService;
import de.lh.tool.util.ValidationUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

@Service
public class ItemHistoryServiceImpl
		extends BasicMappableEntityServiceImpl<ItemHistoryRepository, ItemHistory, ItemHistoryDto, Long>
		implements ItemHistoryService {

	@Autowired
	private ItemService itemService;
	@Autowired
	private UserService userService;
	@Autowired
	private SlotService slotService;

	@Override
	@Transactional
	public Collection<ItemHistoryDto> getDtosByItemId(Long itemId) throws DefaultException {
		ValidationUtil.checkIdNull(itemId);
		Item item = itemService.findById(itemId).orElseThrow(ExceptionEnum.EX_INVALID_ID::createDefaultException);
		if (!itemService.isViewAllowed(item)) {
			ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
		List<ItemHistory> history = item.getHistory().stream()
				.sorted(Comparator.comparing(ItemHistory::getTimestamp).reversed()).collect(Collectors.toList());
		return convertToDtoList(history);
	}

	@Override
	@Transactional
	public UserDto getUserNameDto(Long itemId, Long id) throws DefaultException {
		ValidationUtil.checkIdNull(itemId, id);

		ItemHistory event = findById(id).orElseThrow(ExceptionEnum.EX_INVALID_ID::createDefaultException);
		if (!itemService.isViewAllowed(event.getItem())) {
			ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}

		UserDto userDto = Optional.of(event).map(ItemHistory::getUser)
				// don't expose personal data except name
				.map(user -> UserDto.builder().id(user.getId()).firstName(user.getFirstName())
						.lastName(user.getLastName()).build())
				.orElse(new UserDto());
		return userDto;
	}

	@Override
	@Transactional
	public void logNewBrokenState(Item item) {
		save(ItemHistory.builder().item(item).type(item.getBroken() ? HistoryType.BROKEN : HistoryType.FIXED)
				.user(userService.getCurrentUser()).timestamp(LocalDateTime.now()).build());
	}

	@Override
	@Transactional
	public void logNewSlot(Item item, Slot old) {
		String data = new Gson().toJson(
				new MoveData(slotService.getSlotNameWithStore(old), slotService.getSlotNameWithStore(item.getSlot())));
		save(ItemHistory.builder().item(item).type(HistoryType.MOVED).data(data).user(userService.getCurrentUser())
				.timestamp(LocalDateTime.now()).build());
	}

	@Data
	@AllArgsConstructor
	private class MoveData {
		private String from;
		private String to;
	}

	@Override
	@Transactional
	public void logCreated(Item item) {
		save(ItemHistory.builder().item(item).type(HistoryType.CREATED).user(userService.getCurrentUser())
				.timestamp(LocalDateTime.now()).build());
	}

	@Override
	@Transactional
	public void logUpdated(Item item) {
		save(ItemHistory.builder().item(item).type(HistoryType.UPDATED).user(userService.getCurrentUser())
				.timestamp(LocalDateTime.now()).build());
	}

	@Override
	public ItemHistory convertToEntity(ItemHistoryDto dto) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<ItemHistoryDto, ItemHistory>() {
			@Override
			protected void configure() {
				using(c -> Optional.ofNullable(((ItemHistoryDto) c.getSource()).getItemId())
						.flatMap(itemService::findById).orElse(null)).map(source).setItem(null);
				using(c -> Optional.ofNullable(((ItemHistoryDto) c.getSource()).getUserId())
						.flatMap(userService::findById).orElse(null)).map(source).setUser(null);
			}
		});
		return modelMapper.map(dto, ItemHistory.class);
	}

}
