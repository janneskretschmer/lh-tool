package de.lh.tool.service.entity.interfaces.crud;

import java.util.List;

import de.lh.tool.domain.dto.ItemHistoryDto;
import de.lh.tool.domain.dto.UserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.ItemHistory;
import lombok.NonNull;

public interface ItemHistoryCrudService extends BasicEntityCrudService<ItemHistory, ItemHistoryDto, Long> {
	List<ItemHistoryDto> findDtosByItemId(Long itemId) throws DefaultException;

	UserDto findUserNameDto(@NonNull Long itemId, @NonNull Long id) throws DefaultException;
}
