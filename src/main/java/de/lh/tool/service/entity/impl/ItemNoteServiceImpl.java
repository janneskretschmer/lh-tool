package de.lh.tool.service.entity.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.ItemNoteDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.ItemNote;
import de.lh.tool.repository.ItemNoteRepository;
import de.lh.tool.service.entity.interfaces.ItemNoteService;
import de.lh.tool.service.entity.interfaces.ItemService;
import de.lh.tool.service.entity.interfaces.UserService;

@Service
public class ItemNoteServiceImpl extends BasicMappableEntityServiceImpl<ItemNoteRepository, ItemNote, ItemNoteDto, Long>
		implements ItemNoteService {

	@Autowired
	private ItemService itemService;
	@Autowired
	private UserService userService;

	@Override
	@Transactional
	public Collection<ItemNoteDto> getDtosByItemId(Long itemId) throws DefaultException {
		return convertToDtoList(itemService.findById(itemId)
				.orElseThrow(() -> new DefaultException(ExceptionEnum.EX_INVALID_ID)).getItemNotes());
	}

	@Override
	@Transactional
	public ItemNoteDto createItemNoteDto(ItemNoteDto dto) throws DefaultException {
		if (dto.getId() != null) {
			throw new DefaultException(ExceptionEnum.EX_ID_PROVIDED);
		}
		dto.setUserId(userService.getCurrentUser().getId());
		dto.setTimestamp(new Date());
		return convertToDto(save(convertToEntity(dto)));
	}

	@Override
	@Transactional
	public ItemNoteDto updateItemNoteDto(ItemNoteDto dto, Long id) throws DefaultException {
		dto.setId(ObjectUtils.defaultIfNull(id, dto.getId()));
		if (dto.getId() == null) {
			throw new DefaultException(ExceptionEnum.EX_NO_ID_PROVIDED);
		}
		if (dto.getUserId() != userService.getCurrentUser().getId()) {
			throw new DefaultException(ExceptionEnum.EX_FORBIDDEN);
		}
		dto.setTimestamp(new Date());
		return convertToDto(save(convertToEntity(dto)));
	}

	@Override
	public ItemNote convertToEntity(ItemNoteDto dto) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<ItemNoteDto, ItemNote>() {
			@Override
			protected void configure() {
				using(c -> Optional.ofNullable(((ItemNoteDto) c.getSource()).getItemId()).flatMap(itemService::findById)
						.orElse(null)).map(source).setItem(null);
				using(c -> Optional.ofNullable(((ItemNoteDto) c.getSource()).getUserId()).flatMap(userService::findById)
						.orElse(null)).map(source).setUser(null);
			}
		});
		return modelMapper.map(dto, ItemNote.class);
	}

}
