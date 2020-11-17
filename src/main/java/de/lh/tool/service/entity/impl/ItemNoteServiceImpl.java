package de.lh.tool.service.entity.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

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
import de.lh.tool.service.entity.interfaces.UserService;
import de.lh.tool.service.entity.interfaces.crud.ItemNoteCrudService;
import de.lh.tool.util.ValidationUtil;
import lombok.NonNull;

@Service
public class ItemNoteServiceImpl extends BasicEntityCrudServiceImpl<ItemNoteRepository, ItemNote, ItemNoteDto, Long>
		implements ItemNoteService, ItemNoteCrudService {

	@Autowired
	private ItemService itemService;
	@Autowired
	private UserService userService;

	@Override
	@Transactional
	public List<ItemNoteDto> findDtosByItemId(Long itemId) throws DefaultException {
		checkFindRight();
		Item item = itemService.findByIdOrThrowInvalidIdException(itemId);
		itemService.checkReadPermission(item);
		List<ItemNote> notes = item.getItemNotes().stream().sorted().collect(Collectors.toList());
		return convertToDtoList(notes);
	}

	@Override
	@Transactional
	public ItemNoteDto createDto(ItemNoteDto dto) throws DefaultException {
		dto.setUserId(userService.getCurrentUser().getId());
		dto.setTimestamp(LocalDateTime.now());
		return super.createDto(dto);
	}

	@Override
	@Transactional
	public ItemNoteDto updateDto(ItemNoteDto dto, Long id) throws DefaultException {
		dto.setTimestamp(LocalDateTime.now());
		return super.updateDto(dto, id);
	}

	@Override
	protected void checkValidity(@NonNull ItemNote itemNote) throws DefaultException {
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_NOTE, itemNote.getNote());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_ITEM_ID, itemNote.getItem());
		ValidationUtil.checkNonBlank(ExceptionEnum.EX_NO_TIMESTAMP, itemNote.getTimestamp());
	}

	@Override
	@Transactional
	public UserDto findUserNameDto(@NonNull Long itemId, @NonNull Long noteId) throws DefaultException {
		ItemNote itemNote = findByIdOrThrowInvalidIdException(noteId);

		checkFindPermission(itemNote);

		UserDto userDto = Optional.of(itemNote).map(ItemNote::getUser)
				// don't expose personal data except name
				.map(user -> UserDto.builder().id(user.getId()).firstName(user.getFirstName())
						.lastName(user.getLastName()).build())
				.orElse(new UserDto());

		return userDto;
	}

	@Override
	public boolean hasReadPermission(@NonNull ItemNote entity) {
		return itemService.hasReadPermission(entity.getItem());
	}

	@Override
	public boolean hasWritePermission(@NonNull ItemNote entity) {
		return hasReadPermission(entity) && userService.isCurrentUser(entity.getUser());
	}

	@Override
	public boolean hasDeletePermission(@NonNull ItemNote entity) {
		return super.hasDeletePermission(entity)
				|| userRoleService.hasCurrentUserRight(UserRole.RIGHT_ITEMS_NOTES_DELETE_FOREIGN);
	}

	@Override
	public String getRightPrefix() {
		return UserRole.ITEMS_NOTES_PREFIX;
	}

	@Override
	protected ExceptionEnum getInvalidIdException() {
		return ExceptionEnum.EX_INVALID_NOTE_ID;
	}

}
