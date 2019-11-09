package de.lh.tool.service.entity.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.ItemHistoryDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.HistoryType;
import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemHistory;
import de.lh.tool.repository.ItemHistoryRepository;
import de.lh.tool.service.entity.interfaces.ItemHistoryService;
import de.lh.tool.service.entity.interfaces.ItemService;
import de.lh.tool.service.entity.interfaces.UserService;

@Service
public class ItemHistoryServiceImpl
		extends BasicMappableEntityServiceImpl<ItemHistoryRepository, ItemHistory, ItemHistoryDto, Long>
		implements ItemHistoryService {

	@Autowired
	private ItemService itemService;
	@Autowired
	private UserService userService;

	@Override
	@Transactional
	public Collection<ItemHistoryDto> getDtosByItemId(Long itemId) throws DefaultException {
		return convertToDtoList(itemService.findById(itemId)
				.orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID)).getHistory());
	}

	@Override
	@Transactional
	public void logNewBrokenState(Item item) {
		save(ItemHistory.builder().item(item).type(item.getBroken() ? HistoryType.BROKEN : HistoryType.FIXED)
				.user(userService.getCurrentUser()).timestamp(new Date()).build());
	}

	@Override
	@Transactional
	public void logUpdated(Item item) {
		save(ItemHistory.builder().item(item).type(HistoryType.UPDATED).user(userService.getCurrentUser())
				.timestamp(new Date()).build());
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
