package de.lh.tool.service.entity.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.ItemTagDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.ItemTag;
import de.lh.tool.repository.ItemTagRepository;
import de.lh.tool.service.entity.interfaces.ItemService;
import de.lh.tool.service.entity.interfaces.ItemTagService;

@Service
public class ItemTagServiceImpl extends BasicMappableEntityServiceImpl<ItemTagRepository, ItemTag, ItemTagDto, Long>
		implements ItemTagService {

	@Autowired
	private ItemService itemService;

	@Override
	@Transactional
	public List<ItemTagDto> getItemTagDtosByItemId(Long itemId) throws DefaultException {
		return convertToDtoList(itemService.findById(itemId)
				.orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID)).getTags());
	}

	@Override
	@Transactional
	public ItemTagDto getItemTagDtoById(Long id) throws DefaultException {
		// TODO add implementation
		// Tag tag = findById(id).orElseThrow(() -> new
		// DefaultException(ExceptionEnum.EX_INVALID_ID));

		return convertToDto(null);
	}

	@Override
	@Transactional
	public ItemTagDto createItemTagDto(ItemTagDto dto) throws DefaultException {
		if (dto.getId() != null) {
			throw new DefaultException(ExceptionEnum.EX_ID_PROVIDED);
		}
		ItemTag tag = save(convertToEntity(dto));
		return convertToDto(tag);
	}

	@Override
	@Transactional
	public ItemTagDto updateItemTagDto(ItemTagDto dto, Long id) throws DefaultException {
		// TODO add implementation
//		dto.setId(ObjectUtils.defaultIfNull(id, dto.getId()));
//		if (dto.getId() == null) {
//			throw new DefaultException(ExceptionEnum.EX_NO_ID_PROVIDED);
//		}
//		Tag tag = save(convertToEntity(dto));
//		return convertToDto(tag);
		return null;
	}

}
