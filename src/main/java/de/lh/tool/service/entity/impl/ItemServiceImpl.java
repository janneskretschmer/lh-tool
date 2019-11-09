package de.lh.tool.service.entity.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.ItemDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Item;
import de.lh.tool.repository.ItemRepository;
import de.lh.tool.service.entity.interfaces.ItemHistoryService;
import de.lh.tool.service.entity.interfaces.ItemService;

@Service
public class ItemServiceImpl extends BasicMappableEntityServiceImpl<ItemRepository, Item, ItemDto, Long>
		implements ItemService {

	@Autowired
	ItemHistoryService itemHistoryService;

	@Override
	@Transactional
	public List<ItemDto> getItemDtos() {
		return convertToDtoList(findAll());
	}

	@Override
	@Transactional
	public ItemDto getItemDtoById(Long id) throws DefaultException {
		Item item = findById(id).orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID));
		return convertToDto(item);
	}

	@Override
	@Transactional
	public ItemDto createItemDto(ItemDto dto) throws DefaultException {
		if (dto.getId() != null) {
			throw new DefaultException(ExceptionEnum.EX_ID_PROVIDED);
		}
		Item item = save(convertToEntity(dto));
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

}
