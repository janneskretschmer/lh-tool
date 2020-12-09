package de.lh.tool.service.entity.interfaces.crud;

import java.util.List;

import de.lh.tool.domain.dto.NeedUserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.NeedUser;

public interface NeedUserCrudService extends BasicEntityCrudService<NeedUser, NeedUserDto, Long> {

	NeedUserDto findDtoByNeedIdAndUserId(Long needId, Long userId) throws DefaultException;

	NeedUserDto saveOrUpdateDto(Long needId, Long userId, NeedUserDto dto) throws DefaultException;

	List<NeedUserDto> findDtosByNeedId(Long needId) throws DefaultException;

}
