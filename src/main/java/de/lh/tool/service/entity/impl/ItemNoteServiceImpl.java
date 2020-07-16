package de.lh.tool.service.entity.impl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lh.tool.domain.dto.ItemNoteDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.exception.ExceptionEnum;
import de.lh.tool.domain.model.Item;
import de.lh.tool.domain.model.ItemNote;
import de.lh.tool.domain.model.UserRole;
import de.lh.tool.repository.ItemNoteRepository;
import de.lh.tool.service.entity.interfaces.ItemNoteService;
import de.lh.tool.service.entity.interfaces.ItemService;
import de.lh.tool.service.entity.interfaces.UserRoleService;
import de.lh.tool.service.entity.interfaces.UserService;
import de.lh.tool.util.ValidationUtil;

@Service
public class ItemNoteServiceImpl extends BasicMappableEntityServiceImpl<ItemNoteRepository, ItemNote, ItemNoteDto, Long>
		implements ItemNoteService {

	@Autowired
	private ItemService itemService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserRoleService userRoleService;

	@Override
	@Transactional
	public Collection<ItemNoteDto> getDtosByItemId(Long itemId) throws DefaultException {
		Item item = itemService.findById(itemId).orElseThrow(ExceptionEnum.EX_INVALID_ITEM_ID::createDefaultException);
		if (!itemService.isViewAllowed(item)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
		List<ItemNote> notes = item.getItemNotes().stream()
				.sorted(Comparator.comparing(ItemNote::getTimestamp).reversed()).collect(Collectors.toList());
		return convertToDtoList(notes);
	}

	@Override
	@Transactional
	public ItemNoteDto createItemNoteDto(ItemNoteDto dto) throws DefaultException {
		if (dto.getId() != null) {
			throw ExceptionEnum.EX_ID_PROVIDED.createDefaultException();
		}

		Item item = itemService.findById(dto.getItemId())
				.orElseThrow(ExceptionEnum.EX_INVALID_ITEM_ID::createDefaultException);
		if (!itemService.isViewAllowed(item)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}

		dto.setUserId(userService.getCurrentUser().getId());
		dto.setTimestamp(LocalDateTime.now());
		return convertToDto(save(convertToEntity(dto)));
	}

	@Override
	@Transactional
	public ItemNoteDto updateItemNoteDto(ItemNoteDto dto, Long id) throws DefaultException {

		dto.setId(ObjectUtils.defaultIfNull(id, dto.getId()));
		ValidationUtil.checkIdNull(dto.getId());

		if (dto.getUserId() != userService.getCurrentUser().getId()) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
		dto.setTimestamp(LocalDateTime.now());
		return convertToDto(save(convertToEntity(dto)));
	}

	@Override
	@Transactional
	public void deleteItemNoteById(Long id) throws DefaultException {
		ValidationUtil.checkIdNull(id);

		ItemNote itemNote = findById(id).orElseThrow(ExceptionEnum.EX_INVALID_NOTE_ID::createDefaultException);
		if (!userService.getCurrentUser().getId().equals(itemNote.getUser().getId())
				&& !userRoleService.hasCurrentUserRight(UserRole.RIGHT_ITEMS_NOTES_DELETE_FOREIGN)) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}
		delete(itemNote);
	}

	@Override
	@Transactional
	public UserDto getUserNameDto(Long itemId, Long noteId) throws DefaultException {
		ValidationUtil.checkIdNull(itemId, noteId);

		ItemNote itemNote = findById(noteId).orElseThrow(ExceptionEnum.EX_INVALID_NOTE_ID::createDefaultException);

		if (!itemService.isViewAllowed(itemNote.getItem())) {
			throw ExceptionEnum.EX_FORBIDDEN.createDefaultException();
		}

		UserDto userDto = Optional.of(itemNote).map(ItemNote::getUser)
				// don't expose personal data except name
				.map(user -> UserDto.builder().id(user.getId()).firstName(user.getFirstName())
						.lastName(user.getLastName()).build())
				.orElse(new UserDto());

		return userDto;
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
