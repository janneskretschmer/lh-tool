package de.lh.tool.service.entity.interfaces;

import de.lh.tool.domain.dto.NeedUserDto;
import de.lh.tool.domain.exception.DefaultException;
import de.lh.tool.domain.model.NeedUser;

public interface NeedUserService extends BasicEntityService<NeedUser, Long> {

	NeedUserDto saveDto(Long needId, Long userId, NeedUserDto dto) throws DefaultException;

	void deleteByNeedAndUser(Long needId, Long userId) throws DefaultException;

	NeedUserDto findDtoByUserIdAndProjectId(Long needId, Long userId) throws DefaultException;

}
